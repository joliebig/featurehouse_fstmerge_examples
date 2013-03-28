


package net.sf.freecol.client.gui.panel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.sf.freecol.client.gui.Canvas;



public final class ChatPanel extends FreeColPanel implements ActionListener {

    private static final Logger logger = Logger.getLogger(ChatPanel.class.getName());

    public static final int    CHAT = 1;

    private final JTextField        field;

    
    public ChatPanel(Canvas parent) {
        super(parent);

        JLabel label = new JLabel("Message: ");

        field = new JTextField("", 40);

        setLayout(new BorderLayout(10, 10));

        field.setActionCommand(String.valueOf(CHAT));

        field.addActionListener(this);

        add(label);
        add(field);

        
        label.setFocusable(false);
        field.setFocusable(true);

        setSize(getPreferredSize());
    }

    
    
    

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            switch (Integer.valueOf(command).intValue()) {
                case CHAT:
                    String message = getChatText();
                    getController().sendChat(message);
                    getCanvas().displayChatMessage(message);
                    getCanvas().remove(this);
                    break;
                default:
                    logger.warning("Invalid Actioncommand: invalid number.");
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid Actioncommand: not a number.");
        }
    }


    
    public String getChatText() {
        String message = field.getText();
        field.setText("");
        return message;
    }


    
    public void requestFocus() {
        field.requestFocus();
    }
}
