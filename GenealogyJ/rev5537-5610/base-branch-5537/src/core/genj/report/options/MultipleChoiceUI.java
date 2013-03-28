

package genj.report.options;

import genj.option.OptionUI;
import genj.option.PropertyOption;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;


public class MultipleChoiceUI extends JComboBox implements OptionUI
{

    private PropertyOption option;
    private String[] choices;

    
    public MultipleChoiceUI(PropertyOption option, String[] choices)
    {
        this.option = option;
        this.choices = choices;
        setModel(new DefaultComboBoxModel(choices));
        setSelectedIndex(getIndex());
    }

    
    public JComponent getComponentRepresentation()
    {
        return this;
    }

    
    public String getTextRepresentation()
    {
        int i = getIndex();
        return i == -1 ? "" : choices[i];
    }

    
    public void endRepresentation()
    {
        option.setValue(new Integer(getSelectedIndex()));
    }

    
    protected int getIndex()
    {
        int index = ((Integer)option.getValue()).intValue();
        if (index < 0 || index > choices.length - 1)
            index = -1;
        return index;
    }
}
