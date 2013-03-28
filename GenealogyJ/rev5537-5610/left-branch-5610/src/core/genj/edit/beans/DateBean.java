
package genj.edit.beans;

import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.DateWidget;
import genj.util.swing.ImageIcon;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.PopupWidget;
import genj.util.swing.TextFieldWidget;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;


public class DateBean extends PropertyBean {

  private final static NestedBlockLayout LAYOUT = new NestedBlockLayout("<col><row><a/><b/></row><row><c/><d/></row><row><e wx=\"0.1\"/></row></col>");

  private final static ImageIcon PIT = new ImageIcon(PropertyBean.class, "/genj/gedcom/images/Time");
  
  
  private PropertyDate.Format format; 
  private DateWidget date1, date2;
  private PopupWidget choose;
  private JLabel label2;
  private TextFieldWidget phrase;

  void initialize(Registry setRegistry) {
    super.initialize(setRegistry);
    
    
    setLayout(LAYOUT.copy());
    
    
    List<ChangeFormat> actions = new ArrayList<ChangeFormat>(10);
    for (int i=0;i<PropertyDate.FORMATS.length;i++)
      actions.add(new ChangeFormat(PropertyDate.FORMATS[i]));

    
    choose = new PopupWidget();
    choose.addItems(actions);
    add(choose);
    
    
    date1 = new DateWidget();
    date1.addChangeListener(changeSupport);
    add(date1);

    
    label2 = new JLabel();
    add(label2);
    
    date2 = new DateWidget();
    date2.addChangeListener(changeSupport);
    add(date2);
    
    
    phrase = new TextFieldWidget();
    phrase.addChangeListener(changeSupport);
    add(phrase);
    
    
    defaultFocus = date1;

    
  }
  
  
  public void commit(Property property) {

    super.commit(property);
    
    PropertyDate p = (PropertyDate)property;
    
    p.setValue(format, date1.getValue(), date2.getValue(), phrase.getText());

    
  }

  
  private void setFormat(PropertyDate.Format set) {

    
    if (format==set)
      return;
    
    changeSupport.fireChangeEvent();

    
    format = set;

    
    choose.setToolTipText(format.getName());
    String prefix1= format.getPrefix1Name();
    choose.setIcon(prefix1==null ? PIT : null);
    choose.setText(prefix1==null ? "" : prefix1);
    
    
    if (format.isRange()) {
      date2.setVisible(true);
      label2.setVisible(true);
      label2.setText(format.getPrefix2Name());
    } else {
      date2.setVisible(false);
      label2.setVisible(false);
    }
    
    
    phrase.setVisible(format.usesPhrase());

    
    revalidate();
    repaint();
  }          
  

  
  boolean accepts(Property prop) {
    return prop instanceof PropertyDate;
  }
  public void setPropertyImpl(Property prop) {

    if (prop==null)
      return;
    PropertyDate date = (PropertyDate)prop;
    
    
    date1.setValue(date.getStart());
    date2.setValue(date.getEnd());
    phrase.setText(date.getPhrase());
    setFormat(date.getFormat());
    
    
  }
  
  
  private class ChangeFormat extends Action2 {
    
    private PropertyDate.Format formatToSet;
    
    private ChangeFormat(PropertyDate.Format set) {
      formatToSet = set;
      super.setText(set.getName());
    }
    
    public void actionPerformed(ActionEvent event) {
      setFormat(formatToSet);
      date1.requestFocusInWindow();
    }
    
  } 

} 
