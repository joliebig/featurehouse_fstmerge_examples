

package tree.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;



public class PngWriter extends GraphicsFileOutput
{
	
	public void write(OutputStream out, GraphicsRenderer renderer) throws IOException
    {
        BufferedImage image = new BufferedImage(renderer.getImageWidth(), renderer.getImageHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        renderer.render(graphics);

        
        ImageIO.write(image, "png", out);
	}

	public String getFileExtension() {
		return "png";
	}
}
