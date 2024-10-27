package com.voinearadu.file_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.voinearadu.file_manager.dto.files.FileObject;
import com.voinearadu.utils.file_manager.FileManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileManagerTests {

    private static final String TEST_1 = "test1";

    private static FileManager fileManager;

    @BeforeAll
    public static void init() {
        Gson gson = new GsonBuilder() //NOPMD - suppressed GsonCreatedForEachMethodCall
                .create();
        fileManager = new FileManager(() -> gson, "tmp");
    }

    @AfterAll
    public static void cleanup() {
        deleteDirectory(fileManager.getDataFolder());
    }

    private static void deleteDirectory(File dir) {
        File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }

        //noinspection ResultOfMethodCallIgnored
        dir.delete();
    }

    @Test
    public void testObjectSaveLoad() {
        FileObject object = new FileObject(101, TEST_1);

        fileManager.save(object);

        FileObject loadedObject = fileManager.load(FileObject.class);

        assertEquals(object.data1, loadedObject.data1);
        assertEquals(object.data2, loadedObject.data2);
    }
}
