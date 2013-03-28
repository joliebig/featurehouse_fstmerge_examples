


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

import net.sf.freecol.common.option.LanguageOption;
import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.LanguageOption.Language;




public final class LanguageOptionUI extends JPanel implements OptionUpdater, PropertyChangeListener {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(LanguageOptionUI.class.getName());


    private final LanguageOption option;   
    private final JComboBox comboBox;
    private Language originalValue;

    
    public LanguageOptionUI(final LanguageOption option, boolean editable) {
        super(new FlowLayout(FlowLayout.LEFT));

        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();
        JLabel label = new JLabel(name, JLabel.LEFT);
        label.setToolTipText((description != null) ? description : name);
        add(label);

        Language[] languages = option.getOptions();

        comboBox = new JComboBox(languages);
        reset();
        add(comboBox);
        
        comboBox.setEnabled(editable);
        comboBox.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                if (option.isPreviewEnabled()) {
                    Language value = (Language) comboBox.getSelectedItem();
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
            final Language value = (Language) event.getNewValue();
            if (value != comboBox.getSelectedItem()) {
                comboBox.setSelectedItem(value);
                originalValue = value;
            }
        }
    }
    
    
    public void updateOption() {
        option.setValue((Language) comboBox.getSelectedItem());
    }

    
    public void reset() {
        comboBox.setSelectedItem(option.getValue());
    }
}
