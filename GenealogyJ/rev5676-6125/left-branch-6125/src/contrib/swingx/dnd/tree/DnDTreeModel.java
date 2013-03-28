
package swingx.dnd.tree;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;

import javax.swing.tree.*;


public interface DnDTreeModel extends TreeModel {
    
    
    public static final int COPY = DnDConstants.ACTION_COPY;

    
    public static final int MOVE = DnDConstants.ACTION_MOVE;

    
    public static final int LINK = DnDConstants.ACTION_LINK;

    
    public Transferable createTransferable(Object[] children);

    
    public int getDragActions(Transferable transferable);
    
    
    public int getDropActions(Transferable transferable, Object parent, int index);
    
    
    public void drag(Transferable transferable, int action) throws UnsupportedFlavorException, IOException;
    
    
    public void drop(Transferable transferable, Object parent, int index, int action) throws UnsupportedFlavorException, IOException;
    
    
    public void releaseTransferable(Transferable transferable);
}