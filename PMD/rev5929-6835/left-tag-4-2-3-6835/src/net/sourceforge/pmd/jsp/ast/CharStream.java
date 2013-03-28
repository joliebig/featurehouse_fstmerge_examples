



package net.sourceforge.pmd.jsp.ast;



public interface CharStream extends net.sourceforge.pmd.ast.CharStream {

  
  char readChar() throws java.io.IOException;

  
  int getColumn();

  
  int getLine();

  
  int getEndColumn();

  
  int getEndLine();

  
  int getBeginColumn();

  
  int getBeginLine();

  
  void backup(int amount);

  
  char BeginToken() throws java.io.IOException;

  
  String GetImage();

  
  char[] GetSuffix(int len);

  
  void Done();

}

