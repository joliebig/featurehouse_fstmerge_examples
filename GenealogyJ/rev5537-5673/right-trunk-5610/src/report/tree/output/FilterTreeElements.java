

package tree.output;

import java.awt.Graphics2D;

import tree.FamBox;
import tree.IndiBox;


public class FilterTreeElements implements TreeElements {

    
    protected Graphics2D graphics = null;

    protected TreeElements elements;

    
    public FilterTreeElements(Graphics2D graphics, TreeElements elements) {
        this.graphics = graphics;
        this.elements = elements;
    }

    public FilterTreeElements(TreeElements elements)
    {
        this(null, elements);
    }

    
    public void setGraphics(Graphics2D graphics) {
        this.graphics = graphics;
        elements.setGraphics(graphics);
    }

    
    public void drawIndiBox(IndiBox indibox, int x, int y, int gen) {
        elements.drawIndiBox(indibox, x, y, gen);
    }

    
    public void drawFamBox(FamBox fambox, int x, int y, int gen) {
        elements.drawFamBox(fambox, x, y, gen);
    }

    
    public void drawLine(int x1, int y1, int x2, int y2) {
        elements.drawLine(x1, y1, x2, y2);
    }

    
    public void drawDashedLine(int x1, int y1, int x2, int y2) {
        elements.drawDashedLine(x1, y1, x2, y2);
    }

    
    public void header(int width, int height) {
        elements.header(width, height);
    }

    
    public void footer() {
        elements.footer();
    }

    public void getIndiBoxSize(IndiBox indibox)
    {
        elements.getIndiBoxSize(indibox);
    }

    public void getFamBoxSize(FamBox fambox)
    {
        elements.getFamBoxSize(fambox);
    }
}
