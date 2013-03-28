

package org.jfree.chart.panel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class AbstractMouseHandler implements MouseListener,
        MouseMotionListener {

    
    private int modifier;

    
    public AbstractMouseHandler() {
        this.modifier = 0;
    }

    
    public int getModifier() {
        return this.modifier;
    }

    
    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    
    public void mousePressed(MouseEvent e) {
        
    }

    
    public void mouseReleased(MouseEvent e) {
        
    }

    
    public void mouseClicked(MouseEvent e) {
        
    }

    
    public void mouseEntered(MouseEvent e) {
        
    }

    
    public void mouseMoved(MouseEvent e) {
        
    }

    
    public void mouseExited(MouseEvent e) {
        
    }

    
    public void mouseDragged(MouseEvent e) {
        
    }

}
