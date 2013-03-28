

package org.jmol.export.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.jmol.util.Logger;


public class ImageSelection implements Transferable {

  
  private Image image;
  private String text;
  
  
  
  public static void setClipboard(Image image) {
    ImageSelection sel = new ImageSelection(image);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
  }

  
  public static void setClipboard(String text) {
    ImageSelection sel = new ImageSelection(text);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
  }
  

  
  public ImageSelection(Image image) {
    this.image = image;
  }
  
  
  public ImageSelection(String text) {
    this.text = text;
  }

  
  public DataFlavor[] getTransferDataFlavors() {
    return (text == null ? 
        new DataFlavor[]{ DataFlavor.imageFlavor }
      : new DataFlavor[]{ DataFlavor.stringFlavor });
  }

  
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return DataFlavor.imageFlavor.equals(flavor);
  }

  
  public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException, IOException {
    if (DataFlavor.imageFlavor.equals(flavor)) {
      return image;
    } else     if (DataFlavor.stringFlavor.equals(flavor)) {
      return text;
    }
    throw new UnsupportedFlavorException(flavor);
  }

  
  public static String getClipboardText() {
    String result = null;
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable contents = clipboard.getContents(null);
    boolean hasTransferableText = (contents != null)
        && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    if (hasTransferableText) {
      try {
        result = (String) contents.getTransferData(DataFlavor.stringFlavor);
      } catch (UnsupportedFlavorException ex) {
        
        Logger.error("Clipboard problem", ex);
        ex.printStackTrace();
      } catch (IOException ex) {
        Logger.error("Clipboard problem", ex);
        ex.printStackTrace();
      }
    }
    return result;
  }
}
