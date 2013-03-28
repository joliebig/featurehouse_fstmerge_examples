
package swingx.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ObjectTransferable implements Transferable {

    
    public static final DataFlavor serializedFlavor = new DataFlavor(
            java.io.Serializable.class, "Object");

    
    public static final DataFlavor localFlavor;
    
    static {
      try {
          localFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
                  + ";class=java.lang.Object");
      } catch (ClassNotFoundException e) {
          throw new Error(e);
      }
    }

    private List flavors;

    private Object object;

    public ObjectTransferable(Object object) {
        this.object = object;

        flavors = createFlavors();
    }

    protected List createFlavors() {
        List flavors = new ArrayList();

        flavors.add(localFlavor);

        boolean serializable = true;
        if (object.getClass().isArray()) {
            Object[] array = (Object[])object;
            for (int n = 0; n < array.length; n++) {
                serializable = serializable && (array[n] instanceof Serializable);
            }
        } else {
            serializable = object instanceof Serializable;
        }
        if (serializable) {
            flavors.add(serializedFlavor);
        }

        return flavors;
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {
        if (localFlavor.equals(flavor)) {
            return object;
        }
        if (serializedFlavor.equals(flavor)) {
            return object;
        }
        throw new UnsupportedFlavorException(flavor);
    }

    public DataFlavor[] getTransferDataFlavors() {
        return (DataFlavor[]) flavors.toArray(new DataFlavor[flavors.size()]);
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavors.contains(flavor);
    }

    public static Object getObject(Transferable transferable) throws UnsupportedFlavorException, IOException {
        if (transferable.isDataFlavorSupported(localFlavor)) {
          return transferable.getTransferData(localFlavor);
        } else if (transferable.isDataFlavorSupported(serializedFlavor)) {
          return transferable.getTransferData(serializedFlavor);
        }
        throw new IOException();
    }
    
    
    public static Transferable getTigerTransferable(DropTargetDragEvent dtde) {
        try {
            return (Transferable)DropTargetDragEvent.class.getMethod("getTransferable", new Class[0]).invoke(dtde, new Object[0]);
        } catch (Throwable t) {
            return null;
        }
    }
    
}