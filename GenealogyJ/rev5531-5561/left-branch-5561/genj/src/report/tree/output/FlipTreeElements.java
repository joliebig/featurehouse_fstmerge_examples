

package tree.output;

import java.awt.Graphics2D;

import tree.FamBox;
import tree.IndiBox;


public class FlipTreeElements extends FilterTreeElements {

    public boolean flip = false;

    
    public FlipTreeElements(Graphics2D graphics, TreeElements elements) {
        super(graphics, elements);
    }

    public FlipTreeElements(TreeElements elements)
    {
        super(elements);
    }

    
    public void drawIndiBox(IndiBox indibox, int x, int y, int gen) {
        if (!flip)
        {
            elements.drawIndiBox(indibox, x, y, gen);
            return;
        }
        graphics.translate(x + indibox.width/2, y);
        graphics.scale(-1, 1);
        elements.drawIndiBox(indibox, -indibox.width/2, 0, gen);
        graphics.scale(-1, 1);
        graphics.translate(-x - indibox.width/2, -y);
    }

    
    public void drawFamBox(FamBox fambox, int x, int y, int gen) {
        if (!flip)
        {
            elements.drawFamBox(fambox, x, y, gen);
            return;
        }
        graphics.translate(x + fambox.width/2, y);
        graphics.scale(-1, 1);
        elements.drawFamBox(fambox, -fambox.width/2, 0, gen);
        graphics.scale(-1, 1);
        graphics.translate(-x - fambox.width/2, -y);
    }

    
    public void header(int width, int height) {
        if (flip)
        {
            graphics.translate(width/2, 0);
            graphics.scale(-1, 1);
            graphics.translate(-width/2, 0);
        }
        elements.header(width, height);
    }

    
    public void footer() {
        elements.footer();
    }
}
