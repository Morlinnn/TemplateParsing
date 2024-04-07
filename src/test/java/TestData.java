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
                          |-expObject--...
               |-expSet--(String)
                       |-(String)
                       |-...
         */
    static String t1 = "Config: id(1),type(Object),elements(second: type(Double),required; expMap; id(3); id(4); id(5); id(6); id(7); id(8))";
    static String t2 = "expMap: type(Map),elements(key: type(Char); value: type(Char))";
    static String t3 = "expIsoMap: id(3),type(IsoMap),elements(key: type(Char); value: type(Int))";
    static String t4 = "expList: id(4),type(List),elements(value: type(Int))";
    static String t5 = "expSelect: id(5), type(Select), selection(Int: 1,2,3), constant";
    static String t6 = "expObject: id(6), type(Object), elements(name: type(String), required; expObject)";
    static String t7 = "expSet: id(7), type(Set), elements(value: type(Dynamic))";
    static String t8 = "expObjectList: id(8), type(List), elements(id(6))";
    static String yamlConfig = "Config:\n" +
            "                        second: 1.0\n" +
            "                        expMap:\n" +
            "                            test: t\n" +
            "                        expIsoMap:\n" +
            "                            - test: e\n" +
            "                            - test: s\n" +
            "                        expList:\n" +
            "                            - 1\n" +
            "                            - 2\n" +
            "                        expSelect: 1\n" +
            "                        expObject:\n" +
            "                            name: nameless\n" +
            "                            expObject:\n" +
            "                                name: 123\n" +
            "                                expObject:\n" +
            "                                    name: end\n" +
            "                        expSet: !!set\n" +
            "                            ? set2\n" +
            "                            ? 123\n" +
            "                        expObjectList:\n" +
            "                            - expObject:\n" +
            "                                name: aaa\n" +
            "                            - expObject:\n" +
            "                                name: bbb";
}
