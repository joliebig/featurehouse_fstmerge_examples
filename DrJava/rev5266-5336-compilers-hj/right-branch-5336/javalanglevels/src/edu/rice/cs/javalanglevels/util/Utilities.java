

package edu.rice.cs.javalanglevels.util;

import java.awt.EventQueue;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;

public class Utilities {
  
  
  public static void copyFile(File sourceFile, File destFile) throws IOException {
    if(! destFile.exists()) destFile.createNewFile();
    
    FileChannel source = null;
    FileChannel destination = null;
    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    }
    finally {
      if (source != null) source.close();
      if (destination != null) destination.close();
    }
  }

  
  
  public static void invokeLater(Runnable task) {
    if (EventQueue.isDispatchThread()) {
      task.run(); 
      return;
    }
    EventQueue.invokeLater(task);
  }
  
  public static void invokeAndWait(Runnable task) {
    if (EventQueue.isDispatchThread()) {
      task.run(); 
      return;
    }
    try { EventQueue.invokeAndWait(task); }
    catch(Exception e) { throw new RuntimeException(e); }
  }
  
  public static void main(String[] args) { clearEventQueue(); }
  
  public static void clearEventQueue() {
    Utilities.invokeAndWait(new Runnable() { public void run() { } });
  }
  
  
  public static void show(final String msg) { 
    Utilities.invokeAndWait(new Runnable() { public void run() {
      new ScrollableDialog(null,"Debug Message", "Debug Message from Utilities.show():", msg, false).show(); } } );
  }
  
  
  public static void showDebug(String msg) { showMessageBox(msg, "Debug Message"); }
  
  
  public static void showMessageBox(final String msg, final String title) {
    
    Utilities.invokeAndWait(new Runnable() { public void run() {
      new ScrollableDialog(null, title, "Message:", msg, false).show();
    } } );
  }
  
  public static void showStackTrace(final Throwable t) {
    Utilities.invokeAndWait(new Runnable() { public void run() { 
      new ScrollableDialog(null, "Stack Trace", "Stack Trace:", getStackTrace(t), false).show();
    } } );
  }
  
  
  public static String getClipboardSelection(Component c) {
    Clipboard cb = c.getToolkit().getSystemClipboard();
    if (cb == null) return null;
    Transferable t = cb.getContents(null);
    if (t == null) return null;
    String s = null;
    try {
      java.io.Reader r = DataFlavor.stringFlavor.getReaderForText(t);
      int ch;
      final StringBuilder sb = new StringBuilder();
      while ((ch=r.read()) !=-1 ) { sb.append((char)ch); }
      s = sb.toString();
    }
    catch(UnsupportedFlavorException ufe) {  }
    catch(java.io.IOException ioe) {  }
    return s;
  }
  
  
  public static AbstractAction createDelegateAction(String newName, final Action delegate) {
    return new AbstractAction(newName) {
      public void actionPerformed(ActionEvent ae) { delegate.actionPerformed(ae); }
    };
  }
    
  public static String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString();
  }
  
  
  public static String getStackTrace() {
    try { throw new Exception(); } 
    catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      StackTraceElement[] stes = e.getStackTrace();
      int skip = 1;
      for(StackTraceElement ste: stes) {
        if (skip>0) { --skip; } else { pw.print("at "); pw.println(ste); }
      }
      return sw.toString();
    }
  }
  
  
  public static boolean contains(Object[] a, Object elt) {
    for (Object o: a) { if (o.equals(elt)) return true; }
    return false;
  }
  
  
  public static boolean hasVisibilityModifier(String[] modifiers) {
    for (String s: modifiers) {
      if (s.equals("private") || s.equals("public") || s.equals("protected")) return true;
    }
    return false;
  }
  
  
  public static boolean isFinal(String[] modifiers) { return contains(modifiers, "final"); }
   
  
  public static boolean isStatic(String[] modifiers) { return contains(modifiers, "static"); }
  
  
  public static boolean isPublic(String[] modifiers) { return contains(modifiers, "public"); }
  
  
  public static boolean isAbstract(String[] modifiers) { return contains(modifiers, "abstract"); }
}
