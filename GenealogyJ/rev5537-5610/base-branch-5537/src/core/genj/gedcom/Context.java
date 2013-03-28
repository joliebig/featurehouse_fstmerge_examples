
package genj.gedcom;

import genj.util.swing.ImageIcon;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;


public class Context implements Comparable<Context> {

  private Gedcom gedcom;
  private List entities = new ArrayList();
  private List properties = new ArrayList();
  private Class entityType = null;
  private Class propertyType = null;
  private ImageIcon  img = null;
  private String txt = null;

  
  public Context(Context context) {
    this.gedcom = context.gedcom;
    this.entities.addAll(context.entities);
    this.properties.addAll(context.properties);
    this.entityType = context.entityType;
    this.propertyType = context.propertyType;
    this.img = context.img;
    this.txt = context.txt;
  }

  
  public Context(Gedcom ged) {
    if (ged==null)
      throw new IllegalArgumentException("gedcom for context can't be null");
    gedcom = ged;
  }

  
  public Context(Property prop) {
    this(prop.getGedcom());
    addProperty(prop);
  }

  
  public Context(Entity entity) {
    this(entity.getGedcom());
    addEntity(entity);
  }

  
  public void addEntity(Entity e) {
    
    if (e.getGedcom()!=gedcom)
      throw new IllegalArgumentException("entity's gedcom can't be different");
    
    entities.remove(e);
    if (entityType!=null&&entityType!=e.getClass())
      entityType = Entity.class;
    else
      entityType = e.getClass();
    entities.add(e);
  }

  
  public void addEntities(Entity[] es) {
    for (int i = 0; i < es.length; i++)
      addEntity(es[i]);
  }

  
  public void removeEntities(Collection rem) {

    
    entities.removeAll(rem);

    
    for (ListIterator iterator = properties.listIterator(); iterator.hasNext();) {
      Property prop = (Property) iterator.next();
      if (rem.contains(prop.getEntity()))
        iterator.remove();
    }
  }

  
  public void addProperty(Property p) {
    
    addEntity(p.getEntity());
    if (p instanceof Entity)
      return;
    
    if (p.getGedcom()!=gedcom)
      throw new IllegalArgumentException("property's gedcom can't be different");
    
    properties.remove(p);
    if (propertyType!=null&&propertyType!=p.getClass())
      propertyType = Property.class;
    else
      propertyType = p.getClass();
    
    properties.add(p);
  }

  
  public void addProperties(Property[] ps) {
    for (int i = 0; i < ps.length; i++)
      addProperty(ps[i]);
  }

  
  public void removeProperties(Collection rem) {
    properties.removeAll(rem);
  }

  
  public Gedcom getGedcom() {
    return gedcom;
  }

  
  public Entity getEntity() {
    return entities.isEmpty() ? null : (Entity)entities.get(0);
  }

  
  public Property getProperty() {
    return properties.isEmpty() ? null : (Property)properties.get(0);
  }

  
  public Entity[] getEntities() {
    if (entityType==null)
      return new Entity[0];
    return (Entity[])entities.toArray((Entity[])Array.newInstance(entityType, entities.size()));
  }

  
  public Property[] getProperties() {
    if (propertyType==null)
      return new Property[0];
    return (Property[])properties.toArray((Property[])Array.newInstance(propertyType, properties.size()));
  }

  
  public String getText() {

    if (txt!=null)
      return txt;

    if (properties.size()==1) {
      Property prop = (Property)properties.get(0);
      txt = Gedcom.getName(prop.getTag()) + "/" + prop.getEntity();
    } else if (!properties.isEmpty())
      txt = Property.getPropertyNames(Property.toArray(properties), 5);
    else  if (entities.size()==1)
      txt = entities.get(0).toString();
    else if (!entities.isEmpty())
      txt = Entity.getPropertyNames(Property.toArray(entities), 5);
    else txt = gedcom.getName();

    return txt;
  }

  
  public Context setText(String text) {
    txt = text;
    return this;
  }

  
  public ImageIcon getImage() {
    
    if (img!=null)
      return img;
    
    if (properties.size()==1)
      img = ((Property)properties.get(0)).getImage(false);
    else if (entities.size()==1)
      img = ((Entity)entities.get(0)).getImage(false);
    else img = Gedcom.getImage();
    return img;
  }

  
  public Context setImage(ImageIcon set) {
    img = set;
    return this;
  }

  
  public void addContext(Context context) {
    if (context.getGedcom()!=getGedcom())
      throw new IllegalArgumentException();
    addProperties(context.getProperties());
    addEntities(context.getEntities());
  }

  
  public int compareTo(Context that) {
    if (this.txt==null)
      return -1;
    if (that.txt==null)
      return 1;
    return this.txt.compareTo(that.txt);
  }

} 
