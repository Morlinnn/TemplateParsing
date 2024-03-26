package org.morlinnn.exception;

public class IllegalTypeException extends IllegalArgumentException {
    public IllegalTypeException(Class<?> valueType, Class<?> templateClass, Class<?> objectClass) {
        super("类型不匹配, 值类型为 " + valueType + ", yaml 类型为 " + templateClass + ", field 类型为 " + objectClass);
    }
}
