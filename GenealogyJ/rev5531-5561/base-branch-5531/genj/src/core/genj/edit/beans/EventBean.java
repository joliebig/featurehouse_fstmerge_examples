
package genj.edit.beans;

import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyEvent;
import genj.gedcom.time.Delta;
import genj.gedcom.time.PointInTime;
import genj.util.Registry;
import genj.util.swing.NestedBlockLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class EventBean extends PropertyBean {
  
  private final static NestedBlockLayout LAYOUT = new NestedBlockLayout("<col><row><at/><age wx=\"1\"/></row><row><known/></row></col>");

  
  private JCheckBox cKnown;
  private JLabel lAgeAt;
  private JTextField tAge;
  
  void initialize(Registry setRegistry) {
    super.initialize(setRegistry);
    
    setLayout(LAYOUT.copy());
    
    lAgeAt = new JLabel();
    
    tAge = new JTextField("", 16); 
    tAge.setEditable(false);
    tAge.setFocusable(false);

    cKnown = new JCheckBox(resources.getString("even.known"));
    cKnown.addActionListener(changeSupport);
    
    add(lAgeAt);
    add(tAge);
    add(cKnown);
      
  }

  
  public void commit(Property property) {
    super.commit(property);
    if (cKnown.isVisible()) {
      ((PropertyEvent)property).setKnownToHaveHappened(cKnown.isSelected());
    }
  }

    
  public boolean isEditable() {
    return cKnown.isVisible();
  }

  
  boolean accepts(Property prop) {
    return prop instanceof PropertyEvent;
  }
  
  public void setPropertyImpl(Property prop) {

    if (prop==null)
      return;
    PropertyEvent event = (PropertyEvent)prop;
    PropertyDate date = event.getDate(true);
    
    
    if (event.getEntity() instanceof Indi) {
    
      Indi indi = (Indi)event.getEntity();
      
      
      String ageat = "even.age";
      String age = "";
      if ("BIRT".equals(event.getTag())) {
        ageat = "even.age.today";
        if (date!=null) {
          Delta delta = Delta.get(date.getStart(), PointInTime.getNow());
          if (delta!=null)
            age = delta.toString();
        }
      } else {
        age = date!=null ? indi.getAgeString(date.getStart()) : resources.getString("even.age.?");
      }
      
      lAgeAt.setText(resources.getString(ageat));
      tAge.setText(age);
      
      lAgeAt.setVisible(true);
      tAge.setVisible(true);
    } else {
      lAgeAt.setVisible(false);
      tAge.setVisible(false);
    }

    
    Boolean known = null;
    
    if (!"EVEN".equals(event.getTag())) 
      known = event.isKnownToHaveHappened();
    
    if (known!=null) {
      cKnown.setSelected(known.booleanValue());
      cKnown.setVisible(true);
      defaultFocus = cKnown;
    } else{
      cKnown.setVisible(false);
      defaultFocus = null;
    }
    
    
  }

} 
