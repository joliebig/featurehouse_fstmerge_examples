


package net.sf.freecol.client.gui.panel;

import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.EuropeanNationType;
import net.sf.freecol.common.model.NationType;


public final class AdvantageCellEditor extends DefaultCellEditor {


    
    
    public AdvantageCellEditor() {
        super(new JComboBox(new Vector<EuropeanNationType>(FreeCol.getSpecification().getEuropeanNationTypes())));
    }
    
    public Object getCellEditorValue() {
        return ((JComboBox) getComponent()).getSelectedItem();
    }
}
