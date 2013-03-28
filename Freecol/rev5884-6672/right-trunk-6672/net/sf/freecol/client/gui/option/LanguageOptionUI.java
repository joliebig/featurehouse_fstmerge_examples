

package net.sf.freecol.client.gui.option;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.freecol.common.option.LanguageOption;
import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.LanguageOption.Language;



public final class LanguageOptionUI extends JComboBox implements OptionUpdater, PropertyChangeListener {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(LanguageOptionUI.class.getName());

    private final LanguageOption option;   
    private Language originalValue;
    private JLabel label;

    
    public LanguageOptionUI(final LanguageOption option, boolean editable) {

        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();
        label = new JLabel(name, JLabel.LEFT);
        label.setToolTipText((description != null) ? description : name);

        Language[] languages = option.getOptions();

        setModel(new DefaultComboBoxModel(languages));
        reset();
        
        setEnabled(editable);
        addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                if (option.isPreviewEnabled()) {
                    Language value = (Language) getSelectedItem();
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
            final Language value = (Language) event.getNewValue();
            if (value != getSelectedItem()) {
                setSelectedItem(value);
                originalValue = value;
            }
        }
    }
    
    
    public void updateOption() {
        option.setValue((Language) getSelectedItem());
    }

    
    public void reset() {
        setSelectedItem(option.getValue());
    }
}
