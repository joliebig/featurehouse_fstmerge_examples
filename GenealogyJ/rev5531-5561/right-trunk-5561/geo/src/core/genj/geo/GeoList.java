
package genj.geo;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.view.ContextProvider;
import genj.view.ContextSelectionEvent;
import genj.view.ViewContext;
import genj.view.ViewManager;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import swingx.tree.AbstractTreeModel;


 class GeoList extends JPanel {
  
  private static final String
    TXT_LOCATION = GeoView.RESOURCES.getString("location"),
    TXT_CHANGE = GeoView.RESOURCES.getString("location.change"),
    TXT_LATLON = GeoView.RESOURCES.getString("location.latlon"),
    TXT_UNKNOWN = GeoView.RESOURCES.getString("location.unknown");
  
  
  private GeoView view;
  
  
  private ViewManager viewManager;
  
  
  private GeoModel model;

  
  private Content tree; 
  
  
  private boolean ignoreSelectionChanges = false;
  
  
  
  public GeoList(GeoModel model, GeoView view, ViewManager viewManager) {
    
    
    this.model = model;
    this.view = view;
    this.viewManager = viewManager;
    
    
    tree = new Content(model);
    Update update = new Update();
    tree.getSelectionModel().addTreeSelectionListener(update);
    tree.setCellRenderer(new Renderer());
    
    Box buttonPanel = new Box(BoxLayout.X_AXIS);
    ButtonHelper bh = new ButtonHelper().setContainer(buttonPanel);
    bh.create(new UnFold(true));
    bh.create(new UnFold(false));
    bh.create(update);
    
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, new JScrollPane(tree));
    add(BorderLayout.SOUTH, buttonPanel);
    
    setPreferredSize(new Dimension(160,64));
    
    
  }

  
  public void setSelectedLocations(Collection locations) {
    
    if (ignoreSelectionChanges)
      return;
    
    TreePath[] paths = ((Model)tree.getModel()).getPathsToLocations(locations);
    if (paths.length==0)
      return;
    
    
    Rectangle bounds = tree.getPathBounds(paths[0]);
    bounds.height = tree.getParent().getHeight();
    tree.scrollRectToVisible(bounds);

    
    try {
      ignoreSelectionChanges = true;
      tree.setSelectionPaths(paths);
    } finally {
      ignoreSelectionChanges = false;
    }
  }
  
  
  public void setSelectedContext(ViewContext context) {
    
    if (ignoreSelectionChanges)
      return;
    
    
    Property[] properties = context.getProperties();
    List paths = new ArrayList(properties.length);
    for (int i = 0; i < properties.length; i++) {
      
      TreePath path = ((Model)tree.getModel()).getPathToProperty(properties[i]);
      if (path!=null) {
        paths.add(path.getParentPath());
        paths.add(path);
      }
    }
    
    
    if (paths.isEmpty())
      return;
    
    
    try {
      ignoreSelectionChanges = true;
      tree.getSelectionModel().setSelectionPaths((TreePath[])paths.toArray(new TreePath[paths.size()]));
    } finally {
      ignoreSelectionChanges = false;      
    }
    
    
    TreePath first = (TreePath)paths.get(0);
    tree.makeVisible(first);
    Rectangle bounds = tree.getPathBounds(first);
    bounds.width = 1;
    tree.scrollRectToVisible(bounds);
    
    
  }
  
  
  private class UnFold extends Action2 {
    private boolean fold;
    private UnFold(boolean fold) {
      setText( fold ? "+" : "-");
      this.fold = fold;
    }
    protected void execute() {
      TreePath[] paths = ((Model)tree.getModel()).getPathsToLocations();
      for (int i=0;i<paths.length;i++) {
        if (!fold) tree.collapsePath(paths[i]); else tree.expandPath(paths[i]); 
      }
    }
  }
  
  
  private class Update extends Action2 implements TreeSelectionListener {
    
    private Update() {
      setText(TXT_CHANGE);
      setEnabled(false);
    }
    public void valueChanged(TreeSelectionEvent e) {
      
      setEnabled(tree.getSelectionCount()==1 && tree.getSelectionPath().getPathCount()==2);
    }
    protected void execute() {
      
      Action[] actions = new Action[]{ new Action2(GeoView.RESOURCES, "query.remember"), Action2.cancel()  };
      TreePath selection = tree.getSelectionPath();
      if (selection==null)
        return;
      GeoLocation location = (GeoLocation)selection.getLastPathComponent();
      final QueryWidget query = new QueryWidget(location, view);
      
      int rc = WindowManager.getInstance(GeoList.this).openDialog("query", TXT_CHANGE, WindowManager.QUESTION_MESSAGE, query, actions, GeoList.this);
      
      if (rc==0)  {
        GeoLocation loc = query.getGeoLocation();
        if (loc!=null)
          model.setCoordinates(location, loc.getCoordinate());
      }
      
    }
  }
  
  
  private class Renderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      StringBuffer sb  = new StringBuffer();
      if (value instanceof GeoLocation) {
        GeoLocation loc = (GeoLocation)value;
        sb.append(loc.getJurisdictionsAsString());
        sb.append(" (");
        if (loc.getMatches()==0) {
          sb.append(TXT_UNKNOWN);
        } else {
          sb.append(loc.getCoordinateAsString());
          if (loc.getMatches()>1)
            sb.append("?");
        }
        sb.append(")");
        setText(sb.toString());
        setIcon(null);
        return this;
      } 
      if (value instanceof Property) {
        Property prop = (Property)value;
        Property date = prop.getProperty("DATE", true);
        if (date!=null) {
          sb.append(date.toString());
          sb.append(" ");
        }
        sb.append(Gedcom.getName(prop.getTag()));
        sb.append(" ");
        sb.append(prop.getEntity().toString());
        setText(sb.toString());
        setIcon(prop.getImage(false));
      }
      
      return this;
    }
  }
  
  
  private class Content extends JTree implements TreeSelectionListener, ContextProvider {
    
    
    private Content(GeoModel geomodel) {
      super(new Model(geomodel));
      getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
      setRootVisible(false);
      setShowsRootHandles(true);
      addTreeSelectionListener(this);
    }
    
    
    public ViewContext getContext() {
      ViewContext result = new ViewContext(model.getGedcom());
      TreePath[] selections = getSelectionPaths();
      for (int i = 0; selections!=null  && i < selections.length; i++) {
        Object selection = selections[i].getLastPathComponent();
        if (selection instanceof Property) 
          result.addProperty((Property)selection);
      }
      return result;
    }
    
    
    public void valueChanged(TreeSelectionEvent e) {
      
      if (ignoreSelectionChanges)
        return;
      
      Set props = new HashSet();
      Set locs = new HashSet();
        
      TreePath[] paths = tree.getSelectionModel().getSelectionPaths();
      if (paths!=null) for (int i=0; i<paths.length; i++) {
        if (paths[i].getPathCount()==3)
          props.add(paths[i].getPathComponent(2));
        if (paths[i].getPathCount()>1)
          locs.add(paths[i].getPathComponent(1));
      }

      
      view.setSelection(locs);
      
      
      try {
        ignoreSelectionChanges=true;
        
        
        if (!props.isEmpty()) {
          ViewContext context = new ViewContext(model.getGedcom());
          context.addProperties(Property.toArray(props));
          WindowManager.broadcast(new ContextSelectionEvent(context, this));
        }
      } finally {
        ignoreSelectionChanges = false;        
      }
      
    }
    
  } 
  
  
  private static class Model extends AbstractTreeModel  implements GeoModelListener {
    
    private GeoModel geo;
    private List locations = new ArrayList();
    
    private Model(GeoModel geo) {
      this.geo = geo;
      
      locations.addAll(geo.getLocations());
      Collections.sort(locations);
      
      geo.addGeoModelListener(this);
    }
    
    private TreePath[] getPathsToLocations() {
      return getPathsToLocations(locations);
    }
    private TreePath[] getPathsToLocations(Collection locations) {
      TreePath[] result = new TreePath[locations.size()];
      Iterator it = locations.iterator();
      for (int i=0;i<result.length;i++) 
        result[i] = new TreePath(new Object[] { this, it.next() });
      return result;
    }
    
    private TreePath getPathToProperty(Property prop) {
      
      
      while (!(prop.getParent() instanceof Entity)) {
        prop = prop.getParent();
        if (prop==null) return null;
      }
      
      
      for (int i=0;i<locations.size();i++) {
        GeoLocation loc = (GeoLocation)locations.get(i);
        if (loc.properties.contains(prop))
          return new TreePath(new Object[] { this, loc, prop} );
      }
      
      
      return null;
    }
    
    
    public void locationAdded(GeoLocation location) {
      
      ListIterator it = locations.listIterator();
      while (it.hasNext()) {
        if (((Comparable)it.next()).compareTo(location)>0) {
          it.previous();
          break;
        }
      }
      int pos = it.nextIndex();
      it.add(location);
      
      fireTreeNodesInserted(this, new Object[]{this}, new int[] { pos }, new Object[] { location });
    }

    public void locationUpdated(GeoLocation location) {
      fireTreeStructureChanged(this, new Object[]{ this, location}, null , null);
    }

    public void locationRemoved(GeoLocation location) {
      int i = locations.indexOf(location);
      locations.remove(i);
      fireTreeNodesRemoved(this, new Object[]{this}, new int[] { i }, new Object[] { location });
    }
    
    public void asyncResolveEnd(int status, String msg) {
      
    }

    public void asyncResolveStart() {
      
    }

    
    protected Object getParent(Object node) {
      throw new IllegalArgumentException();
    }
    public Object getRoot() {
      return this;
    }
    public int getChildCount(Object parent) {
      if (geo==null)
        return 0;
      return parent==this ? locations.size() : ((GeoLocation)parent).getNumProperties();
    }
    public boolean isLeaf(Object node) {
      return node instanceof Property;
    }
    public Object getChild(Object parent, int index) {
      return parent==this ? locations.get(index) :  ((GeoLocation)parent).getProperty(index);
    }
    public int getIndexOfChild(Object parent, Object child) {
      if (parent==this)
        return locations.indexOf(child);
      return ((GeoLocation)parent).getPropertyIndex((Property)child);
    }
    
  } 

}
