

package edu.rice.cs.util.sexp;
 
import java.util.*;
import java.io.*;

class Lexer extends StreamTokenizer {
  
  public HashMap<String,SExpToken> wordTable = new HashMap<String,SExpToken>();
  
  private SExpToken buffer;
  
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
  
  
  public SExpToken peek() {
    if (buffer == null) buffer = readToken();
    return buffer;
  }
  
  
  public SExpToken readToken() {
    
    if (buffer != null) {
      SExpToken token = buffer;
      buffer = null;          
      return token;
    }
    
    int tokenType = getToken();
    
    switch (tokenType) {
      case TT_NUMBER: 
        return new NumberToken(nval);
        
      case TT_WORD:
        String s = sval.toLowerCase();
        SExpToken regToken = wordTable.get(s);
        if (regToken == null) return new WordToken(sval);
        
        return regToken;
        
      case TT_EOF: return null;
      case '(': return LeftParenToken.ONLY;
      case ')': return RightParenToken.ONLY;
      case '"': return new QuotedTextToken(sval);
      case '\\': 


















          return BackSlashToken.ONLY;

        
      default:
        return new WordToken("" + (char)tokenType);
    }
  }
  
  
  
  private void initWordTable() {
    
    wordTable.put("true", BooleanToken.TRUE);
    wordTable.put("false", BooleanToken.FALSE);
  }
}