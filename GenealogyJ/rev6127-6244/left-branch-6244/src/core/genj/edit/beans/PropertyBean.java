
package genj.edit.beans;

import genj.gedcom.Entity;
import genj.gedcom.Property;
import genj.gedcom.PropertyAge;
import genj.gedcom.PropertyBlob;
import genj.gedcom.PropertyChoiceValue;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyEvent;
import genj.gedcom.PropertyFile;
import genj.gedcom.PropertyMultilineValue;
import genj.gedcom.PropertyName;
import genj.gedcom.PropertyPlace;
import genj.gedcom.PropertySex;
import genj.gedcom.PropertyXRef;
import genj.renderer.BlueprintManager;
import genj.renderer.BlueprintRenderer;
import genj.util.ChangeSupport;
import genj.util.EnvironmentChecker;
import genj.util.Registry;
import genj.util.Resources;
import genj.view.ContextProvider;
import genj.view.ViewContext;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;


public abstract class PropertyBean extends JPanel implements ContextProvider {
  
  private final static int CACHE_PRELOAD = 10;
  
  protected final static Resources RESOURCES = Resources.get(PropertyBean.class); 
  protected final static Logger LOG = Logger.getLogger("genj.edit.beans");
  protected final static Registry REGISTRY = Registry.get(PropertyBean.class); 

  
  private final static Class<?>[] PROPERTY2BEANTYPE = {
    Entity.class                , EntityBean.class,
    PropertyPlace.class         , PlaceBean.class, 
    PropertyAge.class           , AgeBean.class,
    PropertyChoiceValue.class   , ChoiceBean.class,
    PropertyDate.class          , DateBean.class,
    PropertyEvent.class         , EventBean.class,
    PropertyFile.class          , FileBean.class,
    PropertyBlob.class          , FileBean.class,
    PropertyMultilineValue.class, MLEBean.class,
    PropertyName.class          , NameBean.class,
    PropertySex.class           , SexBean.class,
    PropertyXRef.class          , XRefBean.class,
    Property.class              , SimpleValueBean.class  
  };
  
  private final static boolean isCache = "true".equals(EnvironmentChecker.getProperty("genj.edit.beans.cache", "true", "checking if bean cache is enabled or not"));
  
  private final static Map<Class<? extends PropertyBean>,List<PropertyBean>> BEANCACHE = createBeanCache();
  
  @SuppressWarnings("unchecked")
  private static Map<Class<? extends PropertyBean>,List<PropertyBean>> createBeanCache() {
    LOG.fine("Initializing bean cache");
    
    Map<Class<? extends PropertyBean>,List<PropertyBean>> result = new HashMap<Class<? extends PropertyBean>,List<PropertyBean>>();
    
    if (isCache) for (int i=0;i<PROPERTY2BEANTYPE.length;i+=2) {
      try {
        List<PropertyBean> cache = new ArrayList<PropertyBean>(CACHE_PRELOAD);
        for (int j=0;j<CACHE_PRELOAD;j++)
          cache.add((PropertyBean)PROPERTY2BEANTYPE[i+1].newInstance());
        result.put((Class<? extends PropertyBean>)PROPERTY2BEANTYPE[i+1], cache);
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "can't instantiate bean "+PROPERTY2BEANTYPE[i+1], t);
      }
    }
    return result;
  }

  
  @SuppressWarnings("unchecked")
  public static PropertyBean getBean(Class<? extends Property> property) {
    
    for (int i=0;i<PROPERTY2BEANTYPE.length;i+=2) {
      if (PROPERTY2BEANTYPE[i]!=null&&PROPERTY2BEANTYPE[i].isAssignableFrom(property))
        return getBeanImpl((Class<? extends PropertyBean>)PROPERTY2BEANTYPE[i+1]);
    }

    LOG.warning("Can't find declared bean for property type "+property.getName()+")");
    return getBeanImpl(SimpleValueBean.class);
  }
  
  @SuppressWarnings("unchecked")
  public static PropertyBean getBean(String bean) {
    try {
      return getBeanImpl((Class<? extends PropertyBean>)Class.forName(bean));
    } catch (ClassNotFoundException e) {
      LOG.log(Level.FINE, "Can't find desired bean "+bean, e);
      return getBeanImpl(SimpleValueBean.class);
    }
  }
  
  private static PropertyBean getBeanImpl(Class<? extends PropertyBean> clazz) {
    try {
      
      List<PropertyBean> cache = BEANCACHE.get(clazz);
      if (cache!=null&&!cache.isEmpty()) {
        PropertyBean bean = cache.remove(cache.size()-1);
        if (bean.getParent()==null)
          return bean;
        LOG.log(Level.FINE, "Bean has parent coming out of cache "+bean);
      }
      return ((PropertyBean)clazz.newInstance());
    } catch (Throwable t) {
      LOG.log(Level.FINE, "Problem with bean lookup "+clazz.getName(), t);
      return new SimpleValueBean();
    }
  }
  
  
  public static void recycle(PropertyBean bean) {
    if (bean.getParent()!=null)
      throw new IllegalArgumentException("bean still has parent");
    
    if (!isCache)
      return;
    
    List<PropertyBean> cache = BEANCACHE.get(bean.getClass());
    if (cache==null) {
      cache = new ArrayList<PropertyBean>();
      BEANCACHE.put(bean.getClass(), cache);
    }
    if (cache.size()<CACHE_PRELOAD)
      cache.add(bean);
  }

  
  public static Set<Class<? extends PropertyBean>> getAvailableBeans() {
    return Collections.unmodifiableSet(BEANCACHE.keySet());
  }

  
  
  private Property property;
  
  
  protected JComponent defaultFocus = null;
  
  
  protected ChangeSupport changeSupport = new ChangeSupport(this);
  
  
  protected PropertyBean() {
    setOpaque(false);  
  }
  
  
  public void setPreferHorizontal(boolean set) {
    
  }
  
  
  public final PropertyBean setProperty(Property prop) {
    
    property = prop;

    setPropertyImpl(prop);
    
    changeSupport.setChanged(false);
    
    return this;
  }

  protected abstract void setPropertyImpl(Property prop);
  
  
  public ViewContext getContext() {
    
    
    
    
    
    
    return property==null||property.getEntity()==null ? null : new ViewContext(property);
  }
  
  
  public final Property getProperty() {
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

  
  public final void commit() {
    commit(property);
  }
  
  
  public final void commit(Property property) {
    
    this.property = property;
    
    commitImpl(property);
    
    changeSupport.setChanged(false);
    
  }
  
  protected abstract void commitImpl(Property property);
  
  
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
    
    private BlueprintRenderer renderer;
    
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
        renderer = new BlueprintRenderer(BlueprintManager.getInstance().getBlueprint(entity.getTag(), "Edit"));
      repaint();
    }
  } 

} 
