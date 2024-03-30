package org.morlinnn.autowire;

import org.morlinnn.reader.template.SelectTemplateElement;
import org.morlinnn.reader.template.TemplateElement;

public class AutoWireSelect {
    public static void initCheckSelect(TemplateElement valueElement, Object value) {
        // value 是否在 selection 中
        if (!((SelectTemplateElement) valueElement).isInSelection(value)) {
            throw new IllegalArgumentException(
                    "Select: " + valueElement.getName() +
                            " 在 selection 中: " + ((SelectTemplateElement) valueElement).getSelection() +
                            " 不含: " + value);
        }
    }
}
