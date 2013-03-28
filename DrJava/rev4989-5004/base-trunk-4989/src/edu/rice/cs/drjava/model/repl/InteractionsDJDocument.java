

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;

import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.text.ConsoleDocumentInterface;
import edu.rice.cs.util.text.ConsoleDocument;

import java.awt.*;
import java.util.List;
import java.util.LinkedList;
import javax.swing.text.AbstractDocument;

import static edu.rice.cs.drjava.model.definitions.ColoringView.*;


public class InteractionsDJDocument extends AbstractDJDocument implements ConsoleDocumentInterface {
  
  
  private volatile boolean _hasPrompt;
  
  
  private volatile boolean _toClear = false;
  
  
  public InteractionsDJDocument() { 
    super(); 
    _hasPrompt = false;
  } 
  
  public boolean hasPrompt() { return _hasPrompt; }
  
  
  public void setHasPrompt(boolean val) { 
    _hasPrompt = val;
  }
  
  protected int startCompoundEdit() { return 0;  }
  protected void endCompoundEdit(int key) {  }
  protected void endLastCompoundEdit() {  }
  protected void addUndoRedo(AbstractDocument.DefaultDocumentEvent chng, Runnable undoCommand, Runnable doCommand) { }
  protected void _styleChanged() {  }
  
  
  protected Indenter makeNewIndenter(int indentLevel) { return new Indenter(indentLevel); }
  
  
  private List<Pair<Pair<Integer,Integer>,String>> _stylesList = new LinkedList<Pair<Pair<Integer,Integer>,String>>();
  
  
  public void addColoring(int start, int end, String style) {

    if (_toClear) {
      _stylesList.clear();    
      _toClear = false;
    }
    if (style != null)
      _stylesList.add(0, new Pair<Pair<Integer,Integer>,String>
                      (new Pair<Integer,Integer>(Integer.valueOf(start),Integer.valueOf(end)), style));

  }
  
  
  public Pair<Pair<Integer, Integer>, String>[] getStyles() { 
    synchronized(_stylesList) {
        
        @SuppressWarnings("unchecked")
        Pair<Pair<Integer, Integer>, String>[] result = _stylesList.toArray(new Pair[0]);
        return result;
    }
  }
  
  
  public boolean setColoring(int point, Graphics g) {
    synchronized(_stylesList) {
      for(Pair<Pair<Integer,Integer>,String> p :  _stylesList) {
        Pair<Integer,Integer> loc = p.first();
        if (loc.first() <= point && loc.second() >= point) {
          if (p.second().equals(InteractionsDocument.ERROR_STYLE)) {
            
            g.setColor(ERROR_COLOR);   
            g.setFont(g.getFont().deriveFont(Font.BOLD));
          }
          else if (p.second().equals(InteractionsDocument.DEBUGGER_STYLE)) {
            
            g.setColor(DEBUGGER_COLOR);
            g.setFont(g.getFont().deriveFont(Font.BOLD));
          }
          else if (p.second().equals(ConsoleDocument.SYSTEM_OUT_STYLE)) {
            
            g.setColor(INTERACTIONS_SYSTEM_OUT_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.second().equals(ConsoleDocument.SYSTEM_IN_STYLE)) {
            
            g.setColor(INTERACTIONS_SYSTEM_IN_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.second().equals(ConsoleDocument.SYSTEM_ERR_STYLE)) {
            
            g.setColor(INTERACTIONS_SYSTEM_ERR_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.second().equals(InteractionsDocument.OBJECT_RETURN_STYLE)) {
            g.setColor(NORMAL_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.second().equals(InteractionsDocument.STRING_RETURN_STYLE)) {
            g.setColor(DOUBLE_QUOTED_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.second().equals(InteractionsDocument.NUMBER_RETURN_STYLE)) {
            g.setColor(NUMBER_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.second().equals(InteractionsDocument.CHARACTER_RETURN_STYLE)) {
            g.setColor(SINGLE_QUOTED_COLOR);
            g.setFont(MAIN_FONT);
          }
          else return false;  
          
          return true;
        }
      }
      return false;
    }
  }
  
  
  public void setBoldFonts(int point, Graphics g) {
    synchronized(_stylesList) {
      for(Pair<Pair<Integer,Integer>,String> p :  _stylesList) {
        Pair<Integer,Integer> loc = p.first();
        if (loc.first() <= point && loc.second() >= point) {
          if (p.second().equals(InteractionsDocument.ERROR_STYLE))
            g.setFont(g.getFont().deriveFont(Font.BOLD));
          else if (p.second().equals(InteractionsDocument.DEBUGGER_STYLE))
            g.setFont(g.getFont().deriveFont(Font.BOLD));
          else  g.setFont(MAIN_FONT);
          return;
        }
      }
    }
  }
  
  
  public void clearColoring() { synchronized(_stylesList) { _toClear = true; } }
  
  
  public boolean _inBlockComment() {
        boolean toReturn = _inBlockComment(getLength());
        return toReturn;
  }
  
  
  public void appendExceptionResult(String message, String styleName) {
    
    try { insertText(getLength(), message + "\n", styleName); }
    catch (EditDocumentException ble) { throw new UnexpectedException(ble); }
  } 
}
