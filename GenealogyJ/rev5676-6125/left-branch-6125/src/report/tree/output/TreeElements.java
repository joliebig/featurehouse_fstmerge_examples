

package tree.output;

import java.awt.Graphics2D;

import tree.FamBox;
import tree.IndiBox;


public interface TreeElements {
    
    public void drawIndiBox(IndiBox indibox, int x, int y, int gen);

    
    public void drawFamBox(FamBox fambox, int x, int y, int gen);

    
    public void drawLine(int x1, int y1, int x2, int y2);

    
    public void drawDashedLine(int x1, int y1, int x2, int y2);

    
    public void header(int width, int height);

    
    public void footer();

    
    public void getIndiBoxSize(IndiBox indibox);

    
    public void getFamBoxSize(FamBox fambox);

    
    public void setGraphics(Graphics2D graphics);
}
