package org.morlinnn.content;

import org.morlinnn.exception.IllegalTypeException;
import org.morlinnn.exception.UnknownException;
import org.morlinnn.reader.TemplateReader;
import org.morlinnn.reader.template.TemplateElement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class AutoWire {
    private static Object getNoArgsObject(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz.getConstructor().newInstance();
    }

    public static Object buildObject(
            Class<?> clazz,
            Context context,
            TemplateElement element,
            Map<String, Object> args
    ) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Object obj = getNoArgsObject(clazz);
        autoAssignArgs(
                obj,
                context,
                (Map<String, Object>) args.get(element.getName())
        );
        return obj;
    }

    private static void autoAssignArgs(
            Object obj,
            Context context,
            Map<String, Object> args
    ) {
        Set<Map.Entry<String, Object>> argEntry = args.entrySet();
        argEntry.forEach(entry -> {
            assign(obj, entry.getKey(), entry.getValue(), context, findCorrespondingTE(entry.getKey(), context), args);
        });
    }

    private static TemplateElement findCorrespondingTE(String name, Context context) {
        TemplateElement result = context.find(name);
        if (result != null) return result;
        return context.findInTE(name);
    }

    /**
     *
     * @param obj
     * @param fieldName field 名称
     * @param value field 需要赋的值, 由 Map/List/Set获取
     * @param context 上下文
     * @param valueElement 值的模板
     * @param args 父类数据
     */
    private static void assign(Object obj, String fieldName, Object value, Context context, TemplateElement valueElement, Map<String, Object> args) {
        if (value == null) return;

        Class<?> oClass = obj.getClass();
        try {
            Field field = oClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Class<?> fieldClass = getValidClass(value, valueElement, field);

            // TODO 处理 Select 判断
            // TODO required, constant, limit, parents, default, exclusive, id, name
            if (Integer.class.equals(fieldClass) || int.class.equals(fieldClass)) {
                field.set(obj, value);
            } else if (Short.class.equals(fieldClass) || short.class.equals(fieldClass)) {
                field.set(obj, value instanceof Short? value : Short.valueOf(String.valueOf(value)));
            } else if (Long.class.equals(fieldClass) || long.class.equals(fieldClass)) {
                field.set(obj, value);
            } else if (Character.class.equals(fieldClass) || char.class.equals(fieldClass)) {
                field.set(obj, value instanceof Character? value : ((String) value).charAt(0));
            } else if (String.class.equals(fieldClass)) {
                field.set(obj, value);
            } else if (Boolean.class.equals(fieldClass) || boolean.class.equals(fieldClass)) {
                field.set(obj, value instanceof Boolean? value : Boolean.valueOf((String) value));
            } else if (Byte.class.equals(fieldClass) || byte.class.equals(fieldClass)) {
                field.set(obj, value instanceof Byte? value : Byte.valueOf((String) value));
            } else if (Float.class.equals(fieldClass) || float.class.equals(fieldClass)) {
                field.set(obj, value instanceof Double? ((Double) value).floatValue() : value);
            } else if (Double.class.equals(fieldClass) || double.class.equals(fieldClass)) {
                field.set(obj, value);
            } else if (List.class.equals(fieldClass)) {
                // List, IsoMap 都会使用这个类型
                field.set(obj, value);
            } else if (Map.class.equals(fieldClass)) {
                // Map
                // 转换为 HashMap
                if (value.getClass().equals(HashMap.class)) {
                    field.set(obj, value);
                } else {
                    field.set(obj, transformMap((Map<?, ?>) value));
                }
            } else if (Set.class.equals(fieldClass)) {
                // Set
                if (value.getClass().equals(HashSet.class)) {
                    field.set(obj, value);
                } else {
                    field.set(obj, transformSet((Set<?>) value));
                }
            } else {
                // Object
                Class<?> clazz = context.getCorrelativeClass(fieldName);
                Object fieldObj = getNoArgsObject(clazz);
                autoAssignArgs(fieldObj, context, (Map<String, Object>) args.get(fieldName));
                field.set(obj, fieldObj);
            }
        } catch (NoSuchFieldException ignored) {
            System.out.println("未找到对应的 field: " + fieldName);
        } catch (IllegalAccessException ignored) {
            System.out.println("错误类型的赋值: " + value);
        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static <K, V> HashMap<K, V> transformMap(Map<K, V> map) {
        return new HashMap<>(map);
    }

    private static <V> HashSet<V> transformSet(Set<V> set) {
        return new HashSet<>(set);
    }

    private static Class<?> getValidClass(Object value, TemplateElement valueElement, Field field) {
        Class<?> fieldClass = field.getType();

        // 检查类型
        Class<?> templateType = valueElement.getType().getCorrespondingClass();
        if (templateType == null) throw new UnknownException("值类型为空(来自于 DataType#getCorrespondingClass)");
        // value 与 template
        if (!templateType.isAssignableFrom(value.getClass())
                // field 与 value
                && !fieldClass.isAssignableFrom(value.getClass())
        ) {
            throw new IllegalTypeException(value.getClass(), valueElement.getType().getCorrespondingClass(), fieldClass);
        }
        return fieldClass;
    }

    private static <T> ArrayList<T> createArrayList(Class<T> clazz) {
        return new ArrayList<T>();
    }

    private static String[] splitStringList(String arrayString) {
        if (arrayString.length() < 3) return null;
        if (arrayString.charAt(0) != '(' || arrayString.charAt(arrayString.length()-1) != ')') return null;

        String temp = arrayString.substring(1, arrayString.length()-1);
        return temp.split(",");
    }

    /**
     * 为 IntArray, ByteArray 设计
     * @param arrayString 如: [0, 1, 3]
     * @return
     */
    private static List<Integer> parseIntArrayList(String arrayString) {
        return Arrays.stream(Objects.requireNonNull(splitStringList(arrayString)))
                .map(e -> Integer.parseInt(Objects.requireNonNull(TemplateReader.removeUselessSpace(e))))
                .toList();
    }

    private static List<Byte> parseByteArrayList(String arrayString) {
        return Arrays.stream(Objects.requireNonNull(splitStringList(arrayString)))
                .map(e -> Byte.parseByte(Objects.requireNonNull(TemplateReader.removeUselessSpace(e))))
                .toList();
    }

    private static boolean checkObject(TemplateElement element, Object obj) {
        // TODO
        return true;
    }
}
