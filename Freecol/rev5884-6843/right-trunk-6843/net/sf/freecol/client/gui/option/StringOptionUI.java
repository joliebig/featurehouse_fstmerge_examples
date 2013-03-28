

package net.sf.freecol.client.gui.option;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.plaf.FreeColComboBoxRenderer;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.StringOption;



public final class StringOptionUI extends JComboBox implements OptionUpdater, PropertyChangeListener {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(StringOptionUI.class.getName());

    private final StringOption option;
    private String originalValue;
    private JLabel label;

    
    public StringOptionUI(final StringOption option, boolean editable) {

        this.option = option;
        this.originalValue = option.getValue();

        String name = option.getName();
        String description = option.getShortDescription();
        String text = (description != null) ? description : name;
        label = new JLabel(name, JLabel.LEFT);
        label.setToolTipText(text);

        List<String> choices = generateChoices(option);

        setModel(new DefaultComboBoxModel(choices.toArray(new String[choices.size()])));
        setSelectedItem(option.getValue());
        setRenderer(new ChoiceRenderer());

        setEnabled(editable);
        addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                if (option.isPreviewEnabled()) {
                    String value = (String) getSelectedItem();
                    if (option.getValue().equals(value)) {
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
            final String value = (String) event.getNewValue();
            if (value == null && option.addNone()) {
                setSelectedIndex(0);
                originalValue = null;
            } else if (!value.equals(getSelectedItem())) {
                setSelectedItem(value);
                originalValue = value;
            }
        }
    }
    
    
    public void updateOption() {
        if (getSelectedIndex() == 0 && option.addNone()) {
            option.setValue(StringOption.NONE);
        } else {
            option.setValue((String) getSelectedItem());
        }
    }

    
    public void reset() {
        if (option.getValue() == null && option.addNone()) {
            setSelectedIndex(0);
        } else {
            setSelectedItem(option.getValue());
        }
    }

    private List<String> generateChoices(StringOption option) {
        List<String> choices;
        if (option.getGenerateChoices() == null) {
            choices = option.getChoices();
            if (choices == null || choices.isEmpty()) {
                choices = new ArrayList<String>();
                choices.add(option.getValue());
            }
        } else {
            List<FreeColObject> objects = new ArrayList<FreeColObject>();
            switch(option.getGenerateChoices()) {
            case UNITS:
                objects.addAll(Specification.getSpecification().getUnitTypeList());
                break;
            case IMMIGRANTS:
                for (UnitType unitType : Specification.getSpecification().getUnitTypeList()) {
                    if (unitType.isRecruitable()) {
                        objects.add(unitType);
                    }
                }
                break;
            case NAVAL_UNITS:
                for (UnitType unitType : Specification.getSpecification().getUnitTypeList()) {
                    if (unitType.hasAbility("model.ability.navalUnit")) {
                        objects.add(unitType);
                    }
                }
                break;
            case LAND_UNITS:
                for (UnitType unitType : Specification.getSpecification().getUnitTypeList()) {
                    if (!unitType.hasAbility("model.ability.navalUnit")) {
                        objects.add(unitType);
                    }
                }
                break;
            case BUILDINGS:
                objects.addAll(Specification.getSpecification().getBuildingTypeList());
                break;
            case FOUNDING_FATHERS:
                objects.addAll(Specification.getSpecification().getFoundingFathers());
                break;
            }
            choices = new ArrayList<String>(objects.size());
            for (FreeColObject object : objects) {
                choices.add(object.getId());
            }
            if (option.addNone()) {
                choices.add(0, StringOption.NONE);
            }
        }
        return choices;
    }

    private class ChoiceRenderer extends FreeColComboBoxRenderer {

        @Override
        public void setLabelValues(JLabel label, Object value) {
            String id = (String) value;
            if ("none".equals(id)) {
                label.setText(Messages.message(id));
            } else {
                label.setText(Messages.message((String) value + ".name"));
            }
        }
    }
}
