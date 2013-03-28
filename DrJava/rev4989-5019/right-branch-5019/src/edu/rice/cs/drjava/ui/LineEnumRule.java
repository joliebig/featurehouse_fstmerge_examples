

package edu.rice.cs.drjava.ui;

import java.awt.*;
import javax.swing.*;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;


public class LineEnumRule extends JComponent {
  
  private static final int BORDER_PADDING = 3;

  
  static int SIZE = 35;

  
  static int WHITE_SPACE = 7;
  
  
  private int _increment;

  
  protected DefinitionsPane _pane;

  
  protected FontMetrics _fm;
  
  protected Font _newFont;
  
  protected FontMetrics _nfm;

  
  public LineEnumRule(DefinitionsPane p) {
    _pane = p;
    _fm = _pane.getFontMetrics(_pane.getFont());
    _increment = _fm.getHeight();

    _newFont = _getLineNumFont();
    _nfm = getFontMetrics(_newFont);
    
    SIZE = (int) _nfm.getStringBounds("99999", getGraphics()).getWidth() + 3 +10;
  }

  
  public Dimension getPreferredSize() {
    return new Dimension(SIZE, (int)_pane.getPreferredSize().getHeight());
  }

  
  public void updateFont() {
    _fm = _pane.getFontMetrics(_pane.getFont());
    _newFont = _getLineNumFont();
      
    _nfm = getFontMetrics(_newFont);
    
    SIZE = (int) _nfm.getStringBounds("99999", getGraphics()).getWidth() + 3 + WHITE_SPACE;
  }

  
  public void paintComponent(Graphics g) {
    Rectangle drawHere = g.getClipBounds();

    
    Color backg = DrJava.getConfig().getSetting
      (OptionConstants.DEFINITIONS_LINE_NUMBER_BACKGROUND_COLOR);
    g.setColor(backg);
    g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

    
    g.setFont(_newFont);
    Color foreg = DrJava.getConfig().getSetting
      (OptionConstants.DEFINITIONS_LINE_NUMBER_COLOR);
    g.setColor(foreg);

    
    int start = (drawHere.y / _increment) * _increment;
    int end = (((drawHere.y + drawHere.height) / _increment) + 1) * _increment;


    int baseline = (int) (( _nfm.getAscent() + _fm.getHeight() - _fm.getDescent())/2.0 );

    





    for (int i = start; i < end; i += _increment) {



      String text = Integer.toString(i/_increment +1);

      
      
      SIZE = (int) _nfm.getStringBounds("99999", g).getWidth() + BORDER_PADDING + WHITE_SPACE;
      int offset = SIZE - ((int) (_nfm.getStringBounds(text, g).getWidth() + 3)) - WHITE_SPACE;

      
      if (text != null) {
        
        
        g.drawString(text, offset, i + baseline + 3);
      }
    }
  }

  
  private Font _getLineNumFont() {
    Font lnf = DrJava.getConfig().getSetting(OptionConstants.FONT_LINE_NUMBERS);
    FontMetrics mets = getFontMetrics(lnf);
    Font mainFont = _pane.getFont();

    
    if (mets.getHeight() > _fm.getHeight()) {
      
      
      
      float newSize;
      if (lnf.getSize() > mainFont.getSize()) {
        newSize = mainFont.getSize2D();
      }
      
      else {
        newSize = lnf.getSize2D() - 1f;
      }

      
      do {
        lnf = lnf.deriveFont(newSize);
        mets = getFontMetrics(lnf);
        newSize -= 1f;
      } while (mets.getHeight() > _fm.getHeight());
    }

    return lnf;
  }
}
