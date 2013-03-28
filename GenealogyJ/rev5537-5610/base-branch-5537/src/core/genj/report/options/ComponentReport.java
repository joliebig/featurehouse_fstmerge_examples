

package genj.report.options;

import genj.option.Option;
import genj.option.PropertyOption;
import genj.report.Report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ComponentReport extends Report
{
    
    private List<PropertyOption> options = null;

    
    private Map<String, List<Object>> configurables = new LinkedHashMap<String, List<Object>>();

    
    private Map<String, String> forcedCategories = new HashMap<String, String>();

    
    protected void addOptions(Object component)
    {
        addOptions(component, getName());
    }

    
    protected void addOptions(Object component, String category)
    {
        List<Object> comps = configurables.get(category);
        if (comps == null)
        {
            comps = new ArrayList<Object>();
            configurables.put(category, comps);
        }
        addComponent(comps, component);
    }

    
    private void addComponent(List<Object> comps, Object component)
    {
        if (component instanceof ComponentContainer)
        {
            for (Object o : ((ComponentContainer)component).getComponents())
                if (o == component)
                    comps.add(o);
                else
                    addComponent(comps, o);
        }
        else
            comps.add(component);
    }

    
    protected void setCategory(String property, String category)
    {
        forcedCategories.put(property, category);
    }

    
    @Override
    public List<PropertyOption> getOptions()
    {
        if (options != null)
            return options;

        Map<String, AggregatorOption> optionsCache = new LinkedHashMap<String, AggregatorOption>();

        addOptions(optionsCache, super.getOptions(), getName());

        for (Map.Entry<String, List<Object>> entry : configurables.entrySet())
            for (Object component : entry.getValue())
                addOptions(optionsCache, PropertyOption.introspect(component), entry.getKey());

        options = new ArrayList<PropertyOption>(optionsCache.values());
        
        for (PropertyOption option : options)
        {
            
            option.restore(registry);
            
            
            
            
            String oname = translateOption(option.getProperty());
            if (oname.length() > 0)
                option.setName(oname);
            String toolTipKey = option.getProperty() + ".tip";
            String toolTip = translateOption(toolTipKey);
            if (toolTip.length() > 0 && !toolTip.equals(toolTipKey))
                option.setToolTip(toolTip);
        }
        return options;
    }

    
    private void addOptions(Map<String, AggregatorOption> optionsCache, List<PropertyOption> options, String category)
    {
        for (PropertyOption option : options)
        {
            String property = option.getProperty();
            AggregatorOption aggregator = optionsCache.get(property);
            if (aggregator == null)
            {
                aggregator = new AggregatorOption(this, property);
                optionsCache.put(property, aggregator);
                String cat = forcedCategories.get(property);
                if (cat == null)
                    cat = category;
                aggregator.setCategory(translate(cat));
            }
            aggregator.addOption(option);
        }
    }

    
    @Override
    public void saveOptions()
    {
        
        if (options == null)
            return;
        
        for (Option option : options)
            option.persist(registry);
        
    }
}
