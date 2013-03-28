



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.panel.ColopediaPanel;
import net.sf.freecol.client.gui.panel.ColopediaPanel.PanelType;



public class ColopediaAction extends FreeColAction {

    public static final String id = "colopediaAction.";

    public static final int[] mnemonics = new int[] {
        KeyEvent.VK_T,
        KeyEvent.VK_R,
        KeyEvent.VK_U,
        KeyEvent.VK_G,
        KeyEvent.VK_S,
        KeyEvent.VK_B,
        KeyEvent.VK_F,
        KeyEvent.VK_N,
        KeyEvent.VK_A
    };

    private PanelType panelType;
    
    
    ColopediaAction(FreeColClient freeColClient, PanelType panelType) {
        super(freeColClient, id + panelType);
        this.panelType = panelType;
        setMnemonic(mnemonics[panelType.ordinal()]);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Canvas canvas = freeColClient.getCanvas();
        canvas.showPanel(new ColopediaPanel(canvas, panelType, null));
    }
}
