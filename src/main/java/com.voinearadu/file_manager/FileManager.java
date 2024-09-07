package com.voinearadu.file_manager;

import com.google.gson.Gson;
import com.voinearadu.file_manager.utils.DateUtils;
import com.voinearadu.file_manager.utils.PathUtils;
import com.voinearadu.lambda.lambda.ReturnLambdaExecutor;
import com.voinearadu.logger.Logger;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record FileManager(@NotNull ReturnLambdaExecutor<Gson> gsonProvider, @NotNull String basePath) {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public synchronized String readFile(@NotNull String directory, @NotNull String fileName) {
        Path path = Paths.get(getDataFolder().getPath(), directory, fileName);
        StringBuilder json = new StringBuilder();
        File file = path.toFile();

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException error) {
                Logger.error(error);
                Logger.error("Could not create file " + fileName + " in directory " + directory);
                return "";
            }
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String curLine = reader.readLine();
            while (curLine != null) {
                json.append(curLine).append('\n');
                curLine = reader.readLine();
            }

            return json.toString().strip();
        } catch (Exception error) {
            Logger.error(error);
            Logger.warn("Could not read file " + fileName + " in directory " + directory);
            return "";
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "UnusedReturnValue"})
    public synchronized @Nullable Path writeFile(@NotNull String directory, @NotNull String fileName, @NotNull String content) {
        Path path = Paths.get(getDataFolder().getPath(), directory, fileName);
        File file = path.toFile();
        file.getParentFile().mkdirs();

        if (!file.exists()) {
            try {
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public synchronized void writeFileAndBackup(@NotNull String directory, @NotNull String fileName, @NotNull String newContent) {
        Path path = Paths.get(getDataFolder().getPath(), directory, fileName);
        File file = path.toFile();
        file.getParentFile().mkdirs();

        String oldContent = readFile(directory, fileName);

        if (!oldContent.equals(newContent)) {
            if (oldContent.isEmpty()) {
                Logger.warn("The file " + path + " was empty. Skipping backup...");
            } else {
                Logger.warn("The file " + path + " has been automatically modified. Creating a backup...");
                Logger.log("================================================");
                Logger.log("Old content: " + oldContent);
                Logger.log("New content: " + newContent);
                Logger.log("================================================");

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

    public synchronized void save(Object object, String directory) {
        Class<?> clazz = object.getClass();

        save(object, directory, PathUtils.toSnakeCase(clazz.getSimpleName()));
    }

    @SneakyThrows
    public synchronized void save(@NotNull Object object, @NotNull String directory, @NotNull String fileName) {
        String json = gsonProvider.execute().toJson(object);

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

    @SneakyThrows
    public synchronized <T> @NotNull T load(@NotNull Class<T> clazz, @NotNull String directory, @NotNull String fileName) {
        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }

        String oldJson = readFile(directory, fileName);

        T output;

        if (oldJson.isEmpty()) {
            Logger.log("The file " + fileName + " in directory '" + directory + "' is empty. Creating a new instance...");
            output = clazz.getDeclaredConstructor().newInstance();
        } else {
            output = gsonProvider.execute().fromJson(oldJson, clazz);
        }

        String newJson = gsonProvider.execute().toJson(output);

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
