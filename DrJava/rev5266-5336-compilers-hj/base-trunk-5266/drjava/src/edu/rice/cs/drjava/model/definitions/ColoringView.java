

package edu.rice.cs.drjava.model.definitions;

import javax.swing.text.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import java.util.ArrayList;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.EditDocumentInterface;


public class ColoringView extends PlainView implements OptionConstants {
  
  public static Color COMMENTED_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_COMMENT_COLOR);
  public static Color DOUBLE_QUOTED_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_DOUBLE_QUOTED_COLOR);
  public static Color SINGLE_QUOTED_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_SINGLE_QUOTED_COLOR);
  public static Color NORMAL_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_NORMAL_COLOR);
  public static Color KEYWORD_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_KEYWORD_COLOR);
  public static Color NUMBER_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_NUMBER_COLOR);
  public static Color TYPE_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_TYPE_COLOR);
  public static Font MAIN_FONT = DrJava.getConfig().getSetting(FONT_MAIN);
  
  
  public static Color INTERACTIONS_SYSTEM_ERR_COLOR = DrJava.getConfig().getSetting(SYSTEM_ERR_COLOR);
  public static Color INTERACTIONS_SYSTEM_IN_COLOR = DrJava.getConfig().getSetting(SYSTEM_IN_COLOR);
  public static Color INTERACTIONS_SYSTEM_OUT_COLOR = DrJava.getConfig().getSetting(SYSTEM_OUT_COLOR);
  
  public static Color ERROR_COLOR = DrJava.getConfig().getSetting(INTERACTIONS_ERROR_COLOR);
  public static Color DEBUGGER_COLOR = DrJava.getConfig().getSetting(DEBUG_MESSAGE_COLOR);
  
  
  public ColoringView(Element elem) {
    super(elem);
    
    
    final ColorOptionListener col = new ColorOptionListener();
    final FontOptionListener fol = new FontOptionListener();
    
    Document doc = getDocument();
    if (doc instanceof AbstractDJDocument) {
      
      
      DrJava.getConfig().addOptionListener( OptionConstants.DEFINITIONS_COMMENT_COLOR, col);
      DrJava.getConfig().addOptionListener( OptionConstants.DEFINITIONS_DOUBLE_QUOTED_COLOR, col);
      DrJava.getConfig().addOptionListener( OptionConstants.DEFINITIONS_SINGLE_QUOTED_COLOR, col);
      DrJava.getConfig().addOptionListener( OptionConstants.DEFINITIONS_NORMAL_COLOR, col);
      DrJava.getConfig().addOptionListener( OptionConstants.DEFINITIONS_KEYWORD_COLOR, col);
      DrJava.getConfig().addOptionListener( OptionConstants.DEFINITIONS_NUMBER_COLOR, col);
      DrJava.getConfig().addOptionListener( OptionConstants.DEFINITIONS_TYPE_COLOR, col);
      DrJava.getConfig().addOptionListener( OptionConstants.FONT_MAIN, fol);
      
      DrJava.getConfig().addOptionListener( OptionConstants.SYSTEM_ERR_COLOR, col);
      DrJava.getConfig().addOptionListener( OptionConstants.SYSTEM_IN_COLOR, col);
      DrJava.getConfig().addOptionListener( OptionConstants.SYSTEM_OUT_COLOR, col);
      DrJava.getConfig().addOptionListener( OptionConstants.INTERACTIONS_ERROR_COLOR, col);
      DrJava.getConfig().addOptionListener( OptionConstants.DEBUG_MESSAGE_COLOR, col);
      
    }
    
    if (doc instanceof DefinitionsDocument) {
      
      ((DefinitionsDocument)doc).addDocumentClosedListener(new DocumentClosedListener() {
        public void close() {
          DrJava.getConfig().removeOptionListener( OptionConstants.DEFINITIONS_COMMENT_COLOR, col);
          DrJava.getConfig().removeOptionListener( OptionConstants.DEFINITIONS_DOUBLE_QUOTED_COLOR, col);
          DrJava.getConfig().removeOptionListener( OptionConstants.DEFINITIONS_SINGLE_QUOTED_COLOR, col);
          DrJava.getConfig().removeOptionListener( OptionConstants.DEFINITIONS_NORMAL_COLOR, col);
          DrJava.getConfig().removeOptionListener( OptionConstants.DEFINITIONS_KEYWORD_COLOR, col);
          DrJava.getConfig().removeOptionListener( OptionConstants.DEFINITIONS_NUMBER_COLOR, col);
          DrJava.getConfig().removeOptionListener( OptionConstants.DEFINITIONS_TYPE_COLOR, col);
          DrJava.getConfig().removeOptionListener( OptionConstants.FONT_MAIN, fol);
          DrJava.getConfig().removeOptionListener( OptionConstants.SYSTEM_ERR_COLOR, col);
          DrJava.getConfig().removeOptionListener( OptionConstants.SYSTEM_IN_COLOR, col);
          DrJava.getConfig().removeOptionListener( OptionConstants.SYSTEM_OUT_COLOR, col);
          DrJava.getConfig().removeOptionListener( OptionConstants.INTERACTIONS_ERROR_COLOR, col);
          DrJava.getConfig().removeOptionListener( OptionConstants.DEBUG_MESSAGE_COLOR, col); 
        }
      });
    }
  }
  
  
  protected int drawUnselectedText(Graphics g, int x, int y, int start, int end) throws BadLocationException {
    
    
    if (start == end) return x;
    
    
    
    Document doc = getDocument();
    if (! (doc instanceof AbstractDJDocument)) return x; 
    
    final AbstractDJDocument _doc = (AbstractDJDocument) doc;
    
    ArrayList<HighlightStatus> stats = _doc.getHighlightStatus(start, end);
    if (stats.size() < 1) throw new UnexpectedException("GetHighlightStatus returned nothing!");
    
    for (HighlightStatus stat: stats) {
      int location = stat.getLocation();
      int length = stat.getLength();
      
      
      if (location + length > end) length = end - stat.getLocation();
      
      if (! (_doc instanceof InteractionsDJDocument) || ! ((InteractionsDJDocument)_doc).setColoring((start + end)/2, g))      
        setFormattingForState(g, stat.getState());
      Segment text = getLineBuffer(); 
      _doc.getText(location, length, text);
      x = Utilities.drawTabbedText(text, x, y, g, this, location);  
    }
    return  x;
  }
  
  
  protected int drawSelectedText(Graphics g, int x, int y, int start, int end) throws BadLocationException {




    EditDocumentInterface doc = (EditDocumentInterface) getDocument();
    if (doc instanceof InteractionsDJDocument) ((InteractionsDJDocument)doc).setBoldFonts(end, g);
    
    return  super.drawSelectedText(g, x, y, start, end);
  }
  
  
  private void setFormattingForState(Graphics g, int state) {
    switch (state) {
      case HighlightStatus.NORMAL:
        g.setColor(NORMAL_COLOR);
        break;
      case HighlightStatus.COMMENTED:
        g.setColor(COMMENTED_COLOR);
        break;
      case HighlightStatus.SINGLE_QUOTED:
        g.setColor(SINGLE_QUOTED_COLOR);
        break;
      case HighlightStatus.DOUBLE_QUOTED:
        g.setColor(DOUBLE_QUOTED_COLOR);
        break;
      case HighlightStatus.KEYWORD:
        g.setColor(KEYWORD_COLOR);
        break;
      case HighlightStatus.NUMBER:
        g.setColor(NUMBER_COLOR);
        break;
      case HighlightStatus.TYPE:
        g.setColor(TYPE_COLOR);
        break;
      default:
        throw  new RuntimeException("Can't get color for invalid state: " + state);
    }
    g.setFont(MAIN_FONT);
  }
  
  
  
  private void repaintContainer() {
    Container c = getContainer();
    if (c != null) c.repaint();
  }
    
  
  public void changedUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
    super.changedUpdate(changes, a, f);
    
    repaintContainer();
  }
  
  
  public void updateColors() {
    
    COMMENTED_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_COMMENT_COLOR);
    DOUBLE_QUOTED_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_DOUBLE_QUOTED_COLOR);
    SINGLE_QUOTED_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_SINGLE_QUOTED_COLOR);
    NORMAL_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_NORMAL_COLOR);
    KEYWORD_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_KEYWORD_COLOR);
    NUMBER_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_NUMBER_COLOR);
    TYPE_COLOR = DrJava.getConfig().getSetting(DEFINITIONS_TYPE_COLOR);
    
    INTERACTIONS_SYSTEM_ERR_COLOR = DrJava.getConfig().getSetting(SYSTEM_ERR_COLOR);
    INTERACTIONS_SYSTEM_IN_COLOR = DrJava.getConfig().getSetting(SYSTEM_IN_COLOR);
    INTERACTIONS_SYSTEM_OUT_COLOR = DrJava.getConfig().getSetting(SYSTEM_OUT_COLOR);
    ERROR_COLOR = DrJava.getConfig().getSetting(INTERACTIONS_ERROR_COLOR);
    DEBUGGER_COLOR = DrJava.getConfig().getSetting(DEBUG_MESSAGE_COLOR);
    
    
    repaintContainer();
  }
  
  
  private class ColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) { updateColors(); }
  }
  
  private static class FontOptionListener implements OptionListener<Font> {
    public void optionChanged(OptionEvent<Font> oce) {
      MAIN_FONT = DrJava.getConfig().getSetting(FONT_MAIN);
    }
  }
}