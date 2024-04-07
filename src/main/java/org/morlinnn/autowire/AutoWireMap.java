package org.morlinnn.autowire;

import org.morlinnn.content.ContextContent;
import org.morlinnn.enums.DataType;
import org.morlinnn.exception.IllegalTypeException;
import org.morlinnn.exception.UnknownException;
import org.morlinnn.reader.template.SelectTemplateElement;
import org.morlinnn.reader.template.TemplateElement;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoWireMap {
    public static void setMapArgs(
            Field field,
            Object obj,
            Map<?, ?> value,
            TemplateElement valueElement,
            ContextContent context
    ) throws IllegalAccessException {
        initCheck(field, valueElement, context);

        // limit
        if (valueElement.getLimit() != null) handleLimit(valueElement, value);

        // field 赋值和类型转换
        if (value != null) {
            setField(field, obj, value, valueElement);
        } else if (valueElement.getDefaultValue() != null) {
            setDefault(field, obj, valueElement);
        }
    }

    private static void initCheck(Field field, TemplateElement valueElement, ContextContent context) {
        TemplateElement keyE = context.readField(
                valueElement.getElements()
                        .stream()
                        .filter(e -> e.startsWith("key"))
                        .collect(Collectors.toList())
                        .get(0)
        );
        TemplateElement valueE = context.readField(
                valueElement.getElements()
                        .stream()
                        .filter(e -> !e.startsWith("key"))
                        .collect(Collectors.toList())
                        .get(0)
        );
        // template type
        Class<?> keyType = keyE.getType().getCorrespondingClass();
        Class<?> valueType = valueE instanceof SelectTemplateElement
                ? ((SelectTemplateElement) valueE).getSelectionType().getCorrespondingClass()
                : valueE.getType().getCorrespondingClass();
        // field type
        Type[] argTypes = AutoWireList.getGenericArgType(field.getGenericType());
        Class<?> keyFieldType = (Class<?>) argTypes[0];
        Class<?> valueFieldType = (Class<?>) argTypes[1];

        // dynamic
        if (keyType == DataType.class && keyFieldType != Object.class) {
            throw new IllegalTypeException("Dynamic 类型的接收类应该是 Object");
        } else if (keyType != DataType.class) {
            TabHandler.handleIfTypeNotConsistent(keyFieldType, Object.class, keyType);
        }

        if (valueType == DataType.class && valueFieldType != Object.class) {
            throw new IllegalTypeException("Dynamic 类型的接收类应该是 Object");
        } else if (valueType != DataType.class) {
            TabHandler.handleIfTypeNotConsistent(valueFieldType, Object.class, valueType);
        }
    }

    private static void handleLimit(TemplateElement valueElement, Map<?, ?> value) {
        AutoWireList._handleLimit(valueElement, value.size(), valueElement.getName());
    }

    private static void setField(Field field, Object obj, Map<?, ?> value, TemplateElement valueElement) throws IllegalAccessException {
        if (field.get(obj) == null) {
            field.set(obj, value instanceof HashMap<?,?>? value : new HashMap<>(value));
        } else {
            throw new UnknownException(valueElement.getName() + " 已经赋值, 这是不应该的");
        }
    }

    private static void setDefault(Field field, Object obj, TemplateElement valueElement) {
        try {
            Map<?, ?> map = new Yaml().load(valueElement.getDefaultValue());
            field.set(obj, map instanceof HashMap<?,?> ? map : new HashMap<>(map));
        } catch (ClassCastException ignore) {
            throw new IllegalArgumentException(valueElement.getDefaultValue() + " 不是 Map");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
