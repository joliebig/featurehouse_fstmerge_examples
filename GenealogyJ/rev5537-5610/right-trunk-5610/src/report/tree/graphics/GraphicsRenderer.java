

package tree.graphics;

import java.awt.Graphics2D;


public interface GraphicsRenderer {

    
    public void render(Graphics2D graphics);

    
    public int getImageWidth();

    
    public int getImageHeight();
}
