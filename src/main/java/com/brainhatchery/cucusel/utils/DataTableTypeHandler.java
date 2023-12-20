package com.brainhatchery.cucusel.utils;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DataTableTypeHandler {
    private static final String NODE_SEPARATOR = ".";
    private static final String NODE_SEPARATOR_REGEX = "\\.";
    private static final String COLLECTION_SEPARATOR = ";";
    private static final Set<Class<?>> WRAPPER_CLASSES = Set.of(
            Boolean.class,
            Character.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Void.class,
            String.class,
            Date.class,
            LocalDate.class,
            BigDecimal.class,
            Map.class);

    /**
     *
     * @param clazz - class of data model
     * @param entry - cucumber data table map
     * @return - deserialized cucumber data table map to object model
     */
    public static <E> E createObjectOfClass(Class<E> clazz, Map<String, String> entry) {
        return createObjectOfClass(clazz, entry, "");
    }


    /**
     *
     * @param clazz - class of data model
     * @param entry - cucumber data table map
     * @param fieldsToSkip - Array of object model fields names to be omitted
     * @return - deserialized cucumber data table map to object model
     */
    @SneakyThrows
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E> E createObjectOfClass(Class<E> clazz, Map<String, String> entry, String... fieldsToSkip) {
        List<String> listOfFields = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
        List<String> fieldsToSkipList = Arrays.stream(fieldsToSkip).collect(Collectors.toList());
        E object = clazz.getDeclaredConstructor().newInstance();
        for (String fieldName : listOfFields) {
            if (!fieldsToSkipList.contains(fieldName)) {
                try {
                    Field field = object.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object valueToSet;
                    Map<String, String> nodeMap = new HashMap<>();
                    for (Map.Entry<String, String> element : entry.entrySet()) {
                        if (element.getKey().contains(fieldName + NODE_SEPARATOR)) {
                            String nodeField = element.getKey().split(NODE_SEPARATOR_REGEX)[1];
                            nodeMap.put(nodeField, element.getValue());
                        }
                    }
                    entry = nodeMap.size() > 0 ? nodeMap : entry;
                    if (isWrapperClass(field.getType()) || field.getType().isEnum()) {
                        valueToSet = customTryParse(entry.get(fieldName), field.getType());
                    } else {
                        if (field.getType().getTypeName().contains("List")) {
                            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                            Class<?> fieldType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                            if (isWrapperClass(fieldType)) {
                                String entryValue = entry.get(fieldName);
                                if (entryValue != null && entryValue.contains(COLLECTION_SEPARATOR)) {
                                    valueToSet = Arrays.asList(entry.get(fieldName).split(COLLECTION_SEPARATOR));
                                } else {
                                    valueToSet = entry.get(fieldName);
                                }
                            } else {
                                valueToSet = Collections.singletonList(createObjectOfClass(fieldType, entry, fieldsToSkip));
                            }
                        } else {
                            valueToSet = createObjectOfClass(field.getType(), entry, fieldsToSkip);
                        }
                    }
                    if (field.getType().isEnum() && valueToSet != null) {
                        field.set(object, Enum.valueOf((Class<Enum>) field.getType(), (String) valueToSet));
                    } else {
                        field.set(object, valueToSet);
                    }
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    public static Boolean isWrapperClass(Class<?> clazz) {
        return WRAPPER_CLASSES.contains(clazz);
    }

    @SuppressWarnings({"unchecked", "WrapperTypeMayBePrimitive"})
    private static <T> T customTryParse(String stringToParse, Class<?> fieldType) {
        if (stringToParse != null) {
            if (fieldType.equals(Boolean.class)) {
                Boolean bool = Boolean.parseBoolean(stringToParse);
                return (T) bool;
            } else if (fieldType.equals(Integer.class)) {
                Integer integer = Integer.parseInt(stringToParse);
                return (T) integer;
            } else if (fieldType.equals(Long.class)) {
                Long longType = Long.parseLong(stringToParse);
                return (T) longType;
            }
        }
        return (T) stringToParse;
    }
}
