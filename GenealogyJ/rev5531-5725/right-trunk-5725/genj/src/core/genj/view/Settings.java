
package genj.view;

import javax.swing.JComponent;

public interface Settings {
  
  
  public void init(ViewManager manager);

    public void setView(JComponent view);
  
    public JComponent getEditor();

  
  public void apply();

  
  public void reset();


} 
