
package org.openscience.jmol.app.jmolpanel;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetDragEvent;

import  java.awt.datatransfer.DataFlavor;
import  java.awt.datatransfer.Transferable;
import  java.awt.datatransfer.UnsupportedFlavorException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.IOException;

import java.util.List;

import org.jmol.util.Logger;


public class FileDropper implements DropTargetListener {
  private String fd_oldFileName;
  private PropertyChangeSupport fd_propSupport;

  static public final String FD_PROPERTY_FILENAME = "filename";
  static public final String FD_PROPERTY_INLINE   = "inline";

  public FileDropper() {
    fd_oldFileName = "";
    fd_propSupport = new PropertyChangeSupport(this);
  }

  public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
    fd_propSupport.addPropertyChangeListener(l);
  }

  public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
    fd_propSupport.removePropertyChangeListener(l);
  }

  public void dragOver(DropTargetDragEvent dtde) {
    Logger.debug("DropOver detected...");
		}

  public void dragEnter(DropTargetDragEvent dtde) {
    Logger.debug("DropEnter detected...");
    dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
  }

  public void dragExit(DropTargetEvent dtde) {
    Logger.debug("DropExit detected...");
  }

  public void dropActionChanged(DropTargetDragEvent dtde) {		}

  public void drop (DropTargetDropEvent dtde) {
    Logger.debug("Drop detected...");
    Transferable t = dtde.getTransferable();
    if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
      Object o = null;

      try {
        o = t.getTransferData(DataFlavor.javaFileListFlavor);
      } catch (UnsupportedFlavorException ufe) {
        Logger.error(null, ufe);
      } catch (IOException ioe) {
        Logger.error(null, ioe);
      }

      
      if ((o != null) && (o instanceof List)) {
        List  fileList = (List) o;
        final int length = fileList.size ();

        for (int i = 0; i < length; ++ i) {
          File f = (File) fileList.get(i);
          PropertyChangeEvent pce = new PropertyChangeEvent(
              this, FD_PROPERTY_FILENAME, fd_oldFileName, f.getAbsolutePath());
          fd_propSupport.firePropertyChange(pce);
        }

        dtde.getDropTargetContext().dropComplete(true);
      }
    } else {
      Logger.debug("browsing supported flavours to find something useful...");
      DataFlavor [] df = t.getTransferDataFlavors ();

      if ((df != null) && (df.length > 0)) {
        for (int i = 0; i < df.length; ++ i) {
          DataFlavor flavor = df[i];
          if (Logger.debugging) {
            Logger.debug("df " + i + " flavor " + flavor);
            Logger.debug("  class: " + flavor.getRepresentationClass().getName());
            Logger.debug("  mime : " + flavor.getMimeType());
          }

          if (flavor.getMimeType().startsWith("text/uri-list") &&
              flavor.getRepresentationClass().getName().equals("java.lang.String")) {

            

            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            Object o = null;

            try {
              o = t.getTransferData(flavor);
            } catch (UnsupportedFlavorException ufe) {
              Logger.error(null, ufe);
            } catch (IOException ioe) {
              Logger.error(null, ioe);
            }

            if ((o != null) && (o instanceof String)) {
              if (Logger.debugging) {
                Logger.debug("  String: " + o.toString());
              }

              PropertyChangeEvent pce = new PropertyChangeEvent(
                  this, FD_PROPERTY_FILENAME, fd_oldFileName, o.toString());
              fd_propSupport.firePropertyChange(pce);
              dtde.getDropTargetContext().dropComplete(true);
            }
            return;
          } else if (flavor.getMimeType().equals("application/x-java-serialized-object; class=java.lang.String")) {

            

            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            Object o = null;

            try {
              o = t.getTransferData(df[i]);
            } catch (UnsupportedFlavorException ufe) {
              Logger.error(null, ufe);
            } catch (IOException ioe) {
              Logger.error(null, ioe);
            }

            if ((o != null) && (o instanceof String)) {
              String content = (String)o;
              if (Logger.debugging) {
                Logger.debug("  String: " + content);
              }
              if (content.startsWith("file:/")) {
                PropertyChangeEvent pce = new PropertyChangeEvent(
                    this, FD_PROPERTY_FILENAME, fd_oldFileName, content);
                fd_propSupport.firePropertyChange(pce);
              } else {
                PropertyChangeEvent pce = new PropertyChangeEvent(
                    this, FD_PROPERTY_INLINE, fd_oldFileName, content);
                fd_propSupport.firePropertyChange(pce);
              }
              dtde.getDropTargetContext().dropComplete(true);
            }
            return;
          }
        }
      }

      dtde.rejectDrop();
    }
	}
}
