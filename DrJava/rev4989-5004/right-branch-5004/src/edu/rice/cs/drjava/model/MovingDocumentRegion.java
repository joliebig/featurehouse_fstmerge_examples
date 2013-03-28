

package edu.rice.cs.drjava.model;

import javax.swing.text.Position;
import javax.swing.text.BadLocationException;

import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.plt.lambda.Thunk;


public class MovingDocumentRegion extends DocumentRegion {
  


  protected final Position _startPos;
  protected final Position _endPos;
  protected volatile Position _lineStartPos;
  protected volatile Position _lineEndPos;
    
  
  protected final Thunk<String> _stringSuspension;
  
  
  public void update() {
    try {  
      _lineStartPos =_doc.createPosition(_doc._getLineStartPos(getStartOffset()));
      _lineEndPos = _doc.createPosition(_doc._getLineEndPos(getEndOffset()));
    }
    catch (BadLocationException ble) { throw new UnexpectedException(ble); }  
  }
    
  
  public MovingDocumentRegion(final OpenDefinitionsDocument doc, int start, int end, int lineStart, int lineEnd) {

    super(doc, start, end);

    assert doc != null;
    
    try {
      _startPos = doc.createPosition(start);
      _endPos = doc.createPosition(end);
      _lineStartPos = doc.createPosition(lineStart);
      _lineEndPos = doc.createPosition(lineEnd);
    }
    catch (BadLocationException ble) { throw new UnexpectedException(ble); }  
    
    _stringSuspension = new Thunk<String>() {
      public String value() {
        try {
          update();
          int endSel = getEndOffset();
          int startSel = getStartOffset();
          int selLength = endSel - startSel;
          
          int excerptEnd = getLineEndOffset();
          int excerptStart = getLineStartOffset();
          
          
          int startRed = startSel - excerptStart;
          int endRed = endSel - excerptStart;
          
          int excerptLength = Math.min(120, excerptEnd - excerptStart);
          String text = doc.getText(excerptStart, excerptLength);
          
          
          String prefix, match, suffix;
          if (excerptLength < startRed) { 
            prefix = StringOps.compress(text.substring(0, excerptLength));
            match = " ...";
            suffix = "";
          }
          else {
            prefix = StringOps.compress(text.substring(0, startRed));
            if (excerptLength < startRed + selLength) { 
              match = text.substring(startRed) + " ...";
              suffix = "";
            }
            else {
              match = text.substring(startRed, endRed);
              suffix = StringOps.compress(text.substring(endRed, excerptLength));
            }
          }
          
          
          
          
          StringBuilder sb = new StringBuilder(edu.rice.cs.plt.text.TextUtil.htmlEscape(prefix));
          sb.append("<font color=#ff0000>");

          sb.append(edu.rice.cs.plt.text.TextUtil.htmlEscape(match));
          sb.append("</font>");

          sb.append(edu.rice.cs.plt.text.TextUtil.htmlEscape(suffix));


          return sb.toString();
        }
        catch(BadLocationException e) { return "";   }
      }
    };
  }
  
  
  public OpenDefinitionsDocument getDocument() { return _doc; }
  
  
  public int getStartOffset() { return _startPos.getOffset(); }
  
  
  public int getEndOffset() { return _endPos.getOffset(); }
  
  
  public int getLineStartOffset() { return _lineStartPos.getOffset(); }
  
  
  public int getLineEndOffset() { return _lineEndPos.getOffset(); }
  
  
  public String getString() { return _stringSuspension.value(); }
  
  
  public static boolean equals(Object a, Object b) {
    if (a == null) return (b == null);
    return a.equals(b);
  }
}
