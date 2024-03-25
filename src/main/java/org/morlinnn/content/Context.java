package org.morlinnn.content;

import org.morlinnn.interfaces.Adapter;
import org.morlinnn.reader.template.TemplateElement;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Context extends ContextContent {
    protected final Map<String, Class<? extends Adapter>> adapterMap;

    public Context() {
        super();
        adapterMap = new HashMap<>();
    }

    /**
     * 准确适应类
     * @param name
     * @param adapter
     * @return
     */
    public Context registerAdapter(String name, Class<? extends Adapter> adapter) {
        adapterMap.put(name, adapter);
        return this;
    }

    public Class<? extends Adapter> getAdapter(String name) {
        return adapterMap.getOrDefault(name, null);
    }

    public Object createObject(String name, Map<String, Object> args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TemplateElement element = find(name);
        return createObjectPri(element, args);
    }

    public Object createObject(int id, Map<String, Object> args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TemplateElement element = find(id);
        return createObjectPri(element, args);
    }

    private Object createObjectPri(TemplateElement element, Map<String, Object> args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return AutoWire.buildObject(this, element, args);
    }

    @Override
    public String toString() {
        return "Context(" + "beanMap=" + adapterMap + ")\n" + super.toString();
    }
}
