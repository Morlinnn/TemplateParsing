package org.morlinnn.autowire;

import org.morlinnn.content.Context;
import org.morlinnn.exception.UnknownException;
import org.morlinnn.reader.template.TemplateElement;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoWireObject {
    public static void setObjectArgs(
            Field field,
            Object obj,
            Map<?, ?> args,
            TemplateElement valueElement,
            Context context
    ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        initCheck(field, valueElement);

        // exclusive
        if (valueElement.getExclusive() != null) {
            handleExclusive(args, valueElement);
        }

        // field 赋值和类型转换
        if (args != null) {
            setField(field, obj, args, valueElement, context);
        } else if (valueElement.getDefaultValue() != null) {
            setDefault(field, obj, valueElement, context);
        }
    }

    private static void initCheck(Field field, TemplateElement valueElement) {
        if (!field.getName().equals(valueElement.getName())) {
            throw new IllegalArgumentException(valueElement.getName() + " 被赋值到 field: " + field.getName());
        }
    }

    private static void handleExclusive(Map<?, ?> value, TemplateElement valueElement) {
        List<List<String>> exclusive = valueElement.getExclusive();
        exclusive.forEach(ex -> {
            List<String> matchedStrings = new ArrayList<>();
            ex.forEach(e -> {
                if (value.containsKey(e)) matchedStrings.add(e);
            });
            if (matchedStrings.size() > 1) throw new IllegalArgumentException(
                    valueElement.getName() + " 的参数存在冲突: " + matchedStrings + " 在: " + ex
            );
        });
    }

    private static void setField(Field field, Object obj, Map<?, ?> args, TemplateElement valueElement, Context context) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        if (field.get(obj) == null) {
            Object fieldValue = AutoWire.getNoArgsObject(context.getCorrelativeClass(valueElement.getName()));
            AutoWire.autoAssignArgs(fieldValue, context, valueElement, (Map<String, Object>) args.get(valueElement.getName()));
            field.set(obj, fieldValue);
        } else {
            throw new UnknownException(valueElement.getName() + " 已经赋值, 这是不应该的");
        }
    }

    private static void setDefault(Field field, Object obj, TemplateElement valueElement, Context context) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // 捕获异常
        Object fieldObject = AutoWire.getNoArgsObject(field.getType());
        AutoWire.autoAssignArgs(fieldObject, context, valueElement, new Yaml().load(valueElement.getDefaultValue()));
        field.set(obj, fieldObject);
    }
}
