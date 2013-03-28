
package genj.edit;

import genj.edit.beans.PropertyBean;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.util.ChangeSupport;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.PopupWidget;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;


public class BeanPanel extends JPanel {
  
  private final static Resources RES = Resources.get(BeanPanel.class);
  private final static Registry REGISTRY = Registry.get(BeanPanel.class);

  
  private static Map<String, NestedBlockLayout> DESCRIPTORCACHE = new HashMap<String, NestedBlockLayout>();

  
  protected ChangeSupport changeSupport = new ChangeSupport(this);
  
  
  private List<PropertyBean> beans = new ArrayList<PropertyBean>(32);
  
  
  private static NestedBlockLayout getLayout(String key) {
    
    
    NestedBlockLayout result = DESCRIPTORCACHE.get(key);
    if (result!=null)
      return result.copy();
    if (DESCRIPTORCACHE.containsKey(key))
      return null;
    
    try {
      
      InputStream in = BeanPanel.class.getResourceAsStream(key);
      if (in!=null) try {
        result = new NestedBlockLayout(in);
      } finally {
        in.close();
      }
    } catch (Throwable t) {
      
      
      EditView.LOG.log(Level.WARNING, "cannot read descriptor "+key+" ("+t.getMessage()+")");
    }
    
    
    DESCRIPTORCACHE.put(key, result);

    return result!=null ? result.copy() : null;
  }
  
  private static NestedBlockLayout getLayout(MetaProperty meta) {
    if (Entity.class.isAssignableFrom(meta.getType()))
      return getLayout("descriptors/entities/" + meta.getTag()+".xml");

    
    String key = "descriptors/properties/" + meta.getTag() +".xml";
    NestedBlockLayout result = getLayout(key);
    if (result!=null) 
      return result;
      
    
    result = getLayout("descriptors/properties/" + meta.getType().getSimpleName() +".xml");
    if (result!=null)
      return result;
    
    
    return null;
  }
  
  public BeanPanel() {
    
    
    setFocusTraversalPolicy(new FocusPolicy());
    setFocusCycleRoot(true);
  }
  
  public void addChangeListener(ChangeListener listener) {
    changeSupport.addChangeListener(listener);
  }
  
  public void removeChangeListener(ChangeListener listener) {
    changeSupport.removeChangeListener(listener);
  }
    
  
  public void commit() {
    
    
    for (PropertyBean bean : beans) {
      if (bean.hasChanged()) 
        bean.commit();
    }
  
    changeSupport.setChanged(false);
    
    
  }
  
  
  public List<PropertyBean> getBeans() {
    return beans;
  }


  
  public void select(Property prop) {

    if (prop==null||beans.isEmpty())
      return;
      
    
    for (PropertyBean bean : beans) {
      if (bean.getProperty()==prop && bean.requestFocusInWindow()) 
        return;
    }
      
    
    for (PropertyBean bean : beans) {
      if (bean.isDisplayable() && bean.getProperty()!=null && bean.getProperty().isContained(prop) && bean.requestFocusInWindow()) 
        return;
    }
      
    
    beans.get(0).requestFocusInWindow();
      
    
  }
  
  
  public void setRoot(Property root) {
    
    
    List<PropertyBean> bs = new ArrayList<PropertyBean>(beans);
    beans.clear(); 
    for (PropertyBean bean : bs) {
      bean.removeChangeListener(changeSupport);
      bean.getParent().remove(bean);
      PropertyBean.recycle(bean);
    }
    removeAll();

    
    if (root!=null) {
    
      
      Set<String> beanifiedTags = new HashSet<String>();
      
      
      NestedBlockLayout descriptor = getLayout(root.getMetaProperty());
      if (descriptor!=null) 
        parse(root, root, descriptor, beanifiedTags);
    }
      
    
    revalidate();
    repaint();
  }

  
  private void parse(Property root, Property property, NestedBlockLayout descriptor, Set<String> beanifiedTags)  {

    setLayout(descriptor);
    
    
    for (NestedBlockLayout.Cell cell : descriptor.getCells()) {
      JComponent comp = createComponent(root, property, cell, beanifiedTags);
      if (comp!=null)
        add(comp, cell);
    }
    
    
  }
  
  
  private JComponent createComponent(Property root, Property property, NestedBlockLayout.Cell cell, Set<String> beanifiedTags) {
    
    String element = cell.getElement();
    
    
    String version = cell.getAttribute("gedcom");
    if (version!=null & !property.getGedcom().getGrammar().getVersion().equals(version))
      return null;
    
    
    if ("text".equals(element)) 
      return new JLabel(cell.getAttribute("value"));
    
    
    if ("fold".equals(element)) {
      String key = cell.getAttribute("key");
      String label = RES.getString(key,false);
      if (label==null) label = Gedcom.getName(key);
      
      String indent = cell.getAttribute("indent");
      NestedBlockLayout.Expander result = new NestedBlockLayout.Expander(label, indent!=null ? Integer.parseInt(indent) : 1);
      
      Registry r = new Registry(REGISTRY, root.getTag()+'.'+key);
      result.setCollapsed(r.get("folded", false));
      result.addPropertyChangeListener("folded", r);
      return result;
    }
    
    
    if ("label".equals(element)) {

      String key = cell.getAttribute("key");
      if (key!=null)
        return new JLabel(RES.getString(key));
      
      String path = cell.getAttribute("path");
      if (path==null)
        throw new IllegalArgumentException("label without key or path");

      MetaProperty meta = property.getMetaProperty().getNestedRecursively(new TagPath(path), false);
      if (Entity.class.isAssignableFrom(meta.getType())) 
        return new JLabel(meta.getName() + ' ' + ((Entity)root).getId(), null, SwingConstants.LEFT);
      else
        return new JLabel(meta.getName(cell.isAttribute("plural")), null, SwingConstants.LEFT);
    }
    
    
    if ("bean".equals(element)) {
      
      TagPath path = new TagPath(cell.getAttribute("path"));
      MetaProperty meta = property.getMetaProperty().getNestedRecursively(path, false);
      
      
      PropertyBean bean = createBean(property, path, meta, cell.getAttribute("type"));
      if (bean==null)
        return null;
      
      if ("horizontal".equals(cell.getAttribute("dir")))
        bean.setPreferHorizontal(true);
      
      if (beanifiedTags!=null&&property==root&&path.length()>1)
        beanifiedTags.add(path.get(1));

      return bean;
    }


    
    throw new IllegalArgumentException("Template element "+cell.getElement()+" is unkown");
  }
  
  
  private PropertyBean createBean(Property root, TagPath path, MetaProperty meta, String beanOverride) {

    
    Property prop = root.getProperty(path, false);
    
    
    if (prop instanceof PropertyXRef)
      prop = null;
    
    
    PropertyBean bean;
    if (beanOverride!=null)
      bean = PropertyBean.getBean(beanOverride);
    else if (prop!=null)
      bean = PropertyBean.getBean(prop.getClass());
    else 
      bean = PropertyBean.getBean(root.getMetaProperty().getNestedRecursively(path, false).getType(""));
      
    bean.setContext(root, path, prop, beans);
    bean.addChangeListener(changeSupport);
    beans.add(bean);

    
    return bean;
  }
  






















  





















































































































































  
  private class FocusPolicy extends ContainerOrderFocusTraversalPolicy {
    private Hack hack = new Hack();
    protected boolean accept(Component c) {
      return hack.accept(c);
    }
    private class Hack extends LayoutFocusTraversalPolicy {
      protected boolean accept(Component c) {
        return super.accept(c);
      }
    }
    @Override
    public Component getComponentAfter(Container container, Component component) {
      return beans.isEmpty() ? null : super.getComponentAfter(container, component);
    }
    @Override
    public Component getComponentBefore(Container container, Component component) {
      return beans.isEmpty() ? null : super.getComponentBefore(container, component);
    }
    @Override
    public Component getDefaultComponent(Container container) {
      return beans.isEmpty() ? null : super.getDefaultComponent(container);
    }
  } 
  
  
  private class PopupBean extends PopupWidget implements MouseMotionListener, MouseListener {
    
    private PropertyBean wrapped;
    private JPanel content;
    
    
    private PopupBean(PropertyBean wrapped) {
      
      
      setFocusable(false);
      setBorder(null);
      
      
      this.wrapped = wrapped;
      
      
      Property prop = wrapped.getProperty();
      setToolTipText(prop.getPropertyName());
      ImageIcon img = prop.getImage(false);
      if (prop.getValue().length()==0)
        img = img.getGrayedOut();
      setIcon(img);
      
      
      content = new JPanel(new BorderLayout());
      content.setAlignmentX(0);
      content.setBorder(new TitledBorder(prop.getPropertyName()));
      content.addMouseMotionListener(this);
      content.addMouseListener(this);
      content.add(wrapped);
      content.setFocusCycleRoot(true);
      
      
      addItem(content);
  
      
    }
    
    private ImageIcon getImage(Property prop) {
      while (prop.getParent()!=null && !(prop.getParent() instanceof Entity)) {
        prop = prop.getParent();
      }
      return prop.getImage(false);
    }
    
    
    public void showPopup() {
      
      super.showPopup();
      
      Dimension d = BasicEditor.REGISTRY.get("popup."+wrapped.getProperty().getTag(), (Dimension)null);
      if (d!=null) 
        setPopupSize(d);
      
      SwingUtilities.getWindowAncestor(wrapped).setFocusableWindowState(true);
      wrapped.requestFocus();
      
      setIcon(wrapped.getProperty().getImage(false));
    }
    
    public void mouseDragged(MouseEvent e) {
      
      if (content.getCursor()==Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)) {
        Dimension d = new Dimension(e.getPoint().x, e.getPoint().y);
        BasicEditor.REGISTRY.put("popup."+wrapped.getProperty().getTag(), d);
        setPopupSize(d);
      }
    }
  
    public void mouseMoved(MouseEvent e) {
      
      if (e.getX()>content.getWidth()-content.getInsets().right
        &&e.getY()>content.getHeight()-content.getInsets().bottom) {
        content.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        return;
      }
      
      if (e.getY()<content.getInsets().top) try {
        content.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return;
      } catch (Throwable t) {}
      
      content.setCursor(Cursor.getDefaultCursor());
    }

    public void mouseClicked(MouseEvent e) {
      if (content.getCursor()==Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)) 
        cancelPopup();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
  
  } 

 }