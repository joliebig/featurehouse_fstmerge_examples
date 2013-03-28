

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.model.definitions.reducedmodel.*;
import edu.rice.cs.util.text.SwingDocumentInterface;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;

import java.util.ArrayList;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.ProgressMonitor;


public interface DJDocument extends SwingDocumentInterface {
  
  
  public int getIndent();
  
  
  public void setIndent(int indent);
  
  
  public ArrayList<HighlightStatus> getHighlightStatus(int start, int end);
  
  
  public int getCurrentLocation();
  
  
  public void setCurrentLocation(int loc);
  
  
  public void move(int dist);
  


  
  
  public int balanceBackward();
  
  
  public int balanceForward();
  


  

  
  public ReducedModelState getStateAtCurrent();
  

  
  
  public int findPrevEnclosingBrace(int pos, char opening, char closing) throws BadLocationException;
  
  
  public int findNextEnclosingBrace(int pos, char opening, char closing) throws BadLocationException;
  
  
  public int findPrevDelimiter(int pos, char[] delims) throws BadLocationException;
  
  
  public int findPrevDelimiter(int pos, char[] delims, boolean skipParenPhrases) throws BadLocationException;
  





  








  
  
  public void indentLines(int selStart, int selEnd);
  
  
  public void indentLines(int selStart, int selEnd, Indenter.IndentReason reason, ProgressMonitor pm)
    throws OperationCanceledException;
  
  
  public int getIntelligentBeginLinePos(int currPos) throws BadLocationException;;
  
  
  public int _getIndentOfCurrStmt(int pos) throws BadLocationException;
  
  
  public int _getIndentOfCurrStmt(int pos, char[] delims) throws BadLocationException;
  
  
  public int _getIndentOfCurrStmt(int pos, char[] delims, char[] whitespace)
    throws BadLocationException;
  
  
  public int findCharOnLine(int pos, char findChar);
  
  
  public int _getLineStartPos(int pos);
  
  
  public int _getLineEndPos(int pos);
  
  
  public int _getLineFirstCharPos(int pos) throws BadLocationException;
  
  
  public int getFirstNonWSCharPos(int pos) throws BadLocationException;
  
  
  public int getFirstNonWSCharPos(int pos, boolean acceptComments) 
    throws BadLocationException;
  
  
  public int getFirstNonWSCharPos (int pos, char[] whitespace, boolean acceptComments)
    throws BadLocationException;
  

  





  




  





  










  
  
  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException;
  
  
  public void remove(int offset, int len) throws BadLocationException;
  
  
  public String getText();
  
  
  public void clear();
  
  
  
  
  public ReducedModelControl getReduced();
}