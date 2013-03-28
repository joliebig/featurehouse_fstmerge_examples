


package net.sf.freecol.client.gui.panel;

import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.plaf.FreeColComboBoxRenderer;
import net.sf.freecol.common.model.EuropeanNationType;



public final class AdvantageCellEditor extends DefaultCellEditor {


    
    
    public AdvantageCellEditor() {
        super(new JComboBox(new Vector<EuropeanNationType>(FreeCol.getSpecification().getEuropeanNationTypes())));
        ((JComboBox) getComponent()).setRenderer(new AdvantageRenderer());
    }
    
    public Object getCellEditorValue() {
        return ((JComboBox) getComponent()).getSelectedItem();
    }

    private class AdvantageRenderer extends FreeColComboBoxRenderer {
        @Override
        public void setLabelValues(JLabel label, Object value) {
            label.setText((value == null) ? "" : Messages.message(value.toString() + ".name"));
        }
    }


}
