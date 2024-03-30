## TemplateParsing
使用模板和 yaml 配置文件自动装配为实例类

### 示例
- Class

| className | Test1       | Test2       | Test3       |
|-----------|-------------|-------------|-------------|
| fields    | int num     | Test3 test3 | String name |
|           | Test2 test2 |             |             |

- Template

```
test: id(1), type(Object), elements(num: type(Int); id(2))

test2: id(2), type(Object), elements(test3)

test3: id(3), type(Object), elements(name: type(String))
```

- yaml

```yaml
test1:
  num: 10
  test2:
    test3:
      name: Steve
```

- build Object

```
class Test1 {
|   int num; -> 10
|   Test2 test2; -> class Test2 {
|                   |   Test3 test3; -> class Test3{
|                   |                   |   String name; -> Steve
|                   |                   }
|                   }
}
```

### Wiki / 规范
介绍了模板各类参数的使用方法

[Standard](standard.md)

### Example
参考 [Main](src/test/java/Main.java) (不使用[ParsingOperator](src/main/java/org/morlinnn/ParsingOperator.java), 而是使用手动创建)

### 使用方法

```java
import org.morlinnn.ParsingOperator;

import java.io.File;

class Main {
    public static void main(String[] args) {
        ParsingOperator parsingOperator = new ParsingOperator();
        // 添加模板文件
        parsingOperator.loadTemplateFile(new File(/* Your Template File */));
        // 单独添加模板
        parsingOperator.addTemplateString(/* Template String */);
        // 注册对应类类
        parsingOperator.registerCorrelativeClass(/* yaml 上的 key 名称, 实现类 */);
        // 组装类
        parsingOperator.createObject();
    }
}
```

### License
The MIT License (MIT)