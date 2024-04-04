package org.morlinnn.autowire;

import org.morlinnn.content.ContextContent;
import org.morlinnn.enums.DataType;
import org.morlinnn.exception.IllegalTypeException;
import org.morlinnn.exception.UnknownException;
import org.morlinnn.reader.template.SelectTemplateElement;
import org.morlinnn.reader.template.TemplateElement;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AutoWireList {
    public static void setListArgs(
            Field field,
            Object obj,
            List<?> value,
            TemplateElement valueElement,
            ContextContent context
    ) throws IllegalAccessException {
        // 检查列表元素类型
        initCheckList(field, valueElement, context);

        // constant
        if (valueElement.isConstant()) {
            value = createConstantList(value);
        }

        // limit
        if (valueElement.getLimit() != null) handleLimit(valueElement, value);

        // field 赋值和类型转换
        if (value != null) {
            setField(field, obj, value, valueElement);
        } else if (valueElement.getDefaultValue() != null) {
            // default
            setDefault(field, obj, valueElement);
        }
    }

    private static void initCheckList(Field field, TemplateElement valueElement, ContextContent context) {
        // 元素类型应该一致
        Class<?> fieldElementType = getListFieldElementType(field);
        // 无法在运行时获取元素类型, 因为编译后会进行类型擦除
        Class<?> valueElementType = Object.class;
        Class<?> templateElementType = getTemplateElementType(valueElement, context);

        // dynamic
        if (templateElementType == DataType.class && fieldElementType != Object.class) {
            throw new IllegalTypeException("Dynamic 类型的接收类应该是 Object");
        } else if (fieldElementType == Object.class) {
            return;
        }

        TabHandler.handleIfTypeNotConsistent(fieldElementType, valueElementType, templateElementType);
    }

    private static Class<?> getTemplateElementType(TemplateElement valueElement, ContextContent context) {
        if (valueElement.getType() == DataType.IsoMap) return Map.class;

        List<String> elements = valueElement.getElements();
        // 获取第一个元素
        TemplateElement child = context.readField(elements.get(0));
        return (child instanceof SelectTemplateElement)
            ?((SelectTemplateElement) child).getSelectionType().getCorrespondingClass()
            : child.getType().getCorrespondingClass();
    }

    private static Class<?> getListFieldElementType(Field field) {
        Type genericFieldType = field.getGenericType();
        Type[] argTypes = getGenericArgType(genericFieldType);
        return argTypes[0] instanceof ParameterizedType
                ? (Class<?>) ((ParameterizedType) argTypes[0]).getRawType()
                : (Class<?>) argTypes[0];
    }

    protected static Type[] getGenericArgType(Type genericType) {
        if (genericType instanceof ParameterizedType type) {
            return type.getActualTypeArguments();
        }
        return null;
    }

    private static <V> List<V> createConstantList(List<V> list) {
        List<V> constantList = new ArrayList<>();
        list.forEach(e -> {
            if (!constantList.contains(e)) constantList.add(e);
        });
        return constantList;
    }

    private static void handleLimit(TemplateElement valueElement, List<?> value) {
        _handleLimit(valueElement, value.size(), valueElement.getName());
    }

    protected static void _handleLimit(TemplateElement valueElement, int size, String name) {
        Integer min = valueElement.getLimit().get(0);
        Integer max = valueElement.getLimit().size()>1? valueElement.getLimit().get(1) : null;
        if (size < min) throw new IllegalArgumentException(name + " 超过了 limit 限制的最小值: " + min);
        if (max != null && size > max) throw new IllegalArgumentException(name + " 超过了 limit 限制的最大值: " + max);
    }

    private static void setField(Field field, Object obj, List<?> value, TemplateElement valueElement) throws IllegalAccessException {
        if (field.get(obj) == null) {
            field.set(obj, value instanceof ArrayList<?>? value : new ArrayList<>(value));
        } else {
            throw new UnknownException(valueElement.getName() + " 已经赋值, 这是不应该的");
        }
    }

    private static void setDefault(Field field, Object obj, TemplateElement valueElement) {
        try {
            List<?> list = new Yaml().load(valueElement.getDefaultValue());
            field.set(obj, list instanceof ArrayList<?>? list : new ArrayList<>(list));
        } catch (ClassCastException ignore) {
            throw new IllegalArgumentException(valueElement.getDefaultValue() + " 不是 List");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
