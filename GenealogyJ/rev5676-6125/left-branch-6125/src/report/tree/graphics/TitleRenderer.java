

package tree.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import tree.output.GraphicsTreeElements;


public class TitleRenderer implements GraphicsRenderer
{
    public static final int VERTICAL_MARGIN = 10;
    
    private GraphicsRenderer renderer;

    
    public String title = "";

    
    public int title_height = 0;

    
    public TitleRenderer(GraphicsRenderer renderer)
    {
        this.renderer = renderer;
    }

    public int getImageHeight()
    {
        if (title.equals(""))
            return renderer.getImageHeight();
        return renderer.getImageHeight() + getTitleHeight() + VERTICAL_MARGIN;
    }

    private int getTitleHeight()
    {
        if (title_height > 0)
            return title_height;
        return (renderer.getImageHeight() + renderer.getImageWidth()) / 40; 
    }

    public int getImageWidth()
    {
        return renderer.getImageWidth();
    }

    
    public void render(Graphics2D graphics)
    {
        if (!title.equals(""))
        {
            graphics.setBackground(Color.WHITE);
            graphics.clearRect(0, 0, getImageWidth(), getImageHeight());

            int height = getTitleHeight();
            graphics.setColor(Color.BLACK);
            graphics.setFont(new Font("verdana", Font.BOLD, height));
            GraphicsTreeElements.centerString(graphics, title, getImageWidth() / 2, height * 3/4 + VERTICAL_MARGIN);

            graphics.translate(0, height + VERTICAL_MARGIN); 
        }
        renderer.render(graphics);
    }
}
