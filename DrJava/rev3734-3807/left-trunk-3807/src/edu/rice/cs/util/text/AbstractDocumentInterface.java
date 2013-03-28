

package edu.rice.cs.util.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import javax.swing.text.Position;


public interface AbstractDocumentInterface extends ReadersWritersLocking { 
  
  
  
  Position createPosition(int offs) throws BadLocationException;

  
  int getLength();
  
  
  String getText(int offset, int length) throws BadLocationException;
  
  
  String getText();
  
  
  void insertString(int offset, String str, AttributeSet a) throws BadLocationException;
  
  
  void remove(int offset, int length) throws BadLocationException;
  
  
  void append(String str, AttributeSet set);
}


    
    