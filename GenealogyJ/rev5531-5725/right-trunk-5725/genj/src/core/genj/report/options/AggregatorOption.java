

package genj.report.options;

import genj.option.MultipleChoiceOption;
import genj.option.OptionUI;
import genj.option.OptionsWidget;
import genj.option.PropertyOption;
import genj.report.Report;
import genj.util.Registry;

import java.util.ArrayList;
import java.util.List;


public class AggregatorOption extends PropertyOption
{
    
    private List<PropertyOption> options = new ArrayList<PropertyOption>();

    
    private Report report;

    
    protected AggregatorOption(Report report, String property)
    {
        super(report, property);
        this.report = report;
    }

    
    public void addOption(PropertyOption option)
    {
        options.add(option);
    }

    
    @Override
    public Object getValue()
    {
        return getFirst().getValue();
    }

    @Override
    public void setName(String set)
    {
        getFirst().setName(set);
    }

    @Override
    public void setToolTip(String set)
    {
        getFirst().setToolTip(set);
    }

    
    @Override
    public void setValue(Object set)
    {
        for (int i = 0; i < options.size(); i++)
            ((PropertyOption) options.get(i)).setValue(set);
    }

    @Override
    public String getName()
    {
        return getFirst().getName();
    }

    @Override
    public String getToolTip()
    {
        return getFirst().getToolTip();
    }

    
    @Override
    public OptionUI getUI(OptionsWidget widget)
    {
        Class<? extends OptionUI> uiType = getFirst().getUI(null).getClass();
        
        if (uiType == FontUI.class)
            return new FontUI(this);
        
        if (uiType == BooleanUI.class)
            return new BooleanUI(this);
        
        if (uiType == FileUI.class)
            return new FileUI(this);
        
        if (uiType == MultipleChoiceOption.UI.class)
        {
            Object[] choices = ((MultipleChoiceOption)getFirst()).getChoices();
            String[] translatedChoices = translate(choices);
            return new MultipleChoiceUI(this, translatedChoices);
        }
        
        return new SimpleUI(this);
    }

    
    public Class<? extends PropertyOption> getType()
    {
        return getFirst().getClass();
    }

    
    public String[] getChoices()
    {
        Object[] choices = ((MultipleChoiceOption)getFirst()).getChoices();
        return translate(choices);
    }

    
    private String[] translate(Object[] choices)
    {
        String[] result = new String[choices.length];
        for (int i = 0; i < choices.length; i++)
        {
            String choice = choices[i].toString();
            String key = getProperty() + "." + choice;
            result[i] = report.translate(key);
            if (result[i].equals(key))
                result[i] = report.translate(choice);
        }
        return result;
    }

    
    @Override
    public void restore(Registry registry)
    {
        String value = registry.get(getPropertyKey(), (String)null);
        if (value != null)
            setValue(value);
    }

    
    @Override
    public void persist(Registry registry)
    {
        Object value = getValue();
        if (value != null)
            registry.put(getPropertyKey(), value.toString());
    }

    
    private String getPropertyKey()
    {
        return instance.getClass().getName() + '.' + getProperty();
    }

    
    private PropertyOption getFirst()
    {
        return (PropertyOption)options.get(0);
    }
}
