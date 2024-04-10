package org.morlinnn.reader.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.morlinnn.content.ContextContent;
import org.morlinnn.enums.DataType;
import org.morlinnn.reader.TemplateReader;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    protected List<String> elements;
    protected boolean required = false;
    protected List<Integer> limit;
    protected List<List<String>> exclusive;
    protected String defaultValue;
    protected boolean constant = false;

    public boolean checkIntegrality(ContextContent context) {
        // Set
        if (type == DataType.Set && !constant) constant = true;

        // 检查 List, Set, Map 的 elements
        if (type == DataType.List || type == DataType.Set || type == DataType.Map) {
            // elements 有且必须设置
            if (elements == null || elements.isEmpty()) {
                throw new IllegalArgumentException(type.name() + " 必须设置 elements, name: " + name);
            }
            // List, Set
            if ((type == DataType.List || type == DataType.Set) && elements.size() != 1) {
                throw new IllegalArgumentException(type.name() + " 的 elements 只能设置一项, name: " + name);
            }
            // Map
            if (type == DataType.Map && elements.size() != 2) {
                throw new IllegalArgumentException("Map 的 elements 只能设置两项, name: " + name);
            }
        }

        // exclusive
        // exclusive 元素必须存在于 elements 中
        if (exclusive != null && !exclusive.isEmpty()) {
            for (int i = 0; i < exclusive.size(); i++) {
                A: for (int j = 0; j < exclusive.get(i).size(); j++) {
                    if (elements.contains(exclusive.get(i).get(j))) continue;
                    for (String element : elements) {
                        TemplateElement e = context.readField(element);
                        if (e.getName().equals(exclusive.get(i).get(j))) continue A;
                    }
                    throw new IllegalArgumentException("exclusive: " + exclusive.get(i).get(j) + " 不存在于 name: " + name + " 的 elements 中");
                }
            }
        }

        // limit
        if (limit != null && (limit.isEmpty() || limit.size() > 2)) {
            throw new IllegalArgumentException("已设置的 limit 元素数量应为 1 或 2 项, name: " + name);
        }

        return type != null;
    }

    public boolean containsInElements(ContextContent context, String name) {
        // Select 不使用 elements
        if (this instanceof SelectTemplateElement) return true;
        if (elements == null || elements.isEmpty()) return false;

        AtomicBoolean result = new AtomicBoolean(false);
        elements.forEach(child -> {
            if (result.get()) return;

            String nullableName = TemplateReader.parseUnknownToName(child, context);
            if (nullableName == null) return;
            if (nullableName.equals(name)) result.set(true);
        });
        return result.get();
    }

    @Override
    public String toString() {
        return "TemplateElement(" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", id=" + id +
                ", elements=" + elements +
                ", required=" + required +
                ", limit=" + limit +
                ", exclusive=" + exclusive +
                ", defaultValue=" + defaultValue +
                ", constant=" + constant +
                ')';
    }
}
