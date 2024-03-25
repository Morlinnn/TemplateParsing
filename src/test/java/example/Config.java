package example;

import org.morlinnn.interfaces.Adapter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
public class Config implements Adapter {
    private long second;
    private Map<Character, Character> expMap;
    private List<Map<Character, Integer>> expIsoMap;
    private List<Integer> expList;
    private int expSelect;

    public Config() {}
}
