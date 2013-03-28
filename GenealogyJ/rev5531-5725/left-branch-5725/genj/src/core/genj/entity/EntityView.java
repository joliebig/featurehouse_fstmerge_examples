
package genj.entity;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomListenerAdapter;
import genj.gedcom.Property;
import genj.renderer.Blueprint;
import genj.renderer.BlueprintManager;
import genj.renderer.ChooseBlueprintAction;
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
  
  
  private final static Registry REGISTRY = Registry.get(EntityView.class);
  
        
  private EntityRenderer renderer = null;
  
  
   Context context = new Context();
  
  
  private Map<String, Blueprint> type2blueprint = new HashMap<String, Blueprint>();
  
  
  private boolean isAntialiasing = true;
  
  private GedcomListener callback = new GedcomListenerAdapter() {
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      if (context.getEntity()==entity)
        setContext(new Context(context.getGedcom()), true);
    }
    public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
      if (context.getEntity() == property.getEntity())
        repaint();
    }
    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      gedcomPropertyChanged(gedcom, property);
    }
    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
      gedcomPropertyChanged(gedcom, property);
    }
  };
  
  
  public EntityView() {
    
    
    BlueprintManager bpm = BlueprintManager.getInstance();
    for (int t=0;t<Gedcom.ENTITIES.length;t++) {
      String tag = Gedcom.ENTITIES[t];
      type2blueprint.put(tag, bpm.getBlueprint(tag, REGISTRY.get("blueprint."+tag, "")));
    }
    isAntialiasing  = REGISTRY.get("antial"  , false);
    
    
  }
  
  
  public ViewContext getContext() {
    
    ViewContext result = new ViewContext(context);
    
    
    if (context.getEntity()!=null) {
      
      result.addAction(new ChooseBlueprintAction(context.getEntity(), getBlueprint(context.getEntity().getTag())) {
        @Override
        protected void commit(Entity recipient, Blueprint blueprint) {
          type2blueprint.put(blueprint.getTag(), blueprint);
          setContext(context, false);
          REGISTRY.put("blueprint."+blueprint.getTag(), blueprint.getName());
        }
      });

    }
    
    
    return result;
  }
  
  
  @Override
  public void setContext(Context newContext, boolean isActionPerformed) {
    
    
    if (context.getGedcom()!=null) 
      context.getGedcom().removeGedcomListener((GedcomListener)Spin.over(callback));
    renderer = null;
    
    
    context = newContext;
    
    
    if (context.getGedcom()!=null) {
      context.getGedcom().addGedcomListener((GedcomListener)Spin.over(callback));

      
      Entity e = context.getEntity();
      Blueprint blueprint;
      if (e==null) blueprint = BLUEPRINT_SELECT;
      else blueprint = getBlueprint(e.getTag()); 
      renderer = new EntityRenderer(blueprint);
      
    }
    
    repaint();
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(256,160);
  }
  







  
  protected void paintComponent(Graphics g) {
    
    Rectangle bounds = getBounds();
    g.setColor(Color.white);
    g.fillRect(0,0,bounds.width,bounds.height);
    g.setColor(Color.black);

    if (context==null||renderer==null)
      return;
    
      ((Graphics2D)g).setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        isAntialiasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF
      );

    renderer.render(g, context!=null ? context.getEntity() : null, new Rectangle(0,0,bounds.width,bounds.height));
  }

  
  private Blueprint getBlueprint(String tag) {
    Blueprint result = (Blueprint)type2blueprint.get(tag);
    if (result==null) {
      result = BlueprintManager.getInstance().getBlueprint(tag, "");
      type2blueprint.put(tag, result);
    }
    return result;
  }
  
    public void setAntialiasing(boolean set) {
    isAntialiasing = set;
    repaint();
  }
  
  
  public boolean isAntialiasing() {
    return isAntialiasing;
  }

} 
