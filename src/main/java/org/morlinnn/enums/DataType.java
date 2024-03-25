package org.morlinnn.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

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
            case Object, Select -> {
                return java.lang.Object.class;
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
            default -> {
                return null;
            }
        }
    }
}
