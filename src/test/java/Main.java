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
        context.add(TemplateReader.read(t1), true);
        context.add(TemplateReader.read(t2), true);
        context.add(TemplateReader.read(t3), true);
        context.add(TemplateReader.read(t4), true);
        context.add(TemplateReader.read(t5), true);
        context.add(TemplateReader.read(t6), true);
        context.add(TemplateReader.read(t7), true);
        context.add(TemplateReader.read(t8), true);
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
