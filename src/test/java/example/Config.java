package example;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.morlinnn.interfaces.Adapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
@ToString
public class Config implements Adapter {
    private double second;
    private Map<Character, Character> expMap;
    private List<Map<Character, Integer>> expIsoMap;
    private List<Integer> expList;
    private int expSelect;
    private ExpObject expObject;
    private Set<Object> expSet;
    private List<ExpObject> expObjectList;

    public Config() {}

    @Override
    public boolean equals(Adapter adapter) {
        if (!(adapter instanceof Config)) return false;
        return ((Config) adapter).getSecond() == second
                && expMap.equals(((Config) adapter).getExpMap())
                && expIsoMap.equals(((Config) adapter).getExpIsoMap())
                && expList.equals(((Config) adapter).getExpList())
                && expSet.equals(((Config) adapter).getExpSet())
                && expSelect == ((Config) adapter).getExpSelect()
                && expObject.equals(adapter);
    }
}
