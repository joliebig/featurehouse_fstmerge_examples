

package tree.build;

import tree.IndiBox;
import tree.filter.TreeFilterBase;


public class RemoveFamboxes extends TreeFilterBase {
    protected void preFilter(IndiBox indibox) {
        indibox.family = null;
    }
}
