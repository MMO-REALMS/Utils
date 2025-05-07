package com.raduvoinea.utils.file_manager;

import com.google.gson.Gson;
import com.raduvoinea.utils.file_manager.dto.ISerializer;
import com.raduvoinea.utils.file_manager.utils.DateUtils;
import com.raduvoinea.utils.file_manager.utils.PathUtils;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@AllArgsConstructor
public class FileManager {

	private final @NotNull Holder<ISerializer> gsonHolder;
	private final @NotNull String basePath;

	private synchronized @NotNull String readFile(@NotNull String directory, @NotNull String fileName) {
		Path filePath = Paths.get(getDataFolder().getPath(), directory, fileName);
		File file = filePath.toFile();

		if (!file.exists()) {
			try {
				//noinspection ResultOfMethodCallIgnored
				file.getParentFile().mkdirs();
				//noinspection ResultOfMethodCallIgnored
				file.createNewFile();
			} catch (IOException error) {
				Logger.error("Could not create file " + fileName + " in directory " + Paths.get(getDataFolder().getPath(), directory));
				Logger.error(error);
				return "";
			}
		}
		try {
			return Files.readString(filePath);
		} catch (Exception error) {
			Logger.error(error);
			Logger.warn("Could not read file " + fileName + " in directory " + directory);
			return "";
		}
	}

	@SuppressWarnings("UnusedReturnValue")
	private synchronized @Nullable Path writeFile(@NotNull String directory, @NotNull String fileName, @NotNull String content) {
		Path path = Paths.get(getDataFolder().getPath(), directory, fileName);
		File file = path.toFile();
		//noinspection ResultOfMethodCallIgnored
		file.getParentFile().mkdirs();

		if (!file.exists()) {
			try {
				//noinspection ResultOfMethodCallIgnored
				file.createNewFile();
			} catch (IOException error) {
				Logger.error(error);
				Logger.error("Could not create file " + fileName + " in directory " + directory);
				return null;
			}
		}

		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
			writer.write(content);
		} catch (Exception error) {
			Logger.error(error);
			Logger.error("Could not write to file " + fileName + " in directory " + directory);
		}

		return path;
	}

	private synchronized void writeFileAndBackup(@NotNull String directory, @NotNull String fileName, @NotNull String newContent) {
		Path path = Paths.get(getDataFolder().getPath(), directory, fileName);
		File file = path.toFile();
		file.getParentFile().mkdirs();

		String oldContent = readFile(directory, fileName);

		if (!oldContent.equals(newContent)) {
			if (oldContent.isEmpty()) {
				Logger.warn("The file " + path + " was empty. Skipping backup...");
			} else {
				Logger.warn("The file " + path + " has been automatically modified. Creating a backup...");

				String date = DateUtils.getDate("dd_MM_yyyy_HH_mm_ss");
				String backupFileName = fileName.split(".json")[0] + "_backup_" + date + ".json";

				writeFile(directory, backupFileName, oldContent);
			}

			writeFile(directory, fileName, newContent);
		}
	}

	public synchronized void save(Object object) {
		save(object, "");
	}

	public synchronized void save(@NotNull Object object, String directory) {
		Class<?> clazz = object.getClass();

		save(object, directory, PathUtils.toSnakeCase(clazz.getSimpleName()));
	}

	@SneakyThrows
	private synchronized void save(@NotNull Object object, @NotNull String directory, @NotNull String fileName) {
		String json = gsonHolder.value().serialize(object);

		if (!fileName.endsWith(".json")) {
			fileName += ".json";
		}

		writeFileAndBackup(directory, fileName, json);
	}

	public <T> @NotNull T load(@NotNull Class<T> clazz) {
		return load(clazz, "");
	}

	public <T> @NotNull T load(@NotNull Class<T> clazz, @NotNull String directory) {
		return load(clazz, directory, PathUtils.toSnakeCase(clazz.getSimpleName()));
	}

	private String readFromDisk(@NotNull String directory, @NotNull String fileName) throws IOException, URISyntaxException {
		String fileContents = readFile(directory, fileName);
		String fullPath = directory.isEmpty() ?
				String.join("/", List.of(basePath, fileName)) :
				String.join("/", List.of(basePath, directory, fileName));

		if (!fileContents.isEmpty()) {
			return fileContents;
		}

		Logger.log(new MessageBuilder("The file {path} is empty. Checking for default in resources...")
				.parse("path", fullPath)
		);

		fullPath = "/" + fullPath;

		URL url = this.getClass().getResource(fullPath);

		if (url == null) {
			Logger.log(new MessageBuilder("Failed to read resource:{path}")
					.parse("path", fullPath)
			);
			return "";
		}

		try {
			if ("file".equals(url.getProtocol())) {
				File file = new File(url.toURI());
				return Files.readString(file.toPath());
			} else {
				try (InputStream in = url.openStream()) {
					return new String(in.readAllBytes(), StandardCharsets.UTF_8);
				}
			}
		} catch (Exception e) {
			Logger.log(new MessageBuilder("Failed to read resource:{path}. Error: {error}")
					.parse("path", fullPath)
					.parse("error", e.getMessage())
			);
			return "";
		}
	}

	@SneakyThrows
	public synchronized <T> @NotNull T load(@NotNull Class<T> clazz, @NotNull String directory, @NotNull String fileName) {
		if (!fileName.endsWith(".json")) {
			fileName += ".json";
		}

		String oldJson = readFromDisk(directory, fileName);
		String fullPath = directory.isEmpty() ?
				String.join("/", List.of(basePath, fileName)) :
				String.join("/", List.of(basePath, directory, fileName));

		T output;

		if (oldJson.isEmpty()) {
			Logger.log(new MessageBuilder("Creating new file {path}")
					.parse("path", fullPath)
			);
			output = clazz.getDeclaredConstructor().newInstance();
		} else {
			output = gsonHolder.value().deserialize(oldJson, clazz);
		}

		String newJson = gsonHolder.value().serialize(output);

		writeFileAndBackup(directory, fileName, newJson);

		return output;
	}

	public @NotNull File getDataFolder() {
		String path = this.basePath;
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		return new File(System.getProperty("user.dir") + path);
	}

}
