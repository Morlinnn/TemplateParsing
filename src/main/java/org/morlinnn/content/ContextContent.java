package org.morlinnn.content;

import lombok.ToString;
import org.morlinnn.reader.TemplateReader;
import org.morlinnn.reader.template.SelectTemplateElement;
import org.morlinnn.reader.template.TemplateElement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@ToString
public class ContextContent {
    protected final List<TemplateElement> templateElementList;

    public ContextContent() {
        templateElementList = new ArrayList<>();
    }

    public void add(TemplateElement element, boolean recover) {
        if (element == null) return;
        for (TemplateElement e : templateElementList) {
            if (e.getId() == element.getId()) {
                if (recover) {
                    templateElementList.remove(e);
                    templateElementList.add(element);
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
        for (TemplateElement element : templateElementList) {
            if (element.getId() == id) return element;
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
        for (TemplateElement element : templateElementList) {
            if (element.getName().equals(name)) return element;
        }
        return null;
    }

    public TemplateElement findInTE(String name) {
        AtomicReference<TemplateElement> result = new AtomicReference<>(null);
        for (TemplateElement element : templateElementList) {
            if (element instanceof SelectTemplateElement) continue;
            if (element.getChildren() == null || element.getChildren().isEmpty()) continue;

            List<String> children = element.getChildren();
            children.stream().forEach(str -> {
                if (str.startsWith(name)) {
                    result.set(readField(str));
                }
            });
            if (result.get() != null) return result.get();
        }
        return result.get();
    }

    /**
     * 查询 children 字段的对应内容
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
                            ).getValue())).toList();
        } else if (!stringField.contains(":")) {
            // 写的是名
            // test
            res = templateElementList.stream().filter(e ->
                    e.getName().equals(TemplateReader.removeUselessSpace(stringField))
            ).toList();
        } else {
            // 写的是模板基本数据模板或完整模板
            // 但在 children 中写入完整模板是不推荐的, 因为此处的模板不可复用
            // test: type(Char), required
            return TemplateReader.parse(stringField);
        }
        return res.isEmpty() ? null : res.get(0);
    }
}
