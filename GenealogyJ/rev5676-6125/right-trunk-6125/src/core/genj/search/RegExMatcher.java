
package genj.search;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class RegExMatcher extends Matcher {
  
  
  private Pattern compiled;
  
  
   RegExMatcher() {
  }
  
  
  public void init(String pattern) {
    try {
      compiled = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.DOTALL);
    } catch (PatternSyntaxException pe) {
      throw new IllegalArgumentException("There's a problem with the regular expression '"+pattern+"': "+pe.getDescription());
    }
  }
  
  
  protected void match(String input, List<Match> result) {
    
    java.util.regex.Matcher m = compiled.matcher(input);
    while (true) {
      if (!m.find()) return;
      result.add(new Match(m.start(), m.end()-m.start()));
    }
    
  }

} 