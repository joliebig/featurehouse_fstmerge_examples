


package net.sf.freecol.client.gui.option;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sf.freecol.common.option.BooleanOption;
import net.sf.freecol.common.option.Option;




public final class BooleanOptionUI extends JPanel implements OptionUpdater, PropertyChangeListener {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(BooleanOptionUI.class.getName());


    private final BooleanOption option;
    private final JCheckBox checkBox;
    private boolean originalValue;
    

    
    public BooleanOptionUI(final BooleanOption option, boolean editable) {
        super(new FlowLayout(FlowLayout.LEFT));

        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();
        checkBox = new JCheckBox(name, option.getValue());
        checkBox.setEnabled(editable);
        checkBox.setToolTipText((description != null) ? description : name);
        
        option.addPropertyChangeListener(this);
        checkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (option.isPreviewEnabled()) {
                    boolean value = checkBox.isSelected();
                    if (option.getValue() != value) {
                        option.setValue(value);
                    }
                }
            }
        });
        
        add(checkBox);
        setBorder(null);
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
            boolean value = ((Boolean) event.getNewValue()).booleanValue();
            if (value != checkBox.isSelected()) {
                checkBox.setSelected(value);
                originalValue = value;
            }
        }
    }

    
    public void updateOption() {
        option.setValue(checkBox.isSelected());
    }

    
    public void reset() {
        checkBox.setSelected(option.getValue());
    }
    
    
    public void setValue(boolean b) {
        checkBox.setSelected(b);
    }
    
    
    public void setEnabled(boolean b) {
    	checkBox.setEnabled(b);
    }
}
