package example;

import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ToString
public class Config {
    private double second;
    private Map<Character, Character> expMap;
    private List<Map<Character, Integer>> expIsoMap;
    private List<Integer> expList;
    private int expSelect;
    private ExpObject expObject;
    private Set<String> expSet;

    public Config() {}
}
