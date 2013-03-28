
package genj.edit.beans;

import genj.gedcom.Property;
import genj.gedcom.PropertyMultilineValue;
import genj.util.Registry;
import genj.util.swing.TextAreaWidget;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;


public class MLEBean extends PropertyBean {

  
  private TextAreaWidget tarea;

  void initialize(Registry setRegistry) {
    super.initialize(setRegistry);
    
    tarea = new TextAreaWidget("",3,20);
    tarea.addChangeListener(changeSupport);
    tarea.setLineWrap(true);
    tarea.setWrapStyleWord(true);

    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, new JScrollPane(tarea));
    
    defaultFocus = tarea;

  }
  
  
  public void commit(Property property) {
    
    super.commit(property);
    
    property.setValue(tarea.getText());
  }

  
  boolean accepts(Property prop) {
    return prop instanceof PropertyMultilineValue;
  }
  public void setPropertyImpl(Property property) {

    if (property==null)
      return;
    
    
    tarea.setText(property.getValue());

    
  }

} 
