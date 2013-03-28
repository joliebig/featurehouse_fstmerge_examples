

package org.jfree.chart.title;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.util.HorizontalAlignment;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.chart.util.Size2D;
import org.jfree.chart.util.VerticalAlignment;


public class ImageTitle extends Title {

    
    private Image image;

    
    public ImageTitle(Image image) {
        this(image, image.getHeight(null), image.getWidth(null), 
                Title.DEFAULT_POSITION, Title.DEFAULT_HORIZONTAL_ALIGNMENT,
                Title.DEFAULT_VERTICAL_ALIGNMENT, Title.DEFAULT_PADDING);
    }

    
    public ImageTitle(Image image, RectangleEdge position, 
                      HorizontalAlignment horizontalAlignment, 
                      VerticalAlignment verticalAlignment) {

        this(image, image.getHeight(null), image.getWidth(null),
                position, horizontalAlignment, verticalAlignment, 
                Title.DEFAULT_PADDING);
    }

    
    public ImageTitle(Image image, int height, int width, 
                      RectangleEdge position,
                      HorizontalAlignment horizontalAlignment, 
                      VerticalAlignment verticalAlignment,
                      RectangleInsets padding) {

        super(position, horizontalAlignment, verticalAlignment, padding);
        if (image == null) {
            throw new NullPointerException("Null 'image' argument.");
        }
        this.image = image;
        setHeight(height);
        setWidth(width);

    }

    
    public Image getImage() {
        return this.image;
    }

    
    public void setImage(Image image) {
        if (image == null) {
            throw new NullPointerException("Null 'image' argument.");
        }
        this.image = image;
        notifyListeners(new TitleChangeEvent(this));
    }

    
    public void draw(Graphics2D g2, Rectangle2D titleArea) {

        RectangleEdge position = getPosition();
        if (position == RectangleEdge.TOP || position == RectangleEdge.BOTTOM) {
            drawHorizontal(g2, titleArea);
        }
        else if (position == RectangleEdge.LEFT 
                     || position == RectangleEdge.RIGHT) {
            drawVertical(g2, titleArea);
        }
        else {
            throw new RuntimeException("Invalid title position.");
        }
    }

    
    protected Size2D drawHorizontal(Graphics2D g2, Rectangle2D chartArea) {

        double startY = 0.0;
        double topSpace = 0.0;
        double bottomSpace = 0.0;
        double leftSpace = 0.0;
        double rightSpace = 0.0;

        double w = getWidth();
        double h = getHeight();
        RectangleInsets padding = getPadding();
        topSpace = padding.calculateTopOutset(h);
        bottomSpace = padding.calculateBottomOutset(h);
        leftSpace = padding.calculateLeftOutset(w);
        rightSpace = padding.calculateRightOutset(w);

        if (getPosition() == RectangleEdge.TOP) {
            startY = chartArea.getY() + topSpace;
        }
        else {
            startY = chartArea.getY() + chartArea.getHeight() - bottomSpace - h;
        }

        
        HorizontalAlignment horizontalAlignment = getHorizontalAlignment();
        double startX = 0.0;
        if (horizontalAlignment == HorizontalAlignment.CENTER) {
            startX = chartArea.getX() + leftSpace + chartArea.getWidth() / 2.0 
                     - w / 2.0;
        }
        else if (horizontalAlignment == HorizontalAlignment.LEFT) {
            startX = chartArea.getX() + leftSpace;
        }
        else if (horizontalAlignment == HorizontalAlignment.RIGHT) {
            startX = chartArea.getX() + chartArea.getWidth() - rightSpace - w;
        }
        g2.drawImage(this.image, (int) startX, (int) startY, (int) w, (int) h, 
                null);

        return new Size2D(chartArea.getWidth() + leftSpace + rightSpace,
            h + topSpace + bottomSpace);

    }

    
    protected Size2D drawVertical(Graphics2D g2, Rectangle2D chartArea) {

        double startX = 0.0;
        double topSpace = 0.0;
        double bottomSpace = 0.0;
        double leftSpace = 0.0;
        double rightSpace = 0.0;

        double w = getWidth();
        double h = getHeight();
        
        RectangleInsets padding = getPadding();
        if (padding != null) {
            topSpace = padding.calculateTopOutset(h);
            bottomSpace = padding.calculateBottomOutset(h);
            leftSpace = padding.calculateLeftOutset(w);
            rightSpace = padding.calculateRightOutset(w);
        }

        if (getPosition() == RectangleEdge.LEFT) {
            startX = chartArea.getX() + leftSpace;
        }
        else {
            startX = chartArea.getMaxX() - rightSpace - w;
        }

        
        VerticalAlignment alignment = getVerticalAlignment();
        double startY = 0.0;
        if (alignment == VerticalAlignment.CENTER) {
            startY = chartArea.getMinY() + topSpace 
                     + chartArea.getHeight() / 2.0 - h / 2.0;
        }
        else if (alignment == VerticalAlignment.TOP) {
            startY = chartArea.getMinY() + topSpace;
        }
        else if (alignment == VerticalAlignment.BOTTOM) {
            startY = chartArea.getMaxY() - bottomSpace - h;
        }

        g2.drawImage(this.image, (int) startX, (int) startY, (int) w, (int) h, 
                null);

        return new Size2D(chartArea.getWidth() + leftSpace + rightSpace,
            h + topSpace + bottomSpace);

    }
    
    
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        draw(g2, area);
        return null;
    }

}
