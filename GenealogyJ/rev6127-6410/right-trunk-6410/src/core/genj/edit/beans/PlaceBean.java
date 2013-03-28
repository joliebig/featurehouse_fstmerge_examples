
package genj.edit.beans;

import genj.edit.Options;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.PropertyPlace;
import genj.util.GridBagHelper;
import genj.util.swing.Action2;
import genj.util.swing.ChoiceWidget;
import genj.util.swing.DialogHelper;

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


  public PlaceBean() {
    
    
    changeSupport.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        String confirm = getGlobalConfirmMessage();
        global.setVisible(confirm!=null);
        global.setToolTipText(confirm);
      }
    });
    
    global.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (global.isSelected()) {
          int rc = DialogHelper.openDialog(RESOURCES.getString("choice.global.enable"), DialogHelper.QUESTION_MESSAGE, getGlobalConfirmMessage(), Action2.yesNo(),PlaceBean.this);
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
  
  
  @Override
  protected void commitImpl(Property property) {
    
    
    ((PropertyPlace)property).setValue(getCommitValue(), global.isSelected());
    
    
    setPropertyImpl(property);
    
  }

  
  public void setPropertyImpl(Property prop) {
    
    
    int old = rows;
    rows = 0;
    defaultFocus = null;
    
    Gedcom ged = getRoot().getGedcom();
    PropertyPlace place = (PropertyPlace)prop;
    String value;
    String formatAsString;
    String[] jurisdictions;
    
    if (place==null) {
      sameChoices = new Property[0];
      value = "";
      jurisdictions = new String[0];
      formatAsString = ged.getPlaceFormat();
    } else {
      sameChoices = place.getSameChoices();
      
      value = place.isSecret() ? "" : place.getValue();
      formatAsString = place.getFormatAsString();
      jurisdictions = place.getJurisdictions();
    }
   
    
    if (!Options.getInstance().isSplitJurisdictions || formatAsString.length()==0) {
      createChoice(null, value, PropertyPlace.getAllJurisdictions(ged, -1, true), formatAsString);
    } else {
      String[] format = PropertyPlace.getFormat(ged);
      for (int i=0;i<Math.max(format.length, jurisdictions.length); i++) {
        createChoice(i<format.length ? format[i] : "?", i<jurisdictions.length ? jurisdictions[i] : "", PropertyPlace.getAllJurisdictions(ged, i, true), null);
      }
    }
    
    
    global.setVisible(false);
    global.setSelected(false);
    gh.add(global, 2, rows);
    
    
    gh.addFiller(1,++rows);
    
    
    for (int i=0;i<old;i++)
      remove(0);
    
    
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
    
    
    
    return RESOURCES.getString("choice.global.confirm", ""+sameChoices.length, sameChoices[0].getDisplayValue(), getCommitValue() );
  }
  
} 
