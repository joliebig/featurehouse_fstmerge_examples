
package genj.gedcom;

import java.util.ArrayList;
import java.util.List;


public class Context {

  private Gedcom gedcom;
  private List<Entity> entities = new ArrayList<Entity>();
  private List<Property> properties = new ArrayList<Property>();

  @Override
  public boolean equals(Object obj) {
    Context that = (Context)obj;
    return this.gedcom==that.gedcom && this.entities.equals(that.entities) 
      && this.properties.equals(that.properties);
  }
  
  
  public Context() {
  }
  
  
  public Context(Context context) {
    this.gedcom = context.gedcom;
    this.entities.addAll(context.entities);
    this.properties.addAll(context.properties);
  }

  
  public Context(Gedcom gedcom, List<? extends Entity> entities) {
    this(gedcom, entities, null);
  }
  
  
  public Context(Gedcom gedcom, List<? extends Entity> entities, List<? extends Property> properties) {
    
    this.gedcom = gedcom;

    
    if (entities!=null)
      for (Entity e : entities) {
        if (e.getGedcom()!=gedcom)
          throw new IllegalArgumentException("gedcom must be same");
        if (!this.entities.contains(e))
          this.entities.add(e);
      }

    
    if (properties!=null)
      for (Property p : properties) {
        if (!this.properties.contains(p)) {
          Entity e = p.getEntity();
          if (e.getGedcom()!=gedcom)
            throw new IllegalArgumentException("gedcom must be same");
          this.properties.add(p);
          if (!this.entities.contains(e))
            this.entities.add(e);
        }
      }

    
  }

  
  public Context(Gedcom ged) {
    gedcom = ged;
  }

  
  public Context(Property prop) {
    this(prop.getGedcom());
    properties.add(prop);
    Entity entity = prop.getEntity();
    if (!entities.contains(entity))
      entities.add(entity);
  }

  
  public Context(Entity entity) {
    this(entity.getGedcom());
    entities.add(entity);
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

  
  public List<? extends Entity> getEntities() {
    return entities;
  }

  
  public List<? extends Property> getProperties() {
    return properties;
  }

  
  public String toString() {
    
    if (gedcom==null)
      return "";
    
    StringBuffer result = new StringBuffer();
    result.append(gedcom.getName());
    for (Entity entity : entities) {
      result.append(";");
      result.append(entity.getId());
      
      for (Property prop : properties) {
        if (prop.getEntity()==entity) {
          result.append(",");
          result.append(prop.getPath());
        }
      }
      
    }
    return result.toString();
  }
  
  public static Context fromString(Gedcom gedcom, String toString) throws GedcomException {

    List<Entity> entities = new ArrayList<Entity>();
    List<Property> properties = new ArrayList<Property>();

    String[] es = toString.split(";");
    
    
    if (!es[0].equals(gedcom.getName()))
      throw new GedcomException(es[0]+" doesn't match "+gedcom.getName());
    
    
    for (int e=1; e<es.length; e++) {
      
      String[] ps = es[e].split(",");

      
      Entity entity = gedcom.getEntity(ps[0]);
      if (entity==null)
        throw new GedcomException(ps[0]+" not in "+gedcom);
      entities.add(entity);

      
      for (int p=1; p<ps.length; p++) {
        try {
          Property property = entity.getPropertyByPath(ps[p]);
          if (property==null)
            throw new GedcomException(ps[p]+" not in "+ps[0]+" in "+gedcom);
          properties.add(property);
        } catch (IllegalArgumentException iae) {
          throw new GedcomException(ps[p]+" not valid for "+es[e]);
        }
        
      }
    }
    return new Context(gedcom, entities, properties);
    
  }
  

} 
