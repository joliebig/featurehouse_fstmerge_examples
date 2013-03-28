

package koala.dynamicjava.parser.wrapper;


import java.io.File;

import edu.rice.cs.plt.text.TextUtil;
import koala.dynamicjava.parser.impl.ParseException;
import koala.dynamicjava.parser.impl.Token;
import koala.dynamicjava.tree.SourceInfo;
  


public class ParseError extends Error implements SourceInfo.Wrapper {
    private SourceInfo _si;
    
    
    
    public ParseError(String s, SourceInfo si) {
      super(s);
      _si = si;
    }
    
    
    public ParseError(ParseException e, File f) {
      super(parseExceptionMessage(e), e);
      _si = parseExceptionLocation(e, f);
    }
    
    public ParseError(Throwable t, SourceInfo si) {
      super(t.getMessage(), t);
      _si = si;
    }
    
    public SourceInfo getSourceInfo() { return _si; }
    
    
    private static String parseExceptionMessage(ParseException e) {
      if (e.expectedTokenSequences == null) { return e.getMessage(); }
      else {
        int maxSize = 0;
        for (int i = 0; i < e.expectedTokenSequences.length; i++) {
          if (maxSize < e.expectedTokenSequences[i].length) {
            maxSize = e.expectedTokenSequences[i].length;
          }
        }
        String retval = "Syntax Error: \"";
        Token tok = e.currentToken.next;
        
        for (int i = 0; i < maxSize; i++) {
          if (i != 0) retval += " ";
          if (tok.kind == 0) {
            retval += e.tokenImage[0];
            break;
          }
          retval += TextUtil.javaEscape(tok.image);
          tok = tok.next; 
        }
        retval += "\"";
        return retval;
      }
    }
    
    private static SourceInfo parseExceptionLocation(ParseException e, File f) {
      Token t = e.currentToken;
      if (t == null) { return SourceInfo.point(f, 0, 0); }
      else {
        if (t.next != null) { t = t.next; }
        return SourceInfo.range(f, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
      }
    }
    
}
