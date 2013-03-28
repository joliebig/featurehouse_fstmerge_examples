
package genj.edit.beans;

import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.time.PointInTime;
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

  private final static ImageIcon PIT = new ImageIcon(PropertyBean.class, "/genj/gedcom/images/Time");
  private final static NestedBlockLayout 
    H = new NestedBlockLayout("<row><choose/><date1/><label2/><date2/><phrase/></row>"),
    V = new NestedBlockLayout("<table><row><choose/><date1/></row><row><label2/><date2/></row><row><phrase cols=\"2\"/></row></table>");

  
  private PropertyDate.Format format; 
  private DateWidget date1, date2;
  private PopupWidget choose;
  private JLabel label2;
  private TextFieldWidget phrase;
  
  public DateBean() {

    setLayout(V.copy());
    setAlignmentX(0);

    
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
    
    
    phrase = new TextFieldWidget("",10);
    phrase.addChangeListener(changeSupport);
    add(phrase);
    
    
    setPreferHorizontal(false);
    setFormat(PropertyDate.FORMATS[0]);
    
    
    defaultFocus = date1;
    
    
  }

  @Override
  public void setPreferHorizontal(boolean set) {
    
    setLayout(set ? H.copy() : V.copy());
    PropertyDate.Format f = format;
    format = null;
    setFormat(f);
    
    revalidate();
    repaint();
  }
  
  
  @Override
  protected void commitImpl(Property property) {

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
  

  
  public void setPropertyImpl(Property prop) {

    if (prop==null) {
      PointInTime pit = new PointInTime();
      date1.setValue(pit);
      date2.setValue(pit);
      phrase.setText("");
      setFormat(PropertyDate.FORMATS[0]);
    } else {
      PropertyDate date = (PropertyDate)prop;
      date1.setValue(date.getStart());
      date2.setValue(date.getEnd());
      phrase.setText(date.getPhrase());
      setFormat(date.getFormat());
    }    
    
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
