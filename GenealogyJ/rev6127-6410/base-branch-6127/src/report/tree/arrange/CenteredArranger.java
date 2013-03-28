

package tree.arrange;

import tree.IndiBox;
import tree.IndiBox.Direction;


public class CenteredArranger extends AlignLeftArranger {

    protected AbstractArranger leftArranger;
    protected AbstractArranger rightArranger;

    public CenteredArranger(int spacing) {
        super(spacing);
        leftArranger = new AlignRightArranger(spacing);
        rightArranger = new AlignLeftArranger(spacing);
    }

    protected void arrangeSpouseParent(IndiBox indibox, IndiBox parent) {
        if (indibox.parent != null) {
            parent.x = spacing / 2;
            rightArranger.filter(parent);
        } else {
            parent.x = indibox.spouse.width / 2 - parent.width;
            filter(parent);
        }
        parent.y = -parent.hPlus;
    }

    protected void arrangeParent(IndiBox indibox, IndiBox parent) {
        
        if (indibox.spouse != null && indibox.spouse.parent != null) {
            parent.x = indibox.width - parent.width - spacing / 2;
            leftArranger.filter(parent);
        } else { 
            if (!parent.hasChildren())
                parent.x = -parent.width / 2;
            filter(parent);
        }
        parent.y = -parent.hPlus;
    }

    protected void arrangeChildren(IndiBox indibox) {
        int currentX = 0;
        if (indibox.getDir() == Direction.PARENT)
            currentX = indibox.prev.width / 2 - indibox.x + spacing;

        for (int i = 0; i < indibox.children.length; i++) {
            IndiBox child = indibox.children[i];
            child.y = 1;
            filter(child);
            child.x = currentX + child.wMinus;
            currentX += child.wMinus + child.wPlus + spacing;
        }
        if (indibox.getDir() == Direction.PARENT)
            return;
        int min = indibox.children[0].x - indibox.children[0].wMinus;
        int diff = min + (currentX - spacing - min) / 2;
        diff -= (indibox.wPlus + indibox.wMinus) / 2;
        for (int i = 0; i < indibox.children.length; i++) {
            IndiBox child = indibox.children[i];
            child.x -= diff;
        }
    }
}
