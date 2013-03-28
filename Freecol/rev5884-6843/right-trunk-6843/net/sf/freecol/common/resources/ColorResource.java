

package net.sf.freecol.common.resources;

import java.awt.Color;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.logging.Logger;



public class ColorResource extends Resource {

    private static final Logger logger = Logger.getLogger(ColorResource.class.getName());

    public static final String SCHEME = "color:";

    private Color color;


    public ColorResource(Color color) {
        this.color = color;
    }

    
    ColorResource(URI resourceLocator) throws Exception {
        super(resourceLocator);
        String colorName = resourceLocator.getSchemeSpecificPart().substring(SCHEME.length());
        color = getColor(colorName);
    }

    
    public static Color getColor(String colorName) {
        if (colorName.startsWith("0x") || colorName.startsWith("0X")) {
            return new Color(Integer.decode(colorName));
        } else {
            try {
                Field field = Color.class.getField(colorName);
                return (Color) field.get(null);
            } catch(Exception e) {
                
                logger.warning(e.toString());
            }
            return null;
        }
    }
    
    
    public Color getColor() {
        return color;
    }
}
