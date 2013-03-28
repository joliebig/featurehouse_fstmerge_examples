

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;

import net.sf.freecol.client.gui.Canvas;


public final class ErrorPanel extends FreeColDialog<Boolean> implements ActionListener {

    private static final Logger logger = Logger.getLogger(ErrorPanel.class.getName());

    private static final int lineWidth = 320;

    private LinkedList<JLabel> errorLabels; 

    
    public ErrorPanel(Canvas parent) {
        super(parent);

        setLayout(null);

        errorLabels = null;

       
    }

    
    public void initialize(String message) {
        LinkedList<String> lines = new LinkedList<String>();
        while (getFontMetrics(getFont()).getStringBounds(message, getGraphics()).getWidth() + 40 > lineWidth) {
            int spaceIndex = message.indexOf(' ');
            int previousIndex = -1;
            while (getFontMetrics(getFont()).getStringBounds(message.substring(0, spaceIndex), getGraphics())
                    .getWidth() + 40 <= lineWidth) {
                previousIndex = spaceIndex;
                if ((spaceIndex + 1) >= message.length()) {
                    spaceIndex = 0;
                    break;
                }
                spaceIndex = message.indexOf(' ', spaceIndex + 1);
                if (spaceIndex == -1) {
                    spaceIndex = 0;
                    break;
                }
            }

            if ((previousIndex >= 0) && (spaceIndex >= 0)) {
                lines.add(message.substring(0, previousIndex));
                if (previousIndex + 1 < message.length()) {
                    message = message.substring(previousIndex + 1);
                } else {
                    break;
                }
            } else {
                lines.add(message);
                lines.add("Internal error in ErrorPanel");
                break;
            }
        }

        if (message.trim().length() > 0) {
            lines.add(message);
        }

        if (errorLabels != null) {
            for (int i = 0; i < errorLabels.size(); i++) {
                remove(errorLabels.get(i));
            }

            errorLabels.clear();
        } else {
            errorLabels = new LinkedList<JLabel>();
        }

        for (int i = 0; i < lines.size(); i++) {
            JLabel label = new JLabel(lines.get(i));
            label.setSize(lineWidth, 20);
            label.setLocation(10, 2 + i * 20);
            add(label);
            errorLabels.add(label);
        }

        okButton.setLocation(130, 25 + (lines.size() - 1) * 20);
        Rectangle2D rect = getFontMetrics(getFont()).getStringBounds(okButton.getText(), getGraphics());
        okButton.setSize((int)rect.getWidth() + 40, 25);
        
        add(okButton);

        setSize(340, 50 + (lines.size() - 1) * 20);
    }

    
    @Override
    public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        setResponse(true);
    }
}
