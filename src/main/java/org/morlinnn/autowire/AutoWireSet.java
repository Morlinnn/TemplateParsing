package org.morlinnn.autowire;

import org.morlinnn.content.ContextContent;
import org.morlinnn.exception.UnknownException;
import org.morlinnn.reader.template.SelectTemplateElement;
import org.morlinnn.reader.template.TemplateElement;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class AutoWireSet {
    public static void setSetArgs(
            Field field,
            Object obj,
            Set<?> value,
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
        TemplateElement value = context.readField(valueElement.getElements().get(0));
        // template type
        Class<?> valueType = value instanceof SelectTemplateElement
                ? ((SelectTemplateElement) value).getSelectionType().getCorrespondingClass()
                : value.getType().getCorrespondingClass();
        // field type
        Class<?> fieldType = (Class<?>) AutoWireList.getGenericArgType(field.getGenericType())[0];
        TabHandler.handleIfTypeNotConsistent(fieldType, Object.class, valueType);
    }

    private static void handleLimit(TemplateElement valueElement, Set<?> value) {
        AutoWireList._handleLimit(valueElement, value.size(), valueElement.getName());
    }

    private static void setField(Field field, Object obj, Set<?> value, TemplateElement valueElement) throws IllegalAccessException {
        if (field.get(obj) == null) {
            field.set(obj, value instanceof HashSet<?>? value : new HashSet<>(value));
        } else {
            throw new UnknownException(valueElement.getName() + " 已经赋值, 这是不应该的");
        }
    }

    private static void setDefault(Field field, Object obj, TemplateElement valueElement) {
        try {
            Set<?> set = new Yaml().load(valueElement.getDefaultValue());
            field.set(obj, set instanceof HashSet<?> ? set : new HashSet<>(set));
        } catch (ClassCastException ignore) {
            throw new IllegalArgumentException(valueElement.getDefaultValue() + " 不是 Map");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
