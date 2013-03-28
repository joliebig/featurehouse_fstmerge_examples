
package genj.search;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;



public class SimpleMatcher extends Matcher {
  
  
  private String[] words;
  
  
  public void init(String pattern) {
    StringTokenizer tokens = new StringTokenizer(pattern.toLowerCase());
    words = new String[tokens.countTokens()];
    for (int i=0;i<words.length;i++)
      words[i] = tokens.nextToken();
  }
  
  
  protected void match(String input, List<Match> result) {
    
    input = input.toLowerCase();
    
    ArrayList<Match> matches = new ArrayList<Match>(words.length);
    
    
    for (int i=0;i<words.length;i++) {

      int start = input.indexOf(words[i]);
      if (start<0) 
        return;
      
      while (start>=0) {
        int end = start + words[i].length();
        matches.add(new Match(start, end-start));
        start = input.indexOf(words[i], start+1);
      }
    }
    
    
    result.addAll(matches);
  }

} 