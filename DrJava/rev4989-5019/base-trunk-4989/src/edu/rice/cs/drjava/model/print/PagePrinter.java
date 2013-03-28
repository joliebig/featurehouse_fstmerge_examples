

package edu.rice.cs.drjava.model.print;

import java.util.*;
import java.awt.print.*;
import java.awt.*;
import java.awt.font.*;


public class PagePrinter implements Printable {

  
  private ArrayList<TextLayout> _textLayouts;
  private ArrayList<TextLayout> _lineNumbers;
  private String _fileName;
  private DrJavaBook _parent;

  
  public PagePrinter(int page, String fileName, DrJavaBook parent) {
    
    _textLayouts = new ArrayList<TextLayout>();
    _lineNumbers = new ArrayList<TextLayout>();
    _fileName = fileName;
    _parent = parent;
  }

  
  public void add(TextLayout text, TextLayout lineNumber) {
    _textLayouts.add(text);
    _lineNumbers.add(lineNumber);
  }

  
  public int print(Graphics graphics, PageFormat format, int pageIndex) {
    
    Graphics2D g2d = (Graphics2D) graphics;
    g2d.translate(format.getImageableX(), format.getImageableY());
    g2d.setPaint(Color.black);

    float y = 0;

    
    for (int i=0; i<_textLayouts.size(); i++) {
      TextLayout layout = _textLayouts.get(i);
      TextLayout lineNumber = _lineNumbers.get(i);

      y += layout.getAscent();
      lineNumber.draw(g2d, 0, y);
      layout.draw(g2d, _parent.LINE_NUM_WIDTH, y);
      y += layout.getLeading();
    }

    
    printFooter(g2d, format, pageIndex + 1);

    return PAGE_EXISTS;
  }

  
  private void printFooter(Graphics2D g2d, PageFormat format, int page) {
    TextLayout footerFile = new TextLayout(_fileName, DrJavaBook.FOOTER_FONT, g2d.getFontRenderContext());
    float footerPlace = (float) (format.getImageableWidth() - footerFile.getAdvance()) / 2;

    footerFile.draw(g2d, footerPlace, (float) format.getImageableHeight() - footerFile.getDescent());

    TextLayout footerPageNo = new TextLayout(page + "", DrJavaBook.FOOTER_FONT, g2d.getFontRenderContext());
    footerPageNo.draw(g2d,
                      (float) format.getImageableWidth() - footerPageNo.getAdvance(),
                      (float) format.getImageableHeight() - footerPageNo.getDescent());
  }

}
