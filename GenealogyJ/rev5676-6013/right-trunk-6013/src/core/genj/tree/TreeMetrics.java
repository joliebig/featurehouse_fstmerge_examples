
package genj.tree;

  
public class TreeMetrics {
    
   int
    wIndis, hIndis,
    wFams, hFams,
    pad;
    
  
  public TreeMetrics(int windis, int hindis, int wfams, int hfams, int padng) {
    
    wIndis = windis;
    hIndis = hindis;
    wFams  = wfams;
    hFams  = hfams;
    pad    = padng;
    
  }
  
  
  public boolean equals(Object o) {
    
    if (!(o instanceof TreeMetrics)) 
      return false;
    
    TreeMetrics other = (TreeMetrics)o;
    return 
      wIndis == other.wIndis&&
      hIndis == other.hIndis&&
      wFams  == other.wFams &&
      hFams  == other.hFams &&
      pad    == other.pad   ;
  }

  
  public int hashCode() {
    return wIndis+hIndis+wFams+hFams+pad;
  }

  
   int calcMax() {
    int max = Integer.MIN_VALUE;
    if (wIndis>max) max=wIndis;
    if (hIndis>max) max=hIndis;
    if (wFams >max) max=wFams ;
    if (hFams >max) max=hFams ;
    if (pad   >max) max=pad   ;
    return max;
  }

} 

