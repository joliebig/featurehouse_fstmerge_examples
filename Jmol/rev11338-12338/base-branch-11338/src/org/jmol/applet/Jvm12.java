
package org.jmol.applet;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jmol.api.*;

 public class Jvm12 {

  protected JmolViewer viewer;
  public Component awtComponent;
  
  protected String appletContext;

  Jvm12(Component awtComponent, JmolViewer viewer, String appletContext) {
    this.awtComponent = awtComponent;
    this.viewer = viewer;
    this.appletContext = appletContext;
    try {
      UIManager
          .setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception exc) {
      System.err.println("Error loading L&F: " + exc);
    }
  }

  private final Rectangle rectClip = new Rectangle();
  private final Dimension dimSize = new Dimension();

  Rectangle getClipBounds(Graphics g) {
    return g.getClipBounds(rectClip);
  }

  public Dimension getSize() {
    return awtComponent.getSize(dimSize);
  }

  final protected static String[] imageChoices = { "JPEG", "PNG", "GIF", "PPM" };
  final protected static String[] imageExtensions = { "jpg", "png", "gif", "ppm" };

  static JmolDialogInterface newDialog(boolean forceNewTranslation) {
    JmolDialogInterface sd = (JmolDialogInterface) Interface
        .getOptionInterface("export.dialog.Dialog");
    sd.setupUI(forceNewTranslation);
    return sd;
  }
  
  String inputFileName;
  String outputFileName;
  String dialogType;
  
  public String dialogAsk(String type, String fileName) {
    inputFileName = fileName;
    dialogType = type;
    
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          if (dialogType.equals("load")) {
            outputFileName = newDialog(false).getOpenFileNameFromDialog(
                appletContext, viewer, inputFileName, null, null, false);
            return;
          }
          JmolDialogInterface sd = newDialog(false);
          if (dialogType.equals("save")) {
            outputFileName = sd.getSaveFileNameFromDialog(viewer,
                inputFileName, null);
            return;
          }
          if (dialogType.startsWith("saveImage")) {
            outputFileName = sd.getImageFileNameFromDialog(viewer,
                inputFileName, imageType, imageChoices, imageExtensions,
                qualityJPG, qualityPNG);
            qualityJPG = sd.getQuality("JPG");
            qualityPNG = sd.getQuality("PNG");
            String sType = sd.getType();
            if (sType != null)
              imageType = sType;
            int iQuality = sd.getQuality(sType);
            if (iQuality >= 0)
              imageQuality = iQuality;
            return;
          }
          outputFileName = null;
        }
      });
    } catch (InterruptedException e) {
      System.out.println(e.getMessage());
    } catch (InvocationTargetException e) {
      System.out.println(e.getMessage());
    }
    return outputFileName;
  }

  int qualityJPG = -1;
  int qualityPNG = -1;
  String imageType;
  int imageQuality;

  
  String createImage(String fileName, String type, Object text_or_bytes,
                     int quality) {
    if (quality == Integer.MIN_VALUE) {
      
      fileName = dialogAsk("save", fileName);
    } else {
      imageType = type.toUpperCase();
      imageQuality = quality;
      fileName = dialogAsk("saveImage+" + type, fileName);
      quality = imageQuality;
      type = imageType;
    }
    if (fileName == null)
      return null;
    JmolImageCreatorInterface c = (JmolImageCreatorInterface) Interface
        .getOptionInterface("export.image.ImageCreator");
    c.setViewer(viewer);
    return (String) c.createImage(fileName, type, text_or_bytes, quality);
  }

  String getClipboardText() {
    JmolImageCreatorInterface c = (JmolImageCreatorInterface) Interface
        .getOptionInterface("export.image.ImageCreator");
    c.setViewer(viewer);
    return c.getClipboardText();
  }
}
