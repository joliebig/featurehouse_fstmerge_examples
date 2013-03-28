
package genj.edit.beans;

import genj.gedcom.Entity;
import genj.gedcom.Property;
import genj.gedcom.PropertyChange;
import genj.util.Registry;

import java.awt.BorderLayout;

import javax.swing.JLabel;


public class EntityBean extends PropertyBean {

  private Preview preview;
  private JLabel changed;

    
  public boolean isEditable() {
    return false;
  }

  void initialize(Registry setRegistry) {
    super.initialize(setRegistry);
    
    preview = new Preview();
    changed = new JLabel();
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, preview);
    add(BorderLayout.SOUTH, changed);
  }
  
  
  boolean accepts(Property prop) {
    return prop instanceof Entity;
  }
  public void setPropertyImpl(Property prop) {

    
    Entity entity = (Entity)prop;
    preview.setEntity(entity);

    
    changed.setVisible(false);
    if (entity!=null) {
      PropertyChange change = entity.getLastChange();
      if (change!=null)
        changed.setText(RESOURCES.getString("entity.change", new String[] {change.getDateDisplayValue(), change.getTimeDisplayValue()} ));      
        changed.setVisible(true);
    }
    
    
  }
  
} 
