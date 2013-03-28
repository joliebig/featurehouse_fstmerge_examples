

package org.jfree.chart.util;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;


public class ApplicationFrame extends JFrame implements WindowListener {

    
    public ApplicationFrame(String title) {
        super(title);
        addWindowListener(this);
    }

    
    public void windowClosing(WindowEvent event) {
        if (event.getWindow() == this) {
            dispose();
            System.exit(0);
        }
    }

    
    public void windowClosed(WindowEvent event) {
        
    }

    
    public void windowActivated(WindowEvent event) {
        
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
