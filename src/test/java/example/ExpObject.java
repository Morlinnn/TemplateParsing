package example;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.morlinnn.interfaces.Adapter;

@Setter
@Getter
@ToString
public class ExpObject implements Adapter {
    private String name;
    private ExpObject expObject;

    @Override
    public boolean equals(Adapter adapter) {
        if (!(adapter instanceof ExpObject)) return false;
        return name.equals(((ExpObject) adapter).getName());
    }
}
