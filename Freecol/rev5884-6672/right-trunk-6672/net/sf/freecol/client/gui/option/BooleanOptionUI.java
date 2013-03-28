

package net.sf.freecol.client.gui.option;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JCheckBox;

import net.sf.freecol.common.option.BooleanOption;
import net.sf.freecol.common.option.Option;



public final class BooleanOptionUI extends JCheckBox implements OptionUpdater, PropertyChangeListener {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(BooleanOptionUI.class.getName());

    private final BooleanOption option;
    private boolean originalValue;
    

    
    public BooleanOptionUI(final BooleanOption option, boolean editable) {

        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();
        setText(name);
        setSelected(option.getValue());
        setEnabled(editable);
        setToolTipText((description != null) ? description : name);
        
        option.addPropertyChangeListener(this);
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (option.isPreviewEnabled()) {
                    boolean value = isSelected();
                    if (option.getValue() != value) {
                        option.setValue(value);
                    }
                }
            }
        });
        
    }

    
    
    public void rollback() {
        option.setValue(originalValue);
    }
    
    
    public void unregister() {
        option.removePropertyChangeListener(this);    
    }
    
    
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("value")) {
            boolean value = ((Boolean) event.getNewValue()).booleanValue();
            if (value != isSelected()) {
                setSelected(value);
                originalValue = value;
            }
        }
    }

    
    public void updateOption() {
        option.setValue(isSelected());
    }

    
    public void reset() {
        setSelected(option.getValue());
    }
    
    
    public void setValue(boolean b) {
        setSelected(b);
    }
    
}
