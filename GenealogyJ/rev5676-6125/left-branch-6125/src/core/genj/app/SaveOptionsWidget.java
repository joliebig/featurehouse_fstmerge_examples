
package genj.app;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.TagPath;
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
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;


 class SaveOptionsWidget extends JTabbedPane {
  
  
  private JCheckBox[] checkEntities = new JCheckBox[Gedcom.ENTITIES.length];
  private JCheckBox[] checkFilters;
  private JTextField  textTags, textValues;
  private TextFieldWidget textPassword;
  private JComboBox   comboEncodings;
  private JCheckBox checkFilterEmpties;
   private JCheckBox checkFilterLiving;
  private Resources resources = Resources.get(this);
  private DateWidget dateEventsAfter, dateBirthsAfter;
  
  
  private Filter[] filters;

      
   SaveOptionsWidget(Gedcom gedcom, Filter[] filters) {
    
    
    Box options = new Box(BoxLayout.Y_AXIS);
    options.add(new JLabel(resources.getString("save.options.encoding")));
    comboEncodings = new ChoiceWidget(Gedcom.ENCODINGS, Gedcom.ANSEL);
    comboEncodings.setEditable(false);
    comboEncodings.setSelectedItem(gedcom.getEncoding());
    options.add(comboEncodings);
    options.add(new JLabel(resources.getString("save.options.password")));
    textPassword = new TextFieldWidget(gedcom.hasPassword() ? gedcom.getPassword() : "", 10);
    textPassword.setEditable(gedcom.getPassword()!=Gedcom.PASSWORD_UNKNOWN);
    options.add(textPassword);
    
    
    Box types = new Box(BoxLayout.Y_AXIS);
    for (int t=0; t<Gedcom.ENTITIES.length; t++) {
      checkEntities[t] = new JCheckBox(Gedcom.getName(Gedcom.ENTITIES[t], true), true);
      types.add(checkEntities[t]);
    }
    
    
    Box props = new Box(BoxLayout.Y_AXIS);
    props.add(new JLabel(resources.getString("save.options.exclude.tags")));
    textTags = new TextFieldWidget(resources.getString("save.options.exclude.tags.eg"), 10).setTemplate(true);
    props.add(textTags);
    props.add(new JLabel(resources.getString("save.options.exclude.values")));
    textValues = new TextFieldWidget(resources.getString("save.options.exclude.values.eg"), 10).setTemplate(true);
    props.add(textValues);
    props.add(new JLabel(resources.getString("save.options.exclude.events")));
    dateEventsAfter = new DateWidget();
    props.add(dateEventsAfter);
    props.add(new JLabel(resources.getString("save.options.exclude.indis")));
    dateBirthsAfter = new DateWidget();
    props.add(dateBirthsAfter);
    checkFilterLiving = new JCheckBox(resources.getString("save.options.exclude.living"));
    props.add(checkFilterLiving);
    checkFilterEmpties = new JCheckBox(resources.getString("save.options.exclude.empties"));
    props.add(checkFilterEmpties);
       
    
    Box others = new Box(BoxLayout.Y_AXIS);
    this.filters = filters;
    this.checkFilters = new JCheckBox[filters.length];
    for (int i=0; i<checkFilters.length; i++) {
      checkFilters[i] = new JCheckBox(filters[i].getFilterName(), false);
      others.add(checkFilters[i]);
    }
    
    
    add(resources.getString("save.options"                  ), options);
    add(resources.getString("save.options.filter.entities"  ), types);
    add(resources.getString("save.options.filter.properties"), props);
    add(resources.getString("save.options.filter.views"     ), others);
    
    
  }

  
  public String getPassword() {
    return textPassword.getText();
  }

  
  public String getEncoding() {
    return comboEncodings.getSelectedItem().toString();
  }
  
  
  public Filter[] getFilters() {
    
    
    List result = new ArrayList(10);
    
    
    FilterByType fbt = FilterByType.get(checkEntities);
    if (fbt!=null) result.add(fbt);
    
    
    FilterProperties fp = FilterProperties.get(textTags.getText(), textValues.getText());
    if (fp!=null) result.add(fp);
    
    
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
    
    
    for (int f=0; f<filters.length; f++) {
      if (checkFilters[f].isSelected())
    	 result.add(filters[f]);
    }
    
    
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
    
    
    private Set tags;
    
    
    private Set paths;
    
    
    private String[] values;
    
    
    private FilterProperties(Set tags, Set paths, List values) {
      this.tags = tags;
      this.paths = paths;
      this.values = (String[])values.toArray(new String[0]);
      
    }
    
    
    protected static FilterProperties get(String sTags, String sValues) {
      
      
      Set tags = new HashSet();
      Set paths = new HashSet();
      
      StringTokenizer tokens = new StringTokenizer(sTags, ",");
      while (tokens.hasMoreTokens()) {
        String s = tokens.nextToken().trim();
        if (s.indexOf(':')>0) {
          try {
            paths.add(new TagPath(s));
          } catch (IllegalArgumentException e) { 
          }
        } else {
          tags.add(s);
        }
      }
      
      List values = new ArrayList();
      tokens = new StringTokenizer(sValues, ",");
      while (tokens.hasMoreTokens()) {
        values.add(tokens.nextToken().trim());
      }
     
      
      return (tags.isEmpty() && paths.isEmpty() && values.isEmpty()) ? null : new FilterProperties(tags, paths, values);
    }
    
    
    public boolean checkFilter(Property property) {
      
      if (property instanceof Entity)
        return true;
      
      if (tags.contains(property.getTag())) return false;
      
      if (paths.contains(property.getPath())) return false;
      
      return accept(property.getValue());
    }
    
    
    private boolean accept(String value) {
      if (value==null) return true;
      for (int i=0; i<values.length; i++) {
        if (value.indexOf(values[i])>=0) return false;
      }
      return true;
    }

    public String getFilterName() {
      return toString();
    }
  } 
  
  
  private static class FilterByType implements Filter {
    
    
    private Set types = new HashSet();
    
    
    protected static FilterByType get(JCheckBox[] checks) {
      
      FilterByType result = new FilterByType();
      
      for (int t=0; t<checks.length; t++) {
      	if (checks[t].isSelected())
          result.types.add(Gedcom.ENTITIES[t]);
      }
      return result.types.size()<Gedcom.ENTITIES.length ? result : null;
    }
    
    public boolean checkFilter(Property property) {
      if (property instanceof Entity && !types.contains(property.getTag()))
          return false;
      return true;
    }
    public String getFilterName() {
      return toString();
    }
  } 

} 
