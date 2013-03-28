

package net.sf.freecol.client.gui.option;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.RangeOption;


public final class RangeOptionUI extends JPanel implements OptionUpdater, PropertyChangeListener {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(RangeOptionUI.class.getName());

    private final RangeOption option;
    private final JSlider slider;
    private int originalValue;


    
    public RangeOptionUI(final RangeOption option, boolean editable) {

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), 
                                                   option.getName()));
        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();
        
        
        

        String[] strings = option.getItemValues().values().toArray(new String[0]);

        slider = new JSlider(JSlider.HORIZONTAL, 0, strings.length - 1, option.getValueRank());
        Hashtable<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
        for (int i = 0; i < strings.length; i++) {
            labels.put(new Integer(i), new JLabel(strings[i]));
        }

        slider.setLabelTable(labels);
        slider.setValue(option.getValueRank());
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(1);
        slider.setExtent(0);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setPreferredSize(new Dimension(500, 50));
        slider.setToolTipText((description != null) ? description : name);
        add(slider);

        slider.setEnabled(editable);
        slider.setOpaque(false);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (option.isPreviewEnabled()) {
                    final int value = slider.getValue();
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
            if (value != slider.getValue()) {
                slider.setValue(value);
                originalValue = value;
            }
        }
    }

    
    public void updateOption() {
        option.setValueRank(slider.getValue());
    }

    
    public void reset() {
        slider.setValue(option.getValueRank());
    }
}
