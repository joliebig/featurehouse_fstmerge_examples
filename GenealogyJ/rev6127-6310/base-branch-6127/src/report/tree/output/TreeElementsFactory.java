

package tree.output;

import java.util.ArrayList;
import java.util.List;



public class TreeElementsFactory {
  
    public TreeElements elements;
    public TreeElements rotateElements;
    public TreeElements flipElements;

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

}
