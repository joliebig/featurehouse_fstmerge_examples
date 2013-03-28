

package edu.rice.cs.drjava;

import java.io.*;
import java.util.Vector;


import edu.rice.cs.util.FileOps;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.drjava.model.GlobalEventNotifier;


public class IndentFiles {
  
  
  public static void main(String[] args) {
    Vector<String> fileNames = new Vector<String>();
    int indentLevel = 2;
    boolean silent = false;
    if (args.length < 1) _displayUsage();
    else {
      for (int i = 0; i < args.length; i++) {
        String arg = args[i];
        if (arg.equals("-indent")) {
          i++;
          try { indentLevel = Integer.parseInt(args[i]); }
          catch (Exception e) {
            _displayUsage();
            System.exit(-1);
          }
        }
        else if (arg.equals("-silent")) silent = true;
        else fileNames.add(arg);
      }
      indentFiles(fileNames, indentLevel, silent);
    }
  }

  
  private static void _displayUsage() {
    System.out.println(
      "Usage:" +
      "  java edu.rice.cs.drjava.IndentFile [-indent N] [-silent] [filenames]\n" +
      "  Where N is the number of spaces in an indentation level");
  }
  
  
  public static void indentFiles(Vector<String> fileNames, int indentLevel, boolean silent) {
    
    
    Indenter indenter = new Indenter(indentLevel);
    
    if (!silent) System.out.println("DrJava - Indenting files:");
    for (int i = 0; i < fileNames.size(); i++) {
      String fname = fileNames.get(i);
      File file = new File(fname);
      if (!silent) { 
        System.out.print("  " + fname + " ... ");
        System.out.flush();
      }
      try {
        String fileContents = FileOps.readFileAsString(file);
        DefinitionsDocument doc = new DefinitionsDocument(indenter, new GlobalEventNotifier());
        doc.insertString(0, fileContents, null); 
        int docLen = doc.getLength();
        doc.indentLines(0, docLen);
        fileContents = doc.getText();
        FileOps.writeStringToFile(file, fileContents);
        if (!silent) System.out.println("done.");
      }
      catch (Exception e) {
        if (!silent) {
          System.out.println("ERROR!");
          System.out.println("  Exception: " + e.toString());
          e.printStackTrace(System.out);
          System.out.println();
        }
      }
      
    }
    if (!silent) System.out.println();
  }

  
}
