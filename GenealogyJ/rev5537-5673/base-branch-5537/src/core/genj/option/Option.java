
package genj.option;

import genj.util.Registry;

import java.util.ArrayList;
import java.util.List;




public abstract class Option {

  private List listeners;

  private String category;

  
  public String getCategory() {
    return category;
  }

  
  public void setCategory(String set) {
    category = set;
  }

  
  public abstract String getName();

  
  public abstract String getToolTip();

  
  public abstract void restore(Registry registry);

  
  public abstract void persist(Registry registry);

  
  public abstract OptionUI getUI(OptionsWidget widget);

  
  public void addOptionListener(OptionListener listener) {
    if (listeners==null)
      listeners = new ArrayList(4);
    listeners.add(listener);
  }

  
  protected void fireChangeNotification() {
    if (listeners==null)
      return;
    for (int i = 0; i < listeners.size(); i++)
      ((OptionListener)listeners.get(i)).optionChanged(this);
  }

} 