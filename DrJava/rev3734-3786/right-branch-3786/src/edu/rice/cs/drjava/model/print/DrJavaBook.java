

package edu.rice.cs.drjava.model.print;

import java.util.*;
import java.awt.print.*;
import java.awt.*;
import java.awt.font.*;
import java.text.*;


public class DrJavaBook implements Pageable {

  private ArrayList<PagePrinter> _pagePrinters;
  private PageFormat _format;
  private String _fileName;

  public static final Font PRINT_FONT = new Font("Monospaced", Font.PLAIN, 9);
  public static final Font FOOTER_FONT = new Font("Monospaced", Font.PLAIN, 8);
  public static final Font LINE_FONT = new Font("Monospaced", Font.ITALIC, 8);
  public float LINE_NUM_WIDTH;

  private static FontRenderContext DEFAULT_FRC = new FontRenderContext(null, false, true);

  
  public DrJavaBook(String text, String fileName, PageFormat format) {
    _pagePrinters = new ArrayList<PagePrinter>();
    _format = format;
    _fileName = fileName;

    TextLayout textl = new TextLayout("XXX ", LINE_FONT, DEFAULT_FRC);
    LINE_NUM_WIDTH = textl.getAdvance();

    setUpPagePrinters(text);
  }

  
  private void setUpPagePrinters(String text) {
    int linenum = 0;
    int reallinenum = 1;
    String thisText;
    FontRenderContext frc = new FontRenderContext(null, false, true);

    
    TextLayout textl = new TextLayout("X", PRINT_FONT, frc);
    float lineHeight = textl.getLeading() + textl.getAscent();
    int linesPerPage = (int) (_format.getImageableHeight() / lineHeight) - 1;

    HashMap<TextAttribute,Object> map = new HashMap<TextAttribute,Object>(); 

    map.put(TextAttribute.FONT, PRINT_FONT);

    char[] carraigeReturn = {(char) 10};
    String lineSeparator = new String(carraigeReturn);

    try {
      thisText = text.substring(0, text.indexOf(lineSeparator));
      text = text.substring(text.indexOf(lineSeparator) + 1);
    }
    catch (StringIndexOutOfBoundsException e) {
      thisText = text;
      text = "";
    }

    int page = 0;
    PagePrinter thisPagePrinter = new PagePrinter(page, _fileName, this);
    _pagePrinters.add(thisPagePrinter);

    
    while (! (thisText.equals("") && (text.equals("")))) {
      if (thisText.equals(""))
        thisText = " ";

      AttributedCharacterIterator charIterator = (new AttributedString(thisText, map)).getIterator();
      LineBreakMeasurer measurer = new LineBreakMeasurer(charIterator, frc);

      boolean isCarryLine = false;

      
      while (measurer.getPosition() < charIterator.getEndIndex()) {
        TextLayout pageNumber = new TextLayout(" ", LINE_FONT, DEFAULT_FRC);

        if (! isCarryLine)
          pageNumber = new TextLayout("" + reallinenum, LINE_FONT, DEFAULT_FRC);

        
        thisPagePrinter.add(measurer.nextLayout((float) _format.getImageableWidth() - LINE_NUM_WIDTH), pageNumber);

        linenum++;
        
        if (linenum == (linesPerPage * (page+1)))
        {
          page++;
          thisPagePrinter = new PagePrinter(page, _fileName, this);
          _pagePrinters.add(thisPagePrinter);
        }

        isCarryLine = true;
      }

      reallinenum++;

      
      try {
        thisText = text.substring(0, text.indexOf(lineSeparator));
        text = text.substring(text.indexOf(lineSeparator) + 1);
      } catch (StringIndexOutOfBoundsException e) {
        thisText = text;
        text = "";
      }
    }
  }

  
  public int getNumberOfPages() { return _pagePrinters.size(); }

  
  public PageFormat getPageFormat(int pageIndex) { return _format; }

  
  public Printable getPrintable(int pageIndex) { return _pagePrinters.get(pageIndex); }

}
