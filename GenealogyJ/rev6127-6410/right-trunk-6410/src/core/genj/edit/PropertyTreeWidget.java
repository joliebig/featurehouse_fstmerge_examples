
package genj.edit;
 
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.GedcomListener;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyChange;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.gedcom.UnitOfWork;
import genj.io.PropertyReader;
import genj.io.PropertyTransferable;
import genj.util.swing.HeadlessLabel;
import genj.util.swing.ImageIcon;
import genj.view.ContextProvider;
import genj.view.ViewContext;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import spin.Spin;
import swingx.dnd.tree.DnDTree;
import swingx.dnd.tree.DnDTreeModel;
import swingx.tree.AbstractTreeModel;


public class PropertyTreeWidget extends DnDTree implements ContextProvider {
  
  private final static String UNIX_DND_FILE_PREFIX = "file:";
  
  
  private DefaultTreeCellRenderer defaultRenderer;
  
  
  private Gedcom gedcom;

  
  public PropertyTreeWidget(Gedcom gedcom) {

    
    super.setModel(new Model(gedcom));

    
    this.gedcom = gedcom;
    
    
    setCellRenderer(new Renderer());
    getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    setToggleClickCount(Integer.MAX_VALUE);
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        
        if (e.getButton()==1)
          return;
        
        TreePath path = getPathForLocation(e.getX(), e.getY());
        if (path==null)
          return;
        
        if (!getSelection().contains(path.getLastPathComponent()))
          setSelection(Collections.singletonList((Property)path.getLastPathComponent()));
      }
    });

    setExpandsSelectedPaths(true);
    ToolTipManager.sharedInstance().registerComponent(this);
    
    
  }
  
  
  public ViewContext getContext() {
    
    Entity root = (Entity)getRoot();
    if (root==null) 
      return new ViewContext(gedcom);
    
    List<Property> selection = getSelection();
    if (selection.isEmpty())
      return new ViewContext(root);
    
    return new ViewContext(gedcom, new ArrayList<Entity>(), selection);
  }
  
  
  public void setModel() {
    throw new IllegalArgumentException();
  }

  
  private Model getPropertyModel() {
    return (Model)getModel();
  }
  
  
  public Object[] getPathFor(Property property) {
    return getPropertyModel().getPathToRoot(property);
  }

  
  public void addNotify() {
    
    super.addNotify();
    
    gedcom.addGedcomListener((GedcomListener)Spin.over(getPropertyModel()));
  }

  
  
  public void removeNotify() {
    
    gedcom.removeGedcomListener((GedcomListener)Spin.over(getPropertyModel()));
    
    super.removeNotify();
  }
  
  
  public Dimension getPreferredScrollableViewportSize() {
    return new Dimension(256,128);
  }
  
  
  public void setRoot(Property property) {
    
    if (getPropertyModel().getRoot()==property)
      return;
    
    getPropertyModel().setRoot(property);
    
  }
  
  
  public void expandAllRows() {
    for (int i=0;i<getRowCount();i++)
      expandRow(i); 
  }
  
  
  public void expand(TagPath path) {
    rows: for (int row=getRowCount()-1;row>=0;row--) {
      TreePath tp = getPathForRow(row);
      if (path.length()==tp.getPathCount()) {
        for (int i=0;i<path.length();i++) {
          if (!path.get(i).equals( ((Property)tp.getPathComponent(i)).getTag() ))
            continue rows;
        }
        expandRow(row);
      }
    }
  }
    
 













  
  
  public Property getRoot() {
    return getPropertyModel().getPropertyRoot();
  }

    
  public void setRowHeight(int rowHeight) {
    super.setRowHeight(0);
  }
  
  
  public void setSelection(List<? extends Property> select) {
    clearSelection();
    
    Property root = (Property)getPropertyModel().getRoot();
    if (root==null) 
      return;
    
    TreePath first = null;
    for (Property p : select) {
      try {
        TreePath path = new TreePath(getPropertyModel().getPathToRoot(p));
        addSelectionPath(path);
        if (first==null) first = path;
      } catch (IllegalArgumentException e) {
        
      }
    }
    
    if (first!=null)
      scrollPathToVisible(first);
    
  }
  
  
  public List<Property> getSelection() {
    
    List<Property> result = new ArrayList<Property>();
    TreePath[] paths = getSelectionPaths();
    for (int i=0;paths!=null&&i<paths.length;i++) {
      result.add((Property)paths[i].getLastPathComponent());
    }
    
    return result;
  }
  
  
  public Property getPropertyAt(int x, int y) {
    
    TreePath path = super.getPathForLocation(x, y);
    if ((path==null) || (path.getPathCount()==0)) 
      return null;
    
    return (Property)path.getLastPathComponent();
  }

  
  public Property getPropertyAt(Point pos) {
    return getPropertyAt(pos.x, pos.y);
  }
  
  
  public String getToolTipText(MouseEvent event) {
    
    Property prop = getPropertyAt(event.getX(),event.getY());
    if (prop==null) return null;
    
    if (prop.isTransient()) return null;
    
    if (prop.getEntity()==null) return null;
    
    String info = prop.getPropertyInfo();
    if (info==null) 
      return null;
    
    return "<html><table width=200><tr><td>"+info+"</td></tr></table></html";    
  }

    
  public void setUI(TreeUI ui) {
    
    super.setUI(ui);
    
    defaultRenderer = new DefaultTreeCellRenderer();
  }
  
  
  public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    if (value instanceof Property)
      return ((Property)value).getTag();
    return "";
  }
  
  
  private static Gedcom draggingFrom = null;
  
  
  private class Model extends AbstractTreeModel implements DnDTreeModel, GedcomListener {

    
    private Property root = null;

    
    private Gedcom ged;
    
    
    protected Model(Gedcom gedcom) {
      this.ged = gedcom;
    }
    
    
    protected void setRoot(Property set) {
      
      root = set;
      
      rootExchanged();
      
      setRootVisible(root!=null);
    }
    
    public Property getPropertyRoot() {
      return root;
    }

    
    public Transferable createTransferable(Object[] nodes) {
      
      
      draggingFrom = ged;
      
      
      List<Property> props = new ArrayList<Property>(nodes.length);
      for (Object node : nodes)
        props.add((Property)node);
      List<Property> list = Property.normalize(props);
      
      
      return new PropertyTransferable(list);
    }

    public int getDragActions(Transferable transferable) {
      return COPY | MOVE;
    }

    @SuppressWarnings("unchecked")
    public int getDropActions(Transferable transferable, Object parent, int index) {

      try {

        
        if (transferable.isDataFlavorSupported(PropertyTransferable.VMLOCAL_FLAVOR)) {
          
          List<Property> dragged = (List<Property>)transferable.getTransferData(PropertyTransferable.VMLOCAL_FLAVOR);
          Property pparent = (Property)parent;
          while (pparent!=null) {
            if (dragged.contains(pparent)) 
              return 0;
            pparent = pparent.getParent();
          }
          return COPY | MOVE;
        }
        
        
        if (transferable.isDataFlavorSupported(PropertyTransferable.STRING_FLAVOR)) 
          return COPY | MOVE;
        
        
        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) 
          return COPY | MOVE;

      } catch (Exception e) {
      }
      
      
      return 0;
    }
    
    
    @SuppressWarnings("unchecked")
    public void drop(final Transferable transferable, final Object parent, final int index, final int action) throws IOException, UnsupportedFlavorException {

      final Property pparent = (Property)parent;

      
      if (transferable.isDataFlavorSupported(PropertyTransferable.VMLOCAL_FLAVOR)) {

        
        final List<Property> children = (List<Property>)transferable.getTransferData(PropertyTransferable.VMLOCAL_FLAVOR);
        if (action==MOVE&&pparent.hasProperties(children)) {
          ged.doMuteUnitOfWork(new UnitOfWork() {
            public void perform(Gedcom gedcom) throws GedcomException {
              pparent.moveProperties(children, index);
            }
          });
          return;
        }
        
        
        ged.doMuteUnitOfWork(new IOUnitOfWork() {
          protected void performIO(Gedcom gedcom) throws IOException, UnsupportedFlavorException {
            
            
            List<PropertyXRef> xrefs = new ArrayList<PropertyXRef>();
            String string = transferable.getTransferData(PropertyTransferable.STRING_FLAVOR).toString();
            
            
            new PropertyReader(new StringReader(string), xrefs, true).read(pparent, index);
            
            
            if (action==MOVE&&draggingFrom==gedcom) {
              for (Property child : children) 
                child.getParent().delProperty(child);
            }
            
            
            for (PropertyXRef xref : xrefs) {
              try { 
                xref.link(); 
              } catch (Throwable t) {
                EditView.LOG.log(Level.WARNING, "caught exception during dnd trying to link xrefs", t);
              }
            }
          }
        });
        
        return;
      }
      
      
      if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        
        ged.doMuteUnitOfWork(new IOUnitOfWork() {
          protected void performIO(Gedcom gedcom) throws IOException, UnsupportedFlavorException {
            for (File file : (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor))
              pparent.addFile(file);
          }
        });
        
        return;
      }
      
      
      if (!transferable.isDataFlavorSupported(PropertyTransferable.STRING_FLAVOR))
        return;
      
      final String string = transferable.getTransferData(PropertyTransferable.STRING_FLAVOR).toString();
      if (string.length()<4)
        return;
      
      
      if (string.startsWith(UNIX_DND_FILE_PREFIX)) {
        ged.doMuteUnitOfWork(new UnitOfWork() {
          public void perform(Gedcom gedcom) {
            for (StringTokenizer files = new StringTokenizer(string, "\n"); files.hasMoreTokens(); ) {
              String file = files.nextToken().trim();
              if (file.startsWith(UNIX_DND_FILE_PREFIX)) 
                pparent.addFile(new File(file.substring(UNIX_DND_FILE_PREFIX.length())));
            }
          }
        });
        return;
      }
      
      
      EditView.LOG.fine("reading dropped text '"+string+"'");
      ged.doMuteUnitOfWork(new IOUnitOfWork() {
        protected void performIO(Gedcom gedcom) throws IOException, UnsupportedFlavorException {
          new PropertyReader(new StringReader(string), null, true).read(pparent, index);
        }
      });
      
      
    }

    
    @SuppressWarnings("unchecked")
    public void drag(Transferable transferable, int action) throws UnsupportedFlavorException, IOException {
      
      
      final List<Property> children = (List<Property>)transferable.getTransferData(PropertyTransferable.VMLOCAL_FLAVOR);
      if (children.isEmpty())
        return;
      
      
      if (action==MOVE &&draggingFrom!=ged) {
        
        ged.doMuteUnitOfWork(new UnitOfWork() {
          public void perform(Gedcom gedcom) {
            for (int i=0;i<children.size();i++) {
              Property child = (Property)children.get(i);
              child.getParent().delProperty(child);
            }
          }
        });
      }
      
      
    }
       
    public void releaseTransferable(Transferable transferable) {
      draggingFrom  = null;
    }
    
      
    protected Object getParent(Object node) {
      
      if (node==root)
        return null;
      
      return ((Property)node).getParent();
    }
  
    
    public Object getChild(Object parent, int index) {
      return ((Property)parent).getProperty(index);
    }          
  
    
    public int getChildCount(Object parent) {
      return ((Property)parent).getNoOfProperties();
    }
    
    
    public int getIndexOfChild(Object parent, Object child) {
      try {
        return ((Property)parent).getPropertyPosition((Property)child);
      } catch (Throwable t) {
        return -1;
      }
    }          
  
    
    public Object getRoot() {
      return root;
    }          
  
    
    public boolean isLeaf(Object node) {
      
      return ((Property)node).getNoOfProperties()==0;
    }          
  
    public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
      
    }

    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      
      if (root==entity)
        setRoot(null);
    }

    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      
      
      if (root!=property.getEntity())
        return;
      
      Object[] path = getPathFor(property);
      fireTreeNodesInserted(this, path, new int[] { pos }, new Property[]{ added });
      
      
     
     
     
     
     



      
    }

    public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
      
      if (root!=property.getEntity())
        return;
      
      fireTreeNodesChanged(this, getPathFor(property), null, null);
    }

    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property deleted) {
      
      if (root!=property.getEntity())
        return;
      
      if (property instanceof PropertyChange)
        return;
      
      fireTreeNodesRemoved(this, getPathFor(property), new int[] { pos }, new Property[]{ deleted });
      
    }
  
  } 

  
  private class Renderer extends HeadlessLabel implements TreeCellRenderer {
    
    
    private Renderer() {
      setOpaque(true);
    }
    
    
    public Component getTreeCellRendererComponent(JTree tree, Object object, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

      
      if (!(object instanceof Property))
        return this;
      Property prop = (Property)object;

      
      if (defaultRenderer!=null) {
        if (sel) {
          setForeground(defaultRenderer.getTextSelectionColor());
          setBackground(defaultRenderer.getBackgroundSelectionColor());
        } else {
          setForeground(defaultRenderer.getTextNonSelectionColor());
          setBackground(defaultRenderer.getBackgroundNonSelectionColor());
        }
      }

      
      ImageIcon img = prop.getImage(true);
      if (prop.isPrivate()) 
        img = img.getOverLayed(MetaProperty.IMG_PRIVATE);
      setIcon(img);

      
      setText(prop instanceof Entity ? calcText((Entity)prop) : calcText(prop));

      
      return this;
    }

    private String calcText(Entity entity) {        
      return "@" + entity.getId() + "@ " + entity.getTag();
    } 
      
    private String calcText(Property prop) {

      StringBuffer result = new StringBuffer();
      
      if (!prop.isTransient()) {
        result.append(prop.getTag());
        result.append(' ');
      }

      
      if (prop.isSecret()) {
        result.append("*****");
      } else {
        String val = prop.getDisplayValue();
        int nl = val.indexOf('\n');
        if (nl>=0) val = val.substring(0, nl) + "...";
        result.append(val);
      }
      
      
      return result.toString();
    }
    
  } 
  
  private abstract class IOUnitOfWork implements UnitOfWork {
    public final void perform(Gedcom gedcom) throws GedcomException {
      try {
        performIO(gedcom);
      } catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }
    protected abstract void performIO(Gedcom gedcom) throws IOException, UnsupportedFlavorException;
  }

} 
