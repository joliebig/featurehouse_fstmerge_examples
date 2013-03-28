

package tree.graphics;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.fop.svg.PDFDocumentGraphics2D;



public class PdfWriter extends GraphicsFileOutput
{
	
	public void write(OutputStream out, GraphicsRenderer renderer) throws IOException {
        PDFDocumentGraphics2D pdfGraphics = new PDFDocumentGraphics2D(true, out,
            renderer.getImageWidth(), renderer.getImageHeight());
        pdfGraphics.setGraphicContext(new GraphicContext());
        pdfGraphics.fill(new Rectangle(0, 0, 1, 1)); 
        renderer.render(pdfGraphics);
        pdfGraphics.finish();
	}

	public String getFileExtension() {
		return "pdf";
	}
}
