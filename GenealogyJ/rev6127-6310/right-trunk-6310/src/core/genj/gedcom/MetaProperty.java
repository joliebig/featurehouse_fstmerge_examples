
package genj.gedcom;

import genj.util.swing.ImageIcon;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class MetaProperty implements Comparable<MetaProperty> {

  
  public final static int
    WHERE_NOT_HIDDEN = 1, 
    WHERE_DEFAULT    = 2, 
    WHERE_CARDINALITY_ALLOWS = 4; 
  
      
  private static Map<String,ImageIcon> name2images = new HashMap<String,ImageIcon>();
  
  
  public final static ImageIcon
    IMG_CUSTOM  = loadImage("Attribute"),
    IMG_LINK    = loadImage("Association"),
    IMG_UNKNOWN = loadImage("Question"),
    IMG_ERROR   = loadImage("Error"),
    IMG_PRIVATE = loadImage("Private");
  
  
  private Grammar grammar;
    
  
  private String tag;
  
  
  private ImageIcon image;
  
  
  private String name, names;
  
  
  private Class<? extends Property>[] types;

  
  private String info;
  
  
  boolean isInstantiated = false;
  
  
  private boolean isGrammar;
  
  
  private Map<String,String> attrs;
  
  
  private Map<String,MetaProperty> tag2nested = new HashMap<String,MetaProperty>();
  List<MetaProperty> nested = new ArrayList<MetaProperty>();

  
   MetaProperty(Grammar grammar, String tag, Map<String,String> attributes, boolean isGrammar) {
    
    this.grammar = grammar;
    this.tag = tag;
    this.attrs = attributes;
    this.isGrammar = isGrammar;
    
    MetaProperty spr = getSuper();
    if (spr!=null) 
      copyAttributesFrom(spr);
    
  }
  
  private void copyAttributesFrom(MetaProperty supr) {

    for (MetaProperty sub : new ArrayList<MetaProperty>(supr.nested)) {
      if (!"0".equals(sub.attrs.get("inherit"))) {
        addNested(sub);
      }
    }
    
    if (getAttribute("type")==null)
      attrs.put("type", supr.getAttribute("type"));
    if (getAttribute("img")==null)
      attrs.put("img", supr.getAttribute("img"));
    if (getAttribute("cardinality")==null)
      attrs.put("cardinality", supr.getAttribute("cardinality"));
    if (getAttribute("hide")==null)
      attrs.put("hide", supr.getAttribute("hide"));
  }
  
  
  public MetaProperty getSuper() {
    String path = (String)attrs.get("super");
    return path == null  ? null : grammar.getMetaRecursively(new TagPath(path), false);
  }
  
  
   synchronized void addNested(MetaProperty sub) {
    if (sub==null)
      throw new IllegalArgumentException("Nested meta can't be null");
    
    tag2nested.put(sub.tag, sub);
    
    for (int i=0; i<nested.size(); i++) {
      MetaProperty other = (MetaProperty)nested.get(i);
      if (other.tag.equals(sub.tag)) {
        sub.copyAttributesFrom(other);
        nested.set(i, sub);
        return;       
      }
    }
    nested.add(sub);
    
  }
  
  
   MetaProperty[] getAllNested(Property parent, int filter) {
    
    
    List<MetaProperty> result = new ArrayList<MetaProperty>(nested.size());
    for (int s=0;s<nested.size();s++) {
      
      
      MetaProperty sub = (MetaProperty)nested.get(s);

      
      if ((filter&WHERE_DEFAULT)!=0) {
        if (!sub.isDefault())
          continue;
      }
        
      
      if ((filter&WHERE_NOT_HIDDEN)!=0&&sub.getAttribute("hide")!=null)
        continue;

      
      if ("0".equals(sub.getAttribute("xref")) && parent instanceof PropertyXRef ) continue;
      if ("1".equals(sub.getAttribute("xref")) && !(parent instanceof PropertyXRef)) continue; 
      
      
      if ((filter&WHERE_CARDINALITY_ALLOWS)!=0 && sub.isSingleton() && parent.getProperty(sub.getTag())!=null)
        continue;
        
      
      result.add(sub);
    }
    
    return (MetaProperty[])result.toArray(new MetaProperty[result.size()]);
  }
  
  
   String getAttribute(String key) {
    return (String)attrs.get(key);
  }
  
  
   void assertTag(String tag) throws GedcomException {
    if (!this.tag.equals(tag)) throw new GedcomException("Tag should be "+tag+" but is "+this.tag);
  }
  
  
  public boolean isEntity() {
    return Entity.class.isAssignableFrom(getType());
  }
  
  
  public boolean isSingleton() {
    String c= getAttribute("cardinality");
    return c!=null && c.endsWith(":1");
  }
  
  
  public boolean isVersion(String version) {
    String v = getAttribute("gedcom");
    return v==null || v.equals(version);
  }
  
  public boolean isDefault() {
    String isDefault = getAttribute("default");
    return isDefault!=null&&"1".equals(isDefault);
  }
  
  
  public boolean isRequired() {
    String c = getAttribute("cardinality");
    return c!=null && c.startsWith("1:");
  }
  
  
  public int compareTo(MetaProperty other) {
    return Collator.getInstance().compare(getName(), other.getName());
  }

  
  public boolean allows(String sub) {
    
    MetaProperty meta = (MetaProperty)tag2nested.get(sub);
    return meta==null ? false : meta.isGrammar;
  }
  
  
  public boolean allows(String sub, Class<? extends Property> type) {
    
    MetaProperty meta = (MetaProperty)tag2nested.get(sub);
    return meta!=null && type.isAssignableFrom(meta.getType());
  }
  
  
  public Property create(String value) throws GedcomException {

    
    Property result = null;
    try {
      result = getType(value).getDeclaredConstructor(String.class).newInstance(getTag());
    } catch (Exception e) {
      
      
      Gedcom.LOG.log(Level.WARNING, "Couldn't instantiate property "+getType()+" with value '"+value, e);
      result = new PropertySimpleValue(getTag()); 
    }
    
    result.setValue(value);
    
    
    isInstantiated = true;

    
    return result;
  }
  
  
  public boolean isInstantiated() {
    return isInstantiated;
  }
  
  
  public ImageIcon getImage() {
    if (image==null) {
      
      String s = getAttribute("img");
      
      if (s==null) 
        image = getTag().startsWith("_") ? IMG_CUSTOM : IMG_UNKNOWN;
      else  
        image = loadImage(s);
    }
    return image;
  }

  
  public ImageIcon getImage(String postfix) {
    Object name = getAttribute("img."+postfix);
    if (name==null) {
      
      if ("err".equals(postfix))
        return IMG_ERROR;
      else
        return getImage() ;
    } 
    return loadImage(name.toString());
  }

  
  public String getTag() {
    return tag;
  }

  
  public Class<? extends Property> getType() {
    return getTypes()[0];
  }
  
  public Class<? extends Property> getType(String value) {
    
    for (Class<? extends Property> type : getTypes()) {
      
      
      if (PropertyXRef.class.isAssignableFrom(type) && !(value.trim().startsWith("@")&&value.trim().endsWith("@")) ) 
        continue;
      
      
      return type;
    }
    
    return PropertySimpleValue.class;
  }
  
  
  @SuppressWarnings("unchecked")
  public Class<? extends Property>[] getTypes() {
    
    
    if (types==null) {
      
      String attrType = getAttribute("type");
      if (attrType==null)
        types = new Class[]{ PropertySimpleValue.class };
      else try {
        
        String[] attrTypes = attrType.split("\\|");
        types = new Class[attrTypes.length];
        
        for (int i=0;i<attrTypes.length;i++)
          types[i] = (Class<? extends Property>)Class.forName("genj.gedcom."+attrTypes[i]);
        
      } catch (Throwable t) {
        Gedcom.LOG.log(Level.WARNING, "Property type(s) genj.gedcom."+attrType+" couldn't be instantiated", t);    
        types = new Class[]{ PropertySimpleValue.class };
      }
    }
    
    
    return types;
  }

  
  public String getName() {
    return getName(false);
  }
  
  
  public String getCardinality() {
    return (String)attrs.get("cardinality");
  }
  
  
  public String getName(boolean plural) {
    String result;
    if (plural) {
      result = names;
      if (result ==null)
        result = Gedcom.getName(tag, true);
      names = result;
    } else {
      result = name;
      if (result ==null)
        result = Gedcom.getName(tag, false);
      name = result;
    }
    return result;
  }
  
  
  public String getInfo() {
    
    if (info==null) {
      info = Gedcom.getResources().getString(tag+".info", false);
      if (info==null) {
        char c = tag.charAt(0);
        if (c!='_') c = '?'; 
        info = Gedcom.getResources().getString(  c + ".info");
      }
      
      info = getName() + ":\n" + info;
    }
    
    return info;
  }

  
  public MetaProperty getNestedRecursively(TagPath path, boolean persist) {
    
    String tag = path.get(0);
    if (!this.tag.equals(tag) && !".".equals(tag))
      throw new IllegalArgumentException();
    
    return getNestedRecursively(path, 1, persist);
  }
  
   MetaProperty getNestedRecursively(TagPath path, int pos, boolean persist) {

    
    if (pos==path.length())
      return this;

    
    return getNested(path.get(pos), persist).getNestedRecursively(path, pos+1, persist);
  }

  
  public MetaProperty getNested(String tag, boolean persist) {
    
    if (tag==null||tag.length()==0)
      throw new IllegalArgumentException("tag can't be empty");
    
    MetaProperty result = (MetaProperty)tag2nested.get(tag);
    if (result==null) {
      result = new MetaProperty(grammar, tag, new HashMap<String,String>(), false);
      if (persist) addNested(result);
    }
    
    return result;
  }
  
  
  public int getNestedIndex(String subtag) {
    
    if (subtag.equals("CHAN"))
      return Integer.MAX_VALUE;
    
    for (int i=0;i<nested.size();i++) {
      if (((MetaProperty)nested.get(i)).getTag().equals(subtag))
        return i;
    }
    
    return Integer.MAX_VALUE;
  }
  
  
  private static ImageIcon loadImage(String name) {
    
    ImageIcon result = (ImageIcon)name2images.get(name);
    if (result==null) {
      try {
        
        while (true) {
          try {
            result = new ImageIcon(MetaProperty.class, "images/"+name);
            name2images.put(name, result);
            break;
          } catch (IllegalStateException iae) {
            
          }
        }
      } catch (Throwable t) {
        Gedcom.LOG.log(Level.WARNING, "Unexpected problem reading "+name, t);
        return IMG_ERROR;
      }
    }
    
    return result;
  }
  
  @Override
  public String toString() {
    return getTag() + attrs;
  }
  
} 
