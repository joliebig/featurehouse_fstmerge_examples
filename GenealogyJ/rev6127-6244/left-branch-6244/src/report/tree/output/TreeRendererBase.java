

package tree.output;

import java.awt.Dimension;
import java.awt.Graphics2D;

import tree.IndiBox;
import tree.graphics.GraphicsRenderer;


public abstract class TreeRendererBase implements GraphicsRenderer {

    
    protected static final int PAGE_MARGIN = 10;

    protected IndiBox firstIndi;

    
    protected TreeElements elements;

    public void setFirstIndi(IndiBox firstIndi)
    {
        this.firstIndi = firstIndi;
    }

    public void setElements(TreeElements elements)
    {
        this.elements = elements;
    }

    
	public void render(Graphics2D graphics)
	{
        elements.setGraphics(graphics);
        render();
	}

    
	public void render() {
		elements.header(getImageWidth(), getImageHeight());
        drawTree(firstIndi, firstIndi.wMinus + PAGE_MARGIN, firstIndi.hMinus + PAGE_MARGIN, 0);
        elements.footer();
	}

    
    public int getImageWidth() {
        return firstIndi.wMinus + firstIndi.wPlus + 2 * PAGE_MARGIN;
    }

    
    public int getImageHeight() {
        return firstIndi.hMinus + firstIndi.hPlus + 2 * PAGE_MARGIN;
    }

    
    protected abstract void drawLines(IndiBox indibox, int baseX, int baseY);

    
    protected abstract Dimension getFamboxCoords(IndiBox indibox);

    
    private void drawTree(IndiBox indibox, int baseX, int baseY, int gen)
    {
        baseX += indibox.x;
        baseY += indibox.y;

        
        drawLines(indibox, baseX, baseY);

        
        elements.drawIndiBox(indibox, baseX, baseY, gen);

        
        if (indibox.family != null) {
            Dimension coords = getFamboxCoords(indibox);
            elements.drawFamBox(indibox.family, baseX + coords.width, baseY + coords.height, gen);
        }

        
        if (indibox.spouse != null)
            drawTree(indibox.spouse, baseX, baseY, gen);

        
        if (indibox.parent != null)
            drawTree(indibox.parent, baseX, baseY, gen - 1);

        
        if (indibox.hasChildren())
            for (int i = 0; i < indibox.children.length; i++)
                drawTree(indibox.children[i], baseX, baseY, gen + 1);

        
        if (indibox.nextMarriage != null)
            drawTree(indibox.nextMarriage, baseX, baseY, gen);
    }
}
