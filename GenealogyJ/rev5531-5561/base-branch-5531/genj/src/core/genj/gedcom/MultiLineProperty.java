
package genj.gedcom;


public interface MultiLineProperty {
  
  
  public void setValue(String value);
  
  
  public Collector getLineCollector();

  
  public Iterator getLineIterator();
  
  
  public interface Collector {
    
    
    public boolean append(int indent, String tag, String value);
    
    
    public String getValue();
    
  }
  
  
  public interface Iterator {
    
    
    public void setValue(String value);

    
    public int getIndent();
    
    
    public String getTag();
    
    
    public String getValue();
    
     
    public boolean next();
    
  } 

} 
