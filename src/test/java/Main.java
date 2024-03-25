import org.morlinnn.content.Context;
import example.Config;
import org.yaml.snakeyaml.Yaml;
import org.morlinnn.reader.TemplateReader;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        /*
         Config--seconds(int)(required)
               |-expMap--test(char)
                       |-text(char)
               |-expOMap--test(char)
                        |-test(char)
                        |-...
               |-expList--integer(int)
                        |-integer(int)
                        |-...
         */
        String i1 = "Config: id(1),type(Object),children(seconds: type(Long),required; expMap; id(3); id(5))";
        String i2 = "expMap: type(Map),parents(id(1)),children(key: type(Char); value: type(Char))";
        String i3 = "expIsoMap: id(3),type(IsoMap),children(key: type(Char); value: type(Int))";
        String i4 = "expList: id(4),type(List),children(value: type(Int))";
        String i5 = "expSelect: id(5), type(Select), selection(Int: 1,2,3), constant";
        String yaml = """
                Config:
                    second: 1000000000000000
                    expMap:
                        test: t
                    expIsoMap:
                        - test: e
                        - test: s
                    expList:
                        - integer: 1
                        - integer: 2
                    expSelect: 1
                   \s""";
        Context context = new Context();
        context.add(TemplateReader.parse(i1), true);
        context.add(TemplateReader.parse(i2), true);
        context.add(TemplateReader.parse(i3), true);
        context.add(TemplateReader.parse(i4), true);
        context.add(TemplateReader.parse(i5), true);
        System.out.println(context);

        context.registerAdapter("Config", Config.class);
        try {
            Object config = context.createObject(1, new Yaml().load(yaml));
            System.out.println(Optional.ofNullable(new Yaml().load(yaml)).get());
            System.out.println(config);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
