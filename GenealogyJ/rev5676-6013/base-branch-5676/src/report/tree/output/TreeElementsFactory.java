

package tree.output;

import genj.report.options.ComponentContainer;

import java.util.ArrayList;
import java.util.List;



public class TreeElementsFactory implements ComponentContainer
{
    private TreeElements elements;
    private TreeElements rotateElements;
    private TreeElements flipElements;

    public TreeElementsFactory()
    {
        elements = new GraphicsTreeElements();
        rotateElements = new RotateTreeElements(elements);
        flipElements = new FlipTreeElements(rotateElements);
    }

    public TreeElements createElements()
    {
        return flipElements;
    }

    public List<Object> getComponents()
    {
        List<Object> components = new ArrayList<Object>();
        components.add(elements);
        components.add(rotateElements);
        components.add(flipElements);
        components.add(this);
        return components;
    }
}
