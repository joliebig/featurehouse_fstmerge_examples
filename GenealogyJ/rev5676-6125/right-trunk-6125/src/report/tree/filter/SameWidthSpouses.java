

package tree.filter;

import tree.IndiBox;


public class SameWidthSpouses extends TreeFilterBase {
    protected void preFilter(IndiBox indibox) {
        if (indibox.spouse != null) {
            if (indibox.spouse.width > indibox.width)
                indibox.width = indibox.spouse.width;
            else
                indibox.spouse.width = indibox.width;
        }
    }
}