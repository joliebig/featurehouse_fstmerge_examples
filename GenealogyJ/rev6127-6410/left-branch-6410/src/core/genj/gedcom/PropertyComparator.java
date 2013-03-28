
package genj.gedcom;

import java.util.Comparator;


public class PropertyComparator implements Comparator<Property> {
  
  
  private TagPath path;
  
  
  private int reversed = 1;

  
  public PropertyComparator(String path) {
    this(new TagPath(path));
  }
  
  
  public PropertyComparator(String path, boolean reversed) {
    this(path);
    this.reversed = reversed ? -1 : 1;
  }
  
  
  public PropertyComparator(TagPath path) {
    this.path = path;
  }
  
  
  public TagPath getPath() {
    return path;
  }
  
  
  public int compare(Property p1, Property p2) {
    
    p1 = p1.getProperty(path);
    p2 = p2.getProperty(path);
    
    
    if (p1==p2  ) return  0;
    if (p1==null) return -1 * reversed;
    if (p2==null) return  1 * reversed;
    
    
    return p1.compareTo(p2) * reversed;
    
  }

} 