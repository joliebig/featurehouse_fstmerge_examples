
package genj.view;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.Resources;
import genj.util.swing.Action2;

import java.util.ArrayList;
import java.util.List;


public interface ActionProvider {

  public enum Purpose {
    TOOLBAR,
    CONTEXT,
    MENU
  }

  
  public List<Action2> createActions(Context context, Purpose purpose);

  
  public final class SeparatorAction extends Action2 {
  }
  
  
  public final class HelpActionGroup extends Action2.Group {
    public HelpActionGroup() {
      super(Resources.get(HelpActionGroup.class).getString("group.help"));
    }
    @Override
    public boolean equals(Object obj) {
      return obj instanceof HelpActionGroup;
    }
    @Override
    public int hashCode() {
      return HelpActionGroup.class.hashCode();
    }
  }
  
  
  public final class ToolsActionGroup extends Action2.Group {
    public ToolsActionGroup() {
      super(Resources.get(ToolsActionGroup.class).getString("group.tools"));
    }
    @Override
    public boolean equals(Object obj) {
      return obj instanceof ToolsActionGroup;
    }
    @Override
    public int hashCode() {
      return ToolsActionGroup.class.hashCode();
    }
  }
  
  
  public final class ViewActionGroup extends Action2.Group {
    public ViewActionGroup() {
      super(Resources.get(FileActionGroup.class).getString("group.view"));
    }
    @Override
    public boolean equals(Object obj) {
      return obj instanceof ViewActionGroup;
    }
    @Override
    public int hashCode() {
      return ViewActionGroup.class.hashCode();
    }
  }
  
  
  public final class FileActionGroup extends Action2.Group {
    public FileActionGroup() {
      super(Resources.get(FileActionGroup.class).getString("group.file"));
    }
    @Override
    public boolean equals(Object obj) {
      return obj instanceof FileActionGroup;
    }
    @Override
    public int hashCode() {
      return FileActionGroup.class.hashCode();
    }
  }
  
  
  public final class EditActionGroup extends Action2.Group {
    public EditActionGroup() {
      super(Resources.get(EditActionGroup.class).getString("group.edit"));
    }
    @Override
    public boolean equals(Object obj) {
      return obj instanceof EditActionGroup;
    }
    @Override
    public int hashCode() {
      return EditActionGroup.class.hashCode();
    }
  }
  
  
  public final class PropertyActionGroup extends Action2.Group {
    private Property p;
    public PropertyActionGroup(Property property) {
      super(Property.LABEL+" '"+TagPath.get(property).getName() + '\'', property.getImage(false));
      p = property;
    }
    @Override
    public boolean equals(Object that) {
      return that instanceof PropertyActionGroup && ((PropertyActionGroup)that).p.equals(this.p);
    }
    @Override
    public int hashCode() {
      return p.hashCode();
    }
  }

  
  public class EntityActionGroup extends Action2.Group {
    private Entity e;
    public EntityActionGroup(Entity entity) {
      super(Gedcom.getName(entity.getTag(),false)+" '"+entity.getId()+'\'', entity.getImage(false));
      e = entity;
    }
    @Override
    public boolean equals(Object that) {
      return that instanceof EntityActionGroup && ((EntityActionGroup)that).e.equals(this.e);
    }
    @Override
    public int hashCode() {
      return e.hashCode();
    }
  }

  
  public class PropertiesActionGroup extends Action2.Group {
    private List<Property> ps;
    public PropertiesActionGroup(List<? extends Property> properties) {
      super("'"+Property.getPropertyNames(properties, 5)+"' ("+properties.size()+")");
      ps = new ArrayList<Property>(properties);
    }
    @Override
    public boolean equals(Object that) {
      return that instanceof PropertiesActionGroup && ((PropertiesActionGroup)that).ps.equals(this.ps);
    }
    @Override
    public int hashCode() {
      return ps.hashCode();
    }
  }
  
  
  public class EntitiesActionGroup extends Action2.Group {
    private List<Entity> es;
    public EntitiesActionGroup(List<? extends Entity> entities) {
      super("'"+Property.getPropertyNames(entities,5)+"' ("+entities.size()+")");
      es = new ArrayList<Entity>(entities);
    }
    @Override
    public boolean equals(Object that) {
      return that instanceof EntitiesActionGroup && ((EntitiesActionGroup)that).es.equals(this.es);
    }
    @Override
    public int hashCode() {
      return es.hashCode();
    }
  }

  
  public class GedcomActionGroup extends Action2.Group {
    private Gedcom gedcom;
    public GedcomActionGroup(Gedcom gedcom) {
      super("Gedcom '"+gedcom.getName()+'\'', Gedcom.getImage());
      this.gedcom = gedcom;
    }
    @Override
    public boolean equals(Object that) {
      return that instanceof GedcomActionGroup && ((GedcomActionGroup)that).gedcom.equals(this.gedcom);
    }
    @Override
    public int hashCode() {
      return gedcom.hashCode();
    }
  }
  
} 
