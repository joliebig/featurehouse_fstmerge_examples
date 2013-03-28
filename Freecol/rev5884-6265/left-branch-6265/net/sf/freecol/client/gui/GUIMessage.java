


package net.sf.freecol.client.gui;

import java.awt.Color;
import java.util.Date;
import java.util.logging.Logger;


public final class GUIMessage {
    
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(GUIMessage.class.getName());
    
    private final String    message;
    private final Color     color;
    private final Date      creationTime;
    
    
    public GUIMessage(String message, Color color) {
        this.message = message;
        this.color = color;
        this.creationTime = new Date();
    }
    
    
    public String getMessage() {
        return message;
    }
    
    
    public Color getColor() {
        return color;
    }
    
    
    public Date getCreationTime() {
        return creationTime;
    }
}
