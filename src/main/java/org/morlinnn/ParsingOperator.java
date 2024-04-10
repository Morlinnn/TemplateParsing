package org.morlinnn;

import lombok.Getter;
import org.morlinnn.content.Context;
import org.morlinnn.interfaces.Adapter;
import org.morlinnn.reader.TemplateFileReader;
import org.morlinnn.reader.TemplateReader;
import org.morlinnn.reader.TemplateStreamReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Getter
public class ParsingOperator {
    private final Context context;

    public ParsingOperator() {
        context = new Context();
    }

    public void loadTemplateFile(File templateFile) throws FileNotFoundException {
        TemplateFileReader
                .readTemplateFile(templateFile)
                .forEach(t -> context.add(TemplateReader.read(t, context), true));
    }

    public void loadTemplateStream(InputStream is) throws IOException {
        TemplateStreamReader.readTemplateStream(is)
                .forEach(t -> context.add(TemplateReader.read(t, context), true));
    }

    public void addTemplateString(String templateStr) {
        context.add(TemplateReader.read(templateStr, context), true);
    }

    public ParsingOperator registerCorrelativeClass(String fieldName, Class<? extends Adapter> clazz) {
        context.registerCorrelativeClass(fieldName, clazz);
        return this;
    }

    public Object createObject(String name, Map<String, Object> args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return context.createObject(name, args);
    }

    public Object createObject(int id, Map<String, Object> args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return context.createObject(id, args);
    }
}
