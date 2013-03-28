
package genj.gedcom;

import genj.util.Resources;
import genj.util.WordBuffer;
import genj.util.swing.ImageIcon;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class Property implements Comparable<Property> {

  
  protected final static String 
    UNSUPPORTED_TAG = "Unsupported Tag";
  
  private static final Pattern FORMAT_PATTERN = Pattern.compile("\\{(.*?)\\$(.)(.*?)\\}");
    
  
  private Property parent=null;
  
  
  
  private List<Property> children = null;
  
  
  protected ImageIcon image, imageErr;
  
  
  protected boolean isTransient = false;

  
  private boolean isPrivate = false;
  
  
  protected final static Resources resources = Gedcom.resources;

  
  public final static String LABEL = resources.getString("prop");

  
  private MetaProperty meta = null;

  
   void afterAddNotify() {
    
  }

  
   void beforeDelNotify() {
    
    
    delProperties();
    
    
  }
  
  
   void propagateXRefLinked(PropertyXRef property1, PropertyXRef property2) {
    if (parent!=null)
      parent.propagateXRefLinked(property1, property2);
  }
  
  
   void propagateXRefUnlinked(PropertyXRef property1, PropertyXRef property2) {
    if (parent!=null)
      parent.propagateXRefUnlinked(property1, property2);
  }
  
  
   void propagatePropertyAdded(Property container, int pos, Property added) {
    if (parent!=null)
      parent.propagatePropertyAdded(container, pos, added);
  }
  
  
   void propagatePropertyDeleted(Property container, int pos, Property deleted) {
    if (parent!=null)
      parent.propagatePropertyDeleted(container, pos, deleted);
  }
  
  
   void propagatePropertyChanged(Property property, String oldValue) {
    if (parent!=null)
      parent.propagatePropertyChanged(property, oldValue);
  }
  
  
   void propagatePropertyMoved(Property property, Property moved, int from, int to) {
    if (parent!=null)
      parent.propagatePropertyMoved(property, moved, from, to);
  }
  
  
  public boolean addFile(File file) {
    
    if (!getMetaProperty().allows("FILE")) {
      
      if (!getMetaProperty().allows("OBJE")) 
        return false;
      
      return addProperty("OBJE", "").addFile(file);
    }
    
    List<PropertyFile> pfiles = getProperties(PropertyFile.class);
    PropertyFile pfile;
    if (pfiles.isEmpty()) {
      pfile = (PropertyFile)addProperty("FILE", "");
    } else {
      pfile = (PropertyFile)pfiles.get(0);
    }
    
    return pfile.addFile(file);
  }
  
  
  public boolean addMedia(Media media) {
    
    if (!getMetaProperty().allows("OBJE")) 
      return false;
    
    PropertyMedia xref = new PropertyMedia();
    addProperty(xref);
    xref.setValue(media.getId());
    try {
      xref.link();
    } catch (GedcomException e) {
      Gedcom.LOG.log(Level.FINE, "unexpected", e);
      delProperty(xref);
      return false;
    }
    return true;
  }
  
  
  public Property addProperty(String tag, String value) {
    try {
      return addProperty(tag, value, -1);
    } catch (GedcomException e) {
      
      return addProperty(new PropertySimpleReadOnly(tag, value), -1);
    }
  }
  
  
  public Property addProperty(String tag, String value, int pos) throws GedcomException {
    return addProperty(getMetaProperty().getNested(tag, true).create(value), pos);
  }
  
  
  public Property addSimpleProperty(String tag, String value, int pos) {
    return addProperty(new PropertySimpleValue(tag, value), pos);
  }
  
  
   Property addProperty(Property prop) {
    return addProperty(prop, -1);
  }

  
   Property addProperty(Property child, int pos) {
    
    
    if (child.getParent()!=null||child.getNoOfProperties()>0)
      throw new IllegalArgumentException("Can't add a property that is already contained or contains properties");
    
    
    if (pos<0) {
      MetaProperty meta = getMetaProperty();
      pos = 0;
      int index = meta.getNestedIndex(child.getTag());
      for (;pos<getNoOfProperties();pos++) {
        if (meta.getNestedIndex(getProperty(pos).getTag())>index)
          break;
      }
    } else {
      
      if (pos>getNoOfProperties())
        pos = getNoOfProperties();
    }
    
    
    if (children==null)
      children = new ArrayList<Property>();
    children.add(pos, child);
    
    if (isTransient) child.isTransient = true;
    
    child.parent = this;

    
    propagatePropertyAdded(this, pos, child);
    
    
    child.afterAddNotify();
    
    
    return child;
  }
  
  
  public void delProperties() {
    if (children!=null) {
      
      Property[] cs = (Property[])children.toArray(new Property[children.size()]);
      for (int c = cs.length-1; c>=0; c--) 
        delProperty(cs[c]);
      if (children.isEmpty()) children = null;
    }
  }
  
  
  public void delProperties(String tag) {
    if (children!=null) {
      Property[] cs = (Property[])children.toArray(new Property[children.size()]);
      for (int c = 0; c < cs.length; c++) {
        if (cs[c].getTag().equals(tag))
          delProperty(cs[c]);
      }
      if (children.isEmpty()) children = null;
    }
  }
  
  
  public void delProperty(Property deletee) {
    
    if (children==null)
      throw new IndexOutOfBoundsException("no such child");

    
    int pos = 0;
    for (;;pos++) {
      if (children.get(pos)==deletee)
        break;
    }

    
    delProperty(pos);
    
  }

  
  public void delProperty(int pos) {

    
    if (children==null||pos<0||pos>=children.size())
      throw new IndexOutOfBoundsException("No property "+pos);
    Property removed = (Property)children.get(pos);

    
    removed.beforeDelNotify(); 

    
    children.remove(pos);
    removed.parent = null;
    removed.meta = null;

    
    propagatePropertyDeleted(this, pos, removed);

    
  }
  
  
  public void moveProperties(List<Property> properties, int pos) {
    
    
    for (int i = 0; i < properties.size(); i++) {
      Property prop = (Property)properties.get(i);
      pos = moveProperty(prop, pos);
    }
    
  }

  
  public int moveProperty(Property prop, int to) {
    return moveProperty(children.indexOf(prop), to);
  }
  
  
  public int moveProperty(int from, int to) {
    Property prop = (Property)children.remove(from);
    if (from<to) to--;
    children.add(to, prop);
    
    propagatePropertyMoved(this, prop, from, to);
    
    return to+1;
  }
  
  
  public String getDeleteVeto() {
    return null;
  }

  
  public Entity getEntity() {
    return parent==null ? null : parent.getEntity();
  }

  
  public Gedcom getGedcom() {
    return parent!=null ? parent.getGedcom() : null;
  }
  
  
  public ImageIcon getImage(boolean checkValid) {
    
    
    if (!checkValid||isValid()) {
      if (image==null) 
        image = getMetaProperty().getImage(); 
      return image;
    }
    
    
    if (imageErr==null) 
      imageErr = getMetaProperty().getImage("err"); 
      
    return imageErr;
  }

  
  public int getNoOfProperties() {
    return children==null?0:children.size();
  }

  
  public Property getParent() {
    return parent;
  }
  
  
  public TagPath getPathToNested(Property nested) {    
    Stack<String> result = new Stack<String>();
    nested.getPathToContaining(this, result);
    return new TagPath(result);
  }
  
  private void getPathToContaining(Property containing, Stack<String> result) {
    result.push(getTag());
    if (containing==this)
      return;
    if (parent==null)
      throw new IllegalArgumentException("couldn't find containing "+containing);
    parent.getPathToContaining(containing, result);
  }
  
  
  public TagPath getPath() {
    return getPath(false);
  }
  
  
  public TagPath getPath(boolean unique) {

    Stack<String> stack = new Stack<String>();

    
    String tag = getTag();
    Property parent = getParent();
    while (parent!=null) {
      
      
      if (unique) {
        int qualifier = 0;
        for (int i=0, j=parent.getNoOfProperties(); i<j; i++) {
          Property sibling = parent.getProperty(i);
          if (sibling==this) break;
          if (sibling.getTag().equals(tag)) qualifier++;
        }
        stack.push(tag + "#" + qualifier);
      } else {
        stack.push(tag);
      }

      
      tag = parent.getTag();
      parent = parent.getParent();
    }
    
    
    stack.push(tag);

    
    return new TagPath(stack);
    
  }
  
  
  public boolean contains(Property prop) {
    if (children==null)
      return false;
    for (int c = 0; c < children.size(); c++) {
      Property child = (Property)children.get(c);
      if (child==prop||child.contains(prop))
        return true;
    }
    return false;
  }

  
  public boolean isContained(Property in) {
    Property parent = getParent();
    if (parent==in) return true;
    return parent==null ? false : parent.isContained(in);
  }

  
  public boolean hasProperties(List<Property> props) {
    return children==null ? false : children.containsAll(props);
  }
  
  
  public Property[] getProperties() {
    return children==null ? new Property[0] : toArray(children);
  }
  
  
  public List<Property> findProperties(Pattern tag, Pattern value) {
    
    List<Property> result = new ArrayList<Property>();
    
    if (value==null) value = Pattern.compile(".*");
    
    findPropertiesRecursively(result, tag, value, true);
    
    return result;
  }

  protected boolean findPropertiesRecursivelyTest(Pattern tag, Pattern value) {
    return tag.matcher(getTag()).matches() && value.matcher(getValue()).matches(); 
  }
  
  private void findPropertiesRecursively(Collection<Property> result, Pattern tag, Pattern value, boolean recursively) {
    
    if (findPropertiesRecursivelyTest(tag, value))
      result.add(this);
    
    for (int i=0, j=getNoOfProperties(); i<j ; i++) {
      if (recursively) getProperty(i).findPropertiesRecursively(result, tag, value, recursively);
    }
    
  }
  
  
  public Property[] getProperties(String tag) {
    return getProperties(tag, true);
  }
  
  
  public Property[] getProperties(String tag, boolean validOnly) {
    ArrayList<Property> result = new ArrayList<Property>(getNoOfProperties());
    for (int i=0, j = getNoOfProperties(); i<j ; i++) {
      Property prop = getProperty(i);
      if (prop.getTag().equals(tag)&&(!validOnly||prop.isValid()))
        result.add(prop);
    }
    return toArray(result);
  }
  
  
  public <T> List<T> getProperties(Class<T> type) {
    List<T> props = new ArrayList<T>(10);
    getPropertiesRecursively(props, type);
    return props;
  }
  
  @SuppressWarnings("unchecked")
  private <T> void getPropertiesRecursively(List<T> props, Class<T> type) {
    for (int c=0;c<getNoOfProperties();c++) {
      Property child = getProperty(c);
      if (type.isAssignableFrom(child.getClass())) {
        props.add((T)child);
      }
      child.getPropertiesRecursively(props, type);
    }
  }

  
  public int getPropertyPosition(Property prop) {
    if (children==null)
      throw new IllegalArgumentException("no such property");
    for (int i=0;i<children.size();i++) {
      if (children.get(i)==prop)
        return i;
    }
    throw new IllegalArgumentException("no such property");
  }

  
  public Property getProperty(int n) {
    if (children==null)
      throw new IndexOutOfBoundsException("no property "+n);
    return (Property)children.get(n);
  }

  
  public Property getProperty(String tag) {
    return getProperty(tag, true);
  }

  
  public Property getProperty(String tag, boolean validOnly) {
    
    if (tag.indexOf(':')>0) throw new IllegalArgumentException("Path not allowed");
    
    
    if (children!=null) {
      for (int i=0, j=children.size();i<j;i++) {
        Property child = (Property)children.get(i);
        if (!child.getTag().equals(tag)) continue;
        if (validOnly&&!child.isValid()) continue;
        return child;
      }
    }
    
    return null;
  }

  
  public Property getPropertyByPath(String path) {
    
    
    
    
    return getProperty(new TagPath(path));
  }
  
  
  public Property getProperty(TagPath path) {
    return getProperty(path, true);
  }
  
  
  public Property getProperty(TagPath path, boolean backtrack) {
    
    final Property[] result = new Property[1];

    PropertyVisitor visitor = new PropertyVisitor() {
      protected boolean leaf(Property prop) {
        result[0] = prop;
        return false;
      }
    };
    
    path.iterate(this, visitor, backtrack);
    
    return result[0];
  }
  
  
  public Property[] getProperties(TagPath path) {
    
   final  List<Property> result = new ArrayList<Property>(10);

    PropertyVisitor visitor = new PropertyVisitor() {
      protected boolean leaf(Property prop) {
        result.add(prop);
        return true;
      }
    };
    
    path.iterate(this, visitor);
    
    return Property.toArray(result);
  }
















































































  
  public abstract String getTag();

  
   Property init(MetaProperty meta, String value) throws GedcomException {
    
    this.meta = meta;
    
    setValue(value);
    
    return this;
  }
  
  
  abstract public String getValue();
  
  
  public String getDisplayValue() {
    return getValue();
  }

  
  public String getPropertyValue(String tag) {
    Property child = getProperty(tag);
    return child!=null ? child.getValue() : "";
  }

  
  public String getPropertyDisplayValue(String tag) {
    Property child = getProperty(tag);
    return child!=null ? child.getDisplayValue() : "";
  }

  
  @Override
  public String toString() {
    
    WordBuffer result = new WordBuffer(" ");
    result.append(getPropertyName());
    
    String val = getDisplayValue();
    if (val.length()>0) 
      result.append(val);
    
    Property date = getProperty("DATE");
    if (date instanceof PropertyDate && date.isValid()) 
      result.append(date.getDisplayValue());

    Property plac = getProperty("PLAC");
    if (plac!=null) {
      String s = plac.getDisplayValue();
      if (s.length()>0) 
        result.append(plac.getDisplayValue());
    } else {
      Property addr = getProperty("ADDR");
      if (addr!=null) {
        Property city = addr.getProperty("CITY");
        if (city!=null) {
          String s = city.getDisplayValue();
          if (s.length()>0) 
            result.append(s);
        }
      }
    }
    return result.toString();
  }


  
  public String getValue(final TagPath path, String fallback) {
    Property prop = getProperty(path);
    return prop==null ? fallback : prop.getValue();
  }
  
  
  public Property setValue(final TagPath path, final String value) {

    final Property[] result = new Property[1];
    
    PropertyVisitor visitor = new PropertyVisitor() {
      protected boolean leaf(Property prop) {
        
        if (prop instanceof PropertyXRef && ((PropertyXRef)prop).getTarget()!=null) 
          prop = prop.getParent().addProperty(prop.getTag(), "");
        
        prop.setValue(value);
        result[0] = prop;
        
        return false;
      }
      protected boolean recursion(Property parent,String child) {
        if (parent.getProperty(child, false)==null)
          parent.addProperty(child, "");
        return true;
      }
    };
    
    path.iterate(this, visitor);

    
    return result[0];
  }
  
  
  public abstract void setValue(String value);

  
  public boolean isValid() {
    return true;
  }

  
  public int compareTo(Property that) {
    
    return compare(this.getDisplayValue(), that.getDisplayValue() );
  }
  
  
  protected int compare(String s1, String s2) {
    
    
    
    
    
    
    
    
    Gedcom ged = getGedcom();
    if (ged!=null)
      return ged.getCollator().compare(s1,s2);
      
    
    return s1.compareTo(s2);
  }
  
  
  public boolean isTransient() {
    return isTransient;
  }

  
  public boolean isReadOnly() {
    return false;
  }

  
  public final Property addDefaultProperties() {
    
    
    if (getEntity()==null) throw new IllegalArgumentException("addDefaultProperties() while getEntity()==null!");
    
    
    MetaProperty[] subs = getNestedMetaProperties(MetaProperty.WHERE_DEFAULT); 
    for (int s=0; s<subs.length; s++) {
      if (getProperty(subs[s].getTag())==null)
        addProperty(subs[s].getTag(), "").addDefaultProperties();
    }

    
    return this;
  }
  
  
  public MetaProperty getMetaProperty() {
    if (meta==null)
      meta = getGedcom().getGrammar().getMeta(getPath());    
    return meta;
  }

  
  public MetaProperty[] getNestedMetaProperties(int filter) {
    return getMetaProperty().getAllNested(this, filter);
  }

  
  public static Property[] toArray(Collection<Property> ps) {
    return ps.toArray(new Property[ps.size()]);
  }
  
  
  public boolean isPrivate() {
    return isPrivate;
  }
  
  
  public boolean isSecret() {
    return isPrivate && getGedcom().getPassword()==Gedcom.PASSWORD_UNKNOWN;
  }
  
  
  public void setPrivate(boolean set, boolean recursively) {
    
    
    if (recursively) {
      for (int c=0;c<getNoOfProperties();c++) {
        Property child = getProperty(c);
        child.setPrivate(set, recursively);
      }
    }
    isPrivate = set;
    
    
    propagatePropertyChanged(this, getValue());
    
    
  }

  
  public String getPropertyInfo() {
    return getMetaProperty().getInfo();
  }
  
  
  public String getPropertyName() {
    return Gedcom.getName(getTag());
  }
  
  
  public static String getPropertyNames(Iterable<? extends Property> properties, int limit) {
    
    WordBuffer result = new WordBuffer(", ");
    int i=0;
    for (Property prop : properties) {
      if (i==limit) {
        result.append("...");
        break;
      }
      result.append(prop.getPropertyName());
    }
    return result.toString();
  }
  
  
  public static List<Property> normalize(List<? extends Property> properties) {
    
    ArrayList<Property> result = new ArrayList<Property>(properties.size());
    
    for (Property prop : properties) {
      if (prop.isTransient())
        continue;
      
      Property parent = prop.getParent();
      while (parent!=null) {
        if (properties.contains(parent)) break;
        parent = parent.getParent();
      }
      if (parent==null) result.add(prop);
    }
    
    
    return result;
  }
  
  
  public String format(String format) {
    return format(format, PrivacyPolicy.PUBLIC);
  }
  
  
  public String format(String format, PrivacyPolicy policy) {
  
    
    Matcher matcher = FORMAT_PATTERN.matcher(format);
    
    StringBuffer result = new StringBuffer(format.length()+20);
    int masked = 0;
    int matches = 0;
    int cursor = 0;
    
    while (matcher.find()) {
      
      result.append(format.substring(cursor, matcher.start()));
      
      String prefix = matcher.group(1);
      char marker = format.charAt(matcher.start(2));
      String suffix = matcher.group(3);
      
      Property prop;
      String value;
      switch (marker) {
        case 'D' : { prop = getProperty("DATE"); value = (prop instanceof PropertyDate)&&prop.isValid() ? prop.getDisplayValue() : ""; break; }
        case 'y': { prop = getProperty("DATE"); value = (prop instanceof PropertyDate)&&prop.isValid() ? Integer.toString(((PropertyDate)prop).getStart().getYear()) : ""; break; }
        case 'p': { prop = getProperty("PLAC"); value = (prop instanceof PropertyPlace) ? ((PropertyPlace)prop).getCity() : ""; if (value==null) value=""; break; }
        case 'P': { prop = getProperty("PLAC"); value = (prop instanceof PropertyPlace) ? prop.getDisplayValue() : ""; break;}
        case 'v': { prop = this; value = getDisplayValue(); break; }
        case 'V': { prop = this; value = getValue(); break; }
        case 't': { prop = null; value = getTag(); break; }
        case 'T': { prop = null; value = Gedcom.getName(getTag()); break; }
        default:
          throw new IllegalArgumentException("unknown formatting marker "+marker);
      }
      
      if (prop!=null && policy.isPrivate(prop)) {
        
        value = (masked++==0||prefix.trim().length()>0)  ? Options.getInstance().maskPrivate : "";
      }
      
      if (value.length()>0) {
        result.append(prefix);
        result.append(value);
        result.append(suffix);
        if (prop!=null) matches++;
      }
      
      cursor = matcher.end();
    }
    
    
    result.append(format.substring(cursor));
    
    
    return matches>0 ? result.toString() : "";
  }

  
  public PropertyDate getWhen() {
    Property cursor = this;
    while (cursor!=null) {
      if (this instanceof PropertyDate)
        return (PropertyDate)this;
      if (this instanceof PropertyEvent)
        return ((PropertyEvent)this).getDate();
      cursor = cursor.getParent();
    }
    
    return null;
  }
  
  
  public void copyProperties(Property[] roots, boolean useValues) throws GedcomException {
    for (Property property : roots) {
      copyProperties(property, useValues);
    }
  }

  public void copyProperties(Property root, boolean useValues) throws GedcomException {
    
    Property copy = getProperty(root.getTag(), false);
    if (copy==null) {
      copy = addProperty(root.getTag(), useValues ? root.getValue() : "");
      if (useValues&&copy instanceof PropertyXRef) try {
        ((PropertyXRef)copy).link();
      } catch (GedcomException e) {
        throw new GedcomException("Can't copy '"+root.getTag()+" "+root.getDisplayValue()+"' to "+this.getPath()+": "+e.getMessage());
      }
    }
    
    for (int i=0, j=root.getNoOfProperties(); i<j; i++) {
      Property child = root.getProperty(i);
      
      if (!child.isTransient()) 
        copy.copyProperties(child, useValues);
      
    }
    
  }
  
} 

