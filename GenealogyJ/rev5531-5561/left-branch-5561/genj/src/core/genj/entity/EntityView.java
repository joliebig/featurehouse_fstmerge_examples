
package genj.entity;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomListenerAdapter;
import genj.gedcom.Property;
import genj.renderer.Blueprint;
import genj.renderer.BlueprintManager;
import genj.renderer.EntityRenderer;
import genj.util.Registry;
import genj.util.Resources;
import genj.view.ContextProvider;
import genj.view.View;
import genj.view.ViewContext;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

import spin.Spin;


public class EntityView extends View implements ContextProvider {

    
   final static Resources resources = Resources.get(EntityView.class);

  
  private final static Blueprint BLUEPRINT_SELECT = new Blueprint(resources.getString("html.select"));
  
  
  private Registry registry;
  
        
  private EntityRenderer renderer = null;
  
  
   Gedcom gedcom = null;
  
  
  private Entity entity = null;
  
  
  private Map type2blueprint = new HashMap();
  
  
  private boolean isAntialiasing = false;
  
  private GedcomListener callback = new GedcomListenerAdapter() {
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      if (EntityView.this.entity == entity) {
        setEntity(gedcom.getFirstEntity(Gedcom.INDI));
      }
      repaint();
    }
    public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
      if (property.getEntity()==EntityView.this.entity)
        repaint();
    }
    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      gedcomPropertyChanged(gedcom, property);
    }
    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
      gedcomPropertyChanged(gedcom, property);
    }
  };
  
  
  public EntityView(String title, Context context, Registry reg) {
    
    registry = reg;
    gedcom = context.getGedcom();

    
    BlueprintManager bpm = BlueprintManager.getInstance();
    for (int t=0;t<Gedcom.ENTITIES.length;t++) {
      String tag = Gedcom.ENTITIES[t];
      type2blueprint.put(tag, bpm.getBlueprint(gedcom.getOrigin(), tag, registry.get("blueprint."+tag, "")));
    }
    isAntialiasing  = registry.get("antial"  , false);
    
    
    Entity entity = context.getEntity();
    if (entity!=null)
      setEntity(entity);
    
    
  }
  
  
  public ViewContext getContext() {
    return entity==null ? new ViewContext(gedcom) : new ViewContext(entity);
  }

  
  public Dimension getPreferredSize() {
    return new Dimension(256,160);
  }
  
  public void addNotify() {
    
    super.addNotify();
    
    gedcom.addGedcomListener((GedcomListener)Spin.over(callback));
  }

  
  public void removeNotify() {
    
    super.removeNotify();

    
    gedcom.removeGedcomListener((GedcomListener)Spin.over(callback));
    
    
    for (int t=0;t<Gedcom.ENTITIES.length;t++) {
      String tag = Gedcom.ENTITIES[t];
      registry.put("blueprint."+tag, getBlueprint(tag).getName()); 
    }
    registry.put("antial"  , isAntialiasing );
    if (entity!=null)
      registry.put("entity", entity.getId());
    
    
  }


  
  protected void paintComponent(Graphics g) {
    
    Rectangle bounds = getBounds();
    g.setColor(Color.white);
    g.fillRect(0,0,bounds.width,bounds.height);
    g.setColor(Color.black);

    if (renderer==null)
      return;
    
      ((Graphics2D)g).setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        isAntialiasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF
      );

    renderer.render(g, entity, new Rectangle(0,0,bounds.width,bounds.height));
  }

  
   Blueprint getBlueprint(String tag) {
    Blueprint result = (Blueprint)type2blueprint.get(tag);
    if (result==null) {
      result = BlueprintManager.getInstance().getBlueprint(gedcom.getOrigin(),tag, "");
      type2blueprint.put(tag, result);
    }
    return result;
  }
  
  
   void setBlueprints(Map setType2Blueprints) {
    type2blueprint = setType2Blueprints;
    
    setEntity(entity);
  }
  
  
   Map getBlueprints() {
    return type2blueprint;
  }
  
  
  public void select(Context context, boolean isActionPerformed) {
    Entity e = context.getEntity();
    if (e!=null)
      setEntity(e);
  }
  
  
  public void setEntity(Entity e) {
    
    Blueprint blueprint;
    if (e==null) blueprint = BLUEPRINT_SELECT;
    else blueprint = getBlueprint(e.getTag()); 
    renderer=new EntityRenderer(blueprint);
    
    entity = e;
    
    repaint();
    
  }
  
    public void setAntialiasing(boolean set) {
    isAntialiasing = set;
    repaint();
  }
  
  
  public boolean isAntialiasing() {
    return isAntialiasing;
  }

} 
