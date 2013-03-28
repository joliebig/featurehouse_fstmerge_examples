
package genj.edit.beans;

import genj.gedcom.Property;
import genj.util.swing.TextAreaWidget;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;


public class MLEBean extends PropertyBean {

  
  private TextAreaWidget tarea;

  public MLEBean() {
    
    tarea = new TextAreaWidget("",3,20);
    tarea.addChangeListener(changeSupport);
    tarea.setLineWrap(true);
    tarea.setWrapStyleWord(true);

    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, new JScrollPane(tarea));
    
    defaultFocus = tarea;

  }
  
  
  @Override
  protected void commitImpl(Property property) {
    property.setValue(tarea.getText());
  }

  
  public void setPropertyImpl(Property property) {
    tarea.setText(property!=null ? property.getValue() : "");
  }

} 
