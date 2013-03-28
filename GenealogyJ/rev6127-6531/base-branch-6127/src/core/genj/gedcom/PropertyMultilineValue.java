
package genj.gedcom;

import java.util.ArrayList;


public class PropertyMultilineValue extends Property implements MultiLineProperty {
  
  
  private String tag;
  
  
  private String lines = "";
  
  
  public String getTag() {
    return tag;
  }

  
   Property init(MetaProperty meta, String value) throws GedcomException {
    tag = meta.getTag();
    return super.init(meta, value);
  }
  
  
  public void setValue(String setValue) {
    String old = getValue();
    lines = setValue;
    propagatePropertyChanged(this, old);
  }
  
  
  public String getDisplayValue() {
    return getValue();
  }

  
  public String getValue() {
    return lines.toString();
  }
  
  
  public String[] getLines() {
     ArrayList result = new ArrayList();
     Iterator it = getLineIterator();
     do {
       result.add(it.getValue());
     } while (it.next());
     return (String[])result.toArray(new String[result.size()]);
  }
  
  
  public Iterator getLineIterator() {
    return new ConcContIterator(getTag(), lines);
  }

  
  public Collector getLineCollector() {
    return new ConcContCollector();
  }
  
    
    private static class ConcContIterator implements Iterator {
      
      
      private String firstTag, currentTag, nextTag;
      
      
      private String value;
      
      
      private int start,end;
      
      
      private int valueLineBreak;
      
      
       ConcContIterator(String top, String initValue) {
        valueLineBreak = Options.getInstance().getValueLineBreak();
        firstTag = top;
        setValue(initValue);
      }
      
      
      public void setValue(String setValue) {
  
        value = setValue;
  
        currentTag = firstTag;       
        nextTag = firstTag;
        start = 0;
        end = 0;
  
        next();
      }
      
      
      public int getIndent() {
        return currentTag == firstTag ? 0 : 1;
      }
      
      
      public String getTag() {
        return currentTag;
      }
      
      
      public String getValue() {
        return value.substring(start, end);
      }
        
      
      public boolean next() {
        
        
        if (end==value.length()) 
          return false;
  
        
        start = end;
        
        
        currentTag = nextTag;
        
        
        end = value.length();
        
        
        if (currentTag!=firstTag && value.charAt(start)=='\n')
          start++;
        
        
        
        for (int i=start;i<end;i++) {
          if (value.charAt(i)=='\n') {
            end = i;
            nextTag = "CONT";
            break;
          }
        }
        
        
        if (end-start>valueLineBreak) {
          end = start+valueLineBreak;
          nextTag = "CONC";
          
          
          while ( end<value.length() && end>start+1 && (Character.isWhitespace(value.charAt(end-1)) || Character.isWhitespace(value.charAt(end))) )
            end--;
        }
        
        
        return start!=value.length();
      }
      
    } 
  
  
  private class ConcContCollector implements Collector {
    
    
    private StringBuffer buffer = new StringBuffer(lines.toString());
    
    
    public boolean append(int indent, String tag, String value) {
      
      
      if (indent!=1)
        return false;
        
      
      boolean 
        isCont = "CONT".equals(tag),
        isConc = "CONC".equals(tag);
      if (!(isConc||isCont))
        return false;
        
      
      if (isCont) 
        buffer.append('\n');
        
      buffer.append(value);
      
      
      return true;
    }
    
    
    public String getValue() {
      return buffer.toString();
    }

  } 

} 
