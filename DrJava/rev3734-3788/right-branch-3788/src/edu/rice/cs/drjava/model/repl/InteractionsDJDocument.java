

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;

import edu.rice.cs.util.Pair;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.text.ConsoleDocument;

import java.io.*;
import java.awt.*;
import java.util.List;
import java.util.LinkedList;
import javax.swing.text.AbstractDocument;

import static edu.rice.cs.drjava.model.definitions.ColoringView.*;


public class InteractionsDJDocument extends AbstractDJDocument {
  
  
  private boolean _toClear = false;
  
  
  public InteractionsDJDocument() { super(); }  
  
  protected int startCompoundEdit() { return 0;  }
  
  protected void endCompoundEdit(int key) {  }

  protected void endLastCompoundEdit() {  }
  
  protected void addUndoRedo(AbstractDocument.DefaultDocumentEvent chng, Runnable undoCommand, Runnable doCommand) { }
  protected void _styleChanged() {  }
  
  
  protected Indenter makeNewIndenter(int indentLevel) { return new Indenter(indentLevel); }
  
  
  private List<Pair<Pair<Integer,Integer>,String>> _stylesList = new LinkedList<Pair<Pair<Integer,Integer>,String>>();
  
  
  public void addColoring(int start, int end, String style) {
    synchronized(_stylesList) {
      if (_toClear) {
        _stylesList.clear();    
        _toClear = false;
      }
      if (style != null)
        _stylesList.add(0, new Pair<Pair<Integer,Integer>,String>
                        (new Pair<Integer,Integer>(new Integer(start),new Integer(end)), style));
    }
  }
  
  
  List<Pair<Pair<Integer, Integer>, String>> getStylesList() { return _stylesList; }
  
  
  public boolean setColoring(int point, Graphics g) {
    synchronized(_stylesList) {
      for(Pair<Pair<Integer,Integer>,String> p :  _stylesList) {
        Pair<Integer,Integer> loc = p.getFirst();
        if (loc.getFirst() <= point && loc.getSecond() >= point) {
          if (p.getSecond().equals(InteractionsDocument.ERROR_STYLE)) {
            
            g.setColor(ERROR_COLOR);   
            g.setFont(g.getFont().deriveFont(Font.BOLD));
          }
          else if (p.getSecond().equals(InteractionsDocument.DEBUGGER_STYLE)) {
            
            g.setColor(DEBUGGER_COLOR);
            g.setFont(g.getFont().deriveFont(Font.BOLD));
          }
          else if (p.getSecond().equals(ConsoleDocument.SYSTEM_OUT_STYLE)) {
            
            g.setColor(INTERACTIONS_SYSTEM_OUT_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.getSecond().equals(ConsoleDocument.SYSTEM_IN_STYLE)) {
            
            g.setColor(INTERACTIONS_SYSTEM_IN_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.getSecond().equals(ConsoleDocument.SYSTEM_ERR_STYLE)) {
            
            g.setColor(INTERACTIONS_SYSTEM_ERR_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.getSecond().equals(InteractionsDocument.OBJECT_RETURN_STYLE)) {
            g.setColor(NORMAL_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.getSecond().equals(InteractionsDocument.STRING_RETURN_STYLE)) {
            g.setColor(DOUBLE_QUOTED_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.getSecond().equals(InteractionsDocument.NUMBER_RETURN_STYLE)) {
            g.setColor(NUMBER_COLOR);
            g.setFont(MAIN_FONT);
          }
          else if (p.getSecond().equals(InteractionsDocument.CHARACTER_RETURN_STYLE)) {
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
        Pair<Integer,Integer> loc = p.getFirst();
        if (loc.getFirst() <= point && loc.getSecond() >= point) {
          if (p.getSecond().equals(InteractionsDocument.ERROR_STYLE))
            g.setFont(g.getFont().deriveFont(Font.BOLD));
          else if (p.getSecond().equals(InteractionsDocument.DEBUGGER_STYLE))
            g.setFont(g.getFont().deriveFont(Font.BOLD));
          else  g.setFont(MAIN_FONT);
          return;
        }
      }
    }
  }
    
  
  public void clearColoring() { synchronized(_stylesList) { _toClear = true; } }
  
  
  public boolean isInCommentBlock() {
    readLock();
    try {
      synchronized(_reduced) {
        resetReducedModelLocation();
        ReducedModelState state = stateAtRelLocation(getLength() - _currentLocation);
        boolean toReturn = (state.equals(ReducedModelStates.INSIDE_BLOCK_COMMENT));
        return toReturn;
      }
    }
    finally { readUnlock(); }
  }
  
  
  public void appendExceptionResult(String exceptionClass, String message, String stackTrace, String styleName) {
    
    String c = exceptionClass;
    if (c.indexOf('.') != -1) c = c.substring(c.lastIndexOf('.') + 1, c.length());
    
    writeLock();
    try {
      insertText(getLength(), c + ": " + message + "\n", styleName);
      
      
      
      
      
      
      if (! stackTrace.trim().equals("")) {
        BufferedReader reader = new BufferedReader(new StringReader(stackTrace));
        
        String line;
        
        
        while ((line = reader.readLine()) != null) {
          String fileName;
          int lineNumber;
          
          
          int openLoc = line.indexOf('(');
          if (openLoc != -1) {
            int closeLoc = line.indexOf(')', openLoc + 1);
            
            if (closeLoc != -1) {
              int colonLoc = line.indexOf(':', openLoc + 1);
              if ((colonLoc > openLoc) && (colonLoc < closeLoc)) {
                
                String lineNumStr = line.substring(colonLoc + 1, closeLoc);
                try {
                  lineNumber = Integer.parseInt(lineNumStr);
                  fileName = line.substring(openLoc + 1, colonLoc);
                }
                catch (NumberFormatException nfe) {
                  
                }
              }
            }
          }
          
          insertText(getLength(), line, styleName);
          
          
          
          
          
          
          
          insertText(getLength(), "\n", styleName);
          
        } 
      }
    }
    catch (IOException ioe) { throw new UnexpectedException(ioe); }
    catch (EditDocumentException ble) { throw new UnexpectedException(ble); }
    finally { writeUnlock(); }
  }  
}
