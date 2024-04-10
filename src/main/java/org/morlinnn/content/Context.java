package org.morlinnn.content;

import org.morlinnn.interfaces.Adapter;
import org.morlinnn.autowire.AutoWire;
import org.morlinnn.reader.template.TemplateElement;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Context extends ContextContent {
    /**
     * 处于安全考虑设计了本地检查, 对于使用自定义类的反射构建只能使用注册的类
    */
    private final Map<String, Class<? extends Adapter>> correlativeClassMap;

    public Context() {
        super();
        correlativeClassMap = new HashMap<>();
    }

    /**
     * 注册映射类
     * @param fieldName 类的名称, 这与 yaml 配置中的键的名称对应
     * @param clazz
     * @return
     */
    public Context registerCorrelativeClass(String fieldName, Class<? extends Adapter> clazz) {
        correlativeClassMap.put(fieldName, clazz);
        return this;
    }

    public Class<? extends Adapter> getCorrelativeClass(String fieldName) {
        return correlativeClassMap.get(fieldName);
    }

    public Object createObject(String name, Map<String, Object> args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TemplateElement element = find(name);
        return createObjectPri(correlativeClassMap.get(name), element, args);
    }

    public Object createObject(int id, Map<String, Object> args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TemplateElement element = find(id);
        return createObjectPri(correlativeClassMap.get(find(id).getName()), element, args);
    }

    private Object createObjectPri(Class<?> clazz, TemplateElement element, Map<String, Object> args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (!unchanged) parseIdToName();
        return AutoWire.buildObject(clazz, this, element, args);
    }

    @Override
    public String toString() {
        return "Context(correlativeClassMap=" + correlativeClassMap + ")\n" + super.toString();
    }
}
