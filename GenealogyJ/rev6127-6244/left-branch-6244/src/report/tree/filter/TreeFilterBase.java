

package tree.filter;

import tree.IndiBox;



public abstract class TreeFilterBase implements TreeFilter {

    
    public final void filter(IndiBox indibox) {
        if (indibox == null)
            return;

        preFilter(indibox);

        filter(indibox.parent);
        filter(indibox.spouse);
        filter(indibox.nextMarriage);
        if (indibox.hasChildren())
            for (int i = 0; i < indibox.children.length; i++)
                filter(indibox.children[i]);

        postFilter(indibox);
    }

    
    protected void preFilter(IndiBox indibox) {
    }

    
    protected void postFilter(IndiBox indibox) {
    }
}
