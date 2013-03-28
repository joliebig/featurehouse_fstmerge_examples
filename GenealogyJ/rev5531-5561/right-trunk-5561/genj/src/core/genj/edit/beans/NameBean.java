
package genj.edit.beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import genj.gedcom.Property;
import genj.gedcom.PropertyName;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.ChoiceWidget;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.TextFieldWidget;
import genj.window.WindowManager;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class NameBean extends PropertyBean {

  private final static NestedBlockLayout LAYOUT = new NestedBlockLayout("<col><row><l/><v wx=\"1\"/></row><row><l/><v wx=\"1\"/><check/></row><row><l/><v wx=\"1\"/></row></col>");
  
  
  private Property[] sameLastNames;
  private ChoiceWidget cLast, cFirst;
  private JCheckBox cAll;
  private TextFieldWidget tSuff;

  
  private String getReplaceAllMsg() {
    if (sameLastNames.length<2)
      return null;
    
    
    
    return resources.getString("choice.global.confirm", new String[]{ ""+sameLastNames.length, ((PropertyName)getProperty()).getLastName(), cLast.getText()});
  }
  
  void initialize(Registry setRegistry) {
    super.initialize(setRegistry);
    
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
    
    add(new JLabel(PropertyName.getLabelForFirstName()));
    add(cFirst);

    add(new JLabel(PropertyName.getLabelForLastName()));
    add(cLast);
    add(cAll);

    add(new JLabel(PropertyName.getLabelForSuffix()));
    add(tSuff);

    
    cAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String msg = getReplaceAllMsg();
        WindowManager wm = WindowManager.getInstance(NameBean.this);
        if (wm!=null&&msg!=null&&cAll.isSelected()) {
          int rc = wm.openDialog(null, resources.getString("choice.global.enable"), WindowManager.QUESTION_MESSAGE, msg, Action2.yesNo(), NameBean.this);
          cAll.setSelected(rc==0);
        }        
      }
    });
    
    
    
    defaultFocus = cFirst;

  }

  
  public void commit(Property property) {

    super.commit(property);
    
    
    String first = cFirst.getText().trim();
    String last  = cLast .getText().trim();
    String suff  = tSuff .getText().trim();

    
    PropertyName p = (PropertyName) property;
    p.setName( first, last, suff, cAll.isSelected());

    
  }

  
  boolean accepts(Property prop) {
    return prop instanceof PropertyName;
  }
  public void setPropertyImpl(Property prop) {
    PropertyName name = (PropertyName)prop;
    if (name==null)
      return;
    
    
    sameLastNames = name.getSameLastNames();
    
    
    cLast.setValues(name.getLastNames(true));
    cLast.setText(name.getLastName());
    cFirst.setValues(name.getFirstNames(true));
    cFirst.setText(name.getFirstName()); 
    tSuff.setText(name.getSuffix()); 
    
    cAll.setVisible(false);
    cAll.setSelected(false);

    
  }

} 
