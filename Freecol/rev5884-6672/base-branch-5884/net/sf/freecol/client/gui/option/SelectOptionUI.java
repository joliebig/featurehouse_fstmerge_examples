


package net.sf.freecol.client.gui.option;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.SelectOption;




public final class SelectOptionUI extends JPanel implements OptionUpdater, PropertyChangeListener {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SelectOptionUI.class.getName());


    private final SelectOption option;
    private final JComboBox comboBox;
    private int originalValue;


    
    public SelectOptionUI(final SelectOption option, boolean editable) {
        super(new FlowLayout(FlowLayout.LEFT));

        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();
        JLabel label = new JLabel(name, JLabel.LEFT);
        label.setToolTipText((description != null) ? description : name);
        add(label);

        String[] strings = option.getItemValues().values().toArray(new String[0]);

        comboBox = new JComboBox(strings);
        comboBox.setSelectedIndex(option.getValue());
        add(comboBox);
        
        comboBox.setEnabled(editable);
        comboBox.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                if (option.isPreviewEnabled()) {
                    int value = comboBox.getSelectedIndex();
                    if (option.getValue() != value) {
                        option.setValue(value);
                    }
                }
            }
        });

        option.addPropertyChangeListener(this);
        setOpaque(false);
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
            if (value != comboBox.getSelectedIndex()) {
                comboBox.setSelectedIndex(value);
                originalValue = value;
            }
        }
    }
    
    
    public void updateOption() {
        option.setValue(comboBox.getSelectedIndex());
    }

    
    public void reset() {
        comboBox.setSelectedIndex(option.getValue());
    }
}
