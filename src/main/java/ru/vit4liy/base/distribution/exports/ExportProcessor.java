package ru.vit4liy.base.distribution.exports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.vit4liy.base.distribution.ForeignId;
import ru.vit4liy.base.distribution.PrimaryForeignKey;
import ru.vit4liy.base.distribution.PrimaryId;
import ru.vit4liy.rsc.entity.Entity;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class ExportProcessor {
    private final ObjectMapper mapper;

    public ExportProcessor(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T extends Entity> String serialize(Class<T> clazz, List<T> elements) {
        ArrayNode arrayNode = mapper.createArrayNode();

        for (T element : elements) {
            ObjectNode objectNode = (ObjectNode) mapper.valueToTree(element);
            processAnnotations(element, objectNode);
            arrayNode.add(objectNode);
        }
        return arrayNode.toString();
    }

    private void processAnnotations(Object element, ObjectNode jsonNode) {
        Class<?> currentClass = element.getClass();
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();

                try {
                    Object idValue = field.get(element);

                    if (field.isAnnotationPresent(PrimaryId.class)) {
                        replaceWithUuid(jsonNode, fieldName, idValue, "pk", currentClass.getName());
                    } else if (field.isAnnotationPresent(ForeignId.class)) {
                        ForeignId foreignId = field.getAnnotation(ForeignId.class);
                        replaceWithUuid(jsonNode, fieldName, idValue, "fk", foreignId.foreignClass().getName());
                    } else if (field.isAnnotationPresent(PrimaryForeignKey.class)) {
                        PrimaryForeignKey pfk = field.getAnnotation(PrimaryForeignKey.class);
                        replaceWithUuid(jsonNode, fieldName, idValue, "pfk", pfk.foreignClass().getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Can't get field value: " + fieldName, e);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    private void replaceWithUuid(ObjectNode jsonNode, String originalFieldName, Object idValue, String prefix, String originalClassName) {
        jsonNode.remove(originalFieldName);

        if (idValue == null) {
            jsonNode.putNull(originalFieldName + prefix);
        } else {
            String uniqueUuid = generateDeterministicUuid(originalClassName, idValue).toString();
            jsonNode.put(originalFieldName + prefix, uniqueUuid);
        }
    }

    private UUID generateDeterministicUuid(String className, Object idValue) {
        String seed = className + ":" + idValue.toString();
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8));
    }
}