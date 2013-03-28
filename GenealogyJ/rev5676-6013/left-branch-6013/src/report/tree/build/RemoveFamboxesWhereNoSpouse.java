

package tree.build;

import tree.IndiBox;
import tree.filter.TreeFilterBase;


public class RemoveFamboxesWhereNoSpouse extends TreeFilterBase {
    protected void preFilter(IndiBox indibox) {
        if (indibox.spouse == null)
            indibox.family = null;
    }
}
