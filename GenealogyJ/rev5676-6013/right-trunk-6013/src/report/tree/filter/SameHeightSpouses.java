

package tree.filter;

import tree.IndiBox;


public class SameHeightSpouses extends TreeFilterBase {
    protected void preFilter(IndiBox indibox) {
        if (indibox.spouse != null) {
            if (indibox.spouse.height > indibox.height)
                indibox.height = indibox.spouse.height;
            else
                indibox.spouse.height = indibox.height;
        }
    }
}