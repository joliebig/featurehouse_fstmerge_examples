

package tree;

import genj.gedcom.Indi;
import genj.option.Option;
import genj.option.PropertyOption;
import genj.report.Report;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tree.arrange.LayoutFactory;
import tree.build.BasicTreeBuilder;
import tree.build.TreeBuilder;
import tree.filter.DetermineBoxSizes;
import tree.filter.TreeFilter;
import tree.graphics.GraphicsOutput;
import tree.graphics.GraphicsOutputFactory;
import tree.graphics.GraphicsRenderer;
import tree.output.RendererFactory;
import tree.output.TreeElements;
import tree.output.TreeElementsFactory;


public class ReportGraphicalTree extends Report
{
    
    private Translator translator = new Translator(this);

    
    public TreeBuilder builder = new BasicTreeBuilder();

    
    public TreeElementsFactory treeElements = new TreeElementsFactory();

    
    public LayoutFactory layouts = new LayoutFactory();

    
    public RendererFactory renderers = new RendererFactory(translator);

    
    public GraphicsOutputFactory outputs = new GraphicsOutputFactory();

    
    private Map<PropertyOption, Object> originalValues;

    public ReportGraphicalTree()
    {










    }

    
    public boolean usesStandardOut() {
        return false;
    }

    
    public Object start(Indi indi) {

        
        replaceVariables(indi);

        
        IndiBox indibox = builder.build(indi);

        TreeElements elements = treeElements.createElements();

        new DetermineBoxSizes(elements).filter(indibox);

        
        TreeFilter arranger = layouts.createLayout();;
        arranger.filter(indibox);

        
        GraphicsRenderer renderer = renderers.createRenderer(indibox, elements);

        
        GraphicsOutput output = outputs.createOutput(this);
        if (output == null)  
        {
            restoreOptionValues();
            return null;
        }

        try {
            output.output(renderer);
        } catch (OutOfMemoryError e) {
            println("ERROR! The report ran out of memory.\n");
            println("You can try to do the following things:");
            println("  * Increase the memory limit for GenJ");
            println("  * Build a smaller tree");
            println("  * Choose SVG output (requires the least memory)");
        } catch (IOException e) {
            println("Error generating output: " + e.getMessage());
        }

        
        restoreOptionValues();
        
        return output.result(this);
    }

    
    private void replaceVariables(Indi indi)
    {
        originalValues = new HashMap<PropertyOption, Object>();
        for (Option o : getOptions())
        {
            
            PropertyOption option = (PropertyOption)o;
            
            if (option.getValue().getClass().equals(String.class))
            {
                String value = (String)option.getValue();
                originalValues.put(option, value);

                value = value.replaceAll("\\$i", indi.getId());
                value = value.replaceAll("\\$n", indi.getName());
                value = value.replaceAll("\\$f", indi.getFirstName());
                value = value.replaceAll("\\$l", indi.getLastName());

                option.setValue(value);
            }
        }
    }

    private void restoreOptionValues()
    {
        for (Map.Entry<PropertyOption, Object> entry : originalValues.entrySet())
            entry.getKey().setValue(entry.getValue());
    }
}
