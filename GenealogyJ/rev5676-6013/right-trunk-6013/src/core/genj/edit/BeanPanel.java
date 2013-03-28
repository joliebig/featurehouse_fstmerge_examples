
package genj.edit;

import genj.edit.beans.PropertyBean;
import genj.edit.beans.ReferencesBean;
import genj.edit.beans.RelationshipsBean;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyChoiceValue;
import genj.gedcom.PropertyComparator;
import genj.gedcom.PropertyNumericValue;
import genj.gedcom.PropertySimpleValue;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.gedcom.UnitOfWork;
import genj.util.ChangeSupport;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.util.swing.LinkWidget;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.PopupWidget;
import genj.view.ContextProvider;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;


public class BeanPanel extends JPanel {
  
  private final static Registry REGISTRY = Registry.get(BeanPanel.class);

  private static final String
    PROXY_PROPERTY_ROOT = "beanpanel.bean.root",
    PROXY_PROPERTY_PATH = "beanpanel.bean.path";

  
  private static Map<String, NestedBlockLayout> DESCRIPTORCACHE = new HashMap<String, NestedBlockLayout>();

  
  protected ChangeSupport changeSupport = new ChangeSupport(this);
  
  
  private List<PropertyBean> beans = new ArrayList<PropertyBean>(32);
  
  
  private JPanel detail = new JPanel();
  private JTabbedPane tabs = new ContextTabbedPane();

  private boolean isShowTabs = true;
  
  
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
    
    
    setLayout(new BorderLayout());
    add(detail, BorderLayout.CENTER);
    add(tabs, BorderLayout.SOUTH);
    
    
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
      
      if (bean.hasChanged()&&bean.getProperty()!=null) {
        
        
        Property root = (Property)bean.getClientProperty(PROXY_PROPERTY_ROOT);
        TagPath path = (TagPath)bean.getClientProperty(PROXY_PROPERTY_PATH);
        Property prop = root.getProperty(path,false);
        if (prop==null)
          prop = root.setValue(path, "");
        
        
        bean.commit(prop);
        
      }
    }
    
    changeSupport.setChanged(false);
    
    
  }

  
  public void select(Property prop) {
    
    
    JComponent bean = find(prop);
    if (bean==null) 
      return;

    
    Component parent = bean;
    while (true) {
      if (parent.getParent() instanceof JTabbedPane) {
        ((JTabbedPane)parent.getParent()).setSelectedComponent(parent);
      }
      parent = parent.getParent();
      if (parent==null||parent==this)
        break;
    }        

    
    if (!bean.requestFocusInWindow())
      Logger.getLogger("genj.edit").fine("requestFocusInWindow()==false");
    
    
  }
  
  private JComponent find(Property prop) {
    if (prop==null||beans.isEmpty())
      return null;
    
    
    for (PropertyBean bean : beans) {
      if (bean.getProperty()==prop) 
        return bean;
    }
    
    
    for (PropertyBean bean : beans) {
      if (bean.isDisplayable() && bean.getProperty()!=null && bean.getProperty().isContained(prop)) 
        return bean;
    }
    
    
    for (Component c : tabs.getComponents()) {
      JComponent jc = (JComponent)c;
      if (jc.getClientProperty(Property.class)==prop) 
        return jc;
    }
    
    
    return (PropertyBean)beans.get(0);
    
    
  }
  
  
  public void setShowTabs(boolean set) {
    isShowTabs = set;
    tabs.setVisible(set);
  }

  
  public void setRoot(Property root) {
    
    
    for (PropertyBean bean : beans) {
      bean.removeChangeListener(changeSupport);
      bean.getParent().remove(bean);
      PropertyBean.recycle(bean);
    }
    beans.clear();
    detail.removeAll();
    tabs.removeAll();

    
    if (root!=null) {
    
      
      Set<String> beanifiedTags = new HashSet<String>();
      
      
      NestedBlockLayout descriptor = getLayout(root.getMetaProperty());
      if (descriptor!=null) 
        parse(detail, root, root, descriptor, beanifiedTags);

      if (isShowTabs) {
        
        String restoreTab = REGISTRY.get("tab", "0");

        
        createReferencesTabs(root);
        
        
        createPropertiesTab(root, beanifiedTags);
    
        
        createEventTabs(root, beanifiedTags);
  
        
        createLinkTab(root, beanifiedTags);

        
        try {
          tabs.setSelectedIndex(Integer.parseInt(restoreTab));
        } catch (Throwable t) {
          for (Component c : tabs.getComponents()) {
            Property prop = (Property)((JComponent)c).getClientProperty(Property.class);
            if (prop!=null&&prop.getTag().equals(restoreTab)) {
              tabs.setSelectedComponent(c);
              break;
            }
          }
        }

        
      }
    }
      
    
    revalidate();
    repaint();
  }

  
  private void createPropertiesTab(Property root, Set<String> beanifiedTags) {
    
    JPanel tab = new JPanel(new GridBagLayout());
    tab.setOpaque(false);
    
    MetaProperty[] nested = root.getNestedMetaProperties(MetaProperty.WHERE_NOT_HIDDEN);
    Arrays.sort(nested);
    int row = 0;
    for (MetaProperty meta : nested) {
      
      if (getLayout(meta)!=null)
        continue;
      
      if (meta.getType()!=PropertySimpleValue.class
        &&meta.getType()!=PropertyChoiceValue.class
        &&meta.getType()!=PropertyNumericValue.class)
        continue;
      
      if (!beanifiedTags.add(meta.getTag()))
        continue;
      tab.add(
        new JLabel(meta.getName(), meta.getImage(), SwingConstants.LEFT),
        new GridBagConstraints(0,row,1,1,1,0,GridBagConstraints.CENTER,1,new Insets(0,0,0,0),0,0)
      );
      tab.add(
        createBean(root, new TagPath(root.getTag()+":"+meta.getTag()), meta, null),
        new GridBagConstraints(1,row,1,1,1,0,GridBagConstraints.CENTER,1,new Insets(0,0,0,0),0,0)
      );
      row++;
    }
    tab.add(
        new JLabel(),
        new GridBagConstraints(0,row,1,1,1,1,GridBagConstraints.CENTER,1,new Insets(0,0,0,0),0,0)
      );
    
    tabs.addTab("", MetaProperty.IMG_CUSTOM, tab);
  }
  
  
  private void parse(JPanel panel, Property root, Property property, NestedBlockLayout descriptor, Set<String> beanifiedTags)  {

    panel.setLayout(descriptor);
    
    
    for (NestedBlockLayout.Cell cell : descriptor.getCells()) {
      JComponent comp = createComponent(root, property, cell, beanifiedTags);
      if (comp!=null) 
        panel.add(comp, cell);
    }
    
    
  }
  
  
  private JComponent createComponent(Property root, Property property, NestedBlockLayout.Cell cell, Set<String> beanifiedTags) {
    
    String element = cell.getElement();
    
    
    String version = cell.getAttribute("gedcom");
    if (version!=null & !property.getGedcom().getGrammar().getVersion().equals(version))
      return null;
    
    
    if ("text".equals(element)) {
      return new JLabel(cell.getAttribute("value"));
    }
    
    
    TagPath path = new TagPath(cell.getAttribute("path"));
    MetaProperty meta = property.getMetaProperty().getNestedRecursively(path, false);
    
    
    if ("label".equals(element)) {

      JLabel label;
      if (root instanceof Entity && path.length()==1) 
        label = new JLabel(meta.getName() + ' ' + ((Entity)root).getId(), null, SwingConstants.LEFT);
      else
        label = new JLabel(meta.getName(cell.isAttribute("plural")), null, SwingConstants.LEFT);
      return label;
    }
    
    
    if ("bean".equals(element)) {
      
      PropertyBean bean = createBean(property, path, meta, cell.getAttribute("type"));
      if (bean==null)
        return null;
      
      if ("horizontal".equals(cell.getAttribute("dir")))
        bean.setPreferHorizontal(true);
      
      if (beanifiedTags!=null&&property==root&&path.length()>1)
        beanifiedTags.add(path.get(1));
      
      return cell.getAttribute("popup")==null ? bean : (JComponent)new PopupBean(bean);
    }

    
    throw new IllegalArgumentException("Template element "+cell.getElement()+" is unkown");
  }
  
  
  private PropertyBean createBean(Property root, TagPath path, MetaProperty meta, String beanOverride) {

    
    Property prop = root.getProperty(path, false);
    
    
    
    if (prop==null||prop instanceof PropertyXRef) 
      prop = new PropertyProxy(root).setValue(path, "");
    
    
    PropertyBean bean = beanOverride==null ? PropertyBean.getBean(prop.getClass()) : PropertyBean.getBean(beanOverride);
    bean.setProperty(prop);
    bean.addChangeListener(changeSupport);
    beans.add(bean);

    
    bean.putClientProperty(PROXY_PROPERTY_ROOT, root);
    bean.putClientProperty(PROXY_PROPERTY_PATH, path);
    
    
    return bean;
  }
  
  private void createReferencesTabs(Property root) {
    tabs.addTab("", RelationshipsBean.IMG , new RelationshipsBean().setProperty(root));
    tabs.addTab("", ReferencesBean.IMG , new ReferencesBean().setProperty(root));
  }

  private void createLinkTab(Property root, Set<String> beanifiedTags) {
    
    
    JPanel linksTab = new JPanel(new FlowLayout(FlowLayout.LEFT));
    linksTab.setPreferredSize(new Dimension(64,64));
    linksTab.setOpaque(false);
    tabs.addTab("", Images.imgNew, linksTab);
    MetaProperty[] nested = root.getNestedMetaProperties(MetaProperty.WHERE_NOT_HIDDEN);
    Arrays.sort(nested);
    for (int i=0;i<nested.length;i++) {
      MetaProperty meta = nested[i];
      
      NestedBlockLayout descriptor = getLayout(meta);
      if (descriptor==null||descriptor.getCells().isEmpty())
        continue;
      
      if (beanifiedTags.contains(meta.getTag())&&meta.isSingleton())
        continue;
      
      linksTab.add(new LinkWidget(new AddTab(root, meta)));
    }
  }
  
  
  private void createEventTabs(Property root, Set<String> beanifiedTags) {
    
    
    Set<String> skippedOnceTags = new HashSet<String>();
    Property[] props = root.getProperties();
    Arrays.sort(props, new PropertyComparator(".:DATE"));
    for (Property prop : props) {
      
      String tag = prop.getTag();
      if (skippedOnceTags.add(tag)&&beanifiedTags.contains(tag)) 
        continue;
      beanifiedTags.add(tag);
      
      createEventTab(root, prop);
      
    }
    
  }
  
  
  private void createEventTab(Property root, Property prop) {
     
    
    if (prop instanceof PropertyXRef)
      return;
     
    
    MetaProperty meta = prop.getMetaProperty();
    NestedBlockLayout descriptor = getLayout(meta);
    if (descriptor==null) 
      return;
     
    
    JPanel tab = new JPanel();
    tab.putClientProperty(Property.class, prop);
    tab.setOpaque(false);
    
    parse(tab, root, prop, descriptor, null);
    tabs.addTab(meta.getName() + prop.format("{ $y}"), prop.getImage(false), tab, meta.getInfo());

    
  }
   
  private class ContextTabbedPane extends JTabbedPane implements ContextProvider {
    private ContextTabbedPane() {
      super(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
    }
    public ViewContext getContext() {
      
      Component selection = tabs.getSelectedComponent();
      Property prop = (Property)((JComponent)selection).getClientProperty(Property.class);
      if (prop==null)
        return null;
      
      return new ViewContext(prop).addAction(new DelTab(prop));
    }
    @Override
    protected void fireStateChanged() {
      super.fireStateChanged();
      
      Component selection = tabs.getSelectedComponent();
      if (selection!=null) {
        Property prop = (Property)((JComponent)selection).getClientProperty(Property.class);
        if (prop==null)
          REGISTRY.put("tab", getSelectedIndex());
        else
          REGISTRY.put("tab", prop.getTag());
      }
    }
    
  } 
    
   
   private class AddTab extends Action2 {
     
     private MetaProperty meta;
     private Property root;
     private Property property;
     
     
     private AddTab(Property root, MetaProperty meta) {
       
       this.meta = meta;
       this.root = root;
       
       setText(meta.getName());
       setImage(meta.getImage());
       setTip(meta.getInfo());
     }
   
     
     public void actionPerformed(ActionEvent event) {
       
       root.getGedcom().doMuteUnitOfWork(new UnitOfWork() {
         public void perform(Gedcom gedcom) {
           
           
           if (BeanPanel.this.changeSupport.hasChanged())
             commit();
           
           
           property = root.addProperty(meta.getTag(), "");
         }
       });
       
       
       select(property);
       
       
     }
     
   } 
   
   
   private class DelTab extends Action2 {
     private Property prop;
     private DelTab(Property prop) {
       setText(EditView.RESOURCES.getString("action.del", prop.getPropertyName()));
       setImage(Images.imgCut);
       this.prop = prop;
     }
    public void actionPerformed(ActionEvent event) {
      prop.getGedcom().doMuteUnitOfWork(new UnitOfWork() {
        public void perform(Gedcom gedcom) {
          
          
          if (BeanPanel.this.changeSupport.hasChanged())
            commit();
          
          
          prop.getParent().delProperty(prop);
          
        }
      });
      

      
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

  
  private class PropertyProxy extends Property {
    private Property proxied;
    
    private PropertyProxy(Property prop) {
      this.proxied = prop;
    }
    public Property getProxied() {
      return proxied;
    }
    public boolean isContained(Property in) {
      return proxied==in ? true : proxied.isContained(in);
    }
    public Gedcom getGedcom() { return proxied.getGedcom(); }
    public String getValue() { throw new IllegalArgumentException(); };
    public void setValue(String val) { throw new IllegalArgumentException(); };
    public String getTag() { return proxied.getTag(); }
    public TagPath getPath() { return proxied.getPath(); }
    public MetaProperty getMetaProperty() { return proxied.getMetaProperty(); }
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
  } 
 }