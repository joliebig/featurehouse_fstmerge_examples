
package genj.search;

import java.util.ArrayList;
import java.util.List;


public abstract class Matcher {
  
  
  public abstract void init(String pattern);
  
  
  public final Match[] match(String value) {
    List<Match> result = new ArrayList<Match>(100);
    match(value, result);
    return (Match[])result.toArray(new Match[result.size()]);
  }

  
  protected abstract void match(String value, List<Match> result);
  
  
  public static class Match {
    
    public int pos, len;
    
    protected Match(int p, int l) { pos=p; len=l; }
  } 

} 