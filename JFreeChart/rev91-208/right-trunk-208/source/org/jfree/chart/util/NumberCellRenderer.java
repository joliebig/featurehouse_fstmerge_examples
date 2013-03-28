

package org.jfree.chart.util;

import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;


public class NumberCellRenderer extends DefaultTableCellRenderer {

    
    public NumberCellRenderer() {
        super();
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    
    public Component getTableCellRendererComponent(JTable table, 
            Object value, boolean isSelected, 
            boolean hasFocus, int row, int column) {

        setFont(null);
        NumberFormat nf = NumberFormat.getNumberInstance();
        if (value != null) {
            setText(nf.format(value));
        }
        else {
            setText("");
        }
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        }
        else {
            setBackground(null);
        }
        return this;
    }

}
