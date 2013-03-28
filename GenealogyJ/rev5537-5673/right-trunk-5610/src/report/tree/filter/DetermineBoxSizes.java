

package tree.filter;

import tree.IndiBox;
import tree.output.TreeElements;


public class DetermineBoxSizes extends TreeFilterBase {

    private TreeElements elements;

    public DetermineBoxSizes(TreeElements elements) {
        this.elements = elements;
    }

    public void preFilter(IndiBox indibox) {
        elements.getIndiBoxSize(indibox);
        if (indibox.family != null)
            elements.getFamBoxSize(indibox.family);
    }
}
