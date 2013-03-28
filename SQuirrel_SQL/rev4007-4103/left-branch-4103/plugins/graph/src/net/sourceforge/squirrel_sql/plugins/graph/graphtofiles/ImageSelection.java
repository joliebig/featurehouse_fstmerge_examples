package net.sourceforge.squirrel_sql.plugins.graph.graphtofiles;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.Clipboard;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;



public class ImageSelection extends TransferHandler
   implements Transferable
{

   private static final DataFlavor flavors[] =
      {
         DataFlavor.imageFlavor,
         new DataFlavor("image/jpeg","JPEG image"),
         new DataFlavor("image/gif","GIF Image"),
         new DataFlavor("mage/x-pict", "X Pict")
      };

   private Image image;

   public int getSourceActions(JComponent c)
   {
      return TransferHandler.COPY;
   }

   public boolean canImport(JComponent comp, DataFlavor flavor[])
   {
      if (!(comp instanceof JLabel) || (comp instanceof AbstractButton))
      {
         return false;
      }
      for (int i = 0, n = flavor.length; i < n; i++)
      {
         if (flavor[i].equals(flavors[0]))
         {
            return true;
         }
      }
      return false;
   }

   public Transferable createTransferable(JComponent comp)
   {
      
      image = null;
      Icon icon = null;

      if (comp instanceof JLabel)
      {
         JLabel label = (JLabel) comp;
         icon = label.getIcon();
      }
      else if (comp instanceof AbstractButton)
      {
         AbstractButton button = (AbstractButton) comp;
         icon = button.getIcon();
      }
      if (icon instanceof ImageIcon)
      {
         image = ((ImageIcon) icon).getImage();
         return this;
      }
      return null;
   }

   public boolean importData(JComponent comp, Transferable t)
   {
      ImageIcon icon = null;
      try
      {
         if (t.isDataFlavorSupported(flavors[0]))
         {
            image = (Image) t.getTransferData(flavors[0]);
            icon = new ImageIcon(image);
         }
         if (comp instanceof JLabel)
         {
            JLabel label = (JLabel) comp;
            label.setIcon(icon);
            return true;
         }
         else if (comp instanceof AbstractButton)
         {
            AbstractButton button = (AbstractButton) comp;
            button.setIcon(icon);
            return true;
         }
      }
      catch (UnsupportedFlavorException ignored)
      {
      }
      catch (IOException ignored)
      {
      }
      return false;
   }

   
   public Object getTransferData(DataFlavor flavor)
   {
      if (isDataFlavorSupported(flavor))
      {
         return image;
      }
      return null;
   }

   public DataFlavor[] getTransferDataFlavors()
   {
      return flavors;
   }

   public boolean isDataFlavorSupported(DataFlavor
      flavor)
   {
      return flavor.equals(flavors[0]);
   }
}



































































































