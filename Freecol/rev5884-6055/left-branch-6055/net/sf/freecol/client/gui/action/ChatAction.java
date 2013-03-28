

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class ChatAction extends FreeColAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ChatAction.class.getName());




    public static final String id = "chatAction";


    
    ChatAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.chat", null, KeyStroke.getKeyStroke('T', Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
    }

    
    protected boolean shouldBeEnabled() {
        return super.shouldBeEnabled()
        		&& !getFreeColClient().isSingleplayer()
                && getFreeColClient().getCanvas() != null
                && (!getFreeColClient().getCanvas().isShowingSubPanel() || getFreeColClient().getGame() != null
                        && getFreeColClient().getGame().getCurrentPlayer() != getFreeColClient().getMyPlayer());
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getCanvas().showChatPanel();
    }
}
