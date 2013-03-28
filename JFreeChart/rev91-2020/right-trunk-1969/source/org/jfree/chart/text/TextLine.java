

package org.jfree.chart.text;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.util.Size2D;


public class TextLine implements Serializable {

    
    private static final long serialVersionUID = 7100085690160465444L;

    
    private List fragments;

    
    public TextLine() {
        this.fragments = new java.util.ArrayList();
    }

    
    public TextLine(String text) {
        this(text, TextFragment.DEFAULT_FONT);
    }

    
    public TextLine(String text, Font font) {
        this.fragments = new java.util.ArrayList();
        TextFragment fragment = new TextFragment(text, font);
        this.fragments.add(fragment);
    }

    
    public TextLine(String text, Font font, Paint paint) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.fragments = new java.util.ArrayList();
        TextFragment fragment = new TextFragment(text, font, paint);
        this.fragments.add(fragment);
    }

    
    public void addFragment(TextFragment fragment) {
        this.fragments.add(fragment);
    }

    
    public void removeFragment(TextFragment fragment) {
        this.fragments.remove(fragment);
    }

    
    public void draw(Graphics2D g2, float anchorX, float anchorY,
                     TextAnchor anchor, float rotateX, float rotateY,
                     double angle) {

        float x = anchorX;
        float yOffset = calculateBaselineOffset(g2, anchor);
        Iterator iterator = this.fragments.iterator();
        while (iterator.hasNext()) {
            TextFragment fragment = (TextFragment) iterator.next();
            Size2D d = fragment.calculateDimensions(g2);
            fragment.draw(g2, x, anchorY + yOffset, TextAnchor.BASELINE_LEFT,
                    rotateX, rotateY, angle);
            x = x + (float) d.getWidth();
        }

    }

    
    public Size2D calculateDimensions(Graphics2D g2) {
        double width = 0.0;
        double height = 0.0;
        Iterator iterator = this.fragments.iterator();
        while (iterator.hasNext()) {
            TextFragment fragment = (TextFragment) iterator.next();
            Size2D dimension = fragment.calculateDimensions(g2);
            width = width + dimension.getWidth();
            height = Math.max(height, dimension.getHeight());
        }
        return new Size2D(width, height);
    }

    
    public TextFragment getFirstTextFragment() {
        TextFragment result = null;
        if (this.fragments.size() > 0) {
            result = (TextFragment) this.fragments.get(0);
        }
        return result;
    }

    
    public TextFragment getLastTextFragment() {
        TextFragment result = null;
        if (this.fragments.size() > 0) {
            result = (TextFragment) this.fragments.get(this.fragments.size()
                    - 1);
        }
        return result;
    }

    
    private float calculateBaselineOffset(Graphics2D g2,
                                          TextAnchor anchor) {
        float result = 0.0f;
        Iterator iterator = this.fragments.iterator();
        while (iterator.hasNext()) {
            TextFragment fragment = (TextFragment) iterator.next();
            result = Math.max(result,
                    fragment.calculateBaselineOffset(g2, anchor));
        }
        return result;
    }

    
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof TextLine) {
            TextLine line = (TextLine) obj;
            return this.fragments.equals(line.fragments);
        }
        return false;
    }

    
    public int hashCode() {
        return (this.fragments != null ? this.fragments.hashCode() : 0);
    }

}
