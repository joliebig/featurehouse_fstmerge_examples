
package genj.edit.beans;

import genj.gedcom.Property;
import genj.gedcom.PropertyAge;
import genj.gedcom.time.Delta;
import genj.util.swing.Action2;
import genj.util.swing.ChoiceWidget;
import genj.util.swing.NestedBlockLayout;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;


public class AgeBean extends PropertyBean {
  
  private final static String TEMPLATE = "99y 9m 9d";

  
  private ChoiceWidget choice;
  private ActionUpdate update;
  private String newAge;
  
  
  @Override
  protected void commitImpl(Property property) {
    property.setValue(choice.getText());
  }
  
  public AgeBean() {
    
    choice = new ChoiceWidget(Arrays.asList(PropertyAge.PHRASES));
    choice.addChangeListener(changeSupport);
    
    setLayout(new NestedBlockLayout("<col><row><value/><template/></row><row><action/></row></col>"));
    add(choice);
    add(new JLabel(TEMPLATE));
    
    update =  new ActionUpdate();
    add(new JButton(update));
    
  }
  
  
  public void setPropertyImpl(Property prop) {
    
    PropertyAge age = (PropertyAge)prop;
    
    String txt = age==null? "" : age.getValue(); 

    
    choice.setText(txt);

    if (age!=null) {
      Delta delta = Delta.get(age.getEarlier(), age.getLater());
      newAge = delta==null ? null : delta.getValue();
      update.setEnabled(newAge!=null);
    } else {
      update.setEnabled(false);
    }
    
    
  }
  
  
  private class ActionUpdate extends Action2 {
    
    
    private ActionUpdate() {
      setImage(PropertyAge.IMG);
      setTip(RESOURCES.getString("age.tip"));
    }
    
    public void actionPerformed(ActionEvent event) {
      choice.setText(newAge);
    }
  } 

} 
