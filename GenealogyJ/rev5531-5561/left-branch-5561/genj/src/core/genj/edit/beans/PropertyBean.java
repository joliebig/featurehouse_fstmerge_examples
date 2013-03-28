
package genj.edit.beans;

import genj.gedcom.Entity;
import genj.gedcom.Property;
import genj.renderer.BlueprintManager;
import genj.renderer.EntityRenderer;
import genj.util.ChangeSupport;
import genj.util.Registry;
import genj.util.Resources;
import genj.view.ContextProvider;
import genj.view.ViewContext;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;


public abstract class PropertyBean extends JPanel implements ContextProvider {
  
  
  protected final static Resources resources = Resources.get(PropertyBean.class); 
  
  
  private Property property;
  
  
  protected Registry registry;
  
  
  protected JComponent defaultFocus = null;
  
  
  protected ChangeSupport changeSupport = new ChangeSupport(this);
  
  
   void initialize(Registry setRegistry) {
    registry = setRegistry;
  }
  
  
   abstract boolean accepts(Property prop);
  
  
  public final void setProperty(Property prop) {
    
    property = prop;

    setPropertyImpl(prop);
    
    changeSupport.setChanged(false);
  }

  protected abstract void setPropertyImpl(Property prop);
  
  
  public ViewContext getContext() {
    
    
    
    
    
    
    return property==null||property.getEntity()==null ? null : new ViewContext(property);
  }
  
  
  public Property getProperty() {
    return property;
  }
  
  
  public boolean hasChanged() {
    return changeSupport.hasChanged();
  }
  
  
  public void addChangeListener(ChangeListener l) {
    changeSupport.addChangeListener(l);
  }
  
  
  public void removeChangeListener(ChangeListener l) {
    changeSupport.removeChangeListener(l);
  }

  
  public void commit() {
    commit(property);
  }
  
  
  public void commit(Property property) {
    
    this.property = property;
    
    changeSupport.setChanged(false);
    
  }
  
  
  public boolean isEditable() {
    return true;
  }
  
  
  public boolean requestFocusInWindow() {
    
    if (defaultFocus!=null)
      return defaultFocus.requestFocusInWindow();
    return super.requestFocusInWindow();
  }

  
  public void requestFocus() {
    
    if (defaultFocus!=null)
      defaultFocus.requestFocus();
    else 
      super.requestFocus();
  }
  
  
  public class Preview extends JComponent {
    
    private Entity entity;
    
    private EntityRenderer renderer;
    
    protected Preview() {
      setBorder(new EmptyBorder(4,4,4,4));
    }
    
    protected void paintComponent(Graphics g) {
      Insets insets = getInsets();
      Rectangle box = new Rectangle(insets.left,insets.top,getWidth()-insets.left-insets.right,getHeight()-insets.top-insets.bottom);     
      
      g.setColor(Color.WHITE); 
      g.fillRect(box.x, box.y, box.width, box.height);
      
      if (renderer!=null&&entity!=null) 
        renderer.render(g, entity, box);
      
    }
    protected void setEntity(Entity ent) {
      entity = ent;
      if (entity!=null)
        renderer = new EntityRenderer(BlueprintManager.getInstance().getBlueprint(entity.getGedcom().getOrigin(), entity.getTag(), "Edit"));
      repaint();
    }
  } 

} 
