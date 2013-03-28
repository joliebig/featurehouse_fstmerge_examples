
package genj.edit;

import genj.option.OptionProvider;
import genj.option.PropertyOption;

import java.util.List;


public class Options extends OptionProvider {
  
  
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
