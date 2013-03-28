

package tree.build;

import genj.gedcom.Indi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tree.IndiBox;
import tree.filter.TreeFilterBase;


public class NoSpouseFilter extends TreeFilterBase {

    
    protected void preFilter(IndiBox indibox) {

        if (indibox.getDir() != IndiBox.Direction.PARENT &&
            indibox.getDir() != IndiBox.Direction.SPOUSE)
        {
            indibox.children = getChildren(indibox, indibox.individual);
            indibox.spouse = null;
        }
    }

    
    private IndiBox[] getChildren(IndiBox indibox, Indi parent) {
        IndiBox[] children = null;
        if (indibox.individual == parent || (indibox.spouse != null && indibox.spouse.individual == parent))
        	children = indibox.children;
        
        if (indibox.nextMarriage != null)
            children = merge(children, getChildren(indibox.nextMarriage, parent));
        if (indibox.spouse != null && indibox.spouse.nextMarriage != null)
            children = merge(children, getChildren(indibox.spouse.nextMarriage, parent));
        return children;
    }

    
    private IndiBox[] merge(IndiBox[] a, IndiBox[] b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        List<IndiBox> list = new ArrayList<IndiBox>(Arrays.asList(a));
        list.addAll(Arrays.asList(b));
        return list.toArray(new IndiBox[0]);
    }
}
