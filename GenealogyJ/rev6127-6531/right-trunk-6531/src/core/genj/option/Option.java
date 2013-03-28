
package genj.option;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;




public abstract class Option {

  private List<OptionListener> listeners;

  private String category;

  
  public String getCategory() {
    return category;
  }

  
  public void setCategory(String set) {
    category = set;
  }

  
  public abstract String getName();

  
  public abstract String getToolTip();

  
  public abstract void restore();

  
  public abstract void persist();

  
  public abstract OptionUI getUI(OptionsWidget widget);

  
  public void addOptionListener(OptionListener listener) {
    if (listeners==null)
      listeners = new CopyOnWriteArrayList<OptionListener>();
    listeners.add(listener);
  }

  
  protected void fireChangeNotification() {
    if (listeners==null)
      return;
    for (int i = 0; i < listeners.size(); i++)
      ((OptionListener)listeners.get(i)).optionChanged(this);
  }

} 