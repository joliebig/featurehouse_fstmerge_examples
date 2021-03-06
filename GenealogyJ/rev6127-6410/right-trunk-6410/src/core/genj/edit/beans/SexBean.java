
package genj.edit.beans;

import genj.gedcom.Property;
import genj.gedcom.PropertySex;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;


public class SexBean extends PropertyBean {

  
  private JRadioButton male = new JRadioButton(PropertySex.getLabelForSex(PropertySex.MALE));
  private JRadioButton female = new JRadioButton(PropertySex.getLabelForSex(PropertySex.FEMALE));
  private JRadioButton last;
  private ButtonGroup group = new ButtonGroup();
  
  
  @Override
  protected void commitImpl(Property property) {
    PropertySex sex = (PropertySex)property; 
    sex.setSex(getSex());
  }
  
  public SexBean() {
    
    
    setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    
    add(male);
    add(female);
    
    String tip = RESOURCES.getString("sex.tip");
    male.setToolTipText(tip);
    female.setToolTipText(tip);
    
    group = new ButtonGroup();
    group.add(male);
    group.add(female);

    ActionHandler handler = new ActionHandler();
    male.addActionListener(handler);
    female.addActionListener(handler);

    
  }
  
  @Override
  public Dimension getMaximumSize() {
    return getPreferredSize();
  }
  
  private class ActionHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      if ( (e.getModifiers()&ActionEvent.CTRL_MASK)!=0 ) {
        group.clearSelection();
        last = null;
      } else {
        if (last==e.getSource()) {
          last = last==male ? female : male;
          last.setSelected(true);
        } else {
          last = (JRadioButton)e.getSource();
        }
        if (getProperty()!=null&&getSex()==((PropertySex)getProperty()).getSex())
          return;
      }
      changeSupport.fireChangeEvent();
    }
  }
  
  
  private int getSex() {

    if (male.isSelected())
      return PropertySex.MALE;
    if (female.isSelected())
      return PropertySex.FEMALE;
    return PropertySex.UNKNOWN;
        
  }

  
  public void setPropertyImpl(Property prop) {
    
    
    last = null;
    defaultFocus = male;
    group.clearSelection();
    
    PropertySex sex = (PropertySex)prop;
    if (sex!=null) 
      switch (sex.getSex()) {
        case PropertySex.MALE:
          male.doClick();
          defaultFocus = male;
          break;
        case PropertySex.FEMALE:
          female.doClick();
          defaultFocus = female;
          break;
      }

    
  }

} 

