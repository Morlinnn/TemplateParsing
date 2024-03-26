package org.morlinnn.reader.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.morlinnn.enums.DataType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateElement {
    /**
     * 如果模板 name 使用 java 的保留关键字, 则加载为纯元素(非键值对), 无 name
     */
    protected String name;
    protected DataType type;
    protected int id = -1;
    protected List<String> parents;
    protected List<String> children;
    protected boolean required = false;
    protected List<Integer> limit;
    protected List<String> exclusive;
    protected Object defaultValue;
    protected boolean constant = false;

    public boolean checkIntegrality() {
        return type != null;
    }

    public boolean isSelect() {
        return false;
    }

    @Override
    public String toString() {
        return "TemplateElement(" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", id=" + id +
                ", parents=" + parents +
                ", children=" + children +
                ", required=" + required +
                ", limit=" + limit +
                ", exclusive=" + exclusive +
                ", defaultValue=" + defaultValue +
                ", constant=" + constant +
                ')';
    }
}
