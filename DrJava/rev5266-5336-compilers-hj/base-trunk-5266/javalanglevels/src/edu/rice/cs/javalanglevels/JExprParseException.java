

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.parser.*;
import java.io.File;


public class JExprParseException extends ParseException {

  private File _file;
  private String _message;

  public JExprParseException(File file, 
                        String message,
                        Token currentTokenVal,
                        int[][] expectedTokenSequencesVal,
                        String[] tokenImageVal
                       ) {
    super(currentTokenVal, expectedTokenSequencesVal, tokenImageVal);
    _file = file;
    _message = message;
  }
  
  
  public JExprParseException(ParseException e) {
    super(e.currentToken, e.expectedTokenSequences, e.tokenImage);
    _file = null;
    _message = e.getMessage();
  }
  
  
  public File getFile() { return _file; }
  
  public String getMessage() { return _message; }
  
}
