

package edu.rice.cs.drjava.model.definitions;

import javax.swing.text.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;

import java.util.Vector;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;


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

  
  protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
        
    
    
    Document doc = getDocument();
    AbstractDJDocument _doc = null;
    if (doc instanceof AbstractDJDocument) _doc = (AbstractDJDocument) doc;
    else return x; 
    
    
    
    if (p0 == p1) return  x;

    Vector<HighlightStatus> stats = _doc.getHighlightStatus(p0, p1);
    if (stats.size() < 1) throw  new RuntimeException("GetHighlightStatus returned nothing!");
    
    for (int i = 0; i < stats.size(); i++) {
      HighlightStatus stat = stats.get(i);
      int length = stat.getLength();
      int location = stat.getLocation();
      
      if (location + length > p1) length = p1 - stat.getLocation();
      
      Segment text = getLineBuffer();
      
      if (!(_doc instanceof InteractionsDJDocument) || !((InteractionsDJDocument)_doc).setColoring((p0+p1)/2,g))      
        setFormattingForState(g, stat.getState());
      
      
      
      
      
      
      
      
      _doc.getText(location, length, text);
      x = Utilities.drawTabbedText(text, x, y, g, this, location);
    }
    
    return  x;
  }

  
  protected int drawSelectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
    
    Document doc = getDocument();
    if (doc instanceof InteractionsDJDocument) ((InteractionsDJDocument)doc).setBoldFonts(p1,g);
    
    return  super.drawSelectedText(g, x, y, p0, p1);
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

  
  public void changedUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
    super.changedUpdate(changes, a, f);
    
    Container c = getContainer();
    if (c != null) c.repaint();
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

    
    if ( getContainer() != null) getContainer().repaint();
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