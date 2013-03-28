

package net.sf.freecol.client.gui.option;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.freecol.common.option.IntegerOption;
import net.sf.freecol.common.option.Option;



public final class IntegerOptionUI extends JSpinner implements OptionUpdater, PropertyChangeListener {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(IntegerOptionUI.class.getName());


    private final IntegerOption option;
    private int originalValue;
    private JLabel label;


    
    public IntegerOptionUI(final IntegerOption option, boolean editable) {

        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();
        String text = (description != null) ? description : name;
        label = new JLabel(name, JLabel.LEFT);
        label.setToolTipText(text);

        int stepSize = Math.min((option.getMaximumValue() - option.getMinimumValue()) / 10, 1000);
        setModel(new SpinnerNumberModel(option.getValue(), option.getMinimumValue(),
                                        option.getMaximumValue(), Math.max(1, stepSize)));
        setToolTipText(text);
        
        setEnabled(editable);
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (option.isPreviewEnabled()) {
                    final int value = (Integer) getValue();
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
            final int value = (Integer) event.getNewValue();
            if (value != ((Integer) getValue()).intValue()) {
                setValue(event.getNewValue());
                originalValue = value;
            }
        }
    }
    
    
    public void updateOption() {
        option.setValue(((Integer) getValue()).intValue());
    }

    
    public void reset() {
        setValue(option.getValue());
    }
}
