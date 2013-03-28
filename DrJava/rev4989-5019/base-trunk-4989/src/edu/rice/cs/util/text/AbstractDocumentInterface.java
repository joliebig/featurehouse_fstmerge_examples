

package edu.rice.cs.util.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import javax.swing.text.Position;


public interface AbstractDocumentInterface  { 
  
  
  
  
  public int getLength();
  
  
  public String getText(int offset, int length) throws BadLocationException;
  
  
  public String getText();
  
  
  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException;
  
  
  public void remove(int offset, int length) throws BadLocationException;
  
  
  public void append(String str, AttributeSet set);
  
  
  public Position createPosition(int offs) throws BadLocationException;
}


    
    