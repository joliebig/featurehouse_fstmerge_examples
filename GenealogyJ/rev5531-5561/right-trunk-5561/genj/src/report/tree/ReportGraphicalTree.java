

package tree;

import genj.gedcom.Indi;
import genj.option.PropertyOption;
import genj.report.options.ComponentReport;

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


public class ReportGraphicalTree extends ComponentReport
{
    private static final String OUTPUT_CATEGORY = "output";
    private static final String ELEMENTS_CATEGORY = "elements";
    private static final String RENDERER_CATEGORY = "renderer";
    private static final String LAYOUT_CATEGORY = "layout";
    private static final String BUILDER_CATEGORY = "builder";

    
    private Translator translator = new Translator(this);;

    
    private TreeBuilder builder = new BasicTreeBuilder();

    
    private TreeElementsFactory treeElements = new TreeElementsFactory();

    
    private LayoutFactory layouts = new LayoutFactory();

    
    private RendererFactory renderers = new RendererFactory(translator);

    
    private GraphicsOutputFactory outputs = new GraphicsOutputFactory();

    
    private Map<PropertyOption, Object> originalValues;

    public ReportGraphicalTree()
    {
        
        addOptions(builder, BUILDER_CATEGORY);
        addOptions(layouts, LAYOUT_CATEGORY);
        addOptions(treeElements, ELEMENTS_CATEGORY);
        addOptions(renderers, RENDERER_CATEGORY);
        addOptions(outputs, OUTPUT_CATEGORY);

        
        setCategory("flip", LAYOUT_CATEGORY);
        setCategory("rotation", LAYOUT_CATEGORY);
    }

    
    public boolean usesStandardOut() {
        return false;
    }

    
    public void start(Indi indi) {

        
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
            return;
        }

        try {
            output.output(renderer);
            output.display(this);
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
    }

    
    private void replaceVariables(Indi indi)
    {
        originalValues = new HashMap<PropertyOption, Object>();
        for (PropertyOption option : getOptions())
        {
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
