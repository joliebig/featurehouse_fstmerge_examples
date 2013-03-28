

package edu.rice.cs.drjava.model.definitions;

import javax.swing.text.*;
import java.awt.*;

import java.util.ArrayList;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;




public class ColoringGlyphPainter extends GlyphView.GlyphPainter implements OptionConstants {
  
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
  
  private boolean _listenersAttached;
  private Runnable _lambdaRepaint;
  private FontMetrics _metrics;
  
  public ColoringGlyphPainter(Runnable lambdaRepaint) {
    _listenersAttached = false;
    _lambdaRepaint = lambdaRepaint;
    
  }
  
  
  public void paint(GlyphView v, Graphics g, Shape a, int start, int end) {
    
    
    
    if (start == end) return;
    
    sync(v);
    
    
    
    Document doc = v.getDocument();
    AbstractDJDocument djdoc = null;
    if (doc instanceof AbstractDJDocument)
      djdoc = (AbstractDJDocument) doc;
    else
      return; 

    Segment text;
    TabExpander expander = v.getTabExpander();
    Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
    
    
    int x = alloc.x;
    int p = v.getStartOffset();
    if (p != start) {
      text = v.getText(p, start);
      int width = Utilities.getTabbedTextWidth(text, _metrics, x, expander, p);
      x += width;
    }
    
    
    int y = alloc.y + _metrics.getHeight() - _metrics.getDescent();
    
    text = v.getText(start, end);
    
    ArrayList<HighlightStatus> stats = djdoc.getHighlightStatus(start, end);
    if (stats.size() < 1) throw  new RuntimeException("GetHighlightStatus returned nothing!");
    try {
      for (HighlightStatus stat: stats) {
        int length = stat.getLength();
        int location = stat.getLocation();
        
        if (location < end && location + length > start) {
          
          
          if (location < start) {
            length -= (start-location);
            location = start;
          }        
          if (location + length > end) length = end - location;
          
          if (! (djdoc instanceof InteractionsDJDocument) || 
              ! ((InteractionsDJDocument)djdoc).setColoring((start+end)/2,g))      
            setFormattingForState(g, stat.getState());
          
          djdoc.getText(location, length, text);
          x = Utilities.drawTabbedText(text, x, y, g, v.getTabExpander(), location);
        }
      }
    }
    catch(BadLocationException ble) {  }
  }
  
  
  public float getSpan(GlyphView v, int start, int end, TabExpander e, float x) {
    sync(v);
    Segment text = v.getText(start, end);
    int width = Utilities.getTabbedTextWidth(text, _metrics, (int) x, e, start);
    return width;
  }
  
  
  public float getHeight(GlyphView v) {
    sync(v);
    return _metrics.getHeight();
  }
  
  
  public float getAscent(GlyphView v) {
    sync(v);
    return _metrics.getAscent();
  }
  
  
  public float getDescent(GlyphView v) {
    sync(v);
    return _metrics.getDescent();
  }
  
  
  public Shape modelToView(GlyphView v, int pos, Position.Bias bias, Shape a) throws BadLocationException {
    
    sync(v);
    Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
    int start = v.getStartOffset();
    int end = v.getEndOffset();
    TabExpander expander = v.getTabExpander();
    Segment text;
    
    if(pos == end) {
      
      
      return new Rectangle(alloc.x + alloc.width, alloc.y, 0,
                           _metrics.getHeight());
    }
    if ((pos >= start) && (pos <= end)) {
      
      text = v.getText(start, pos);
      int width = Utilities.getTabbedTextWidth(text, _metrics, alloc.x, expander, start);
      return new Rectangle(alloc.x + width, alloc.y, 0, _metrics.getHeight());
    }
    throw new BadLocationException("modelToView - can't convert", end);
  }
  
  
  public int viewToModel(GlyphView v, float x, float y, Shape a, Position.Bias[] biasReturn) {
    sync(v);
    Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
    int start = v.getStartOffset();
    int end = v.getEndOffset();
    TabExpander expander = v.getTabExpander();
    Segment text = v.getText(start, end);
    
    int offs = Utilities.getTabbedTextOffset(text, _metrics, 
                                             alloc.x, (int) x, expander, start);
    int retValue = start + offs;
    if(retValue == end) {
      
      
      retValue--;
    }
    biasReturn[0] = Position.Bias.Forward;
    return retValue;
  }

  
  public int getBoundedPosition(GlyphView v, int start, float x, float len) {
    sync(v);
    TabExpander expander = v.getTabExpander();
    Segment s = v.getText(start, v.getEndOffset());
    int index = Utilities.getTabbedTextOffset(s, _metrics, (int)x, (int)(x+len), expander, start, false);
    int end = start + index;
    return end;
  }
  
  
  void sync(GlyphView v) {
    Font f = v.getFont();
    if ((_metrics == null) || (! f.equals(_metrics.getFont()))) {
      
      Toolkit kit;
      Component c = v.getContainer();
      if (c != null) {
        kit = c.getToolkit();
      } else {
        kit = Toolkit.getDefaultToolkit();
      }
      
      @SuppressWarnings("deprecation") 
      FontMetrics newMetrics = kit.getFontMetrics(f);
      _metrics = newMetrics;
    }
    
    Document doc = v.getDocument();
    if (! _listenersAttached && (doc instanceof AbstractDJDocument)) {
      attachOptionListeners((AbstractDJDocument)doc);
    }
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
  
  











  
  private void attachOptionListeners(AbstractDJDocument doc) {
    
    final ColorOptionListener col = new ColorOptionListener();
    final FontOptionListener fol = new FontOptionListener();
    
    

    
    
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
    _listenersAttached = true;
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
    
    EventQueue.invokeLater(_lambdaRepaint);
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
