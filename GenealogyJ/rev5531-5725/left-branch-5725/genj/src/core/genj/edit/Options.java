
package genj.edit;

import java.util.List;

import genj.option.OptionProvider;
import genj.option.PropertyOption;


public class Options extends OptionProvider {
  
  
  public boolean isOpenEditor = true;
  
  
  public boolean isAutoCommit = false;
  
  
  public boolean isSplitJurisdictions = true;
  
  
  private static Options instance = new Options();

  
  public static Options getInstance() {
    return instance;
  }
  
  
  public List getOptions() {
    return PropertyOption.introspect(instance);
  }

} 
