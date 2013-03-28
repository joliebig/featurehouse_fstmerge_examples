
package genj.edit;

import genj.common.SelectEntityWidget;
import genj.edit.beans.PropertyBean;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyEvent;
import genj.gedcom.TagPath;
import genj.gedcom.UnitOfWork;
import genj.io.PropertyReader;
import genj.io.PropertyTransferable;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.WordBuffer;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.TextAreaWidget;
import genj.view.ActionProvider;
import genj.view.SelectionSink;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;


 class AdvancedEditor extends Editor {
  
  private final static String
    ACC_CUT = "ctrl X",
    ACC_COPY = "ctrl C",
    ACC_PASTE = "ctrl V";

  private final static Clipboard clipboard = initClipboard();
  
  private final static Registry REGISTRY = Registry.get(AdvancedEditor.class);
  
  private Set<TagPath> expands = new HashSet<TagPath>();
  
  private boolean ignoreTreeSelection = false;

  
  private static Clipboard initClipboard() {
    try {
      return Toolkit.getDefaultToolkit().getSystemClipboard();
    } catch (Throwable t) {
      return new Clipboard("GenJ");
    }

  }
  
  
  private static Resources resources = Resources.get(AdvancedEditor.class);

  
  private Gedcom gedcom;

  
  private PropertyTreeWidget tree = null;

  
  private JPanel            editPane;
  private PropertyBean      bean = null;

  
  private JSplitPane        splitPane = null;

  
  private EditView view;

  
  private Callback callback;
  
  
  public AdvancedEditor(Gedcom gedcom, EditView view) {
    
    
    this.gedcom = gedcom;
    this.view = view;
    
    
    tree = new Tree();
    callback = new Callback();
    tree.addTreeSelectionListener(callback);
    tree.addTreeWillExpandListener(callback);
    
    JScrollPane treePane = new JScrollPane(tree);
    treePane.setMinimumSize  (new Dimension(160, 128));
    treePane.setPreferredSize(new Dimension(160, 128));
    treePane.getHorizontalScrollBar().setFocusable(false); 
    treePane.getVerticalScrollBar().setFocusable(false);
        
    
    editPane = new JPanel(new BorderLayout());
    JScrollPane editScroll = new JScrollPane(editPane);
    
    editScroll.getVerticalScrollBar().setFocusable(false);
    editScroll.getHorizontalScrollBar().setFocusable(false);

    
    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treePane, editScroll);
    splitPane.setDividerLocation(REGISTRY.get("divider",-1));
    splitPane.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        if (JSplitPane.DIVIDER_LOCATION_PROPERTY.equals(evt.getPropertyName()))
          REGISTRY.put("divider",splitPane.getDividerLocation());
      }
    });

    
    setLayout(new BorderLayout());
    add(splitPane, BorderLayout.CENTER);
    
    
    setFocusTraversalPolicy(new FocusPolicy());
    setFocusCycleRoot(true);

    
    String paths = REGISTRY.get("expand", "INDI:BIRT,INDI:RESI,INDI:OBJE,FAM:MARR");
    for (String path : paths.split(",")) {
      try {
        expands.add(new TagPath(path));
      } catch (IllegalArgumentException iae) {
        
      }
    }

    
  }
  
  @Override
  public void removeNotify() {
    
    WordBuffer paths = new WordBuffer(",");
    for (TagPath path : expands) 
      paths.append(path);
    REGISTRY.put("expand", paths.toString());
    
    super.removeNotify();
  }
  
  
  public ViewContext getContext() {
    return tree.getContext();
  }

  
  @Override
  public void setContext(Context context) {
    setContextImpl(context, true);
  }
  
  private void setContextImpl(Context context, boolean pickFirstProperty) {
    
    
    if (bean!=null) 
      bean.removeChangeListener(changes);
    bean = null;
    editPane.removeAll();
    editPane.revalidate();
    editPane.repaint();
    changes.setChanged(false);
    
    
    if (context.getGedcom()==null||context.getEntities().isEmpty()) {
      try {
        ignoreTreeSelection = true;
        tree.setRoot(null);
      } finally {
        ignoreTreeSelection = false;
      }
      return;
    }
    
    ignoreTreeSelection = true;
    
    
    tree.clearSelection();

    
    Entity entity = context.getEntity();
    if (entity!=tree.getRoot()) {
      tree.setRoot(entity);
      for (TagPath path : expands)
        expand(path);
    }

    
    Property root = tree.getRoot();
    if (root!=null) {
      Gedcom gedcom = root.getGedcom();
      
      view.commit();
    }

    
    List<? extends Property> props = context.getProperties();
    if (props.isEmpty()) {
      if (pickFirstProperty&&entity.getNoOfProperties()>0)
        props = Collections.singletonList(entity.getProperty(0)); 
      else
        props = Collections.singletonList(entity); 
    }
    tree.setSelection(props);
    
    ignoreTreeSelection = false;
    
    
    if (props.size()!=1)
      return;
    Property prop = props.get(0);
    try {

      
      bean = PropertyBean.getBean(prop.getClass()).setContext(prop);
      
      
      editPane.add(bean, BorderLayout.CENTER);

      
      final JLabel label = new JLabel(Gedcom.getName(prop.getTag()), prop.getImage(false), SwingConstants.LEFT);
      editPane.add(label, BorderLayout.NORTH);

      
      changes.setChanged(false);
      bean.addChangeListener(changes);
      
      
      if (view.isGrabFocus()) 
        bean.requestFocus();

    } catch (Throwable t) {
      EditView.LOG.log(Level.WARNING,  "Property bean "+bean, t);
    }
    
    
  }

  
  public void expand(TagPath path) {
    tree.expand(path);
  }

  @Override
  public void commit() {
    Property root = tree.getRoot();
    if (root==null)
      return;
    Gedcom gedcom = root.getGedcom();

    if (bean!=null) 
      bean.commit();

  }
  
  
  private class Propagate extends Action2 {
    
    private Entity entity;
    private List<Property> properties;
    private String what;
    
    
    private Propagate(List<Property> selection) {
      
      this.entity = (Entity)tree.getRoot();
      properties = Property.normalize(selection);
      
      if (properties.isEmpty()) {
        setText(resources.getString("action.propagate", ""));
        setEnabled(false);
        return;
      }
      
      this.what = "'"+Property.getPropertyNames(properties,5)+"' ("+properties.size()+")";
      setText(resources.getString("action.propagate", what)+" ...");
    }
    
    public void actionPerformed(ActionEvent event) {
      
      
      final TextAreaWidget text = new TextAreaWidget("", 4, 10, false, true);
      final SelectEntityWidget select = new SelectEntityWidget(gedcom, entity.getTag(), resources.getString("action.propagate.toall"));
      select.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Entity target = select.getSelection();
          String string = target==null ? resources.getString("action.propagate.all", new Object[] { what, ""+select.getEntityCount(), Gedcom.getName(entity.getTag()) } )
              : resources.getString("action.propagate.one", new Object[]{ what, target.getId(), Gedcom.getName(target.getTag()) });
          text.setText(string);
        }
      });
      
      final JCheckBox check = new JCheckBox(resources.getString("action.propagate.value"));
      
      JPanel panel = new JPanel(new NestedBlockLayout("<col><select wx=\"1\"/><note wx=\"1\" wy=\"1\"/><check wx=\"1\"/></col>"));
      panel.add(select);
      panel.add(new JScrollPane(text));
      panel.add(check);
      
      
      select.setSelection(gedcom.getEntity(REGISTRY.get("select."+entity.getTag(), (String)null)));

      
      boolean cancel = 0!=DialogHelper.openDialog(getText(), DialogHelper.WARNING_MESSAGE, panel, Action2.okCancel(), AdvancedEditor.this);
      if (cancel)
        return;

      final Entity selection = select.getSelection();
      
      
      REGISTRY.put("select."+entity.getTag(), selection!=null ? selection.getId() : null);
      
      
      try {
        gedcom.doUnitOfWork(new UnitOfWork() {        
          public void perform(Gedcom gedcom) throws GedcomException {
            for (Entity to : selection!=null ? Collections.singletonList(selection) : gedcom.getEntities(entity.getTag())) 
              Propagate.this.copy(properties, entity, to, check.isSelected());
          }
        });
      } catch (GedcomException e) {
        DialogHelper.openDialog(null,DialogHelper.ERROR_MESSAGE,e.getMessage(),Action2.okOnly(),AdvancedEditor.this);
      }

      
    }
    
    private void copy(List<Property> selection, Entity from, Entity to, boolean values)  throws GedcomException {
      
      if (from==to)
        return;
      
      for (Property property : selection) {
        TagPath path = property.getParent().getPath();
        Property root = to.getProperty(path);
        if (root==null)
          root = to.setValue(path, "");
        root.copyProperties(property, values);
      }
      
    }
  } 
  
  
  private class Cut extends Action2 {

    
    protected List<Property> presetSelection; 
    
    
    private Cut(List<Property> preset) {
      presetSelection = Property.normalize(preset);
      super.setImage(Images.imgCut);
      super.setText(resources.getString("action.cut"));
    }
    
    private Cut() {
      
    }
    
    
    public void actionPerformed(ActionEvent event) {
      
      
      final List<Property> selection = presetSelection!=null ? presetSelection : Property.normalize(tree.getSelection());
      if (selection.isEmpty())
        return;
      
      
      if (selection.contains(tree.getRoot())) {
        selection.clear();
        selection.addAll(Arrays.asList(tree.getRoot().getProperties()));
      }
      
      
      String veto = getVeto(selection);
      if (veto.length()>0) {
        int rc = DialogHelper.openDialog(resources.getString("action.cut"), DialogHelper.WARNING_MESSAGE, veto, new Action[]{ new Action2(resources.getString("action.cut")), Action2.cancel() }, AdvancedEditor.this );
        if (rc!=0)
          return;
      }
      
      
      try {
        clipboard.setContents(new PropertyTransferable(selection).getStringTransferable(), null);
      } catch (Throwable t) {
        EditView.LOG.log(Level.WARNING, "Couldn't copy properties", t);
        return;
      }
      
      
      gedcom.doMuteUnitOfWork(new UnitOfWork() {
        public void perform(Gedcom gedcom) {
          for (Property p : selection)  
            p.getParent().delProperty(p);
        }
      });
      
    }
    
    
    private String getVeto(List<Property> properties) {
      
      StringBuffer result = new StringBuffer();
      for (Property p : properties) {
        
        String veto = p.getDeleteVeto();
        if (veto!=null) {
          
          result.append(resources.getString("del.warning", p.getPropertyName(), p.getParent().getPropertyName(), veto  ));
          result.append("\n");
        }
      }

      return result.toString();
    }
      
  } 

  
  private class Copy extends Action2 {
  	
    
    protected List<Property> presetSelection; 
    
    
    protected Copy(List<Property> preset) {
      presetSelection = Property.normalize(preset);
      setText(resources.getString("action.copy"));
      setImage(Images.imgCopy);
    }
    
    protected Copy() {
    }
    
    public void actionPerformed(ActionEvent event) {
      
      
      List<Property> selection = presetSelection;
      if (selection==null) 
        selection = Property.normalize(tree.getSelection());
      
      
      if (selection.contains(tree.getRoot()))
        selection = Arrays.asList(tree.getRoot().getProperties());
      
      try {
        clipboard.setContents(new PropertyTransferable(selection).getStringTransferable(), null);
      } catch (Throwable t) {
        EditView.LOG.log(Level.WARNING, "Couldn't copy properties", t);
      }
    }

  } 
    
  
  private class Paste extends Action2 {
  	
    
    private Property presetParent; 
    
    
    protected Paste(Property property) {
      presetParent = property;
      setText(resources.getString("action.paste"));
      setImage(Images.imgPaste);
      
      
      
      
    }
    
    protected Paste() {
    }
    
    public void actionPerformed(ActionEvent event) {

      
      final String content;
      try {
        content = clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor).toString();
      } catch (Throwable t) {
        EditView.LOG.log(Level.INFO, "Accessing system clipboard as stringFlavor failed ("+t.getMessage()+")");
        return;
      }
      
      
      final Property parent;
      if (presetParent!=null) 
        parent = presetParent;
      else if (tree.getSelectionCount()==1)
        parent = (Property)tree.getSelection().get(0);
      else 
        return;
      
      
      gedcom.doMuteUnitOfWork(new UnitOfWork() {
        public void perform(Gedcom gedcom) throws GedcomException {
          PropertyReader reader = new PropertyReader(new StringReader(content), null, true);
          reader.setMerge(true);
          try {
            reader.read(parent);
          } catch (IOException e) {
            throw new GedcomException("IO during read()");
          }
        }
      });

      
    }
  
  } 
  
  
  private class Add extends Action2 {
    
    private Property parent; 
    private String[] tags;
    private boolean addDefaults = true;
    
    protected Add(Property parent, MetaProperty meta) {
      this.parent = parent;
      String txt = meta.getName();
      if (!txt.equals(meta.getTag()))
        txt += " ("+meta.getTag()+")";
      setText(txt);
      setImage(meta.getImage());
      tags = new String[]{meta.getTag()};
    }
    
    protected Add(Property parent) {
      this.parent = parent;
      setText(resources.getString("action.add")+" ...");
      setImage(Images.imgAdd);
    }
    
    public void actionPerformed(ActionEvent event) {
      
      
      if (tags==null) {
        JLabel label = new JLabel(resources.getString("add.choose"));
        ChoosePropertyBean choose = new ChoosePropertyBean(parent);
        JCheckBox check = new JCheckBox(resources.getString("add.default_too"),addDefaults);
        int option = DialogHelper.openDialog(resources.getString("add.title"),DialogHelper.QUESTION_MESSAGE,new JComponent[]{ label, choose, check },Action2.okCancel(),AdvancedEditor.this); 
        if (option!=0)
          return;
        
        tags = choose.getSelectedTags();
        addDefaults = check.isSelected();
        if (tags.length==0)  {
          DialogHelper.openDialog(null,DialogHelper.ERROR_MESSAGE,resources.getString("add.must_enter"),Action2.okOnly(),AdvancedEditor.this);
          return;
        }
      }
      
      
      tree.clearSelection();
  
      
      final List<Property> newProps = new ArrayList<Property>();
      gedcom.doMuteUnitOfWork(new UnitOfWork() {
        public void perform(Gedcom gedcom) {
          for (int i=0;i<tags.length;i++) {
            Property prop = parent.addProperty(tags[i], "");
            newProps.add(prop);
            if (addDefaults) prop.addDefaultProperties();
          } 
        };
      });
    
      
      Property newProp = newProps.isEmpty() ? null : (Property)newProps.get(0);
      if (newProp instanceof PropertyEvent) {
        Property pdate = ((PropertyEvent)newProp).getDate(false);
        if (pdate!=null) newProp = pdate;
      }
      tree.setSelectionPath(new TreePath(tree.getPathFor(newProp)));
      
    
      
    }

  } 
    
  
  private class Callback implements TreeSelectionListener, TreeWillExpandListener {
    
    
    public void valueChanged(TreeSelectionEvent e) {
      
      if (ignoreTreeSelection||tree.getRoot()==null) 
        return;
      List<Property> selection = tree.getSelection();
      Context ctx = new Context(gedcom, Collections.singletonList((Entity)tree.getRoot()), selection);
      setContextImpl(ctx, false);
      if (!selection.isEmpty())
        SelectionSink.Dispatcher.fireSelection(AdvancedEditor.this, ctx, false);
    }

    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
      TreePath path = event.getPath();
      int len = path.getPathCount();
      if (len==1)
        throw new ExpandVetoException(event);
      String[] tags = new String[len];
      for (int i=0;i<len;i++) 
        tags[i] = ((Property)path.getPathComponent(i)).getTag();
      expands.remove(new TagPath(tags, null));
    }

    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
      TreePath path = event.getPath();
      int len = path.getPathCount();
      if (len==1)
        return;
      String[] tags = new String[len];
      for (int i=0;i<len;i++) 
        tags[i] = ((Property)path.getPathComponent(i)).getTag();
      expands.add(new TagPath(tags, null));
    }
  
  } 

  
  private class FocusPolicy extends LayoutFocusTraversalPolicy {
    public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
      
      Component result = super.getComponentAfter(focusCycleRoot, aComponent);
      if (result==null)
        return null;
      
      
      
      if (bean!=null&&!SwingUtilities.isDescendingFrom(result, bean)) {
        int[] selection = tree.getSelectionRows();
        if (selection!=null&&selection.length>0)
          tree.setSelectionRow( (selection[0]+1) % tree.getRowCount());
      }
      
      return result;
    }
    public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
      
      Component result = super.getComponentBefore(focusCycleRoot, aComponent);
      if (result==null)
        return null;
      
      
      
      if (bean!=null&&!SwingUtilities.isDescendingFrom(result, bean)) 
        tree.setSelectionRow( (tree.getSelectionRows()[0]-1) % tree.getRowCount());
      
      return result;
    }
  } 
  
  
  private class Tree extends PropertyTreeWidget {
    
    
    private Tree() {
      super(gedcom);
      
      
      setRequestFocusEnabled(false);
      
      new Cut().install(this, ACC_CUT, JComponent.WHEN_FOCUSED);
      new Copy().install(this, ACC_COPY, JComponent.WHEN_FOCUSED);
      new Paste().install(this, ACC_PASTE, JComponent.WHEN_FOCUSED);
    }

    
    public ViewContext getContext() {
      
      
      ViewContext result = super.getContext();
      List<? extends Property> props = result.getProperties();
      List<Property> selection = tree.getSelection();

      
      if (!props.isEmpty()) {
        result.addAction(new Cut(selection));
        result.addAction(new Copy(selection));
      }
      if (selection.size()==1) {
        result.addAction(new Paste((Property)selection.get(0)));
        
        
        result.addAction(new ActionProvider.SeparatorAction());
        Property prop = (Property)selection.get(0);
        if (!prop.isTransient()) {
          result.addAction(new Add(prop));
          Action2.Group group = new Action2.Group(resources.getString("action.add"));
          MetaProperty[] metas = prop.getNestedMetaProperties(MetaProperty.WHERE_NOT_HIDDEN | MetaProperty.WHERE_CARDINALITY_ALLOWS);
          Arrays.sort(metas);
          for (int i=0;i<metas.length;i++)
            if (metas[i].isInstantiated())
              group.add(new Add(prop, metas[i]));
          result.addActions(group);
        }
      }
      
      if (!selection.isEmpty()&&!selection.contains(tree.getRoot()))
          result.addAction(new Propagate(selection));

      
      return result;
    }

  } 
  
} 
