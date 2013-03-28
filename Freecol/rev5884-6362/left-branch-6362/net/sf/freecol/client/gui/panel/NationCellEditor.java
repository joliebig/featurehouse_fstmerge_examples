

package net.sf.freecol.client.gui.panel;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

import net.sf.freecol.common.model.Nation;


public final class NationCellEditor extends DefaultCellEditor {

    
    public NationCellEditor(Nation[] nations) {
        super(new JComboBox(nations));
    }
    
    public Object getCellEditorValue() {
        return ((JComboBox) getComponent()).getSelectedItem();
    }
}
