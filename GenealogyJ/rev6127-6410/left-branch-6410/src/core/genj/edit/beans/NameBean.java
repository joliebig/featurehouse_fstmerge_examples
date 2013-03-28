
package genj.edit.beans;

import genj.edit.Options;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.PropertyName;
import genj.util.swing.Action2;
import genj.util.swing.ChoiceWidget;
import genj.util.swing.DialogHelper;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.TextFieldWidget;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class NameBean extends PropertyBean {

  private final static NestedBlockLayout LAYOUT = new NestedBlockLayout(
      "<table><row><l/><v wx=\"1\"/></row><row><l/><row><v wx=\"1\"/><check/></row></row><row><l/><v/></row></table>"
  );
  
  
  private Property[] sameLastNames = new Property[0];
  private ChoiceWidget cLast, cFirst;
  private JCheckBox cAll;
  private TextFieldWidget tSuff;

  
  private String getReplaceAllMsg() {
    if (sameLastNames.length<2)
      return null;
    
    
    
    return RESOURCES.getString("choice.global.confirm", ""+sameLastNames.length, ((PropertyName)getProperty()).getLastName(), cLast.getText());
  }
  
  public NameBean() {
    
    setLayout(LAYOUT.copy());

    cLast  = new ChoiceWidget();
    cLast.addChangeListener(changeSupport);
    cLast.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        String msg = getReplaceAllMsg();
        if (msg!=null) {
          cAll.setVisible(true);
          cAll.setToolTipText(msg);
        }
      }
    });
    cLast.setIgnoreCase(true);
    cFirst = new ChoiceWidget();
    cFirst.addChangeListener(changeSupport);
    cFirst.setIgnoreCase(true);
    tSuff  = new TextFieldWidget("", 10); 
    tSuff.addChangeListener(changeSupport);

    cAll = new JCheckBox();
    cAll.setBorder(new EmptyBorder(1,1,1,1));
    cAll.setVisible(false);
    cAll.setRequestFocusEnabled(false);
    
    JLabel lFirst= new JLabel(PropertyName.getLabelForFirstName());
    JLabel lLast = new JLabel(PropertyName.getLabelForLastName());
    JLabel lSuff = new JLabel(PropertyName.getLabelForSuffix());
    
    add(lFirst);
    add(cFirst);

    add(lLast);
    add(cLast);
    add(cAll);

    add(lSuff);
    add(tSuff);

    
    cAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String msg = getReplaceAllMsg();
        if (msg!=null&&cAll.isSelected()) {
          int rc = DialogHelper.openDialog(RESOURCES.getString("choice.global.enable"), DialogHelper.QUESTION_MESSAGE, msg, Action2.yesNo(), NameBean.this);
          cAll.setSelected(rc==0);
        }        
      }
    });
    
    
    
    defaultFocus = cFirst;

  }

  
  @Override
  protected void commitImpl(Property property) {

    PropertyName p = (PropertyName) property;
    
    
    String first = cFirst.getText().trim();
    String last  = cLast .getText().trim();
    String suff  = tSuff .getText().trim();
    
    Gedcom ged = p.getGedcom();
    if (ged!=null) {
      switch (Options.getInstance().correctName) {
      
      case 2:
        last = last.toUpperCase(ged.getLocale());
        cLast.setText(last);
      
      case 1:
        if (first.length()>0) {
          first = Character.toString(first.charAt(0)).toUpperCase(ged.getLocale()) + first.substring(1);
          cFirst.setText(first);
        }
      }
    }

    
    p.setName( first, last, suff, cAll.isSelected());

    
  }

  
  public void setPropertyImpl(Property prop) {
    
    PropertyName name = (PropertyName)prop;
    if (name==null) {
      sameLastNames = new Property[0];
      cLast.setValues(new PropertyName[0]);
      cLast.setText("");
      cFirst.setValues(new PropertyName[0]);
      cFirst.setText("");
    } else {
      
      sameLastNames = name.getSameLastNames();
      
      cLast.setValues(name.getLastNames(true));
      cLast.setText(name.getLastName());
      cFirst.setValues(name.getFirstNames(true));
      cFirst.setText(name.getFirstName()); 
      tSuff.setText(name.getSuffix()); 
    }
    
    cAll.setVisible(false);
    cAll.setSelected(false);

    
  }

} 
