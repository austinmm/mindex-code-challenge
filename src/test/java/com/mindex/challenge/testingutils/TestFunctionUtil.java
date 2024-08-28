package com.mindex.challenge.testingutils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class TestFunctionUtil {

    private static final String BASE_FILE_PATH = "src/test/resources/data/";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String buildUrlWithPortAndPath(int port, String path) {
        return String.format("http://localhost:%d%s", port, path);
    }

    public static <T> T extractObjectFromJsonFile(String fileName, Class<T> classType) {
        File file = getFileFromFileName(fileName);
        try {
            return OBJECT_MAPPER.readValue(file, classType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract file:" + fileName, e);
        }
    }

    private static File getFileFromFileName(String fileName) {
        return new File(BASE_FILE_PATH + fileName);
    }
}
