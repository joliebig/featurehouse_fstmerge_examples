
package genj.edit;

import genj.option.Option;
import genj.option.OptionProvider;
import genj.option.PropertyOption;
import genj.util.Resources;

import java.util.List;


public class Options extends OptionProvider {
  
  private final static Resources RES = Resources.get(Options.class);
  
  
  public boolean isAutoCommit = false;
  
  
  public boolean isSplitJurisdictions = true;
  
  
  public int correctName = 0;
  
  public final static String[] correctNames = { 
    RES.getString("option.correctName.none"),
    RES.getString("option.correctName.caps"),
    RES.getString("option.correctName.allcaps")
  };
  
  
  private static Options instance = new Options();

  
  public static Options getInstance() {
    return instance;
  }
  
  
  public List<? extends Option> getOptions() {
    return PropertyOption.introspect(instance);
  }

} 
