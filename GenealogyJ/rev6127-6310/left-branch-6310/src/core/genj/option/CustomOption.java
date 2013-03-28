
package genj.option;

import java.awt.event.ActionEvent;

import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;

import javax.swing.JComponent;


public abstract class CustomOption extends Option {

  
  protected OptionsWidget widget;
  
  
  private UI ui;
  
  
  public OptionUI getUI(OptionsWidget widget) {
    this.widget = widget;
    
    if (ui==null)  ui = new UI();
    return ui;
  }
  
  
  protected abstract void edit();
    
  
  private class UI extends Action2 implements OptionUI {
    
    
    public String getTextRepresentation() {
      return null;
    }

    
    public JComponent getComponentRepresentation() {
      setText("...");
      return new ButtonHelper().setInsets(2).create(this);
    }

        
    public void endRepresentation() {
    }
    
    
    public void actionPerformed(ActionEvent event) {
      edit();
    }
  
  } 

} 
 