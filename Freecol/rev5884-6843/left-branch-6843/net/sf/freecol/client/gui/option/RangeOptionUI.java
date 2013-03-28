

package net.sf.freecol.client.gui.option;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.RangeOption;


public final class RangeOptionUI extends JSlider implements OptionUpdater, PropertyChangeListener {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(RangeOptionUI.class.getName());

    private final RangeOption option;
    private int originalValue;


    
    public RangeOptionUI(final RangeOption option, boolean editable) {

        this.option = option;
        this.originalValue = option.getValue();

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), 
                                                   option.getName()));
        String name = option.getName();
        String description = option.getShortDescription();

        setModel(new DefaultBoundedRangeModel(option.getValueRank(), 0, 0, option.getItemValues().size() - 1));
        setOrientation(JSlider.HORIZONTAL);
        Hashtable<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
        int index = 0;
        for (String string : option.getItemValues().values()) {
            labels.put(index, new JLabel(string));
            index++;
        }

        setLabelTable(labels);
        setValue(option.getValueRank());
        setPaintLabels(true);
        setMajorTickSpacing(1);
        setExtent(0);
        setPaintTicks(true);
        setSnapToTicks(true);
        setPreferredSize(new Dimension(500, 50));
        setToolTipText((description != null) ? description : name);

        setEnabled(editable);
        setOpaque(false);
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (option.isPreviewEnabled()) {
                    final int value = getValue();
                    if (option.getValue() != value) {
                        option.setValueRank(value);
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
            if (value != getValue()) {
                setValue(value);
                originalValue = value;
            }
        }
    }

    
    public void updateOption() {
        option.setValueRank(getValue());
    }

    
    public void reset() {
        setValue(option.getValueRank());
    }
}
