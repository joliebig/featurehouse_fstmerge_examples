
package genj.io;


import genj.gedcom.Property;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;



public class PropertyTransferable implements Transferable {

  public final static DataFlavor 
    VMLOCAL_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=java.util.List", "GENJ"),
    STRING_FLAVOR = DataFlavor.stringFlavor,
    TEXT_FLAVOR = DataFlavor.getTextPlainUnicodeFlavor();
  
  private final static DataFlavor[] FLAVORS = { VMLOCAL_FLAVOR, STRING_FLAVOR, TEXT_FLAVOR };
  
  
  private List props;
  
  
  private String string;
  
  
  public PropertyTransferable(List properties) {
    
    
    props = properties;
    
    
  }
  
  
  public DataFlavor[] getTransferDataFlavors() {
    return FLAVORS;
  }

  
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    for (int i = 0; i < FLAVORS.length; i++) {
      if (flavor.equals(FLAVORS[i]))
        return true;
    }
    return false;
  }

  
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    
    if (flavor.equals(TEXT_FLAVOR))
      return new StringReader(getStringData());
    
    if (flavor.equals(STRING_FLAVOR))
      return getStringData();
    
    if (flavor.equals(VMLOCAL_FLAVOR))
      return props;
    throw new UnsupportedFlavorException(flavor);
  }
  
  
  public StringSelection getStringTransferable() throws IOException {
    return new StringSelection(getStringData());
  }
  
  
  private String getStringData() throws IOException {
    
    StringWriter out = new StringWriter();
    PropertyWriter writer = new PropertyWriter(out, true);
    for (int i=0;i<props.size();i++)
      writer.write(0, (Property)props.get(i));
    
    return out.toString();
  }
  
} 