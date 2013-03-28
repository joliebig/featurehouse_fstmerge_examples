

package tree.output;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import tree.FamBox;
import tree.IndiBox;


public class RotateTreeElements extends FilterTreeElements {

    public static final int ROTATE_0 = 0; 
    public static final int ROTATE_270 = 1;
    public static final int ROTATE_180 = 2;
    public static final int ROTATE_90 = 3;

    
    public int rotation = 0;

    public String[] rotations = { "none", "270", "180" , "90" };

    
    public RotateTreeElements(Graphics2D graphics, TreeElements elements) {
        super(graphics, elements);
    }

    public RotateTreeElements(TreeElements elements) {
        this(null, elements);
    }

    
    public void drawIndiBox(IndiBox indibox, int x, int y, int gen) {
        AffineTransform transform = graphics.getTransform();
        transform(x, y, indibox.width, indibox.height);
        transpose(indibox);
        elements.drawIndiBox(indibox, 0, 0, gen);
        transpose(indibox);
        graphics.setTransform(transform);
    }

    
    public void drawFamBox(FamBox fambox, int x, int y, int gen) {
        AffineTransform transform = graphics.getTransform();
        transform(x, y, fambox.width, fambox.height);
        transpose(fambox);
        elements.drawFamBox(fambox, 0, 0, gen);
        transpose(fambox);
        graphics.setTransform(transform);
    }

	
    private void transform(int x, int y, int w, int h) {
        switch (rotation) {
            case ROTATE_0:
                graphics.translate(x, y);
                break;
            case ROTATE_90:
                graphics.translate(x, y + h);
                graphics.rotate(-Math.PI/2);
                break;
            case ROTATE_180:
                graphics.translate(x + w, y + h);
                graphics.rotate(Math.PI);
                break;
            case ROTATE_270:
                graphics.translate(x + w, y);
                graphics.rotate(Math.PI/2);
                break;
        }
    }

    private void transpose(IndiBox indibox) {
        if (rotation == ROTATE_0 || rotation == ROTATE_180)
            return;
        int tmp = indibox.width;
        indibox.width = indibox.height;
        indibox.height = tmp;
    }

    private void transpose(FamBox fambox) {
        if (rotation == ROTATE_0 || rotation == ROTATE_180)
            return;
        int tmp = fambox.width;
        fambox.width = fambox.height;
        fambox.height = tmp;
    }

    public void getIndiBoxSize(IndiBox indibox)
    {
        elements.getIndiBoxSize(indibox);
        transpose(indibox);
    }

    public void getFamBoxSize(FamBox fambox)
    {
        elements.getFamBoxSize(fambox);
        transpose(fambox);
    }
}
