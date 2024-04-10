package org.morlinnn.content;

import lombok.ToString;
import org.morlinnn.reader.TemplateReader;
import org.morlinnn.reader.template.SelectTemplateElement;
import org.morlinnn.reader.template.TemplateElement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@ToString
public class ContextContent {
    protected final List<TemplateElement> templateElementList;
    protected boolean unchanged = false;

    public ContextContent() {
        templateElementList = new ArrayList<>();
    }

    public void add(TemplateElement element, boolean recover) {
        if (element == null) return;
        for (int i = 0; i < templateElementList.size(); i++) {
            TemplateElement e = templateElementList.get(i);
            if (e.getId() == element.getId() && e.getId() != -1) {
                if (recover) {
                    templateElementList.remove(e);
                    templateElementList.add(element);

                    unchanged = false;
                } else {
                    return;
                }
            }
        }
        templateElementList.add(element);
    }

    /**
     * 按 id 查询模板元素,
     * 基本数据类型不应该被复用
     * @param id
     * @return
     */
    public TemplateElement find(int id) {
        for (int i = 0; i < templateElementList.size(); i++) {
            TemplateElement e = templateElementList.get(i);
            if (e.getId() == id) return e;
        }
        return null;
    }

    /**
     * 按名称查找模板元素,
     * 基本数据类型不应该被复用
     * @param name
     * @return
     */
    public TemplateElement find(String name) {
        for (int i = 0; i < templateElementList.size(); i++) {
            TemplateElement e = templateElementList.get(i);
            if (e.getName().equals(name)) return e;
        }
        return null;
    }

    /**
     * 从加入的模板中
     * @param name
     * @return
     */
    public TemplateElement findInElement(String name) {
        AtomicReference<TemplateElement> result = new AtomicReference<>(null);
        for (int i = 0; i < templateElementList.size(); i++) {
            TemplateElement element = templateElementList.get(i);
            if (element instanceof SelectTemplateElement) continue;
            if (element.getElements() == null || element.getElements().isEmpty()) continue;

            List<String> elements = element.getElements();
            elements.forEach(str -> {
                if (result.get() == null && str.equals(name)) {
                    result.set(readField(str));
                }
                if (result.get() == null && str.contains(":") && TemplateReader.divide(str)[0].equals(name)) {
                    result.set(readField(str));
                }
            });
            if (result.get() != null) return result.get();
        }
        return result.get();
    }

    /**
     * 查询 elements 字段的对应内容
     * @param stringField
     * @return
     */
    public TemplateElement readField(String stringField) {
        List<TemplateElement> res;
        if (stringField.contains("id(")) {
            // 写的是 id(?)
            // id(2)
            res = templateElementList.stream().filter(e ->
                    e.getId() == Integer.parseInt(
                            TemplateReader.readEntry(
                                    TemplateReader.removeUselessSpace(stringField)
                            ).getValue())).collect(Collectors.toList());
        } else if (!stringField.contains(":")) {
            // 写的是名
            // test
            res = templateElementList.stream().filter(e ->
                    e.getName().equals(TemplateReader.removeUselessSpace(stringField))
            ).collect(Collectors.toList());
        } else {
            // 写的是模板基本数据模板或完整模板
            // 但在 elements 中写入完整模板是不推荐的, 因为此处的模板不可复用
            // test: type(Char), required
            return TemplateReader.read(stringField, this);
        }
        return res.isEmpty() ? null : res.get(0);
    }

    protected void parseIdToName() {
        unchanged = true;
        parseExclusiveItemToName();
        parseElementItemToName();
    }

    private void parseElementItemToName() {
        templateElementList.forEach(templateElement -> {
            if (templateElement.getElements() == null || templateElement.getElements().isEmpty()) return;

            for (int i = 0; i < templateElement.getElements().size(); i++) {
                String element = templateElement.getElements().get(i);
                if (element.startsWith("id(")) {
                    templateElement.getElements().set(i, TemplateReader.parseUnknownToName(element, this));
                }
            }
        });
    }

    private void parseExclusiveItemToName() {
        templateElementList.forEach(templateElement -> {
            if (templateElement.getExclusive() == null || templateElement.getExclusive().isEmpty()) return;

            for (int i = 0; i < templateElement.getExclusive().size(); i++) {
                List<String> exclusiveItem = templateElement.getExclusive().get(i);
                for (int j = 0; j < exclusiveItem.size(); j++) {
                    String exclusiveItemItem = exclusiveItem.get(j);
                    if (exclusiveItemItem.startsWith("id(") || exclusiveItemItem.contains(":")) {
                        exclusiveItem.set(j, TemplateReader.parseUnknownToName(exclusiveItemItem, this));
                    }
                }
            }
        });
    }
}
