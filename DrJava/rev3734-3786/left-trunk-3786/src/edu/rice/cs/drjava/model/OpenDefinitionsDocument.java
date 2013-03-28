

package edu.rice.cs.drjava.model;


import java.util.Vector;
import java.io.*;
import java.awt.print.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;

import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.util.docnavigation.*;
import edu.rice.cs.util.text.AbstractDocumentInterface;
import edu.rice.cs.drjava.model.debug.Breakpoint;
import edu.rice.cs.drjava.model.Finalizable;
import edu.rice.cs.drjava.model.definitions.*;


public interface OpenDefinitionsDocument extends DJDocument, Finalizable<DefinitionsDocument>,
  Comparable<OpenDefinitionsDocument>, INavigatorItem, AbstractDocumentInterface {
  
  

  
  public int id();
  public int commentLines(int selStart, int selEnd);
  public int uncommentLines(int selStart, int selEnd);
  public boolean getClassFileInSync();
  public int getCurrentCol();
  public int getOffset(int lineNum);
  public String getQualifiedClassName() throws ClassNameNotFoundException;
  public String getQualifiedClassName(int pos) throws ClassNameNotFoundException;
  public CompoundUndoManager getUndoManager();
  public void resetUndoManager();
  public File getCachedClassFile();
  public void setCachedClassFile(File f);
  public DocumentListener[] getDocumentListeners();
  public UndoableEditListener[] getUndoableEditListeners();
  
  
  
  
  
  
  public File getRawFile();
  
  
  public File getFile() throws FileMovedException;

  
  public void setFile(File file);
  
  
  public String getFileName();
  
  
  public String getCanonicalPath(); 

  
  public String getCompletePath(); 

  
  public File getParentDirectory();

  public Pageable getPageable() throws IllegalStateException;
  
  
  
  
  public boolean undoManagerCanUndo();
  
  
  public boolean undoManagerCanRedo();
  
  
  public boolean inProjectPath();
  
  
  public boolean inNewProjectPath(File root);
  
  
  public boolean isAuxiliaryFile();
  
  
  public boolean isSourceFile();
  
  
  public boolean inProject();
  
  
  public boolean isUntitled();
 
  
  public boolean fileExists();
  
  
  public boolean modifiedOnDisk();
  
  
  public void resetModification();
  
  
  public long getTimestamp();
  
  
  
  
  public String getFirstTopLevelClassName() throws ClassNameNotFoundException;
  
  
  public boolean verifyExists();  
  
  
  public boolean saveFile(FileSaveSelector com) throws IOException;

  
  public void revertFile() throws IOException;

  
  public boolean saveFileAs(FileSaveSelector com) throws IOException;

  
  public void startCompile() throws IOException;

  
  public void runMain() throws ClassNameNotFoundException, IOException;

  
  public void startJUnit() throws ClassNotFoundException, IOException;

  
  public void generateJavadoc(FileSaveSelector saver) throws IOException;

  
  public boolean isModifiedSinceSave();

  
  public boolean revertIfModifiedOnDisk() throws IOException;

  
  public boolean canAbandonFile();
  
  
  public boolean quitFile();

  
  public int gotoLine(int line);

  
  public File getSourceRoot() throws InvalidPackageException;
  
  
  public String getPackageNameFromDocument();

  
  public String getPackageName();
  
  
  public void setPackage(String s);
  
  
  public String getEnclosingClassName(int pos, boolean qual) throws BadLocationException, ClassNameNotFoundException;

  public void preparePrintJob() throws BadLocationException, FileMovedException;

  public void print() throws PrinterException, BadLocationException, FileMovedException;

  public void cleanUpPrintJob();

  
  public boolean checkIfClassFileInSync();

  
  public void documentSaved();
  
   
  public void documentModified();
  
   
  public void documentReset();
    
  
  public Breakpoint getBreakpointAt( int lineNumber);

  
  public void addBreakpoint( Breakpoint breakpoint);

  
  public void removeBreakpoint( Breakpoint breakpoint);

  
  public Vector<Breakpoint> getBreakpoints();

  
  public void clearBreakpoints();

  
  public void removeFromDebugger();
  
  
  public void updateModifiedSinceSave();

  
  public void close();
  
  
  public int getInitialVerticalScroll();
  
  
  public int getInitialHorizontalScroll();
  
  
  public int getInitialSelectionStart();
  
  
  public int getInitialSelectionEnd();
  
  
  public int getNumberOfLines();
  
  
  public int getCaretPosition();
}
