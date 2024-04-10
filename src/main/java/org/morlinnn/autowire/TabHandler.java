package org.morlinnn.autowire;

import lombok.NonNull;
import org.morlinnn.content.ContextContent;
import org.morlinnn.exception.IllegalTypeException;
import org.morlinnn.reader.template.TemplateElement;

public class TabHandler {
    public static void required(@NonNull TemplateElement valueElement, Object value, String fieldName) {
        if (valueElement.isRequired() && value == null) {
            throw new NullPointerException(fieldName + " 被标记 required, 不可为空");
        }
    }

    public static void inElementsOfParent(TemplateElement objElement, ContextContent context, String fieldName) {
        if (!objElement.containsInElements(context, fieldName)) {
            throw new IllegalArgumentException(
                    fieldName + " 在错误的位置赋值: " + objElement.getName()
            );
        }
    }

    public static boolean needToSetDefault(TemplateElement valueElement, Object value) {
        return value == null && valueElement.getDefaultValue() != null;
    }

    public static void handleIfTypeNotConsistent(Class<?> fieldClass, Class<?> valueClass, Class<?> templateType) {
        if (typeNotConsistent(fieldClass, valueClass, templateType)) {
            throw new IllegalTypeException(valueClass, templateType, fieldClass);
        }
    }

    public static boolean typeNotConsistent(Class<?> fieldClass, Class<?> valueClass, Class<?> templateType) {
        if (fieldClass == null || valueClass == null || templateType == null) throw new IllegalArgumentException("field, value, templateType 不能为空");
        // value 与 template
        try {
            // 如果 field 和 template 都是 String
            if (templateType == String.class && fieldClass == String.class) return false;

            return (!(templateType.isAssignableFrom(valueClass) || valueClass.isAssignableFrom(templateType)) && isNotWrapperOfPrimitive(templateType, valueClass))
                    // field 与 value
                    || (!(fieldClass.isAssignableFrom(valueClass) || valueClass.isAssignableFrom(fieldClass)) && isNotWrapperOfPrimitive(fieldClass, valueClass));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isNotWrapperOfPrimitive(Class<?> class1, Class<?> class2) throws NoSuchFieldException, IllegalAccessException {
        if (class1.isPrimitive()) {
            return class2.isPrimitive() ? !class2.equals(class1) : !class2.getField("TYPE").get(null).equals(class1);
        } else {
            return class2.isPrimitive() ? !class1.getField("TYPE").get(null).equals(class2) : !class2.equals(class1);
        }
    }
}
