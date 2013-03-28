

package tree.arrange;

import tree.IndiBox;
import tree.filter.TreeFilter;


public abstract class AbstractArranger implements TreeFilter {

    
	protected int spacing;

    
	public AbstractArranger(int spacing) {
		this.spacing = spacing;
	}

    
	protected abstract void arrangeSpouse(IndiBox indibox, IndiBox spouse);

    
	protected abstract void arrangeChildren(IndiBox indibox);

    
	protected abstract void arrangeNextMarriages(IndiBox indibox, IndiBox next);

    
	protected abstract void arrangeSpouseParent(IndiBox indibox, IndiBox parent);

    
	protected abstract void arrangeParent(IndiBox indibox, IndiBox parent);

    
	public void filter(IndiBox indibox) {

		
		if (indibox.spouse != null) {
			arrangeSpouse(indibox, indibox.spouse);
			updateSpouse(indibox);
		}

		
		if (indibox.hasChildren()) {
			arrangeChildren(indibox);
			updateChildren(indibox);
		}

		
		if (indibox.spouse != null && indibox.spouse.nextMarriage != null) {
            arrangeNextMarriages(indibox, indibox.spouse.nextMarriage);
			updateSpouseNextMarriage(indibox);
		}
        if (indibox.nextMarriage != null) {
            arrangeNextMarriages(indibox, indibox.nextMarriage);
            updateNextMarriage(indibox);
        }

		
        if (indibox.spouse != null && indibox.spouse.parent != null) {
            arrangeSpouseParent(indibox, indibox.spouse.parent);
            updateSpouseParent(indibox);
        }
        if (indibox.parent != null) {
            arrangeParent(indibox, indibox.parent);
            updateParent(indibox);
        }
	}

    protected void updateSpouse(IndiBox indibox) {
        if (indibox.spouse.x > 0)
            indibox.wPlus = indibox.spouse.width + indibox.spouse.x;
        else if (indibox.spouse.x < 0)
            indibox.wMinus = -indibox.spouse.x;
        if (indibox.spouse.y > 0)
            indibox.hPlus = indibox.spouse.height + indibox.spouse.y;
        else if (indibox.spouse.y < 0)
            indibox.hMinus = -indibox.spouse.y;
    }

    protected void updateChildren(IndiBox indibox) {
        for (int i = 0; i < indibox.children.length; i++) {
        	IndiBox child = indibox.children[i];
            if (child.y + child.hPlus > indibox.hPlus)
                indibox.hPlus = child.y + child.hPlus;
            if (-child.y + child.hMinus > indibox.hMinus)
                indibox.hMinus = -child.y + child.hMinus;
        	if (child.x + child.wPlus > indibox.wPlus)
        		indibox.wPlus = child.x + child.wPlus;
        	if (-child.x + child.wMinus > indibox.wMinus)
        		indibox.wMinus = -child.x + child.wMinus;
        }
    }

    protected void updateSpouseNextMarriage(IndiBox indibox) {
        IndiBox next = indibox.spouse.nextMarriage;

        if (next.hPlus > indibox.hPlus)
            indibox.hPlus = next.hPlus;
        if (indibox.spouse.wMinus < next.wMinus - next.x)
            indibox.spouse.wMinus = next.wMinus - next.x;
        if (indibox.spouse.wPlus < next.wPlus + next.x)
            indibox.spouse.wPlus = next.wPlus + next.x;
        if (indibox.wMinus < indibox.spouse.wMinus - indibox.spouse.x)
            indibox.wMinus = indibox.spouse.wMinus - indibox.spouse.x;
        if (indibox.wPlus < indibox.spouse.wPlus + indibox.spouse.x)
            indibox.wPlus = indibox.spouse.wPlus + indibox.spouse.x;

        if (next.wPlus > indibox.wPlus)
            indibox.wPlus = next.wPlus;
        if (indibox.spouse.hMinus < next.hMinus - next.y)
            indibox.spouse.hMinus = next.hMinus - next.y;
        if (indibox.spouse.hPlus < next.hPlus + next.y)
            indibox.spouse.hPlus = next.hPlus + next.y;
        if (indibox.hMinus < indibox.spouse.hMinus - indibox.spouse.y)
            indibox.hMinus = indibox.spouse.hMinus - indibox.spouse.y;
        if (indibox.hPlus < indibox.spouse.hPlus + indibox.spouse.y)
            indibox.hPlus = indibox.spouse.hPlus + indibox.spouse.y;
    }

    protected void updateNextMarriage(IndiBox indibox) {
        IndiBox next = indibox.nextMarriage;

        if (next.hPlus > indibox.hPlus)
            indibox.hPlus = next.hPlus;
        if (next.wPlus > indibox.wPlus)
            indibox.wPlus = next.wPlus;
        if (indibox.wMinus < next.wMinus - next.x)
            indibox.wMinus = next.wMinus - next.x;
        if (indibox.wPlus < next.wPlus + next.x)
            indibox.wPlus = next.wPlus + next.x;

        if (next.wPlus > indibox.wPlus)
            indibox.wPlus = next.wPlus;
        if (next.hPlus > indibox.hPlus)
            indibox.hPlus = next.hPlus;
        if (indibox.hMinus < next.hMinus - next.y)
            indibox.hMinus = next.hMinus - next.y;
        if (indibox.hPlus < next.hPlus + next.y)
            indibox.hPlus = next.hPlus + next.y;
    }

    protected void updateSpouseParent(IndiBox indibox) {
        IndiBox parent = indibox.spouse.parent;

        if (parent.wPlus + parent.x > indibox.spouse.wPlus)
        	indibox.spouse.wPlus = parent.wPlus + parent.x;
        if (indibox.spouse.wPlus + indibox.spouse.x > indibox.wPlus)
        	indibox.wPlus = indibox.spouse.wPlus + indibox.spouse.x;
        if (parent.wMinus - parent.x > indibox.spouse.wMinus)
        	indibox.spouse.wMinus = parent.wMinus - parent.x;
        if (indibox.spouse.wMinus - indibox.spouse.x > indibox.wMinus)
        	indibox.wMinus = indibox.spouse.wMinus - indibox.spouse.x;

        if (parent.hPlus + parent.y > indibox.spouse.hPlus)
            indibox.spouse.hPlus = parent.hPlus + parent.y;
        if (indibox.spouse.hPlus + indibox.spouse.y > indibox.hPlus)
            indibox.hPlus = indibox.spouse.hPlus + indibox.spouse.y;
        if (parent.hMinus - parent.y > indibox.spouse.hMinus)
            indibox.spouse.hMinus = parent.hMinus - parent.y;
        if (indibox.spouse.hMinus - indibox.spouse.y > indibox.hMinus)
            indibox.hMinus = indibox.spouse.hMinus - indibox.spouse.y;
    }

    protected void updateParent(IndiBox indibox) {
        IndiBox parent = indibox.parent;

        if (parent.hPlus + parent.y > indibox.hPlus)
            indibox.hPlus = parent.hPlus + parent.y;
        if (parent.hMinus - parent.y > indibox.hMinus)
            indibox.hMinus = parent.hMinus - parent.y;
        if (parent.wPlus + parent.x > indibox.wPlus)
        	indibox.wPlus = parent.wPlus + parent.x;
        if (parent.wMinus - parent.x > indibox.wMinus)
        	indibox.wMinus = parent.wMinus - parent.x;
    }
}
