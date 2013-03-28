


package net.sf.freecol.client.gui.panel;

import java.awt.FlowLayout;
import java.util.logging.Logger;

import javax.swing.JLabel;

import net.sf.freecol.client.gui.Canvas;



public final class StatusPanel extends FreeColPanel {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(StatusPanel.class.getName());

    private final JLabel        statusLabel;

    
    public StatusPanel(Canvas parent) {
        super(parent, new FlowLayout());

        setFocusCycleRoot(false);
        setFocusable(false);
        
        statusLabel = new JLabel();
        add(statusLabel);

        setSize(260, 60);
    }
    
    
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
        setSize(getPreferredSize());
    }
}
