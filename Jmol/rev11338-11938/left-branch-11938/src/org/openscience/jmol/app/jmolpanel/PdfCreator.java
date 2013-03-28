
package org.openscience.jmol.app.jmolpanel;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jmol.api.JmolPdfCreatorInterface;

public class PdfCreator implements JmolPdfCreatorInterface {
  
  public PdfCreator() {
   
  }
  
  public String createPdfDocument(String fileName, Image image) {
    Document document = new Document();
    File file = null;
    try {
      int w = image.getWidth(null);
      int h = image.getHeight(null);
      file = new File(fileName);
      PdfWriter writer = PdfWriter.getInstance(document,
          new FileOutputStream(file));
      document.open();
      PdfContentByte cb = writer.getDirectContent();
      PdfTemplate tp = cb.createTemplate(w, h);
      Graphics2D g2 = tp.createGraphics(w, h);
      g2.setStroke(new BasicStroke(0.1f));
      tp.setWidth(w);
      tp.setHeight(h);
      g2.drawImage(image, 0, 0, w, h, 0, 0, w, h, null);
      g2.dispose();
      cb.addTemplate(tp, 72, 720 - h);
    } catch (DocumentException de) {
      return de.getMessage();
    } catch (IOException ioe) {
      return ioe.getMessage();
    }
    document.close();
    return null;
  }

}
