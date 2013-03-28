


package net.sf.freecol.client.gui.option;

import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.freecol.common.option.IntegerOption;
import net.sf.freecol.common.option.Option;




public final class IntegerOptionUI extends JPanel implements OptionUpdater, PropertyChangeListener {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(IntegerOptionUI.class.getName());


    private final IntegerOption option;
    private final JSpinner spinner;
    private int originalValue;


    
    public IntegerOptionUI(final IntegerOption option, boolean editable) {
        super(new FlowLayout(FlowLayout.LEFT));

        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();
        JLabel label = new JLabel(name, JLabel.LEFT);
        label.setToolTipText((description != null) ? description : name);
        add(label);

        int stepSize = Math.min((option.getMaximumValue() - option.getMinimumValue()) / 10, 1000);
        spinner = new JSpinner(new SpinnerNumberModel(option.getValue(), option.getMinimumValue(),
                option.getMaximumValue(), Math.max(1, stepSize)));
        spinner.setToolTipText(option.getShortDescription());
        add(spinner);
        
        spinner.setEnabled(editable);
        spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (option.isPreviewEnabled()) {
                    final int value = (Integer) spinner.getValue();
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
            final int value = (Integer) event.getNewValue();
            if (value != ((Integer) spinner.getValue()).intValue()) {
                spinner.setValue(event.getNewValue());
                originalValue = value;
            }
        }
    }
    
    
    public void updateOption() {
        option.setValue(((Integer) spinner.getValue()).intValue());
    }

    
    public void reset() {
        spinner.setValue(option.getValue());
    }
}
