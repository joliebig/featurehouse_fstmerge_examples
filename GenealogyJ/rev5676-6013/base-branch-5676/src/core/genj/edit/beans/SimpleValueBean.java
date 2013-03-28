
package genj.edit.beans;

import genj.gedcom.Property;
import genj.util.Registry;
import genj.util.swing.TextFieldWidget;

import java.awt.BorderLayout;


public class SimpleValueBean extends PropertyBean {

  
  private TextFieldWidget tfield;

  void initialize(Registry setRegistry) {
    super.initialize(setRegistry);
    
    tfield = new TextFieldWidget("", 8);
    tfield.addChangeListener(changeSupport);
    
    setLayout(new BorderLayout());
    add(BorderLayout.NORTH, tfield);
  }

  
  public void commit(Property property) {
    
    super.commit(property);
    
    if (!property.isReadOnly())
      property.setValue(tfield.getText());
  }

    
  public boolean isEditable() {
    return tfield.isEditable();
  }
  
  
  public boolean accepts(Property prop) {
    return true;
  }

  
  public void setPropertyImpl(Property property) {

    if (property==null)
      return;
    
    
    String txt = property.getDisplayValue();
    tfield.setText(txt);
    tfield.setEditable(!property.isReadOnly());
    tfield.setVisible(!property.isReadOnly()||txt.length()>0);
    
    defaultFocus = tfield.isEditable() ? tfield : null;
    
    
    changeSupport.setChanged(false);
  }
  
}