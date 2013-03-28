
package genj.edit.beans;

import genj.gedcom.Property;
import genj.util.EnvironmentChecker;
import genj.util.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BeanFactory {
  
  private Logger LOG = Logger.getLogger("genj.edit"); 
  
  private final static Class[] beanTypes = {
    EntityBean.class,
    PlaceBean.class, 
    AgeBean.class,
    ChoiceBean.class,
    DateBean.class,
    EventBean.class,
    FileBean.class,
    MLEBean.class,
    NameBean.class,
    SexBean.class,
    XRefBean.class,
    SimpleValueBean.class 
  };
  
  private boolean isRecycling = true;
  
  
  private Registry registry;
  
  
  private final static Map property2cached= new HashMap();
  
  
  private static Map proxy2type = new HashMap();
  
  
  public BeanFactory(Registry registry) {
    this.registry = registry;

    if ("false".equals(EnvironmentChecker.getProperty(this, "genj.bean.recycle", "true", "checking whether to recycle beans"))) {
      isRecycling = false;
      Logger.getLogger("genj.edit.beans").log(Level.INFO, "Not recycling beansas genj.bean.recycle=false");
    }
      
  }

  
  public PropertyBean get(String type, Property prop) {
    
    
    PropertyBean bean = getBeanOfType(type);
    
    
    bean.setProperty(prop);
    
    
    return bean;
  }
  
  
  public PropertyBean get(Property prop) {

    
    PropertyBean bean = getBeanFor(prop);
    
    
    bean.setProperty(prop);
    
    
    return bean;
  }
  
  
  private synchronized PropertyBean getBeanOfType(String type) {
    
    try {
      
      PropertyBean bean = (PropertyBean)Class.forName(type).newInstance();
      bean.initialize(registry);
      
      return bean;
      
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "can't instantiate bean of type "+type, t);
    }
    
    
    PropertyBean bean = (PropertyBean)new SimpleValueBean();
    bean.initialize(registry);
    return bean;
  }
  
  
  private synchronized PropertyBean getBeanFor(Property prop) {
    
    List cached = (List)property2cached.get(prop.getClass());
    if (cached!=null&&!cached.isEmpty()) {
      PropertyBean result = (PropertyBean)cached.remove(cached.size()-1);
      return result;
    }
    
    try {
      for (int i=0;i<beanTypes.length;i++) {
        PropertyBean bean = (PropertyBean)beanTypes[i].newInstance();
        if (bean.accepts(prop)) {
          bean.initialize(registry);
          return bean;
        }
      }
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "can't instantiate/init bean for "+prop.getClass().getName(), t);
    }
    return new SimpleValueBean();
  }
  
  
  public synchronized void recycle(PropertyBean bean) {
    
    
    if (!isRecycling) 
      return;
    
    Property property = bean.getProperty();
    if (property==null)
      return;
    
    List cached = (List)property2cached.get(property.getClass());
    if (cached==null) {
      cached = new ArrayList();
      property2cached.put(property.getClass(), cached);
    }
    cached.add(bean);
    
  }
  
}
