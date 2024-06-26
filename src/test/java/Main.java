import example.Config;
import example.ExpObject;
import org.morlinnn.content.Context;
import org.morlinnn.reader.TemplateReader;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class Main extends TestData {
    public static void main(String[] args) {
        Context context = new Context();
        context.add(TemplateReader.read(t1, context), true);
        context.add(TemplateReader.read(t2, context), true);
        context.add(TemplateReader.read(t3, context), true);
        context.add(TemplateReader.read(t4, context), true);
        context.add(TemplateReader.read(t5, context), true);
        context.add(TemplateReader.read(t6, context), true);
        context.add(TemplateReader.read(t7, context), true);
        context.add(TemplateReader.read(t8, context), true);
        context.add(TemplateReader.read(t9, context), true);
        System.out.println(context);

        context.registerCorrelativeClass("Config", Config.class);
        context.registerCorrelativeClass("expObject", ExpObject.class);

        try {
            Object loadedYaml = new Yaml().load(yamlConfig);
            Object config = context.createObject(1, (Map<String, Object>) loadedYaml);
            System.out.println(loadedYaml);
            System.out.println(config);

            // dynamic example in set
            System.out.println("\ndynamic example");
            ((Config) config).getExpSet()
                    .forEach(e -> System.out.println(e.getClass()));
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
