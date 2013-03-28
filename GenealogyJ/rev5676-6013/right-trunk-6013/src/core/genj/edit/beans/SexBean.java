
package genj.edit.beans;

import java.awt.event.ActionEvent;

import genj.gedcom.Property;
import genj.gedcom.PropertySex;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JRadioButton;


public class SexBean extends PropertyBean {

  
  private AbstractButton[] buttons = new AbstractButton[3];
  
  
  @Override
  protected void commitImpl(Property property) {
    PropertySex sex = (PropertySex)property; 
    sex.setSex(getSex());
  }
  
  public SexBean() {
    
    
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      
    
    ButtonHelper bh = new ButtonHelper()
      .setButtonType(JRadioButton.class)
      .setContainer(this);
    bh.createGroup();
    for (int i=0;i<buttons.length;i++)
      buttons[i] = bh.create( new Gender(i) );
    
    
  }
  
  
  private int getSex() {
    
    
    for (int i=0;i<buttons.length;i++) {
      if (buttons[i].isSelected()) 
        return i;
    }
        
    
    return PropertySex.UNKNOWN;
  }

  
  public void setPropertyImpl(Property prop) {
    PropertySex sex = (PropertySex)prop;
    if (sex==null)
      return;
    
    
    buttons[sex.getSex()].setSelected(true);
    defaultFocus = buttons[0];

    
  }
  
  
  private class Gender extends Action2 {
    int sex;
    private Gender(int sex) {
      this.sex = sex;
      setText(PropertySex.getLabelForSex(sex));
    }
    public void actionPerformed(ActionEvent event) {
      SexBean.this.changeSupport.fireChangeEvent();
    }

  } 

} 

