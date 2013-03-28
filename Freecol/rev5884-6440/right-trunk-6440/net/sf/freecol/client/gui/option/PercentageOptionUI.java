

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
import net.sf.freecol.common.option.PercentageOption;
import net.sf.freecol.common.option.RangeOption;


public final class PercentageOptionUI extends JPanel implements OptionUpdater, PropertyChangeListener {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(PercentageOptionUI.class.getName());

    private final PercentageOption option;

    private final JSlider slider;
    private int originalValue;


    
    public PercentageOptionUI(final PercentageOption option, boolean editable) {

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), 
                                                   option.getName()));
        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();

        slider = new JSlider(JSlider.HORIZONTAL, 0, 100, option.getValue());
        Hashtable<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
        labels.put(new Integer(0), new JLabel("0 %"));
        labels.put(new Integer(25), new JLabel("25 %"));
        labels.put(new Integer(50), new JLabel("50 %"));
        labels.put(new Integer(75), new JLabel("75 %"));
        labels.put(new Integer(100), new JLabel("100 %"));
        slider.setLabelTable(labels);
        slider.setValue(option.getValue());
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(5);
        slider.setExtent(0);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(false);
        slider.setPreferredSize(new Dimension(500, 50));
        slider.setToolTipText((description != null) ? description : name);
        add(slider);

        slider.setEnabled(editable);
        slider.setOpaque(false);

        slider.addChangeListener(new ChangeListener () {
            public void stateChanged(ChangeEvent e) {
                if (option.isPreviewEnabled()) {
                    if (option.getValue() != slider.getValue()) {
                        option.setValue(slider.getValue());
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
        option.setValue(slider.getValue());
    }

    
    public void reset() {
        slider.setValue(option.getValue());
    }
}
