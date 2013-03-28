

package edu.rice.cs.util.text;

import java.awt.print.Pageable;
import java.awt.print.PrinterException;



public interface EditDocumentInterface  {
  
  
  public DocumentEditCondition getEditCondition();
  
  
  public void setEditCondition(DocumentEditCondition condition);
  
  
  public void insertText(int offs, String str, String style);
  
  
  public void forceInsertText(int offs, String str, String style);
  
  
  public void removeText(int offs, int len);
  
  
  public void forceRemoveText(int offs, int len);
  
  
  public int getLength();
  
  
  public String getDocText(int offs, int len);
  
  
  public void append(String str, String style);
  
  
  public String getDefaultStyle();
  
  
  public Pageable getPageable() throws IllegalStateException;
  
  
  public void print() throws PrinterException;
}
