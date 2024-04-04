package org.morlinnn.enums;

import org.morlinnn.exception.IllegalOperationException;
import org.morlinnn.exception.UnknownException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public enum DataType {
    Int,
    Long,
    Short,
    Float,
    Double,
    String,
    Char,
    Byte,
    Bool,
    Dynamic,
    List,
    Object,
    Map,
    IsoMap,
    Set,
    Select;

    public boolean isBasicType() {
        return switch (this) {
            case Int, Bool, Long, Short, Byte, Char, Float, Double, String -> true;
            default -> false;
        };
    }

    public Class<?> getCorrespondingClass() {
        switch (this) {
            case Object -> {
                return java.lang.Object.class;
            }
            case Dynamic -> {
                // 并没有打算创建一个新的标记类, 这只是偷懒的方法
                return DataType.class;
            }
            case Int -> {
                return Integer.class;
            }
            case Float -> {
                return java.lang.Float.class;
            }
            case Short -> {
                return java.lang.Short.class;
            }
            case Bool -> {
                return Boolean.class;
            }
            case Byte -> {
                return Byte.class;
            }
            case Char -> {
                return Character.class;
            }
            case Long -> {
                return java.lang.Long.class;
            }
            case Double -> {
                return Double.class;
            }
            case String -> {
                return java.lang.String.class;
            }
            case Set -> {
                return HashSet.class;
            }
            case List, IsoMap -> {
                return ArrayList.class;
            }
            case Map -> {
                return HashMap.class;
            }
            case Select -> {
                throw new IllegalOperationException("真的需要获取 Select 的类型吗, 如果需要获取子项的类型应该使用 SelectTemplate#getSelectionType#getCorrespondingClass");
            }
            default -> {
                throw new UnknownException("值类型为空(来自于 DataType#getCorrespondingClass)");
            }
        }
    }
}
