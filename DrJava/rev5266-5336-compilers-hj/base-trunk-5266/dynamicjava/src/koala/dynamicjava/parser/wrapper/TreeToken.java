

package koala.dynamicjava.parser.wrapper;

import java.io.File;

import koala.dynamicjava.parser.impl.Token;
import koala.dynamicjava.tree.IdentifierToken;
import koala.dynamicjava.tree.SourceInfo;



public class TreeToken implements IdentifierToken {
  
  private final Token token;
  
  private final SourceInfo sourceInfo;
  
  
  public TreeToken(Token t, File f) {
    token = t;
    sourceInfo = SourceInfo.range(f, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
  }
  
  
  public Token getToken() {
    return token;
  }
  
  
  public String image() {
    return token.image;
  }
  
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }
  
  public String toString() {
    return image();
  }
}
