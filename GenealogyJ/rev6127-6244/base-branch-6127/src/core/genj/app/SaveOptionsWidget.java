
package genj.app;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.time.PointInTime;
import genj.io.Filter;
import genj.util.Resources;
import genj.util.swing.ChoiceWidget;
import genj.util.swing.DateWidget;
import genj.util.swing.TextFieldWidget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;


 class SaveOptionsWidget extends JTabbedPane {
  
  private final static Resources RESOURCES = Resources.get(SaveOptionsWidget.class);
  
  
  private JCheckBox[] checkEntities = new JCheckBox[Gedcom.ENTITIES.length];
  private JCheckBox[] checkFilters;
  private JTextField  textTags, textMarkers;
  private TextFieldWidget textPassword;
  private JComboBox   comboEncodings;
  private JCheckBox checkFilterEmpties;
  private JCheckBox checkFilterLiving;
  private DateWidget dateEventsAfter, dateBirthsAfter;

      
   SaveOptionsWidget(Gedcom gedcom) {
    
    
    Box options = new Box(BoxLayout.Y_AXIS);
    options.add(new JLabel(RESOURCES.getString("save.options.encoding")));
    comboEncodings = new ChoiceWidget(Gedcom.ENCODINGS, Gedcom.ANSEL);
    comboEncodings.setEditable(false);
    comboEncodings.setSelectedItem(gedcom.getEncoding());
    options.add(comboEncodings);
    options.add(new JLabel(RESOURCES.getString("save.options.password")));
    textPassword = new TextFieldWidget(gedcom.hasPassword() ? gedcom.getPassword() : "", 10);
    textPassword.setEditable(gedcom.getPassword()!=Gedcom.PASSWORD_UNKNOWN);
    options.add(textPassword);
    
    
    Box types = new Box(BoxLayout.Y_AXIS);
    for (int t=0; t<Gedcom.ENTITIES.length; t++) {
      checkEntities[t] = check(Gedcom.getName(Gedcom.ENTITIES[t], true), false);
      types.add(checkEntities[t]);
    }
    
    
    Box props = new Box(BoxLayout.Y_AXIS);
    props.add(new JLabel(RESOURCES.getString("save.options.exclude.tags")));
    textTags = new TextFieldWidget(RESOURCES.getString("save.options.exclude.tags.eg"), 10).setTemplate(true);
    props.add(textTags);
    props.add(new JLabel(RESOURCES.getString("save.options.exclude.markers")));
    textMarkers = new TextFieldWidget(RESOURCES.getString("save.options.exclude.markers.eg"), 10).setTemplate(true);
    props.add(textMarkers);
    props.add(new JLabel(RESOURCES.getString("save.options.exclude.events")));
    dateEventsAfter = new DateWidget();
    dateEventsAfter.setOpaque(false);
    props.add(dateEventsAfter);
    props.add(new JLabel(RESOURCES.getString("save.options.exclude.indis")));
    dateBirthsAfter = new DateWidget();
    dateBirthsAfter.setOpaque(false);
    props.add(dateBirthsAfter);
    checkFilterLiving = check(RESOURCES.getString("save.options.exclude.living"),false);
    props.add(checkFilterLiving);
    checkFilterEmpties = check(RESOURCES.getString("save.options.exclude.empties"), false);
    props.add(checkFilterEmpties);
       
    
    add(RESOURCES.getString("save.options"                  ), options);
    add(RESOURCES.getString("save.options.filter.entities"  ), types);
    add(RESOURCES.getString("save.options.filter.properties"), props);
    
    
  }
  
  private JCheckBox check(String text, boolean selected) {
    JCheckBox result = new JCheckBox(text, selected);
    result.setOpaque(false);
    return result;
  }

  
  public String getPassword() {
    return textPassword.getText();
  }

  
  public String getEncoding() {
    return comboEncodings.getSelectedItem().toString();
  }
  
  
  public Filter[] getFilters() {
    
    
    List result = new ArrayList(10);
    
    
    result.add(new FilterByType(checkEntities));
    
    
    result.add(new FilterProperties(textTags.getText().split(","), textMarkers.getText().split(",")));
    
    
    PointInTime eventsAfter = dateEventsAfter.getValue();
    if (eventsAfter!=null&&eventsAfter.isValid())
      result.add(new FilterEventsAfter(eventsAfter));
    
    
    PointInTime birthsAfter = dateBirthsAfter.getValue();
    if (birthsAfter!=null&&birthsAfter.isValid())
      result.add(new FilterIndividualsBornAfter(birthsAfter));
    
    
    if (checkFilterLiving.isSelected())
      result.add(new FilterLivingIndividuals());
        
    
    if (checkFilterEmpties.isSelected())
      result.add(new FilterEmpties());
    
    
    return (Filter[])result.toArray(new Filter[result.size()]);
  }
  
  
  private static class FilterEmpties implements Filter {
   
    public boolean checkFilter(Property property) {
      for (int i = 0; i < property.getNoOfProperties(); i++) {
        if (checkFilter(property.getProperty(i)))
            return true;
      }
      return property.getValue().trim().length()>0;
    }
    
    public String getFilterName() {
      return toString();
    }
    
  }

  
  private static class FilterIndividualsBornAfter implements Filter {
    
    private PointInTime after;
    
    
    private FilterIndividualsBornAfter(PointInTime after) {
      this.after = after;
    }
    
    
    public boolean checkFilter(Property property) {
      if (property instanceof Indi) {
        Indi indi = (Indi)property;
        PropertyDate birth = indi.getBirthDate();
        if (birth!=null) return birth.getStart().compareTo(after)<0;
      }
        
      
      return true;
    }
    
    public String getFilterName() {
      return toString();
    }
  }
  
  
  private static class FilterLivingIndividuals implements Filter {

    private FilterLivingIndividuals() {
    }

    
    public boolean checkFilter(Property property) {
      if (property instanceof Indi) {
        return ((Indi)property).isDeceased();
      }

     
     return true;
   }

   public String getFilterName() {
     return toString();
   }
 }

  
  private static class FilterEventsAfter implements Filter {
    
    private PointInTime after;
    
    
    private FilterEventsAfter(PointInTime after) {
      this.after = after;
    }
    
    
    public boolean checkFilter(Property property) {
      PropertyDate when = property.getWhen();
      return when==null || when.getStart().compareTo(after)<0;
    }
    public String getFilterName() {
      return toString();
    }
  }
  
  
  private static class FilterProperties implements Filter {
    
    
    private String[] tags;
    
    
    private String[] markers;
    
    
    public FilterProperties(String[] tags, String[] markers) {
      this.tags = tags;
      this.markers = markers;
      
    }
        
    
    public boolean checkFilter(Property property) {
      
      for (String tag : tags)
        if (tag.equals(property.getTag())) 
          return false;
      
      for (String marker : markers)
        if (property.getProperty(marker)!=null)
          return false;
      
      return true;
    }
    
    public String getFilterName() {
      return toString();
    }
  } 
  
  
  private static class FilterByType implements Filter {
    
    
    private Set<String> excluded = new HashSet<String>();
    
    
    FilterByType(JCheckBox[] checks) {
      for (int t=0; t<checks.length; t++) {
      	if (checks[t].isSelected())
          excluded.add(Gedcom.ENTITIES[t]);
      }
    }
    
    public boolean checkFilter(Property property) {
      return !(property instanceof Entity) || !excluded.contains(property.getTag());
    }
    public String getFilterName() {
      return toString();
    }
  } 

} 
