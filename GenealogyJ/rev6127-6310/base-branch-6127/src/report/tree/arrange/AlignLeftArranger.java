

package tree.arrange;

import tree.IndiBox;
import tree.IndiBox.Direction;


public class AlignLeftArranger extends AbstractArranger {

    
	public AlignLeftArranger(int spacing) {
		super(spacing);
	}

    public void filter(IndiBox indibox) {
        indibox.wPlus = indibox.width;
        indibox.hPlus = 1;
        super.filter(indibox);
    }

    protected void arrangeSpouse(IndiBox indibox, IndiBox spouse) {
        spouse.wPlus = spouse.width;
		spouse.x = indibox.width;
	}

	protected void arrangeChildren(IndiBox indibox) {
		int currentX = 0;
		if (indibox.getDir() == Direction.PARENT)
            currentX = indibox.prev.width / 2 - indibox.x + spacing;

		for (int i = 0; i < indibox.children.length; i++) {
			IndiBox child = indibox.children[i];
			child.x = currentX;
			child.y = 1;
			filter(child);
			currentX += child.wPlus + spacing;
		}
		if (indibox.children.length == 1) {
            IndiBox child = indibox.children[0];
            int parentWidth = indibox.wMinus + indibox.wPlus;
            int childWidth = child.wMinus + child.wPlus;
            int centerX = (parentWidth - childWidth) / 2 - indibox.wMinus + child.wMinus;
            if (child.x < centerX)
                child.x = centerX;
        }
	}

	protected void arrangeNextMarriages(IndiBox indibox, IndiBox next) {
		filter(next);
        next.x = indibox.wPlus + next.wMinus + spacing;
        if (indibox.spouse != null && indibox.spouse.nextMarriage == next)
            next.x -= indibox.spouse.x;
	}

	protected void arrangeSpouseParent(IndiBox indibox, IndiBox parent) {
		filter(parent);
		parent.y = -parent.hPlus;
	}

	protected void arrangeParent(IndiBox indibox, IndiBox parent) {
		filter(parent);
		parent.y = -indibox.hMinus - parent.hPlus;
	}
}
