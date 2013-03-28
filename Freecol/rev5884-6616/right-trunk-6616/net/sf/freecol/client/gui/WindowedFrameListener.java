


package net.sf.freecol.client.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


public final class WindowedFrameListener implements WindowListener {

    private final Canvas parent;
    
    
    public WindowedFrameListener(Canvas canvas) {
        parent = canvas;
    }
    
    
    public void windowActivated(WindowEvent event) {
    }
    
    
    public void windowClosed(WindowEvent event) {
    }
    
    
    public void windowClosing(WindowEvent event) {
        parent.quit();
    }
    
    
    public void windowDeactivated(WindowEvent event) {
    }
    
    
    public void windowDeiconified(WindowEvent event) {
    }
    
    
    public void windowIconified(WindowEvent event) {
    }
    
    
    public void windowOpened(WindowEvent event) {
    }
}
