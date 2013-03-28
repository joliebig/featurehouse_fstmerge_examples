

package org.jfree.chart.text;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class TextBlockAnchor implements Serializable {

    
    private static final long serialVersionUID = -3045058380983401544L;

    
    public static final TextBlockAnchor TOP_LEFT
            = new TextBlockAnchor("TextBlockAnchor.TOP_LEFT");

    
    public static final TextBlockAnchor TOP_CENTER = new TextBlockAnchor(
            "TextBlockAnchor.TOP_CENTER");

    
    public static final TextBlockAnchor TOP_RIGHT = new TextBlockAnchor(
           "TextBlockAnchor.TOP_RIGHT");

    
    public static final TextBlockAnchor CENTER_LEFT = new TextBlockAnchor(
            "TextBlockAnchor.CENTER_LEFT");

    
    public static final TextBlockAnchor CENTER = new TextBlockAnchor(
            "TextBlockAnchor.CENTER");

    
    public static final TextBlockAnchor CENTER_RIGHT = new TextBlockAnchor(
            "TextBlockAnchor.CENTER_RIGHT");

    
    public static final TextBlockAnchor BOTTOM_LEFT
        = new TextBlockAnchor("TextBlockAnchor.BOTTOM_LEFT");

    
    public static final TextBlockAnchor BOTTOM_CENTER
            = new TextBlockAnchor("TextBlockAnchor.BOTTOM_CENTER");

    
    public static final TextBlockAnchor BOTTOM_RIGHT
            = new TextBlockAnchor("TextBlockAnchor.BOTTOM_RIGHT");

    
    private String name;

    
    private TextBlockAnchor(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TextBlockAnchor)) {
            return false;
        }

        TextBlockAnchor other = (TextBlockAnchor) obj;
        if (!this.name.equals(other.name)) {
            return false;
        }

        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(TextBlockAnchor.TOP_CENTER)) {
            return TextBlockAnchor.TOP_CENTER;
        }
        else if (this.equals(TextBlockAnchor.TOP_LEFT)) {
            return TextBlockAnchor.TOP_LEFT;
        }
        else if (this.equals(TextBlockAnchor.TOP_RIGHT)) {
            return TextBlockAnchor.TOP_RIGHT;
        }
        else if (this.equals(TextBlockAnchor.CENTER)) {
            return TextBlockAnchor.CENTER;
        }
        else if (this.equals(TextBlockAnchor.CENTER_LEFT)) {
            return TextBlockAnchor.CENTER_LEFT;
        }
        else if (this.equals(TextBlockAnchor.CENTER_RIGHT)) {
            return TextBlockAnchor.CENTER_RIGHT;
        }
        else if (this.equals(TextBlockAnchor.BOTTOM_CENTER)) {
            return TextBlockAnchor.BOTTOM_CENTER;
        }
        else if (this.equals(TextBlockAnchor.BOTTOM_LEFT)) {
            return TextBlockAnchor.BOTTOM_LEFT;
        }
        else if (this.equals(TextBlockAnchor.BOTTOM_RIGHT)) {
            return TextBlockAnchor.BOTTOM_RIGHT;
        }
        return null;
    }

}
