package org.morlinnn.reader.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    public boolean checkIntegrality() {
        return super.checkIntegrality() && selectionType != null && selection != null && !selection.isEmpty();
    }

    @Override
    public boolean isSelect() {
        return true;
    }

    @Override
    public String toString() {
        return "SelectTemplateElement(" +
                "selectionType=" + selectionType +
                ", selection=" + selection +
                ", name='" + name + '\'' +
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
