

package org.jfree.chart.ui;

import java.awt.Font;
import java.util.ResourceBundle;

import javax.swing.JTextField;


public class FontDisplayField extends JTextField {

    
    private Font displayFont;

    
    protected static final ResourceBundle localizationResources = 
            ResourceBundle.getBundle("org.jfree.chart.ui.LocalizationBundle");

    
    public FontDisplayField(Font font) {
        super("");
        setDisplayFont(font);
        setEnabled(false);
    }

    
    public Font getDisplayFont() {
        return this.displayFont;
    }

    
    public void setDisplayFont(Font font) {
        this.displayFont = font;
        setText(fontToString(this.displayFont));
    }

    
    private String fontToString(Font font) {
        if (font != null) {
            return font.getFontName() + ", " + font.getSize();
        }
        else {
            return localizationResources.getString("No_Font_Selected");
        }
    }

}
