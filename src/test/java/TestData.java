public class TestData {
    /*
         Config--seconds(int)(required)
               |-expMap--key(char), value(char)
               |-expOMap--key(char), value(int)
                        |-key(char), value(int)
                        |-...
               |-expList--(int)
                        |-(int)
                        |-...
               |-expSelect--(int)
               |-expObject--name(String)
               |-expSet--(String)
                       |-(String)
                       |-...
         */
    static String t1 = "Config: id(1),type(Object),children(seconds: type(Double),required; expMap; id(3); id(5); id(6); id(7))";
    static String t2 = "expMap: type(Map),parents(id(1)),children(key: type(Char); value: type(Char))";
    static String t3 = "expIsoMap: id(3),type(IsoMap),children(key: type(Char); value: type(Int))";
    static String t4 = "expList: id(4),type(List),children(value: type(Int))";
    static String t5 = "expSelect: id(5), type(Select), selection(Int: 1,2,3), constant";
    static String t6 = "expObject: id(6), type(Object), children(name: type(String), required)";
    static String t7 = "expSet: id(7), type(Set), children(value: type(String))";
    static String yamlConfig = """
                    Config:
                        second: 1.0
                        expMap:
                            test: t
                        expIsoMap:
                            - test: e
                            - test: s
                        expList:
                            - integer: 1
                            - integer: 2
                        expSelect: 1
                        expObject:
                            name: nameless
                        expSet: !!set
                            ? set2
                            ? set1
                   \s""";
}
