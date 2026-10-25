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
import java.util.*;

public class ExportProcessor {
    private final ObjectMapper mapper;
    private final BackendDataBus bus;

    public ExportProcessor(ObjectMapper mapper, BackendDataBus bus) {
        this.mapper = mapper;
        this.bus = bus;
    }

    public <T extends Entity> String serialize(Class<T> clazz, List<T> elements) {
        // 1. ТОПОЛОГИЧЕСКАЯ СОРТИРОВКА: Гарантируем, что родители идут раньше детей
        List<T> sortedElements = topologicalSort(clazz, elements);

        ArrayNode arrayNode = mapper.createArrayNode();
        for (T element : sortedElements) {
            ObjectNode objectNode = (ObjectNode) mapper.valueToTree(element);
            processAnnotations(clazz, element, objectNode);
            arrayNode.add(objectNode);
        }
        return arrayNode.toString();
    }

    /**
     * Сортирует элементы так, чтобы родительские записи шли перед дочерними.
     * Это критически важно для корректного импорта древовидных структур.
     */
    private <T extends Entity> List<T> topologicalSort(Class<T> clazz, List<T> elements) {
        Field selfRefField = findSelfReferencingForeignId(clazz);
        if (selfRefField == null) {
            return elements; // Не древовидная структура, сортировка не нужна
        }

        Map<Object, T> idToElement = new HashMap<>();
        for (T el : elements) {
            Object id = getPrimaryKeyValue(clazz, el);
            if (id != null) {
                idToElement.put(id, el);
            }
        }

        List<T> sorted = new ArrayList<>();
        Set<Object> addedIds = new HashSet<>();
        int maxIterations = elements.size() * elements.size() + 1; // Защита от бесконечного цикла
        int iteration = 0;

        while (sorted.size() < elements.size() && iteration < maxIterations) {
            iteration++;
            for (T el : elements) {
                Object currentId = getPrimaryKeyValue(clazz, el);
                if (currentId == null || addedIds.contains(currentId)) continue;

                try {
                    selfRefField.setAccessible(true);
                    Object parentId = selfRefField.get(el);

                    // Если родителя нет (null) или он уже добавлен в отсортированный список, добавляем текущий элемент
                    if (parentId == null || addedIds.contains(parentId)) {
                        sorted.add(el);
                        addedIds.add(currentId);
                    }
                } catch (Exception e) {
                    // В случае ошибки доступа считаем, что родителя нет, и добавляем элемент
                    sorted.add(el);
                    addedIds.add(currentId);
                }
            }
        }

        // Если остались элементы (например, циклические ссылки), добавляем их в конец
        for (T el : elements) {
            Object currentId = getPrimaryKeyValue(clazz, el);
            if (currentId != null && !addedIds.contains(currentId)) {
                sorted.add(el);
            }
        }

        return sorted;
    }

    public Object getUniqueValue(Class<? extends Entity> clazz, Object element) {
        // Распаковка Optional
        if (element instanceof Optional) {
            element = ((Optional<?>) element).orElse(null);
        }
        if (element == null) {
            return null;
        }

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

    /**
     * Возвращает реальное значение первичного ключа (например, Long), а не строку.
     * Это необходимо для корректной работы топологической сортировки.
     */
    private Object getPrimaryKeyValue(Class<?> clazz, Object element) {
        if (element instanceof Optional) {
            element = ((Optional<?>) element).orElse(null);
        }
        if (element == null) return null;

        Class<?> searchClass = element.getClass();
        while (searchClass != null && searchClass != Object.class) {
            for (Field field : searchClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(PrimaryId.class)) {
                    try {
                        field.setAccessible(true);
                        return field.get(element);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Can't get PrimaryId field value: " + field.getName(), e);
                    }
                }
            }
            searchClass = searchClass.getSuperclass();
        }
        return null;
    }

    private Long safeGetLongValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalArgumentException("Ожидалось числовое значение (Number), но получено: " + value.getClass().getName());
    }

    private Field findSelfReferencingForeignId(Class<?> clazz) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(ForeignId.class)) {
                    ForeignId ann = field.getAnnotation(ForeignId.class);
                    if (ann.foreignClass().equals(clazz)) {
                        return field;
                    }
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private String buildUniqueSeed(Class<?> clazz, Object element) {
        // 2. КРИТИЧЕСКАЯ ФИКСАЦИЯ: Распаковка Optional в самом начале
        if (element instanceof Optional) {
            element = ((Optional<?>) element).orElse(null);
        }
        if (element == null) {
            return clazz.getName() + ":null";
        }

        Object currentUnique = getUniqueValue((Class<? extends Entity>) clazz, element);
        String currentUniqueStr = (currentUnique != null) ? currentUnique.toString() : String.valueOf(getPrimaryKeyValue(clazz, element));

        Field selfRefField = findSelfReferencingForeignId(clazz);

        if (selfRefField != null) {
            try {
                selfRefField.setAccessible(true);
                Object parentIdValue = selfRefField.get(element);
                if (parentIdValue != null) {
                    Long parentIdLong = safeGetLongValue(parentIdValue);
                    if (parentIdLong != null) {
                        Class<?> foreignClass = selfRefField.getAnnotation(ForeignId.class).foreignClass();
                        Object parentElement = ((GenericEntityBusService<?>) bus.getService(foreignClass))
                                .getById(ClientUser.get(), parentIdLong);

                        if (parentElement != null) {
                            // Рекурсивный вызов безопасно обработает Optional, если сервис его вернет
                            String parentSeed = buildUniqueSeed(foreignClass, parentElement);

                            String prefix = foreignClass.getName() + ":";
                            String parentPath = parentSeed.startsWith(prefix) ? parentSeed.substring(prefix.length()) : parentSeed;

                            return clazz.getName() + ":" + parentPath + "/" + currentUniqueStr;
                        }
                    }
                }
            } catch (Exception e) {
                Log.wrn("Error building hierarchical unique seed for " + clazz.getName() + ": " + e.getMessage());
            }
        }

        return clazz.getName() + ":" + currentUniqueStr;
    }

    private void processAnnotations(Class<? extends Entity> clazz, Object element, ObjectNode jsonNode) {
        Class<?> currentClass = element.getClass();
        List<Field> primaryKeys = new ArrayList<>();

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();

                try {
                    Object fieldValue = field.get(element);

                    if (field.isAnnotationPresent(PrimaryId.class)) {
                        primaryKeys.add(field);
                        String seed = buildUniqueSeed(clazz, element);
                        replaceWithUuid(jsonNode, fieldName, seed, "pk", currentClass.getName());

                    } else if (field.isAnnotationPresent(ForeignId.class)) {
                        ForeignId foreignId = field.getAnnotation(ForeignId.class);
                        Class<? extends Entity> foreignClass = foreignId.foreignClass();

                        Long foreignIdValue = safeGetLongValue(fieldValue);
                        if (foreignIdValue != null) {
                            Object fetchedEntity = ((GenericEntityBusService<?>) bus.getService(foreignClass))
                                    .getById(ClientUser.get(), foreignIdValue);

                            String seed = buildUniqueSeed(foreignClass, fetchedEntity);
                            replaceWithUuid(jsonNode, fieldName, seed, "fk", foreignClass.getName());
                        } else {
                            replaceWithUuid(jsonNode, fieldName, null, "fk", foreignClass.getName());
                        }

                    } else if (field.isAnnotationPresent(PrimaryForeignKey.class)) {
                        PrimaryForeignKey pfk = field.getAnnotation(PrimaryForeignKey.class);
                        Class<? extends Entity> foreignClass = pfk.foreignClass();

                        Long foreignIdValue = safeGetLongValue(fieldValue);
                        if (foreignIdValue != null) {
                            Object fetchedEntity = ((GenericEntityBusService<?>) bus.getService(foreignClass))
                                    .getById(ClientUser.get(), foreignIdValue);

                            String seed = buildUniqueSeed(foreignClass, fetchedEntity);
                            replaceWithUuid(jsonNode, fieldName, seed, "pfk", foreignClass.getName());
                        } else {
                            replaceWithUuid(jsonNode, fieldName, null, "pfk", foreignClass.getName());
                        }
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