
package genj.edit.beans;

import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.time.PointInTime;
import genj.util.swing.Action2;
import genj.util.swing.DateWidget;
import genj.util.swing.ImageIcon;
import genj.util.swing.PopupWidget;
import genj.util.swing.TextFieldWidget;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;


public class DateBean extends PropertyBean {

  private final static ImageIcon PIT = new ImageIcon(PropertyBean.class, "/genj/gedcom/images/Time");
  
  
  private PropertyDate.Format format; 
  private DateWidget date1, date2;
  private PopupWidget choose;
  private JLabel label2;
  private TextFieldWidget phrase;
  private JComponent[][] preferredLayout;
  
  public DateBean() {

    setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    setAlignmentX(0);

    
    List<ChangeFormat> actions = new ArrayList<ChangeFormat>(10);
    for (int i=0;i<PropertyDate.FORMATS.length;i++)
      actions.add(new ChangeFormat(PropertyDate.FORMATS[i]));

    
    date1 = new DateWidget();
    date1.addChangeListener(changeSupport);

    
    choose = new PopupWidget();
    choose.addItems(actions);
    
    
    label2 = new JLabel();
    
    date2 = new DateWidget();
    date2.addChangeListener(changeSupport);
    
    
    phrase = new TextFieldWidget("",10);
    phrase.addChangeListener(changeSupport);
    
    
    setPreferHorizontal(false);
    setFormat(PropertyDate.FORMATS[0]);
    
    
    defaultFocus = date1;
    
    
  }

  @Override
  public void setPreferHorizontal(boolean set) {
    
    if (set) {
      preferredLayout = new JComponent[1][0];
      preferredLayout[0] = new JComponent[] { date1, choose, label2, date2,phrase };
    } else {
      preferredLayout = new JComponent[3][0];
      preferredLayout[0] = new JComponent[] { date1, choose };
      preferredLayout[1] = new JComponent[] { label2, date2 };
      preferredLayout[2] = new JComponent[] { phrase };
    }
  }
  
  @Override
  public Dimension getPreferredSize() {
    
    if (isPreferredSizeSet()) 
      return super.getPreferredSize();
    
    Dimension result = new Dimension();
    for (int y=0;y<preferredLayout.length;y++) {
      Dimension line = new Dimension();
      for (int x=0;x<preferredLayout[y].length;x++) {
        Component c = preferredLayout[y][x];
        if (c.isVisible()) {
          Dimension pref = c.getPreferredSize();
          line.width += pref.width;
          line.height = Math.max(line.height, pref.height);
        }
      }      
      result.width = Math.max(result.width, line.width);
      result.height += line.height;
    }
    return result;
  }
  
  
  @Override
  protected void commitImpl(Property property) {

    PropertyDate p = (PropertyDate)property;
    
    p.setValue(format, date1.getValue(), date2.getValue(), phrase.getText());

    
  }

  
  private void setFormat(PropertyDate.Format set) {

    
    if (format==set)
      return;
    
    
    if (set==PropertyDate.FORMATS[0]) {
      removeAll();
      add(date1);
      add(choose);
      add(label2);
      add(date2);
      add(phrase);
    } else {
      removeAll();
      add(choose);
      add(date1);
      add(label2);
      add(date2);
      add(phrase);
    }
    
    
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
