
package genj.edit.beans;

import genj.edit.Options;
import genj.gedcom.Property;
import genj.gedcom.PropertyPlace;
import genj.util.GridBagHelper;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.ChoiceWidget;
import genj.window.WindowManager;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class PlaceBean extends PropertyBean {

  private GridBagHelper gh = new GridBagHelper(this);
  private int rows = 0;
  private JCheckBox global = new JCheckBox();
  
  private Property[] sameChoices = new Property[0];


  void initialize(Registry setRegistry) {
    super.initialize(setRegistry);
    
    changeSupport.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        String confirm = getGlobalConfirmMessage();
        global.setVisible(confirm!=null);
        global.setToolTipText(confirm);
      }
    });
    
    global.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        WindowManager wm = WindowManager.getInstance();
        if (wm!=null&&global.isSelected()) {
          int rc = wm.openDialog(null, RESOURCES.getString("choice.global.enable"), WindowManager.QUESTION_MESSAGE, getGlobalConfirmMessage(),Action2.yesNo(), PlaceBean.this);
          global.setSelected(rc==0);
        }        
      }
    });
    
  }
  
  
  private String getCommitValue() {
    
    boolean hierarchy = Options.getInstance().isSplitJurisdictions && ((PropertyPlace)getProperty()).getFormatAsString().length()>0;
    
    
    StringBuffer result = new StringBuffer();
    for (int c=0, n=getComponentCount(), j=0; c<n; c++) {
      
      
      Component comp = getComponent(c);
      if (comp instanceof ChoiceWidget) {
        
        String jurisdiction = ((ChoiceWidget)comp).getText().trim();
        
        
        if (hierarchy) jurisdiction = jurisdiction.replaceAll(PropertyPlace.JURISDICTION_SEPARATOR, ";"); 
          
        
        if (j++>0)  result.append(PropertyPlace.JURISDICTION_SEPARATOR); 
        result.append(jurisdiction);
        
      }
      
    }

    return result.toString();
  }
  
  
  public void commit(Property property) {
    
    super.commit(property);
    
    
    ((PropertyPlace)property).setValue(getCommitValue(), global.isSelected());
    
    
    
    setProperty(property);
  
  }

  
  boolean accepts(Property prop) {
    return prop instanceof PropertyPlace;
  }
  public void setPropertyImpl(Property prop) {
    PropertyPlace place = (PropertyPlace)prop;
    if (place==null)
      return;
    
    sameChoices = place.getSameChoices();
    
    
    removeAll();
    rows = 0;
    defaultFocus = null;
    
    
    
    
    String value = place.isSecret() ? "" : place.getValue();
   
    
    if (!Options.getInstance().isSplitJurisdictions || place.getFormatAsString().length()==0) {
      createChoice(null, value, place.getAllJurisdictions(-1,true), place.getFormatAsString());
    } else {
      String[] format = place.getFormat();
      String[] jurisdictions = place.getJurisdictions();
      for (int i=0;i<Math.max(format.length, jurisdictions.length); i++) {
        createChoice(i<format.length ? format[i] : "?", i<jurisdictions.length ? jurisdictions[i] : "", place.getAllJurisdictions(i, true), null);
      }
    }

    
    global.setVisible(false);
    global.setSelected(false);
    gh.add(global, 2, rows);
    
    
    gh.addFiller(1,++rows);
    
    
  }
  
  private void createChoice(String label, String value, String[] values, String tip) {
    
    rows++;
    
    if (label!=null) 
      gh.add(new JLabel(label, SwingConstants.RIGHT), 0, rows, 1, 1, GridBagHelper.FILL_HORIZONTAL);
    
    ChoiceWidget choice = new ChoiceWidget();
    choice.setIgnoreCase(true);
    choice.setEditable(true);
    choice.setValues(values);
    choice.setText(value);
    choice.addChangeListener(changeSupport);
    if (tip!=null&&tip.length()>0)
      choice.setToolTipText(tip);
    gh.add(choice, 1, rows, 1, 1, GridBagHelper.GROWFILL_HORIZONTAL);
    
    if (defaultFocus==null) defaultFocus = choice;
    
  }

  
  private String getGlobalConfirmMessage() {
    if (sameChoices.length<2)
      return null;
    
    
    
    return RESOURCES.getString("choice.global.confirm", new String[]{ ""+sameChoices.length, sameChoices[0].getDisplayValue(), getCommitValue() });
  }
  
} 
