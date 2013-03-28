

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.model.definitions.reducedmodel.*;
import edu.rice.cs.util.text.AbstractDocumentInterface;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.drjava.model.definitions.ClassNameNotFoundException;

import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.ProgressMonitor;


public interface DJDocument extends StyledDocument, AbstractDocumentInterface {
  
  
  public int getIndent();
  
  
  public void setIndent(int indent);
  
  
  public Vector<HighlightStatus> getHighlightStatus(int start, int end);
  
  
  public int getCurrentLocation();
  
  
  public void setCurrentLocation(int loc);
  
  
  public void move(int dist);
  
  
  public int balanceBackward();
  
  
  public int balanceForward();
  
  
  public IndentInfo getIndentInformation();
  
  public ReducedModelState stateAtRelLocation(int dist);
  
  public ReducedModelState getStateAtCurrent();
  
  public void resetReducedModelLocation();
  
  
  public int findPrevEnclosingBrace(int pos, char opening, char closing) throws BadLocationException;
  
  
  public int findNextEnclosingBrace(int pos, char opening, char closing) throws BadLocationException;
  
  
  public int findPrevDelimiter(int pos, char[] delims) throws BadLocationException;
  
  
  public int findPrevDelimiter(int pos, char[] delims, boolean skipParenPhrases) throws BadLocationException;
  
  
  public boolean findCharInStmtBeforePos(char findChar, int position);
  
  
  public int findPrevCharPos(int pos, char[] whitespace) throws BadLocationException;
  
  
  public void indentLines(int selStart, int selEnd);
  
  
  public void indentLines(int selStart, int selEnd, int reason, ProgressMonitor pm)
    throws OperationCanceledException;
  
  
  public int getIntelligentBeginLinePos(int currPos) throws BadLocationException;;
  
  
  public String getIndentOfCurrStmt(int pos) throws BadLocationException;
  
  
  public String getIndentOfCurrStmt(int pos, char[] delims) throws BadLocationException;
  
  
  public String getIndentOfCurrStmt(int pos, char[] delims, char[] whitespace)
     throws BadLocationException;
  
  
  public int findCharOnLine(int pos, char findChar);
  
  
  public int getLineStartPos(int pos);
  
  
  public int getLineEndPos(int pos);
  
  
  public int getLineFirstCharPos(int pos) throws BadLocationException;
  
  
  public int getFirstNonWSCharPos(int pos) throws BadLocationException;
  
  
  public int getFirstNonWSCharPos(int pos, boolean acceptComments) 
    throws BadLocationException;
  
  
  public int getFirstNonWSCharPos (int pos, char[] whitespace, boolean acceptComments)
     throws BadLocationException;
  
  public int findPrevNonWSCharPos(int pos) throws BadLocationException;
  
  
  public boolean posInParenPhrase(int pos);
  
  
  public boolean posInParenPhrase();
  
  
  public int getWhiteSpace();
  
  
  public void setTab(String tab, int pos);
  
  
  public void insertString(int offset, String str, AttributeSet a)
    throws BadLocationException;
  
  
  public void remove(int offset, int len) throws BadLocationException;
  
  
  public String getText();
  
  
  public void clear();
  
  
  
  
  public void readLock();
  
  
  public void readUnlock();

  
  public void modifyLock();
  
  
  public void modifyUnlock();
}