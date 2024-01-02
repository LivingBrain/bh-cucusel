package com.brainhatchery.cucusel.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.nio.file.NoSuchFileException;

public class JsonHandler {

    @SneakyThrows
    public <T> T loadFileAsInputStream(Class<T> valueType, String jsonPathWithName) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(jsonPathWithName);
        if (inputStream == null) {
            String[] pathAsArray = jsonPathWithName.split("/");
            String fileName = pathAsArray[pathAsArray.length - 1];
            inputStream = this.getClass().getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new NoSuchFileException("File not found.");
            }
        }
        return valueType.cast(convertJsonFileToDataModel(inputStream, valueType));
    }

    private static <T> T convertJsonFileToDataModel(InputStream filePath, Class<T> valueType) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return objectMapper.readValue(filePath, valueType);
    }

    private static <T> T convertJsonAsStringToDataModel(String jsonAsString, Class<T> valueType) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        if (isJson(jsonAsString)) {
            return objectMapper.readValue(jsonAsString, valueType);
        }
        return null;
    }

    private static boolean isJson(final String json) {
        boolean valid = true;
        try {
            new ObjectMapper().readTree(json);
        } catch (JsonProcessingException e) {
            valid = false;
        }
        return valid;
    }
}
