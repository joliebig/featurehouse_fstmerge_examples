
package genj.edit;

import genj.edit.beans.BeanFactory;
import genj.edit.beans.PropertyBean;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomListenerAdapter;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.gedcom.UnitOfWork;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.LinkWidget;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.PopupWidget;
import genj.view.ContextProvider;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import spin.Spin;


class BasicEditor extends Editor implements ContextProvider {

  
  private static Map<MetaProperty, NestedBlockLayout> META2DESCRIPTOR = new HashMap<MetaProperty, NestedBlockLayout>();
  
  
  private Gedcom gedcom = null;

  
  private Entity currentEntity = null;

  
  private Registry registry;

  
  private EditView view;
  
  
  private Action2 ok = new OK(), cancel = new Cancel();
  
  
  private BeanPanel beanPanel;
  private JPanel buttonPanel;
  
  private GedcomListener callback = new Callback();

  
  public void init(Gedcom gedcom, EditView edit, Registry registry) {

    
    this.gedcom = gedcom;
    this.view = edit;
    this.registry = registry;
    
    
    setFocusTraversalPolicy(new FocusPolicy());
    setFocusCycleRoot(true);

    
    buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    ButtonHelper bh = new ButtonHelper().setInsets(0).setContainer(buttonPanel);
    bh.create(ok).setFocusable(false);    
    bh.create(cancel).setFocusable(false);
    
    
  }
  
  
  public void addNotify() {
    
    super.addNotify();
    
    gedcom.addGedcomListener((GedcomListener)Spin.over(callback));
    
  }

  
  public void removeNotify() {
    
    setEntity(null, null);
    
    gedcom.removeGedcomListener((GedcomListener)Spin.over(callback));
    
    super.removeNotify();
  }

  
  public ViewContext getContext() {
    
    PropertyBean bean = getFocus();
    if (bean!=null&&bean.getContext()!=null) 
      return bean.getContext();
    
    if (currentEntity!=null)
      return new ViewContext(currentEntity);
    
    return new ViewContext(gedcom);
  }

  
  public void setContext(Context context) {
    
    
    if (currentEntity != context.getEntity()) {
      
      
      setEntity(context.getEntity(), context.getProperty());
      
    } else {

      
      if (beanPanel!=null)
        beanPanel.select(context.getProperty());
      
    }

    
  }
  
  @Override
  public void commit() {
    
    
    if (ok.isEnabled())
      return;
    
    
    try {
      gedcom.removeGedcomListener((GedcomListener)Spin.over(callback));
      gedcom.doMuteUnitOfWork(new UnitOfWork() {
        public void perform(Gedcom gedcom) {
          beanPanel.commit();
        }
      });
    } finally {
      gedcom.addGedcomListener((GedcomListener)Spin.over(callback));
    }

    
    PropertyBean focussedBean = getFocus();
    Property focus = focussedBean !=null ? focussedBean.getProperty() : null;
    
    
    beanPanel.select(focus);

    
    ok.setEnabled(false);
    cancel.setEnabled(false);
  }
  
  
  public void setEntity(Entity set, Property focus) {
    
    
    if (ok.isEnabled()&&!gedcom.isWriteLocked()&&currentEntity!=null&&view.isCommitChanges()) 
      commit();

    
    currentEntity = set;
    
    
    if (focus==null) {
      
      PropertyBean bean = getFocus();
      if (bean!=null&&bean.getProperty()!=null&&bean.getProperty().getEntity()==currentEntity) focus  = bean.getProperty();
      
      if (focus==null) focus = currentEntity;
    }
    
    
    if (beanPanel!=null) {
      removeAll();
      beanPanel=null;
    }

    
    if (currentEntity!=null) {
      
      try {
        beanPanel = new BeanPanel();
        
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, new JScrollPane(beanPanel));
        add(BorderLayout.SOUTH, buttonPanel);

      } catch (Throwable t) {
        EditView.LOG.log(Level.SEVERE, "problem changing entity", t);
      }

      
      ok.setEnabled(false);
      cancel.setEnabled(false);

    }
    
    
    revalidate();
    repaint();
    
    
    if (beanPanel!=null)
      beanPanel.select(focus);

    
  }
  
  
  private PropertyBean getFocus() {
    
    Component focus = FocusManager.getCurrentManager().getFocusOwner();
    while (focus!=null&&!(focus instanceof PropertyBean))
      focus = focus.getParent();
    
    if (!(focus instanceof PropertyBean))
      return null;
    
    return SwingUtilities.isDescendingFrom(focus, this) ? (PropertyBean)focus : null;

  }

  
  private static NestedBlockLayout getSharedDescriptor(MetaProperty meta) {
    
    
    NestedBlockLayout descriptor  = (NestedBlockLayout)META2DESCRIPTOR.get(meta);
    if (descriptor!=null) 
      return descriptor;

    
    if (META2DESCRIPTOR.containsKey(meta))
      return null;
    
    
    for (MetaProperty cursor = meta; descriptor==null && cursor!=null ; cursor = cursor.getSuper() ) {
      
      String file  = "descriptors/" + (cursor.isEntity() ? "entities" : "properties") + "/" + cursor.getTag()+".xml";
      
      try {
        InputStream in = BasicEditor.class.getResourceAsStream(file);
        if (in==null) continue;
        descriptor = new NestedBlockLayout(in);
        in.close();
      } catch (IOException e) {
        EditView.LOG.log(Level.WARNING, "problem reading descriptor "+file+" ("+e.getMessage()+")");
      } catch (Throwable t) {
        
        EditView.LOG.log(Level.WARNING, "problem parsing descriptor "+file+" ("+t.getMessage()+")");
      }
    }
      
      
    
    META2DESCRIPTOR.put(meta, descriptor);

    
    return descriptor;
  }
  
  
  private static class PropertyProxy extends Property {
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
    
  
  private class PopupBean extends PopupWidget {
    
    private PropertyBean wrapped;
    
    
    private PopupBean(PropertyBean wrapped) {
      
      
      this.wrapped = wrapped;
      wrapped.setAlignmentX(0);
      
      
      Property prop = wrapped.getProperty();
      ImageIcon img = prop.getImage(false);
      if (prop.getValue().length()==0)
        img = img.getDisabled(50);
      setIcon(img);
      setToolTipText(prop.getPropertyName());
      
      
      setFocusable(false);
      setBorder(null);
      
      
      List actions = new ArrayList();
      actions.add(new JLabel(prop.getPropertyName()));
      actions.add(wrapped);
      setActions(actions);

      
    }
    
    
    public void showPopup() {
      
      super.showPopup();
      
      SwingUtilities.getWindowAncestor(wrapped).setFocusableWindowState(true);
      wrapped.requestFocus();
      
      setIcon(wrapped.getProperty().getImage(false));
    }
    
      
  } 
  
  
  private class OK extends Action2 {

    
    private OK() {
      setText(Action2.TXT_OK);
    }

    
    public void actionPerformed(ActionEvent event) {
      commit();
    }

  } 

  
  private class Cancel extends Action2 {

    
    private Cancel() {
      setText(Action2.TXT_CANCEL);
    }

    
    public void actionPerformed(ActionEvent event) {
      
      ok.setEnabled(false);
      cancel.setEnabled(false);

      
      setEntity(currentEntity, null);
    }

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
  
  
  private class BeanPanel extends JPanel implements ChangeListener {

    
    private Set topLevelTags = new HashSet();
    
    
    private List beans = new ArrayList(32);
    
    
    private JTabbedPane tabsPane;
    
    
    BeanPanel() {
      
      
      parse(this, currentEntity, getSharedDescriptor(currentEntity.getMetaProperty()).copy() );
      
      
    }
    
    
    public void removeNotify() {
      
      
      removeAll();
      
      
      BeanFactory factory = view.getBeanFactory();
      for (Iterator it=beans.iterator(); it.hasNext(); ) {
        PropertyBean bean = (PropertyBean)it.next();
        bean.removeChangeListener(this);
        bean.setProperty(null);
        try {
          factory.recycle(bean);
        } catch (Throwable t) {
          EditView.LOG.log(Level.WARNING, "Problem cleaning up bean "+bean, t);
        }
      }
      beans.clear();
      
      
      super.removeNotify();
      
    }
    
    
    void commit() {
      
      
      try{
        for (Iterator it = beans.iterator(); it.hasNext();) {
          
          PropertyBean bean = (PropertyBean)it.next();
          if (bean.hasChanged()&&bean.getProperty()!=null) {
            Property prop = bean.getProperty();
            
            PropertyProxy proxy = (PropertyProxy)prop.getContaining(PropertyProxy.class);
            if (proxy!=null) 
              prop = proxy.getProxied().setValue(prop.getPathToContaining(proxy), "");
            
            bean.commit(prop);
            
          }
        }
      } finally {
        ok.setEnabled(false);
      }
      
    }

    
    void select(Property prop) {
      
      
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
      
      for (Iterator it=beans.iterator(); it.hasNext(); ) {
        PropertyBean bean = (PropertyBean)it.next();
        if (bean.getProperty()==prop) 
          return bean;
      }
      
      
      for (Iterator it=beans.iterator(); it.hasNext(); ) {
        PropertyBean bean = (PropertyBean)it.next();
        if (bean.isDisplayable() && bean.getProperty()!=null && bean.getProperty().isContained(prop)) 
          return bean;
      }
      
      
      if (tabsPane!=null) {
        Component[] cs  = tabsPane.getComponents();
        for (int i = 0; i < cs.length; i++) {
          JComponent c = (JComponent)cs[i];
          if (c.getClientProperty(Property.class)==prop) 
            return c;
        }
      }
      
      
      return (PropertyBean)beans.get(0);
      
      
    }

    
    public void stateChanged(ChangeEvent e) {
      ok.setEnabled(true);
      cancel.setEnabled(true);
    }
    
    
    private void parse(JPanel panel, Property root, NestedBlockLayout descriptor)  {

      panel.setLayout(descriptor);
      
      
      for (Iterator cells = descriptor.getCells().iterator(); cells.hasNext(); ) {
        NestedBlockLayout.Cell cell = (NestedBlockLayout.Cell)cells.next();
        JComponent comp = createComponent(root, cell);
        if (comp!=null) 
          panel.add(comp, cell);
      }
      
      
    }
    
    
    private JComponent createComponent(Property root, NestedBlockLayout.Cell cell) {
      
      String element = cell.getElement();
      
      
      String version = cell.getAttribute("gedcom");
      if (version!=null & !root.getGedcom().getGrammar().getVersion().equals(version))
        return null;
      
      
      if ("tabs".equals(element)) {
        tabsPane = new ContextTabbedPane();
        
        for (Iterator tabs=cell.getNestedLayouts().iterator(); tabs.hasNext();) {
          NestedBlockLayout tabLayout = (NestedBlockLayout)tabs.next();
          JPanel tab = new JPanel();
          parse(tab, root, tabLayout);
          tabsPane.addTab("", root.getImage(false), tab);
        }
        
        createTabs(tabsPane);
        
        return tabsPane;
      }
      
      
      TagPath path = new TagPath(cell.getAttribute("path"));
      MetaProperty meta = root.getMetaProperty().getNestedRecursively(path, false);
      
      
      String iff = cell.getAttribute("if"); 
      if (iff!=null&&root.getProperty(new TagPath(iff))==null)
          return null;
      String ifnot = cell.getAttribute("ifnot"); 
      if (ifnot!=null&&root.getProperty(new TagPath(ifnot))!=null)
          return null;
      
      
      if ("label".equals(element)) {

        JLabel label;
        if (path.length()==1&&path.getLast().equals(currentEntity.getTag()))
          label = new JLabel(meta.getName() + ' ' + currentEntity.getId(), currentEntity.getImage(false), SwingConstants.LEFT);
        else
          label = new JLabel(meta.getName(cell.isAttribute("plural")), meta.getImage(), SwingConstants.LEFT);

        return label;
      }
      
      
      if ("bean".equals(element)) {
        
        PropertyBean bean = createBean(root, path, meta, cell.getAttribute("type"));
        if (bean==null)
          return null;
        
        if (root==currentEntity&&path.length()>1)
          topLevelTags.add(path.get(1));
        
        return cell.getAttribute("popup")==null ? bean : (JComponent)new PopupBean(bean);
      }

      
      throw new IllegalArgumentException("Template element "+cell.getElement()+" is unkown");
    }
    
    
    private PropertyBean createBean(Property root, TagPath path, MetaProperty meta, String beanOverride) {

      
      
      
      
      
      
      
      
      
      
      Property prop = root.getProperty(path, false);
      
      
      
      
      if (prop==null) 
        prop = new PropertyProxy(root).setValue(path, "");

      
      BeanFactory factory = view.getBeanFactory();
      PropertyBean bean = beanOverride==null ? factory.get(prop) : factory.get(beanOverride, prop);
      bean.addChangeListener(this);
      beans.add(bean);
      
      
      return bean;
    }
    
    
    private void createTabs(JTabbedPane tabs) {
      
      
      Set skippedTags = new HashSet();
      for (int i=0, j=currentEntity.getNoOfProperties(); i<j; i++) {
        Property prop = currentEntity.getProperty(i);
        
        String tag = prop.getTag();
        if (skippedTags.add(tag)&&topLevelTags.contains(tag)) 
          continue;
        topLevelTags.add(tag);
        
        createTab(prop, tabs);
        
      }
      
      
      JPanel newTab = new JPanel(new FlowLayout(FlowLayout.LEFT));
      newTab.setPreferredSize(new Dimension(64,64));
      tabs.addTab("", Images.imgNew, newTab);
      
      
      MetaProperty[] nested = currentEntity.getNestedMetaProperties(MetaProperty.WHERE_NOT_HIDDEN);
      Arrays.sort(nested);
      for (int i=0;i<nested.length;i++) {
        MetaProperty meta = nested[i];
        
        NestedBlockLayout descriptor = getSharedDescriptor(meta);
        if (descriptor==null||descriptor.getCells().isEmpty())
          continue;
        
        if (topLevelTags.contains(meta.getTag())&&meta.isSingleton())
          continue;
        
        newTab.add(new LinkWidget(new AddTab(meta)));
      }
    
      
    }
    
    
   private void createTab(Property prop, JTabbedPane tabs) {
     
     
     if (prop instanceof PropertyXRef) {
       
       try {
         String tt = ((PropertyXRef)prop).getTargetType();
         if (tt.equals(Gedcom.INDI)||tt.equals(Gedcom.FAM))
           return;
       } catch (IllegalArgumentException e) {
         
         return;
       }
       
       tabs.insertTab(prop.getPropertyName(), prop.getImage(false), view.getBeanFactory().get(prop), prop.getPropertyInfo(), 0);
       return;
     }
     
     
     MetaProperty meta = prop.getMetaProperty();
     NestedBlockLayout descriptor = getSharedDescriptor(meta);
     if (descriptor==null) 
       return;
     
     
     JPanel tab = new JPanel();
     tab.putClientProperty(Property.class, prop);

     parse(tab, prop, descriptor.copy());
     tabs.insertTab(meta.getName() + prop.format("{ $y}"), prop.getImage(false), tab, meta.getInfo(), 0);

     
   }
   
   private class ContextTabbedPane extends JTabbedPane implements ContextProvider {
     private ContextTabbedPane() {
       super(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
     }
     public ViewContext getContext() {
       
       Component selection = tabsPane.getSelectedComponent();
       Property prop = (Property)((JComponent)selection).getClientProperty(Property.class);
       if (prop==null)
         return null;
       
       return new ViewContext(prop).addAction(new DelTab(prop));
     }
   } 
    
  } 
  
  
  private class AddTab extends Action2 {
    
    private MetaProperty meta;
    private Property property;
    
    
    private AddTab(MetaProperty meta) {
      
      this.meta = meta;
      
      setText(meta.getName());
      setImage(meta.getImage());
      setTip(meta.getInfo());
    }
  
    
    public void actionPerformed(ActionEvent event) {
      
      
      if (currentEntity==null)
        return;
      
      gedcom.doMuteUnitOfWork(new UnitOfWork() {
        public void perform(Gedcom gedcom) {
          
          
          if (ok.isEnabled()&&view.isCommitChanges()) 
            beanPanel.commit();
          
          
          property = currentEntity.addProperty(meta.getTag(), "");
        }
      });
      
      
      if (beanPanel!=null) SwingUtilities.invokeLater(new Runnable() {
        
        public void run() {
          beanPanel.select(property);
        }
      });
      
      
    }
    
  } 
  
  
  private class DelTab extends Action2 {
    private Property prop;
    private DelTab(Property prop) {
      setText(EditView.resources.getString("action.del", prop.getPropertyName()));
      setImage(Images.imgCut);
      this.prop = prop;
    }
   public void actionPerformed(ActionEvent event) {
     
     
     if (currentEntity==null)
       return;
     
     gedcom.doMuteUnitOfWork(new UnitOfWork() {
       public void perform(Gedcom gedcom) {
         
         
         if (ok.isEnabled()&&view.isCommitChanges()) 
           beanPanel.commit();
         
         
         prop.getParent().delProperty(prop);
         
       }
     });
     

     
   }
 }

  
  private class Callback extends GedcomListenerAdapter {
    
    private Property setFocus;
    
    public void gedcomWriteLockAcquired(Gedcom gedcom) {
      setFocus = null;
    }
    
    public void gedcomWriteLockReleased(Gedcom gedcom) {
      setEntity(currentEntity, setFocus);
    }
    
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      if (currentEntity==entity)
        currentEntity = null;
    }
    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      if (setFocus==null && property.getEntity()==currentEntity) {
        setFocus = added;
      }
    }
    public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
      if (setFocus==null && property.getEntity()==currentEntity)
        setFocus = property;
    }
  };
  
} 
