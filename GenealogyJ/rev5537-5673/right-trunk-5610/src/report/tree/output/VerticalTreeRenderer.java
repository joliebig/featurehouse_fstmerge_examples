

package tree.output;

import java.awt.Dimension;
import java.util.SortedSet;
import java.util.TreeSet;

import tree.IndiBox;
import tree.IndiBox.Direction;
import tree.arrange.LayoutFactory;


public class VerticalTreeRenderer extends TreeRendererBase {

    
	protected void drawLines(IndiBox indibox, int baseX, int baseY) {

        int midX = baseX + getMidX(indibox);

        
        int lineY = baseY + getChildrenLineY(indibox);

        if (indibox.hasChildren() || indibox.getDir() == Direction.PARENT) {
            int midY = baseY + indibox.height;

            if (indibox.spouse != null)
                midY -= indibox.height / 2;

            if (indibox.family != null)
                midY = baseY + indibox.height + indibox.family.height;

            elements.drawLine(midX, midY, midX, lineY);

            SortedSet<Integer> xSet = new TreeSet<Integer>();
            xSet.add(midX);
            if (indibox.getDir() == Direction.PARENT)
                xSet.add(baseX - indibox.x + indibox.prev.width / 2);
            if (indibox.hasChildren())
                for (int i = 0; i < indibox.children.length; i++)
                    xSet.add(baseX + indibox.children[i].x + indibox.children[i].width / 2);
            int x1 = xSet.first();
            int x2 = xSet.last();

            elements.drawLine(x1, lineY, x2, lineY);
        }

		
		if (indibox.parent != null) {
            int parentLineY = baseY + indibox.parent.y + getChildrenLineY(indibox.parent);
            elements.drawLine(baseX + indibox.width / 2, baseY, baseX + indibox.width / 2, parentLineY);
		}

		
		if (indibox.hasChildren())
			for (int i = 0; i < indibox.children.length; i++) {
                int x = baseX + indibox.children[i].x + indibox.children[i].width / 2;
                elements.drawLine(x, baseY + indibox.children[i].y, x, lineY);
			}

		
		if (indibox.nextMarriage != null) {
            lineY = indibox.height / 2;
            if (indibox.nextMarriage.height < indibox.height)
                lineY = indibox.nextMarriage.height / 2;
			if (indibox.nextMarriage.x > 0)
                elements.drawDashedLine(baseX + indibox.width, baseY + lineY,
				        baseX + indibox.nextMarriage.x, baseY + lineY);
			else
                elements.drawDashedLine(baseX, baseY + lineY,
				        baseX + indibox.nextMarriage.x + indibox.nextMarriage.width, baseY + lineY);
		}
	}

    private int getChildrenLineY(IndiBox indibox) {
        int lineY;
        if (indibox.hasChildren()) {
            lineY = indibox.children[0].y;
            for (int i = 1; i < indibox.children.length; i++)
                lineY = Math.min(lineY, indibox.children[i].y);
            lineY -= LayoutFactory.SPACING;
        } else {
            lineY = indibox.height + LayoutFactory.SPACING;
            if (indibox.family != null)
                lineY += indibox.family.height;
        }
        return lineY;
    }

    
    protected Dimension getFamboxCoords(IndiBox indibox) {
        int x = getMidX(indibox) - indibox.family.width / 2;
        int y = indibox.height;
        return new Dimension(x, y);
    }

    private int getMidX(IndiBox indibox) {
        if (indibox.spouse == null)
            return indibox.width / 2;

        int x;
        if (indibox.spouse.x > 0)
            x = (indibox.spouse.x + indibox.width) / 2;
        else
            x = (indibox.spouse.x + indibox.spouse.width) / 2;

        if (indibox.family != null) {
            if (indibox.spouse.x > 0) {
                if (indibox.spouse.width < indibox.family.width) {
                    int x1 = (indibox.spouse.x + indibox.spouse.width) / 2;
                    int x2 = indibox.spouse.x + (indibox.spouse.width - indibox.family.width) / 2;
                    x = Math.max(x1, x2);
                } else if (indibox.width < indibox.family.width) {
                    int x1 = (indibox.spouse.x + indibox.spouse.width) / 2;
                    int x2 = (indibox.width + indibox.family.width) / 2;
                    x = Math.min(x1, x2);
                }
            } else if (indibox.spouse.x <= 0) {
                if (indibox.spouse.width < indibox.family.width) {
                    int x1 = (indibox.width + indibox.spouse.x) / 2;
                    int x2 = indibox.spouse.x + (indibox.family.width + indibox.spouse.width) / 2;
                    x = Math.min(x1, x2);
                } else if (indibox.width < indibox.family.width) {
                    int x1 = (indibox.width + indibox.spouse.x) / 2;
                    int x2 = (indibox.width - indibox.family.width) / 2;
                    x = Math.max(x1, x2);
                }
            }
        }

        return x;
    }
}
