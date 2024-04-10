package org.morlinnn.reader.template;

import org.morlinnn.enums.DataType;
import org.morlinnn.reader.TemplateReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateElementBuilder {
    private final TemplateElement element;

    public TemplateElementBuilder(String name, boolean isSelect) {
        element = isSelect ? new SelectTemplateElement() : new TemplateElement();
        element.setName(name);
    }

    public void addEntry(Map.Entry<String, String> entry) {
        switch (entry.getKey()) {
            case "id": {
                try {
                    String value = TemplateReader.removeUselessSpace(entry.getValue());
                    if (value != null) {
                        element.setId(Integer.parseInt(value));
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("id 应为 Integer 类型, 而不是 " + entry.getValue() + ", name: " + element.getName());
                }
                break;
            }
            case "type": {
                try {
                    element.setType(DataType.valueOf(TemplateReader.removeUselessSpace(entry.getValue())));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("type 的书写错误 " + entry.getValue() + ", name: " + element.getName());
                }
                break;
            }
            case "elements": {
                element.setElements(readElements(TemplateReader.removeUselessSpace(entry.getValue())));
                break;
            }
            case "limit": {
                try {
                    element.setLimit(readLimit(TemplateReader.removeUselessSpace(entry.getValue())));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("limit 应为 Integer 类型, 而不是 " + entry.getValue() + ", name: " + element.getName());
                }
                break;
            }
            case "exclusive": {
                setExclusive(TemplateReader.removeUselessSpace(entry.getValue()));
                break;
            }
            case "default": {
                element.setDefaultValue(TemplateReader.removeUselessSpace(entry.getValue()));
                break;
            }
            case "selection": {
                if (!(element instanceof SelectTemplateElement))
                    throw new IllegalArgumentException("非 Select 模板写入了 selection, name: " + element.getName());
                String[] dividedSelect = TemplateReader.divide(entry.getValue());
                if (dividedSelect == null) throw new IllegalArgumentException("Select 未指定有效的 selection, name: " + element.getName());
                if (dividedSelect[0] == null || dividedSelect[1] == null) throw new IllegalArgumentException("Select 应指定类型, name: " + element.getName());

                try {
                    ((SelectTemplateElement) element).setSelectionType(DataType.valueOf(TemplateReader.removeUselessSpace(dividedSelect[0])));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("type 的书写错误 " + TemplateReader.removeUselessSpace(dividedSelect[0]) + ", name: " + element.getName());
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
                    throw new IllegalArgumentException("selection 书写错误 " + dividedSelect[1] + ", name: " + element.getName());
                }
                break;
            }
            default: System.out.println("未匹配的键值对: " + entry + ", name: " + element.getName());
        }
    }

    public void setRequired() {
        element.setRequired(true);
    }

    public void setConstant() {
        if (element.type != DataType.Map && element.type != DataType.Object) element.setConstant(true);
    }

    public TemplateElement build() {
//        System.out.println(element);
        if (!element.checkIntegrality()) return null;
        return element;
    }

    private void setExclusive(String str) {
        if (element.getExclusive() == null) element.setExclusive(new ArrayList<>());
        element.getExclusive().add(TemplateReader.divideFiled(str, ';'));
    }

    private List<String> readElements(String str) {
        return new ArrayList<>(TemplateReader.divideFiled(str, ';'));
    }

    private List<Integer> readLimit(String str) {
        List<Integer> res = TemplateReader.divideFiled(str, ',').stream().map(Integer::parseInt).collect(Collectors.toList());
        if (res.size() > 2) throw new IllegalArgumentException("limit 应最多包含 2 个元素, 而不是 " + str + ", name: " +element.getName());
        return res;
    }

    private List<?> readSelection(String str, DataType type) {
        List<String> selectionStrings = TemplateReader.divideFiled(str, ',');
        switch (type) {
            case String: {
                return selectionStrings;
            }
            case Int: {
                return selectionStrings.stream().map(Integer::parseInt).collect(Collectors.toList());
            }
            case Short: {
                return selectionStrings.stream().map(Short::parseShort).collect(Collectors.toList());
            }
            case Long: {
                return selectionStrings.stream().map(Long::parseLong).collect(Collectors.toList());
            }
            case Char: {
                return selectionStrings.stream().map(e -> e.charAt(0)).collect(Collectors.toList());
            }
            case Byte: {
                return selectionStrings.stream().map(Byte::parseByte).collect(Collectors.toList());
            }
            case Bool: {
                return selectionStrings.stream().map(Boolean::parseBoolean).collect(Collectors.toList());
            }
            case Double: {
                return selectionStrings.stream().map(Double::parseDouble).collect(Collectors.toList());
            }
            case Float: {
                return selectionStrings.stream().map(Float::parseFloat).collect(Collectors.toList());
            }
            default: {
                return null;
            }
        }
    }
}
