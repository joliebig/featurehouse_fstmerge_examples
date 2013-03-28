
package genj.report;

import genj.util.swing.ImageIcon;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.Icon;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.HTML;
import javax.swing.text.html.ImageView;


public class ClassLoaderImageView extends ImageView {

    
    private Image image;

    
    private int width;

    
    private int height;

    
    public ClassLoaderImageView(Element elem, Class from) {
        super(elem);
        String src = (String) getElement().getAttributes().getAttribute(
                HTML.Attribute.SRC);
        try {
            image = new ImageIcon(from, src).getImage();
            width = image.getWidth(null);
            height = image.getHeight(null);
        } catch (Exception e) {
            image = null;
            Icon icon = getNoImageIcon();
            if (icon != null) {
                width = getNoImageIcon().getIconWidth();
                height = getNoImageIcon().getIconHeight();
            }
        }
    }

    
    public void paint(Graphics g, Shape a) {

        Rectangle rect = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();
        Rectangle clip = g.getClipBounds();

        if (clip != null)
            g.clipRect(rect.x, rect.y, rect.width, rect.height);

        if (image != null)
            g.drawImage(image, rect.x, rect.y, width, height, null);
        else {
            Icon icon = getNoImageIcon();
            if (icon != null)
                icon.paintIcon(getContainer(), g, rect.x, rect.y);
        }

        if (clip != null)
            g.setClip(clip.x, clip.y, clip.width, clip.height);
    }

    
    public float getPreferredSpan(int axis) {
        if (axis == View.X_AXIS)
            return width;
        return height;
    }

    
    public void setSize(float width, float height) {
    }
}
