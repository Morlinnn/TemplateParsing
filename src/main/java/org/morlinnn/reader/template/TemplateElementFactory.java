package org.morlinnn.reader.template;

import org.morlinnn.enums.DataType;
import org.morlinnn.reader.TemplateReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemplateElementFactory {
    private final TemplateElement element;

    public TemplateElementFactory(String name, boolean isSelect) {
        element = isSelect ? new SelectTemplateElement() : new TemplateElement();
        element.setName(name);
    }

    public void addEntry(Map.Entry<String, String> entry) {
        switch (entry.getKey()) {
            case "id" -> {
                try {
                    String value = TemplateReader.removeUselessSpace(entry.getValue());
                    if (value != null) {
                        element.setId(Integer.parseInt(value));
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("id 应为 Integer 类型, 而不是 " + entry.getValue());
                }
            }
            case "type" -> {
                try {
                    element.setType(DataType.valueOf(TemplateReader.removeUselessSpace(entry.getValue())));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("type 的书写错误 " + entry.getValue());
                }
            }
            case "parents" -> element.setParents(readStrings(TemplateReader.removeUselessSpace(entry.getValue())));
            case "children" -> element.setChildren(readChildren(TemplateReader.removeUselessSpace(entry.getValue())));
            case "limit" -> {
                try {
                    element.setLimit(readLimit(TemplateReader.removeUselessSpace(entry.getValue())));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("limit 应为 Integer 类型, 而不是 " + entry.getValue());
                }
            }
            case "exclusive" -> element.setExclusive(readStrings(TemplateReader.removeUselessSpace(entry.getValue())));
            case "default" -> element.setDefaultValue(TemplateReader.removeUselessSpace(entry.getValue()));
            case "selection" -> {
                if (!(element instanceof SelectTemplateElement))
                    throw new IllegalArgumentException("非 Select 模板写入了 selection");
                String[] dividedSelect = TemplateReader.divide(entry.getValue());
                if (dividedSelect == null) throw new IllegalArgumentException("Select 未指定有效的 selection");
                if (dividedSelect[0] == null) throw new IllegalArgumentException("Select 应指定类型");
                if (dividedSelect[1] == null) throw new IllegalArgumentException("Select 应指定选项");

                try {
                    ((SelectTemplateElement) element).setSelectionType(DataType.valueOf(TemplateReader.removeUselessSpace(dividedSelect[0])));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("type 的书写错误 " + TemplateReader.removeUselessSpace(dividedSelect[0]));
                }
                try {
                    ((SelectTemplateElement) element)
                            .setSelection(
                                    readSelection(
                                            dividedSelect[1],
                                            ((SelectTemplateElement) element).getSelectionType()
                                    )
                            );
                } catch (Exception e) {
                    throw new IllegalArgumentException("selection 书写错误 " + dividedSelect[1]);
                }
            }
            default -> System.out.println("未匹配的键值对: " + entry);
        }
    }

    public void setRequired() {
        element.setRequired(true);
    }

    public void setConstant() {
        if (element.type != DataType.Map && element.type != DataType.Object) element.setConstant(true);
    }

    public TemplateElement build() {
        if (!element.checkIntegrality()) return null;
        if (!element.constant && element.type == DataType.Set) element.constant = true;
        return element;
    }

    private List<String> readStrings(String str) {
        return new ArrayList<>(TemplateReader.divideFiled(str, ','));
    }

    private List<String> readChildren(String str) {
        return new ArrayList<>(TemplateReader.divideFiled(str, ';'));
    }

    private List<Integer> readLimit(String str) {
        List<Integer> res = TemplateReader.divideFiled(str, ',').stream().map(Integer::parseInt).toList();
        if (res.size() > 2) throw new IllegalArgumentException("limit 应最多包含 2 个元素, 而不是 " + str);
        return res;
    }

    private List<?> readSelection(String str, DataType type) {
        List<String> selectionStrings = TemplateReader.divideFiled(str, ',');
        switch (type) {
            case String -> {
                return selectionStrings;
            }
            case Int -> {
                return selectionStrings.stream().map(Integer::parseInt).toList();
            }
            case Short -> {
                return selectionStrings.stream().map(Short::parseShort).toList();
            }
            case Long -> {
                return selectionStrings.stream().map(Long::parseLong).toList();
            }
            case Char -> {
                return selectionStrings.stream().map(e -> e.charAt(0)).toList();
            }
            case Byte -> {
                return selectionStrings.stream().map(Byte::parseByte).toList();
            }
            case Bool -> {
                return selectionStrings.stream().map(Boolean::parseBoolean).toList();
            }
            case Double -> {
                return selectionStrings.stream().map(Double::parseDouble).toList();
            }
            case Float -> {
                return selectionStrings.stream().map(Float::parseFloat).toList();
            }
            default -> {
                return null;
            }
        }
    }
}
