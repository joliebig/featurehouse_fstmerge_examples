
package genj.util;


public class WordBuffer {
  
  
  private StringBuffer buffer;
  
  
  private String filler = " ";
  
  
  public WordBuffer(String filler) {
    this();
    setFiller(filler);
  }
  
  
  public WordBuffer() {
    buffer = new StringBuffer(80);
  }
  
    public WordBuffer setFiller(String set) {
    filler = set;
    return this;
  }
  
  
  public String toString() {
    return buffer.toString();
  }
  
  
  public WordBuffer append(int i) {
    if (buffer.length()>0)
      buffer.append(filler);
    buffer.append(i);
    return this;
  }

  
  public WordBuffer append(Object object) {
    if (object!=null) append(object.toString());
    return this;
  }

  
  public WordBuffer append(Object object, String nullSubst) {
    if (object==null) return append(nullSubst);
    return append(object.toString(), nullSubst);
  }

    
  public WordBuffer append(String word) {
    return (word==null) ? this :append(word, null);
  }
  
    
  public WordBuffer append(String word, String nullSubst) {
    
    if ((word==null)||(word.length()==0)) return append(nullSubst);
    
    if ((buffer.length()>0)&&(!isStartingWithPunctuation(word))) buffer.append(filler);
    
    buffer.append(word.trim());
    
    return this;
  }
  
  
  private final boolean isStartingWithPunctuation(String word) {
    switch (word.charAt(0)) {
      default: return false;
      case '.': return true;
      case ',': return true;
      case ':': return true;
    }
  }
  
  
  public int length() {
    return buffer.length();
  }

} 
