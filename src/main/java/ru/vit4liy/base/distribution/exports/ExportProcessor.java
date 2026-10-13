package ru.vit4liy.base.distribution.exports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.vit4liy.base.bus.BackendDataBus;
import ru.vit4liy.base.distribution.ForeignId;
import ru.vit4liy.base.distribution.PrimaryForeignKey;
import ru.vit4liy.base.distribution.PrimaryId;
import ru.vit4liy.base.distribution.UniqueValueIdentifier;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.frontend.util.ClientUser;
import ru.vit4liy.rsc.entity.Entity;
import ru.vit4liy.rsc.service.GenericEntityBusService;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExportProcessor {
    private final ObjectMapper mapper;
    private final BackendDataBus bus;

    public ExportProcessor(ObjectMapper mapper, BackendDataBus bus) {
        this.mapper = mapper;
        this.bus = bus;
    }

    public <T extends Entity> String serialize(Class<T> clazz, List<T> elements) {
        ArrayNode arrayNode = mapper.createArrayNode();

        for (T element : elements) {
            ObjectNode objectNode = (ObjectNode) mapper.valueToTree(element);
            processAnnotations(clazz, element, objectNode);
            arrayNode.add(objectNode);
        }
        return arrayNode.toString();
    }

    public Object getUniqueValue(Class<? extends Entity> clazz, Object element){
        Object uniqueId = null;
        Class<?> searchClass = clazz;
        while (searchClass != null && searchClass != Object.class) {
            for (Field field : searchClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(UniqueValueIdentifier.class)) {
                    try {
                        field.setAccessible(true);
                        uniqueId = field.get(element);
                        break;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Can't get UniqueValueIdentifier field value: " + field.getName(), e);
                    }
                }
            }
            if (uniqueId != null) {
                break;
            }
            searchClass = searchClass.getSuperclass();
        }
        return uniqueId;
    }

    private void processAnnotations(Class<? extends Entity> clazz, Object element, ObjectNode jsonNode) {
        //Поиск уникального идентификатора (например, название, инвентарный номер)
        Object currentUniqueId = getUniqueValue(clazz, element);

        //обработка первичных и вторичных ключей
        Class<?> currentClass = element.getClass();
        List<Field> primaryKeys = new ArrayList<>();

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();

                try {
                    Object valueForUuid = (currentUniqueId != null) ? currentUniqueId : field.get(element);

                    if (field.isAnnotationPresent(PrimaryId.class)) {
                        primaryKeys.add(field);
                        replaceWithUuid(jsonNode, fieldName, valueForUuid, "pk", currentClass.getName());
                    } else if (field.isAnnotationPresent(ForeignId.class)) {
                        ForeignId foreignId = field.getAnnotation(ForeignId.class);
                        Class<? extends Entity> foreignClass = foreignId.foreignClass();
                        Object uniqueValue = getUniqueValue(foreignClass, ((GenericEntityBusService<?>) bus.getService(foreignClass)).getById(ClientUser.get(), (Long) field.get(element)));

                        replaceWithUuid(jsonNode, fieldName, uniqueValue != null ? uniqueValue : field.get(element), "fk", foreignClass.getName());
                    } else if (field.isAnnotationPresent(PrimaryForeignKey.class)) {
                        PrimaryForeignKey pfk = field.getAnnotation(PrimaryForeignKey.class);
                        Class<? extends Entity> foreignClass = pfk.foreignClass();
                        Object uniqueValue = getUniqueValue(foreignClass, bus.getService(foreignClass)); //todo: сделать аналогично верхнему возможность пройтись по двум. Может избравиться от pfk

                        replaceWithUuid(jsonNode, fieldName, uniqueValue != null ? uniqueValue : field.get(element), "pfk", foreignClass.getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Can't get field value: " + fieldName, e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    private void replaceWithUuid(ObjectNode jsonNode, String originalFieldName, Object idValue, String prefix, String originalClassName) {
        jsonNode.remove(originalFieldName);

        Log.wrn("Executed for " + prefix);

        if (idValue == null) {
            jsonNode.putNull(originalFieldName + prefix);
        } else {
            String uniqueUuid = generateDeterministicUuid(originalClassName, idValue).toString();
            jsonNode.put(originalFieldName + prefix, uniqueUuid);
            Log.wrn("Result " + uniqueUuid);
        }
    }

    private UUID generateDeterministicUuid(String className, Object idValue) {
        String seed = className + ":" + idValue.toString();
        Log.out("Exported on %s".formatted(seed));
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8));
    }
}