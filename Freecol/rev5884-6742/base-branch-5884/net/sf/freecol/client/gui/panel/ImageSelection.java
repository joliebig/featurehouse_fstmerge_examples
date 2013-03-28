


package net.sf.freecol.client.gui.panel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.logging.Logger;

import javax.swing.JLabel;


public final class ImageSelection implements Transferable {
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ImageSelection.class.getName());

    

    private JLabel label;

    
    public ImageSelection(JLabel label) {
        this.label = label;
    }

    
    public Object getTransferData(DataFlavor flavor) {
        if (isDataFlavorSupported(flavor)) {
            return label;
        }
        return null;
    }

    
    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] flavors = {DefaultTransferHandler.flavor};
        return flavors;
    }

    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DefaultTransferHandler.flavor);
    }
}
