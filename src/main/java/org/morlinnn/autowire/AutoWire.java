package org.morlinnn.autowire;

import lombok.NonNull;
import org.morlinnn.content.Context;
import org.morlinnn.content.ContextContent;
import org.morlinnn.enums.DataType;
import org.morlinnn.exception.IllegalTypeException;
import org.morlinnn.reader.template.SelectTemplateElement;
import org.morlinnn.reader.template.TemplateElement;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class AutoWire {
    protected static Object getNoArgsObject(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
                element,
                (Map<String, Object>) args.get(element.getName())
        );
        return obj;
    }

    /**
     *
     * @param obj
     * @param context
     * @param objElement
     * @param args 装载的数据
     */
    protected static void autoAssignArgs(
            Object obj,
            Context context,
            TemplateElement objElement,
            Map<String, Object> args
    ) {
        Set<Map.Entry<String, Object>> argEntry = args.entrySet();
        argEntry.forEach(entry -> {
            System.out.println(entry);
            assign(
                    obj,
                    entry.getKey(),
                    entry.getValue(),
                    context,
                    objElement,
                    findCorrespondingElement(entry.getKey(), context),
                    args
            );
        });
    }

    private static TemplateElement findCorrespondingElement(String name, ContextContent context) {
        TemplateElement result = context.find(name);
        if (result != null) return result;
        return context.findInElement(name);
    }

    /**
     *
     * @param obj
     * @param fieldName field 名称
     * @param value field 需要赋的值, 由 Map/List/Set获取
     * @param context 上下文
     * @param objElement obj 的模板
     * @param valueElement 值的模板
     * @param args 父类数据
     */
    private static void assign(
            Object obj,
            String fieldName,
            Object value,
            Context context,
            TemplateElement objElement,
            @NonNull TemplateElement valueElement,
            Map<String, Object> args
    ) {
        // required
        TabHandler.required(valueElement, value, fieldName);

        // parent's element
        TabHandler.inElementsOfParent(objElement, context, fieldName);

        // default
        if (TabHandler.needToSetDefault(valueElement, value)) {
            value = new Yaml().load(valueElement.getDefaultValue());
        }

        // 空赋值不必要进行
        if (value == null) return;

        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            Class<?> fieldClass = checkIfTypeConsistentGetFieldClass(value, valueElement, field);
            field.setAccessible(true);

            if (valueElement instanceof SelectTemplateElement) {
                // 处理 Select
                AutoWireSelect.initCheckSelect(valueElement, value);
            }

            if (Integer.class.equals(fieldClass) || int.class.equals(fieldClass)) {
                field.set(obj, value);
            } else if (Short.class.equals(fieldClass) || short.class.equals(fieldClass)) {
                // TODO 待验证 Short
                field.set(obj, value instanceof Short? value : Short.valueOf(String.valueOf(value)));
            } else if (Long.class.equals(fieldClass) || long.class.equals(fieldClass)) {
                field.set(obj, value);
            } else if (Character.class.equals(fieldClass) || char.class.equals(fieldClass)) {
                // TODO 待验证 Character
                field.set(obj, value instanceof Character? value : ((String) value).charAt(0));
            } else if (String.class.equals(fieldClass)) {
                field.set(obj, value instanceof String? value : String.valueOf(value));
            } else if (Boolean.class.equals(fieldClass) || boolean.class.equals(fieldClass)) {
                // TODO 待验证 Boolean
                field.set(obj, value instanceof Boolean? value : Boolean.valueOf((String) value));
            } else if (Byte.class.equals(fieldClass) || byte.class.equals(fieldClass)) {
                // TODO 待验证 Byte
                field.set(obj, value instanceof Byte? value : Byte.valueOf((String) value));
            } else if (Float.class.equals(fieldClass) || float.class.equals(fieldClass)) {
                // TODO 待验证 Float
                field.set(obj, value instanceof Double? ((Double) value).floatValue() : value);
            } else if (Double.class.equals(fieldClass) || double.class.equals(fieldClass)) {
                // TODO 待验证 Double
                field.set(obj, value);
            } else if (List.class.equals(fieldClass) || fieldClass.isAssignableFrom(List.class)) {
                // List, IsoMap
                AutoWireList.setListArgs(field, obj, (List<?>) value, valueElement, context);
            } else if (Map.class.equals(fieldClass) || fieldClass.isAssignableFrom(Map.class)) {
                // Map
                AutoWireMap.setMapArgs(field, obj, (Map<?, ?>) value, valueElement, context);
            } else if (Set.class.equals(fieldClass) || fieldClass.isAssignableFrom(Set.class)) {
                // Set
                AutoWireSet.setSetArgs(field, obj, (Set<?>) value, valueElement, context);
            } else {
                // Object
                AutoWireObject.setObjectArgs(field, obj, args, valueElement, context);
            }

//            System.out.println("name: "+ fieldName +"\nvalue: " + value + " type: " + value.getClass() +
//                    "\nfield: " + field.get(obj).getClass() + "\nVE: "+
//                    (valueElement instanceof SelectTemplateElement?((SelectTemplateElement) valueElement).getSelectionType().getCorrespondingClass():valueElement.getType().getCorrespondingClass()) + "\n");
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

    /**
     * 检查 value valueElement field 的类型, 如果正确返回 field 的类型
     * @param value
     * @param valueElement
     * @param field
     * @return
     */
    private static Class<?> checkIfTypeConsistentGetFieldClass(Object value, TemplateElement valueElement, Field field) {
        Class<?> fieldClass = field.getType();

        // Object
        if (valueElement.getType() == DataType.Object) {
            if (!(value instanceof Map)) {
                throw new IllegalTypeException(value.getClass(), Object.class);
            } else {
                return fieldClass;
            }
        }

        // 检查类型
        Class<?> templateType = valueElement instanceof SelectTemplateElement
                ? ((SelectTemplateElement) valueElement).getSelectionType().getCorrespondingClass()
                : valueElement.getType().getCorrespondingClass();
        TabHandler.handleIfTypeNotConsistent(fieldClass, value.getClass(), templateType);
        return fieldClass;
    }

    private static <T> ArrayList<T> createArrayList(Class<T> clazz) {
        return new ArrayList<>();
    }

    private static boolean checkObject(TemplateElement element, Object obj) {
        // TODO
        return true;
    }
}
