


package net.sf.freecol.client.gui.panel;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.TransferHandler;


public final class DropListener extends MouseAdapter {
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(DropListener.class.getName());

    
    public void mouseReleased(MouseEvent e) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipData = clipboard.getContents(clipboard);
        if (clipData != null) {
            if (clipData.isDataFlavorSupported(DefaultTransferHandler.flavor)) {
                JComponent comp = (JComponent)e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.importData(comp, clipData);
            }
        }
    }
}
