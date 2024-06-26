# 解析规范
## 多行书写
模板以隔行经行模板分割
```
template1: id(1)

template2: id(2)
```

## type

### 注意
如果 name 为 Java 保留字段, 则会作为值加载

保留字段:
int, short, long, float, double, String, boolean, byte, char

        例:
        test: type(Int) -> test: 10
        int: type(Int) -> 10

### 基本数据类型--DataType
#### Int
Integer.class
#### Short
**(默认识别为`Integer`)**
Integer.class -> Short.class
#### Long
**需要按照`String`来书写否则会被识别为int**
String.class -> Long.class
#### Float
**需要标注小数点(默认识别为`Double`)**
Double.class -> float.class
#### Double
Double.class
#### String
**如果有空格需要双引号, 如`"test 2"`**
String.class
#### Bool
Boolean.class
#### Byte
Integer.class -> Byte
#### Char
**注意表上引号以识别为`String`, 只能有一个字符**
String.class -> Character.class
#### Dynamic
类型不定的动态类型
Object.class

### 结构数据类型
##### List
注意 elements 的键设置是无效的

子项类型作为数组类型, 子项唯一。
子项名称没有实际意义, 它不会被加载, 可以不指定。
List 的 value 可以使用 Select 组成多选列表, value 部分可以替换为引用 (name, id) 指向其他模板

|          | expList                                          |
|----------|--------------------------------------------------|
| template | expList: type(List), elements(value: type(Int))  |
| in class | List<Integer> expList                            |
| instance | ArrayList.class                                  |
| tab      | id, default, required, elements, limit, constant |

```yaml
expList:
  - 10
  - 20
```

##### Map
注意 elements 的键设置是无效的

子项为 key 和 value, 用于指定 Map 的键值对类型。
两个子项唯一, 名称用于区分键值类型, key 名称设置是无效的。

|          | expMap                                                         |
|----------|----------------------------------------------------------------|
| template | expMap: type(Map), elements(key: type(Int); value: type(Char)) |
| in class | Map<Integer, Character> expMap                                 |
| instance | HashMap.class                                                  |
| tab      | id, default, required, elements, limit                         |

```yaml
expMap:
  10: c
  20: d
```

##### IsoMap
注意 elements 的 key 名称设置是无效的
Isolation Map, 隔离图, 用于存放多个同类但类容不同的 Map

|          | expIsoMap                                                          |
|----------|--------------------------------------------------------------------|
| template | expOMap: type(IsoMap), elements(key: type(Int), value: type(Char)) |
| in class | List<Map<Int, Char>> expIsoMap                                     |
| instance | ArrayList.class                                                    |
| tab      | id, default, required, elements, limit, constant                   |
```yaml
expIsoMap:
  - 10: a
    20: b
  - 80: e
```

##### Set
注意 elements 的键设置是无效的

|          | expSet                                                |
|----------|-------------------------------------------------------|
| template | expSet: type(Set), elements(value: type(Char))        |
| in class | Set<Character> expIsoMap                              |
| instance | HashSet.class                                         |
| tab      | id, default, required, elements, limit, *constant(默认) |

```yaml
expSet: !!set
  - c
  - d
```

#### 特殊
##### Object
以实现类来存储数据, 而不是 List, Map, Set...

|          | expObj                                                                    |
|----------|---------------------------------------------------------------------------|
| template | expObj: type(Object), elements(test1: type(Int); test2: type(OtherClass)) |
| in class | YourObject expObj                                                         |
| instance | YourObject.class                                                          |
| tab      | id, default, required, elements, exclusive                                |

```yaml
expObj:
  test1: 10
  test2: ...
```

##### Select
仅选择

|          | expSelect                                             |
|----------|-------------------------------------------------------|
| template | expSelect: type(Select), selection(Int: 1,23,66,9,-1) |
| in class | int expSelect                                         |
| instance | SelectionType                                         |
| tab      | id, default, required, selection, *constant(默认)       |


##### 支持 Object 的 List, Map, IsoMap
```java
private List<Obj> TestA;
```

```yaml
# Object -> List
Obj:
  name: Steve
TestA:
  - Obj:
      name: Steve
  - Obj...
# Object -> Map
TestB:
  randomA: Obj...
  randomB: Obj...
# Object -> IsoMap
TestC:
  - randomA: Obj...
  - randomB: Obj...
```

## 模板规范
### 结构
以树形结构为数据层次关系, 以元素表为引用

例
```
Elements:
    Config: id(1),type(Map),elements(int: type(Int),required; map; id(2))
    map: type(Map),elements(test: type(Char); text: type(Char))
    omap: id(2),type(OMap),elements(test: type(Char); id(1))
```
```
Config:
    int: 1    <- required
    map:
      test: t
      text: t
    omap:
      - test: s
      - test: x
      - Config:
          int: 2    <- required
```
### 标记
#### 标记分隔符
以`,`分隔属性, 以换行`\n`作为属性结尾, 以`;`作为元素分隔
#### 类型
`type(int)`
#### id
用于同名元素区分

id 应该是唯一的, 但名字不唯一

id应当是int类型

id默认为-1, 所以-1不应当被用于id

`id(123)`
#### default
默认值
内容为 yaml String
**计划实现** 自定义 Object 的默认值

`default(10)`
#### required
必须

常用于必要子项的标记

### 关系标记
#### elements
子项

适用于 `list` `map` `omap` `set` `choice`

`elements(c1;c2)`
`elements(id(12);c2;id(10))`
#### limit
限制子项数量

`limit(1)` 最小1个元素
`limit(1,3)` 最小1个, 最大3个
#### exclusive
独占, 只能写入其中一项
这个配置可以重复写

`exclusive(x1;x2;id(3)), exclusive(x2;x5)`
#### constant
限制 List 的重复选项, 不允许重复

## yaml 规范