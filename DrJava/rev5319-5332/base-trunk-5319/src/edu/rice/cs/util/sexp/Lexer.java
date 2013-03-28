

package edu.rice.cs.util.sexp;

import java.util.*;
import java.io.*;

class Lexer extends StreamTokenizer {
  
  public HashMap<String,Tokens.SExpToken> wordTable = new HashMap<String,Tokens.SExpToken>();
  
  private Tokens.SExpToken buffer;
  
  public Lexer(File file) throws FileNotFoundException{
    this(new BufferedReader(new FileReader(file)));
  }
  
  public Lexer(Reader reader) {
    super(new BufferedReader(reader));
    initLexer();
  }
  
  private void initLexer() {
    
    
    resetSyntax();
    parseNumbers();
    slashSlashComments(true);
    wordChars('!','\'');
    wordChars('*','~');
    quoteChar('"');
    ordinaryChars('(',')');
    whitespaceChars(0,' ');
    commentChar(';');
    
    initWordTable();
    buffer = null;  
  }
  
  
  public void flush() throws IOException {
    eolIsSignificant(true);
    while (nextToken() != TT_EOL) ; 
    eolIsSignificant(false);
  }
  
  
  private int getToken() {
    try {
      int tokenType = nextToken();
      return tokenType;
    } catch(IOException e) {
      throw new LexingException("Unable to read the data from the given input");
    }
  }
  
  
  public Tokens.SExpToken peek() {
    if (buffer == null) buffer = readToken();
    return buffer;
  }
  
  
  public Tokens.SExpToken readToken() {
    
    if (buffer != null) {
      Tokens.SExpToken token = buffer;
      buffer = null;          
      return token;
    }
    
    int tokenType = getToken();
    
    switch (tokenType) {
      case TT_NUMBER: 
        return new Tokens.NumberToken(nval);
        
      case TT_WORD:
        String s = sval.toLowerCase();
        Tokens.SExpToken regToken = wordTable.get(s);
        if (regToken == null) return new Tokens.WordToken(sval);
        
        return regToken;
        
      case TT_EOF: return null;
      case '(': return Tokens.LeftParenToken.ONLY;
      case ')': return Tokens.RightParenToken.ONLY;
      case '"': return new Tokens.QuotedTextToken(sval);
      case '\\': 


















        return Tokens.BackSlashToken.ONLY;

        
      default:
        return new Tokens.WordToken("" + (char)tokenType);
    }
  }
  
  
  
  private void initWordTable() {
    
    wordTable.put("true", Tokens.BooleanToken.TRUE);
    wordTable.put("false", Tokens.BooleanToken.FALSE);
  }
}