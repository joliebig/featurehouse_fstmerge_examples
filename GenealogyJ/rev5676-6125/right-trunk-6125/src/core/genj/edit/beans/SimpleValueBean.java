
package genj.edit.beans;

import genj.gedcom.Property;
import genj.util.swing.TextFieldWidget;

import java.awt.BorderLayout;


public class SimpleValueBean extends PropertyBean {

  
  private TextFieldWidget tfield;

  public SimpleValueBean() {
    
    tfield = new TextFieldWidget("", 8);
    tfield.addChangeListener(changeSupport);
    
    setLayout(new BorderLayout());
    add(BorderLayout.NORTH, tfield);
  }

  
  @Override
  protected void commitImpl(Property property) {
    if (!property.isReadOnly())
      property.setValue(tfield.getText());
  }
  
    
  public boolean isEditable() {
    return tfield.isEditable();
  }
  
  
  public void setPropertyImpl(Property property) {

    if (property==null) {
      tfield.setText("");
      tfield.setEditable(true);
      tfield.setVisible(true);
    } else {
      String txt = property.getDisplayValue();
      tfield.setText(txt);
      tfield.setEditable(!property.isReadOnly());
      tfield.setVisible(!property.isReadOnly()||txt.length()>0);
      defaultFocus = tfield.isEditable() ? tfield : null;
    }    
    
    changeSupport.setChanged(false);
  }
  
}