

package net.sf.freecol.client.gui.option;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.SelectOption;



public final class SelectOptionUI extends JComboBox implements OptionUpdater, PropertyChangeListener {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SelectOptionUI.class.getName());

    private final SelectOption option;
    private int originalValue;
    private JLabel label;

    
    public SelectOptionUI(final SelectOption option, boolean editable) {

        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();
        String text = (description != null) ? description : name;
        label = new JLabel(name, JLabel.LEFT);
        label.setToolTipText(text);

        String[] strings = option.getItemValues().values().toArray(new String[0]);

        setModel(new DefaultComboBoxModel(strings));
        setSelectedIndex(option.getValue());
        
        setEnabled(editable);
        addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                if (option.isPreviewEnabled()) {
                    int value = getSelectedIndex();
                    if (option.getValue() != value) {
                        option.setValue(value);
                    }
                }
            }
        });

        option.addPropertyChangeListener(this);
        setOpaque(false);
    }

    
    public JLabel getLabel() {
        return label;
    }

    
    public void setLabel(final JLabel newLabel) {
        this.label = newLabel;
    }

    
    public void rollback() {
        option.setValue(originalValue);
    }
    
    
    public void unregister() {
        option.removePropertyChangeListener(this);    
    }
    
    
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("value")) {
            final int value = ((Integer) event.getNewValue()).intValue();
            if (value != getSelectedIndex()) {
                setSelectedIndex(value);
                originalValue = value;
            }
        }
    }
    
    
    public void updateOption() {
        option.setValue(getSelectedIndex());
    }

    
    public void reset() {
        setSelectedIndex(option.getValue());
    }
}
