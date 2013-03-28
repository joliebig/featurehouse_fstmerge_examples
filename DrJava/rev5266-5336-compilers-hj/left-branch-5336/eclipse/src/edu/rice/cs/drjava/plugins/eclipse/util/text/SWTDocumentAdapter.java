

package edu.rice.cs.drjava.plugins.eclipse.util.text;

import java.awt.print.Pageable;
import java.awt.print.PrinterException;
import java.util.Hashtable;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;

import edu.rice.cs.util.text.*;


public class SWTDocumentAdapter implements EditDocumentInterface, ConsoleDocumentInterface {

  
  

  
 private static final long serialVersionUID = 5467877329916880674L;


  protected StyledText _pane;

  
  protected StyledTextContent _text;

  
  protected Hashtable<String, SWTStyle> _styles;

  
  protected DocumentEditCondition _condition;

  
  protected boolean _forceInsert;

  
  protected boolean _forceRemove;

  
  protected EditDocumentException _editException;

  
  public SWTDocumentAdapter(StyledText pane) {
    _pane = pane;
    _text = pane.getContent();
    _styles = new Hashtable<String, SWTStyle>();
    _condition = new DocumentEditCondition();
    _forceInsert = false;
    _forceRemove = false;
    _editException = null;

    
    addVerifyListener(new ConditionListener());
  }

  
  public void addVerifyListener(VerifyListener l) {
    _pane.addVerifyListener(l);
  }

  
  public void removeVerifyListener(VerifyListener l) {
    _pane.removeVerifyListener(l);
  }

  
  public void addModifyListener(ModifyListener l) {
    _pane.addModifyListener(l);
  }

  
  public void removeModifyListener(ModifyListener l) {
    _pane.removeModifyListener(l);
  }

  
  public void addDocStyle(String name, SWTStyle s) {
    _styles.put(name, s);
  }

  
  public DocumentEditCondition getEditCondition() {
    return _condition;
  }

  
  public void setEditCondition(DocumentEditCondition condition) {
    _condition = condition;
  }

  
  public void insertText(int offs, String str, String style) throws EditDocumentException {
    _insertText(offs, str, style);
  }
  public void _insertText(int offs, String str, String style) throws EditDocumentException {
    if (_condition.canInsertText(offs)) forceInsertText(offs, str, style);
  }

  
  
  
  public synchronized void forceInsertText(final int offs, final String str, final String style)
    throws EditDocumentException {
    _forceInsertText(offs, str, style);
  }
  
  public void _forceInsertText(final int offs, final String str, final String style) {
    SWTStyle s = null;
    if (style != null) s = _styles.get(style);
    
    final SWTStyle chosenStyle = s;

    _editException = null;
    _forceInsert = true;

    
    _pane.getDisplay().syncExec(new Runnable() {
      public void run() {
        try {
          _pane.replaceTextRange(offs, 0, str);

          
          if (chosenStyle != null) {
            StyleRange range = new StyleRange();
            range.start = offs;
            range.length = str.length();
            range.fontStyle = chosenStyle.getFontStyle();
            range.foreground = chosenStyle.getColor();
            _pane.setStyleRange(range);
          }
        }
        catch (IllegalArgumentException e) {
          _editException = new EditDocumentException(e);
        }
      }
    });
    _forceInsert = false;
    if (_editException != null) {
      throw _editException;
    }
  }

  
  public void removeText(int offs, int len) throws EditDocumentException {
    _removeText(offs, len);
  }
  
  public void _removeText(int offs, int len) throws EditDocumentException {
    if (_condition.canRemoveText(offs)) { 
      forceRemoveText(offs, len);
    }
  }

  
  public synchronized void forceRemoveText(final int offs, final int len) throws EditDocumentException {
    _editException = null;
    _forceRemove = true;

    
    _pane.getDisplay().syncExec(new Runnable() {
      public void run() {
        try {
          _pane.replaceTextRange(offs, len, "");
        }
        catch (IllegalArgumentException e) {
          _editException = new EditDocumentException(e);
        }
      }
    });
    _forceRemove = false;
    if (_editException != null) {
      throw _editException;
    }
  }

  
  public int getLength() { return _text.getCharCount(); }

  
  public String getDocText(int offs, int len) throws EditDocumentException {
    try { return _text.getTextRange(offs, len); }
    catch (IllegalArgumentException e) {
      throw new EditDocumentException(e);
    }
  }
  
  public void append(String str, String style) {
    int offs = getLength();
    forceInsertText(offs, str, style);
  }
                                                                                                                        

  
  
  public void highlightRange(int offset, int length, Color color) {
    StyleRange range = new StyleRange();
    range.start = offset;
    range.length = length;
    range.background = color;
    _pane.setStyleRange(range);
  }

  
  protected class ConditionListener implements VerifyListener {
    public void verifyText(VerifyEvent e) {
      if (e.text.length() == 0) {
        
        e.doit = _canRemove(e);
      }
      else if (e.start == e.end) {
        
        e.doit = _canInsert(e);
      }
      else {
        
        e.doit = _canRemove(e) && _canInsert(e);
      }
    }
    
    protected boolean _canInsert(VerifyEvent e) {
      return _forceInsert ||
   _condition.canInsertText(e.start); 
    }
    
    protected boolean _canRemove(VerifyEvent e) {
      return _forceRemove ||
   _condition.canRemoveText(e.start); 
    }
  }

  
  public static class SWTStyle {
    
    protected Color _color;
    protected int _fontStyle;

    
    public SWTStyle(Color color, int fontStyle) {
      _color = color;
      _fontStyle = fontStyle;
    }

    public Color getColor() { return _color; }
    public int getFontStyle() { return _fontStyle; }
  }

    

   
   public void acquireReadLock() { }

    
   public void releaseReadLock() { }

     
   public void acquireWriteLock() { }

    
   public void releaseWriteLock(){ }

   
   public String getDefaultStyle() {
     return "NONE";
   }
    

   
   public Pageable getPageable() throws IllegalStateException {
    return null;
   }
   
   
   public void print() throws PrinterException { }
   
   private volatile boolean _hasPrompt = true;
   
   
   public boolean hasPrompt() { return _hasPrompt; }
   
   
   public void setHasPrompt(boolean val) { _hasPrompt = val; }
}
