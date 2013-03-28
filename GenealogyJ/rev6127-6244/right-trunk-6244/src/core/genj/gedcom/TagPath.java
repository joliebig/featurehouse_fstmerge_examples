
package genj.gedcom;

import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;
import java.util.regex.Pattern;


public class TagPath {
  
  
  private String name = null;

  
  private String tags[];
  private int qualifiers[];
  
  
  private int len;
  
  
  private int hash = 0;
  
  
  public final static char SEPARATOR = ':';
  public final static String SEPARATOR_STRING = String.valueOf(SEPARATOR);
  private final static char SELECTOR = '#';

  
  public TagPath(String path) throws IllegalArgumentException {
    this(path, null);
  }
  
  public TagPath(String[] path, String name) throws IllegalArgumentException {
    
    this.name = name;

    
    len = path.length;
    if (len==0)
      throw new IllegalArgumentException("No valid path '"+path+"'");

    
    tags = new String[len];
    qualifiers = new int[len];
    for (int i=0;i<len;i++) {
      
      
      String tag = path[i];
      if (tag.length()==0) 
        throw new IllegalArgumentException("Empty tag in '"+Arrays.toString(path)+"' is not valid");

      
      set(i, tag);
      
    }
    
    
  }
  
  
  public TagPath(String path, String name) throws IllegalArgumentException {
    this(path.split(SEPARATOR_STRING), name);
  }
  
  private void set(int pos, String tag) {
    
    
    int qualifier = -1;
    int separator = tag.indexOf('#');
    if (separator>0) {
      try {
        qualifier = Integer.parseInt(tag.substring(separator+1));
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Illegal tag qualifier in '"+tag+"'");
      }
      tag = tag.substring(0, separator);
    }

    
    tags[pos] = tag;
    qualifiers[pos] = qualifier;
    hash += tag.hashCode();

  }
  
  
  public TagPath(TagPath other) {
    this(other, other.len);
  }

  
  public TagPath(TagPath other, int length) {
    
    len = length;
    tags = other.tags;
    qualifiers = other.qualifiers;
    for (int i=0; i<len; i++)
      hash += tags[i].hashCode();
    
  }

  
  public TagPath(TagPath other, String tag) {
    
    
    len = other.len+1;
  
    
    tags = new String[len];
    qualifiers = new int[len];
    
    System.arraycopy(other.tags, 0, tags, 0, other.len);
    System.arraycopy(other.qualifiers, 0, qualifiers, 0, other.len);
    
    tags[len-1] = tag;
    qualifiers[len-1] = -1;
    
    
    hash = other.hash + tag.hashCode();
  }
  
  
   TagPath(Stack<String> path) throws IllegalArgumentException {
    
    len = path.size();
    tags = new String[len];
    qualifiers = new int[len];
    for (int i=0;i<len;i++) 
      set(i, path.pop().toString());
    
  }
  
  
  public boolean startsWith(TagPath prefix) {
    
    if (prefix.len>len) 
      return false;
    
    for (int i=0;i<prefix.len;i++) {
      if (!tags[i].equals(prefix.tags[i]) || qualifiers[i]!=prefix.qualifiers[i]) 
        return false;
    }
    
    return true;
  }

  
  public boolean equals(Object obj) {

    
    if (obj==this) 
      return true;

    
    if (!(obj instanceof TagPath))
      return false;

    
    TagPath other = (TagPath)obj;
    if (other.len!=len) 
      return false;

    
    for (int i=0;i<len;i++) {
      if (!tags[i].equals(other.tags[i]) || qualifiers[i]!=other.qualifiers[i]) 
        return false;
    }

    
    return true;
  }
  
  
  public String get(int which) {
    return tags[which];
  }

  
  public String getFirst() {
    return get(0);
  }

  
  public String getLast() {
    return get(len-1);
  }

  
  public int length() {
    return len;
  }
    
  
  public String toString() {
    StringBuffer result = new StringBuffer();
    for (int i=0;i<len;i++) {
      if (i>0) result.append(':');
      result.append(tags[i]);
      if (qualifiers[i]>=0) {
        result.append('#');
        result.append(qualifiers[i]);
      }
    }
    return result.toString();
  }
  
  
  public int hashCode() {
    return hash;
  }
  
  
  public String getName() {
    if (name==null) {
      
      int i = length()-1;
      String tag = get(i);
      while (i>1&&!Character.isJavaIdentifierPart(tag.charAt(0))) 
        tag = get(--i);
      
      
      name = Gedcom.getName(tag);
      
      
      
      
      
      if (i>1 && Character.isLetter(get(i-1).charAt(0))) {
        String up = Gedcom.getName(get(i-1));
        if (!Pattern.compile(".*"+up+".*", Pattern.CASE_INSENSITIVE).matcher(name).find())
          name = name + " - " + up;
      }
    }
    return name;
  }

  
  public static TagPath get(Property prop) {
    
    String p = prop.getTag();
    while (!(prop instanceof Entity)) {
      prop = prop.getParent();
      p = prop.getTag() + ":" + p;
    }
    
    
    return new TagPath(p);
  }

  
  public static TagPath[] toArray(Collection<TagPath> c) {
    return c.toArray(new TagPath[c.size()]);
  }
  
  
  public static TagPath[] toArray(String[] paths) {
    TagPath[] result = new TagPath[paths.length];
    for (int i=0; i<result.length; i++) {
      result[i] = new TagPath(paths[i]);
    }
    return result;
  }

  
  public void iterate(Property root, PropertyVisitor visitor) {
    iterate(root, visitor, true);
  }
  public void iterate(Property root, PropertyVisitor visitor, boolean backtrack) {
    
    
    String tag = get(0);
    char c = tag.charAt(0);
    if (c=='.'||c=='*')
      iterate(0, root, visitor, backtrack);
    else if (tag.equals(root.getTag()))
      iterate(1, root, visitor, backtrack);
  }
  
  private boolean iterate(int pos, Property prop, PropertyVisitor visitor, boolean backtrack) {
    
    String tag;
    
    
    for (;;pos++) {
      
      
      if (pos==length()) 
        return visitor.leaf(prop);
      
      
      tag = get(pos);
      
       
      if (tag.equals("..")) {
        if (prop.getParent()!=null)
          prop = prop.getParent();
        continue;
      }
      
      if (tag.equals( ".")) {
        continue;
      }
      
      if (tag.equals( "*")) {
        
        if (!(prop instanceof PropertyXRef)||((PropertyXRef)prop).getTarget()==null)
          return false;
        prop = ((PropertyXRef)prop).getTarget();
        continue;
      }
      
      break;
    }
    
    
    if (!visitor.recursion(prop, tag))
      return false;
    
    
    int qualifier = qualifiers[pos];
    for (int i=0, c=0;i<prop.getNoOfProperties();i++) {
      Property child = prop.getProperty(i);
      if (!backtrack && prop.getProperty(child.getTag())!=child)
        continue;
      if (tag.equals(child.getTag())) {
        if (qualifier<0||qualifier==c++) {
          if (!iterate(pos+1, child, visitor, backtrack))
            return false;
        }
      }
    }
    
    
    return true;
  }

  
  public boolean contains(String tag) {
    for (int i=0;i<len;i++) 
      if (tags[i].equals(tag))
        return true;
    return false;
  }
  
} 
