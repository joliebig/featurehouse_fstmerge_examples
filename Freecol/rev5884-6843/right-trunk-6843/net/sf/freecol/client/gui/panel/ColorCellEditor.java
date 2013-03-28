


package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;

import net.miginfocom.swing.MigLayout;



public final class ColorCellEditor extends AbstractCellEditor implements TableCellEditor,
        ActionListener {


    private static final Logger logger = Logger.getLogger(ColorCellEditor.class.getName());

    private static final String EDIT = "EDIT",
                                OK = "OK",
                                CANCEL = "CANCEL";

    private final JButton              colorEditButton;
    private final JColorChooser        colorChooser;
    private final ColorChooserPanel    colorChooserPanel;
    private final Canvas               canvas;

    private Color currentColor;


    
    private final class ColorChooserPanel extends FreeColPanel {

        
        public ColorChooserPanel(ActionListener l) {
            super(canvas);

            JButton okButton = new JButton( Messages.message("ok") );
            JButton cancelButton = new JButton( Messages.message("cancel") );

            setLayout(new MigLayout("", "", ""));

            add(colorChooser);
            add(okButton, "newline 20, split 2, tag ok");
            add(cancelButton, "tag cancel");

            okButton.setActionCommand(OK);
            cancelButton.setActionCommand(CANCEL);

            okButton.addActionListener(l);
            cancelButton.addActionListener(l);

            setOpaque(true);
            setSize(getPreferredSize());
        }
    }


    
    public ColorCellEditor(Canvas canvas) {
        this.canvas = canvas;

        colorEditButton = new JButton();
        colorEditButton.setActionCommand(EDIT);
        colorEditButton.addActionListener(this);
        colorEditButton.setBorderPainted(false);

        colorChooser = new JColorChooser();

        colorChooserPanel = new ColorChooserPanel(this);
        colorChooserPanel.setLocation(canvas.getWidth() / 2 - colorChooserPanel.getWidth()
                / 2, canvas.getHeight() / 2 - colorChooserPanel.getHeight() / 2);
    }


    
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(EDIT)) {
            if (!canvas.isAncestorOf(colorChooserPanel)) {
                colorChooser.setColor(currentColor);
    
                
                canvas.addAsFrame(colorChooserPanel);
                colorChooserPanel.requestFocus();
            }
        } else if (event.getActionCommand().equals(OK)) {
            currentColor = colorChooser.getColor();
            
            canvas.remove(colorChooserPanel);
            fireEditingStopped();
        } else if (event.getActionCommand().equals(CANCEL)) {
            
            canvas.remove(colorChooserPanel);
            fireEditingCanceled();
        } else {
            logger.warning("Invalid action command");
        }
    }


    
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean hasFocus, int row, int column) {

        currentColor = (Color)value;
        return colorEditButton;
    }


    
    public Object getCellEditorValue() {
        return currentColor;
    }
}
