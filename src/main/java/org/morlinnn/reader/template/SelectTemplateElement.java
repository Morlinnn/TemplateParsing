package org.morlinnn.reader.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.morlinnn.content.ContextContent;
import org.morlinnn.enums.DataType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectTemplateElement extends TemplateElement {
    private DataType selectionType;
    private List<?> selection;

    @Override
    public boolean checkIntegrality(ContextContent context) {
        return super.checkIntegrality(context) && selectionType != null && selection != null && !selection.isEmpty();
    }

    public boolean isInSelection(Object select) {
        return selection.contains(select);
    }

    @Override
    public String toString() {
        return "SelectTemplateElement(" +
                "selectionType=" + selectionType +
                ", selection=" + selection +
                ", name='" + name + '\'' +
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
