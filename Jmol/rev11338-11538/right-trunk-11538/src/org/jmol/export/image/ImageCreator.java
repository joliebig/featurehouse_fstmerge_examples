

package org.jmol.export.image;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.jmol.api.Interface;
import org.jmol.api.JmolImageCreatorInterface;
import org.jmol.api.JmolPdfCreatorInterface;
import org.jmol.api.JmolViewer;
import org.jmol.util.Base64;
import org.jmol.util.JpegEncoder;
import org.jmol.util.Logger;
import org.jmol.viewer.Viewer;

public class ImageCreator implements JmolImageCreatorInterface {
  
  JmolViewer viewer;
  
  
  public ImageCreator() {
    
  }
  
  public ImageCreator(JmolViewer viewer){
    this.viewer = viewer;
  }
 
  public void setViewer(JmolViewer viewer) {
    this.viewer = viewer;
  }
  
  public String clipImage(String text) {
    String msg;
    try {
      if (text == null) {
        Image image = viewer.getScreenImage();
        ImageSelection.setClipboard(image);
        msg = "OK " + (image.getWidth(null) * image.getHeight(null));
      } else {
        ImageSelection.setClipboard(text);
        msg = "OK " + text.length();
      }
    } catch (Error er) {
      msg = viewer.getErrorMessage();
    } finally {
      if (text == null)
        viewer.releaseScreenImage();
    }
    return msg;
  }

  public String getClipboardText() {
    return ImageSelection.getClipboardText();
  }
  
  public static String getClipboardTextStatic() {
    return ImageSelection.getClipboardText();
  }

  
  public Object createImage(String fileName, String type, Object text_or_bytes, 
                            int quality) {
    
    boolean isBytes = (text_or_bytes instanceof byte[]);
    boolean appendText = (text_or_bytes instanceof Object[]);
    if (appendText)
      text_or_bytes = ((Object[])text_or_bytes)[0];
    String text = (isBytes ? null : (String) text_or_bytes);
    boolean isText = (quality == Integer.MIN_VALUE);
    if ((isText || isBytes) && text_or_bytes == null)
      return "NO DATA";
    FileOutputStream os = null;
    long len = -1;
    try {
      if (isBytes) {
        len = ((byte[]) text_or_bytes).length;
        os = new FileOutputStream(fileName);
        os.write((byte[]) text_or_bytes);
        os.flush();
        os.close();
      } else if (isText) {
        os = new FileOutputStream(fileName);
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw, 8192);
        len = text.length();
        bw.write(text);
        bw.close();
        os = null;
      } else { 
        len = 1;
        Object bytesOrError = getImageBytes(type, quality, fileName, 
            (appendText ? text_or_bytes :  null ), null);
        if (bytesOrError instanceof String)
          return (String) bytesOrError;
        byte[] bytes = (byte[]) bytesOrError;
        if (bytes != null)
          return new String(bytes);
        len = (new File(fileName)).length();
      }
    } catch (IOException exc) {
      if (exc != null) {
        Logger.error("IO Exception", exc);
        return exc.toString();
      }
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          
        }
      }
    }
    return (len < 0 ? "Creation of " + fileName + " failed: " + viewer.getErrorMessageUntranslated() : "OK " + type
        + " " + len + " " + fileName
        + (quality == Integer.MIN_VALUE ? "" : "; quality=" + quality));
  }

  public Object getImageBytes(String type, int quality, String fileName,
                              Object appendText, OutputStream os) throws IOException {
    byte[] bytes = null;
    String errMsg = null;
    boolean isPDF = type.equalsIgnoreCase("PDF");
    boolean isOsTemp = (os == null && fileName != null && !isPDF);
    boolean asBytes = (os == null && fileName == null && !isPDF);
    Image image = viewer.getScreenImage();
    try {
      if (image == null) {
        errMsg = viewer.getErrorMessage();
      }else {
        if (isOsTemp)
            os = new FileOutputStream(fileName);
        if (type.equalsIgnoreCase("JPEG") || type.equalsIgnoreCase("JPG")) {
          if (quality <= 0)
            quality = 75;
          if (asBytes) {
            bytes = JpegEncoder.getBytes(image, quality, Viewer.getJmolVersion());
          } else {
            JpegEncoder.write(image, quality, os, (String) viewer.getProperty("DATA_API","wrappedState", null));
            bytes = null;
          }
        } else if (type.equalsIgnoreCase("JPG64") || type.equalsIgnoreCase("JPEG64")) {
          if (quality <= 0)
            quality = 75;
          bytes = JpegEncoder.getBytes(image, quality, Viewer.getJmolVersion());
          if (asBytes) {
            bytes = Base64.getBytes64(bytes);
          } else {
            Base64.write(bytes, os);
            bytes = null;
          }
        } else if (type.equalsIgnoreCase("PNG")) {
          if (quality < 0)
            quality = 2;
          else if (quality > 9)
            quality = 9;
          if (asBytes) {
            bytes = PngEncoder.getBytes(image, quality);
          } else {
            PngEncoder.write(image, quality, os);
            if (appendText == null)
              os.write(((String) viewer.getProperty("DATA_API","wrappedState", null)).getBytes());
            bytes = null;
          }
        } else if (type.equalsIgnoreCase("PPM")) {
          if (asBytes) {
            bytes = PpmEncoder.getBytes(image);
          } else {
            PpmEncoder.write(image, os);
            bytes = null;
          }
        } else if (type.equalsIgnoreCase("GIF")) {
          if (asBytes) {
            bytes = GifEncoder.getBytes(image);
          } else {
            GifEncoder.write(image, os);
            bytes = null;
          }
        } else if (type.equalsIgnoreCase("PDF")) {
          
          
            JmolPdfCreatorInterface pci = (JmolPdfCreatorInterface) Interface
                .getApplicationInterface("jmolpanel.PdfCreator");
            errMsg = pci.createPdfDocument(fileName, image);
        }
        if (appendText != null && os != null)
          os.write(
              (appendText instanceof byte[] ? 
                  (byte[]) appendText 
                : ((String) appendText).getBytes()));
        if (os != null)
          os.flush();
        if (isOsTemp)
          os.close();
      }
    } catch (IOException e) {
      viewer.releaseScreenImage();
      throw new IOException("" + e);
    } catch (Error er) {
      viewer.releaseScreenImage();
      throw new Error(er);
    }
    viewer.releaseScreenImage();
    if (errMsg != null)
      return errMsg;
    return bytes;
  }
}
