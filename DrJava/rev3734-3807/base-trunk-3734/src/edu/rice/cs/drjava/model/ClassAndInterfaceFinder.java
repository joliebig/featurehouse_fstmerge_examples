

package edu.rice.cs.drjava.model;

import java.io.*;



public class ClassAndInterfaceFinder {
  
  private StreamTokenizer tokenizer;
  
  
  public ClassAndInterfaceFinder(Reader r) {
    initialize(r);
  }
  
  
  public ClassAndInterfaceFinder(File f) {
    Reader r;
    try { r = new FileReader(f); }
    catch(FileNotFoundException e) { 
      r = new StringReader("");
    }
    initialize(r);
  }
  
  private void initialize(Reader r) {
    
    tokenizer = new StreamTokenizer(r);
    tokenizer.slashSlashComments(true);
    tokenizer.slashStarComments(true);
    tokenizer.lowerCaseMode(false);
    tokenizer.wordChars('_','_');
    tokenizer.wordChars('.','.');
  }
  
  
  public String getClassOrInterfaceName() { return getName(true); }
  
  
  public String getClassName() { return getName(false); }
  
  
  String getName(boolean interfaceOK) {
    try {
      String package_name = "";
      int tokenType;
      
      
      do {
        tokenType = tokenizer.nextToken();
      } while(! isClassOrInterfaceWord(tokenType,interfaceOK) && ! isPackageWord(tokenType));

      if (isEOF(tokenType)) return "";
      
      
      String keyword = tokenizer.sval;
      
      
      do {
        tokenType = tokenizer.nextToken();
      } while (! isWord(tokenType));

      if (isEOF(tokenType)) return "";
      
      if (keyword.equals("class")) return tokenizer.sval;  
        
      if (interfaceOK && keyword.equals("interface")) return tokenizer.sval; 
  
      if (keyword.equals("package")) package_name = tokenizer.sval;
      
      
      do { tokenType = tokenizer.nextToken(); } while (! isClassOrInterfaceWord(tokenType, interfaceOK));
      
      
      do { tokenType = tokenizer.nextToken(); } while (! isWord(tokenType));
      
      if (tokenType == StreamTokenizer.TT_EOF) return "";
      
      if (package_name.length() > 0) return package_name + "." + tokenizer.sval;
      
      return tokenizer.sval;
      
    } catch(IOException e) {
      return "";
    }
  }
  
  
  private static boolean isWord(int tt) { return tt == StreamTokenizer.TT_WORD || isEOF(tt); }
  
  private static boolean isEOF(int tt)  { return tt == StreamTokenizer.TT_EOF; }
  
  
  
  private boolean isClassOrInterfaceWord(int tt, boolean interfaceOK) {
    return  isEOF(tt) || 
      (tt == StreamTokenizer.TT_WORD && tokenizer.sval.equals("class")) ||
      (tt == StreamTokenizer.TT_WORD && interfaceOK && tokenizer.sval.equals("interface"));
  }

  
  private boolean isPackageWord(int tt) {
    return (tt == StreamTokenizer.TT_WORD && tokenizer.sval.equals("package") ||
            tt == StreamTokenizer.TT_EOF);
  }
}



