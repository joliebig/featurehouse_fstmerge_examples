
package genj.edit.beans;

import genj.gedcom.Property;
import genj.gedcom.PropertyChoiceValue;
import genj.util.GridBagHelper;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.ChoiceWidget;
import genj.window.WindowManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ChoiceBean extends PropertyBean {

  
  private ChoiceWidget choices;
  private JCheckBox global;
  private Property[] sameChoices = new Property[0];
  
  
  private String getGlobalReplaceMsg() {
    if (sameChoices.length<2)
      return null;
    
    
    
    return RESOURCES.getString("choice.global.confirm", new String[]{ ""+sameChoices.length, sameChoices[0].getDisplayValue(), choices.getText()});
  }
  
  void initialize(Registry setRegistry) {
    super.initialize(setRegistry);
    
    
    choices = new ChoiceWidget();
    choices.addChangeListener(changeSupport);
    choices.setIgnoreCase(true);

    
    global = new JCheckBox();
    global.setBorder(new EmptyBorder(1,1,1,1));
    global.setVisible(false);
    global.setRequestFocusEnabled(false);
    
    
    choices.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        String msg = getGlobalReplaceMsg();
        if (msg!=null) {
          global.setVisible(true);
          global.setToolTipText(msg);
        }
      }
    });
    
    
    global.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String msg = getGlobalReplaceMsg();
        WindowManager wm = WindowManager.getInstance();
        if (wm!=null&&msg!=null&&global.isSelected()) {
          int rc = wm.openDialog(null, RESOURCES.getString("choice.global.enable"), WindowManager.QUESTION_MESSAGE, msg, Action2.yesNo(), ChoiceBean.this);
          global.setSelected(rc==0);
        }        
      }
    });
    
    
    GridBagHelper layout = new GridBagHelper(this);
    layout.add(choices, 0, 0, 1, 1, GridBagHelper.GROWFILL_HORIZONTAL);
    layout.add(global, 1, 0);
    layout.addFiller(0,1);
    
    
    defaultFocus = choices;
  }
  
  
  public void commit(Property property) {
    
    super.commit(property);
    
    PropertyChoiceValue choice = (PropertyChoiceValue)property;

    
    String text = choices.getText();
    choice.setValue(text, global.isSelected());
    
    
    choices.setValues(((PropertyChoiceValue)property).getChoices(true));
    choices.setText(text);
    global.setSelected(false);
    global.setVisible(false);
      
    
  }

  
  boolean accepts(Property prop) {
    return prop instanceof PropertyChoiceValue;
  }
  
  public void setPropertyImpl(Property prop) {
    
    if (prop==null)
      return;
    PropertyChoiceValue choice = (PropertyChoiceValue)prop;

    
    
    
    
    choices.setValues(choice.getChoices(true));
    choices.setText(choice.isSecret() ? "" : choice.getDisplayValue());
    global.setSelected(false);
    global.setVisible(false);
    
    
    sameChoices = choice.getSameChoices();
    
    
  }

} 
