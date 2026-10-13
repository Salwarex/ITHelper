package ru.vit4liy.base.distribution.imports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.vit4liy.base.bus.BackendDataBus;
import ru.vit4liy.base.distribution.ForeignId;
import ru.vit4liy.base.distribution.PrimaryForeignKey;
import ru.vit4liy.rsc.entity.Entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ImportProcessor {
    private final ObjectMapper mapper;
    private final BackendDataBus bus;
    private final Map<MappingKey, Object> idMapping = new HashMap<>();

    public ImportProcessor(ObjectMapper mapper, BackendDataBus bus) {
        this.mapper = mapper;
        this.bus = bus;
    }

    public <T extends Entity> void processNodeArray(JsonNode arrayNode, Class<T> clazz, ServiceAdditionFunction saf) throws Exception {
        if (arrayNode == null || !arrayNode.isArray()) return;

        for (JsonNode node : arrayNode) {
            Map<String, UUID> pks = new HashMap<>();
            Map<String, UUID> fks = new HashMap<>();

            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();

                String realFieldName = null;
                String suffixType = null;

                if (key.endsWith("pk")) {
                    realFieldName = key.substring(0, key.length() - 2);
                    suffixType = "pk";
                } else if (key.endsWith("fk")) {
                    realFieldName = key.substring(0, key.length() - 2);
                    suffixType = "fk";
                } else if (key.endsWith("pfk")) {
                    realFieldName = key.substring(0, key.length() - 3);
                    suffixType = "pfk";
                }

                if (realFieldName != null) {
                    if (field.getValue().isNull()) continue;

                    if (!field.getValue().isTextual()) {
                        throw new RuntimeException("Expected UUID string for field " + key + ", but got: " + field.getValue());
                    }

                    UUID value = UUID.fromString(field.getValue().asText());

                    if (suffixType.equals("pk") || suffixType.equals("pfk")) {
                        pks.put(key, value);
                    }
                    if (suffixType.equals("fk") || suffixType.equals("pfk")) {
                        fks.put(key, value);
                    }
                }
            }

            ObjectNode cleanNode = ((ObjectNode) node).deepCopy();
            for (String key : pks.keySet()) {
                cleanNode.remove(key);
            }
            for (String key : fks.keySet()) {
                cleanNode.remove(key);
            }

            T object = mapper.treeToValue(cleanNode, clazz);

            for (Map.Entry<String, UUID> fkEntry : fks.entrySet()) {
                String jsonKey = fkEntry.getKey();
                UUID originalUuid = fkEntry.getValue();

                String realFieldName;
                if (jsonKey.endsWith("pfk")) {
                    realFieldName = jsonKey.substring(0, jsonKey.length() - 3);
                } else {
                    realFieldName = jsonKey.substring(0, jsonKey.length() - 2);
                }

                Class<?> foreignClass = getForeignClass(clazz, realFieldName);
                MappingKey key = new MappingKey(foreignClass, originalUuid);
                Object newDbId = idMapping.get(key);

                if (newDbId != null) {
                    Field field = findField(clazz, realFieldName);
                    if (field == null) {
                        throw new RuntimeException("Field " + realFieldName + " not found in class " + clazz.getName());
                    }
                    field.setAccessible(true);
                    Object valueToSet = convertType(newDbId, field.getType());
                    field.set(object, valueToSet);
                } else {
                    throw new RuntimeException("Mapping not found for FK '" + realFieldName + "' with UUID " + originalUuid + ". Import order might be violated.");
                }
            }

            Object newId = saf.execute(clazz, object);

            for (Map.Entry<String, UUID> pkEntry : pks.entrySet()) {
                MappingKey pkKey = new MappingKey(clazz, pkEntry.getValue());
                idMapping.put(pkKey, newId);
            }
        }
    }

    @FunctionalInterface
    public interface ServiceAdditionFunction {
        Object execute(Class<?> clazz, Entity entity) throws Exception;
    }

    private record MappingKey(Class<?> entityClass, UUID originalUuid) {}

    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private Class<?> getForeignClass(Class<?> clazz, String fieldName) throws Exception {
        Field field = findField(clazz, fieldName);
        if (field == null) {
            throw new RuntimeException("Field " + fieldName + " not found in " + clazz.getName());
        }

        ForeignId foreignId = field.getAnnotation(ForeignId.class);
        if (foreignId != null) {
            return foreignId.foreignClass();
        }

        PrimaryForeignKey pfk = field.getAnnotation(PrimaryForeignKey.class);
        if (pfk != null) {
            return pfk.foreignClass();
        }

        throw new RuntimeException("Field " + fieldName + " does not have @ForeignId or @PrimaryForeignKey annotation");
    }

    private Object convertType(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isAssignableFrom(value.getClass())) return value;

        if (value instanceof Long) {
            long l = (Long) value;
            if (targetType == int.class || targetType == Integer.class) return (int) l;
            if (targetType == long.class || targetType == Long.class) return l;
        }

        return value;
    }

    public void clearMapping() {
        idMapping.clear();
    }
}