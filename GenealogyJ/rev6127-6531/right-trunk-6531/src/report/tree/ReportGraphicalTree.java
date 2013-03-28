

package tree;

import genj.gedcom.Indi;
import genj.report.Report;

import java.io.IOException;

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

    public ReportGraphicalTree()
    {










    }

    
    public Object start(Indi indi) {

        
        IndiBox indibox = builder.build(indi);

        TreeElements elements = treeElements.createElements();

        new DetermineBoxSizes(elements).filter(indibox);

        
        TreeFilter arranger = layouts.createLayout();;
        arranger.filter(indibox);

        
        GraphicsRenderer renderer = renderers.createRenderer(indibox, elements);

        
        GraphicsOutput output = outputs.createOutput(this);
        if (output == null)  
        {
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

        return output.result(this);
    }

}
