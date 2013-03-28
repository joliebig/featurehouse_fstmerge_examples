
package genj.common;

import genj.gedcom.Gedcom;
import genj.gedcom.Grammar;
import genj.gedcom.TagPath;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


public class PathTreeWidget extends JScrollPane {

  
  private List<Listener> listeners = new ArrayList<Listener>();
  
  
  private Grammar grammar = Grammar.V55;
  
  
  private JTree   tree;
  
  
  private Model model = new Model();

  
  public PathTreeWidget() {

    Callback callback = new Callback();
    
    
    tree = new JTree(model);
    tree.setShowsRootHandles(false);
    tree.setRootVisible(false);
    tree.setCellRenderer(new Renderer());
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addMouseListener(callback);
    tree.addTreeWillExpandListener(callback);

    
    setMinimumSize(new Dimension(160, 160));
    setPreferredSize(new Dimension(160, 160));
    getViewport().setView(tree);

    
  }
  
  public void setGrammar(Grammar grammar) {
    this.grammar = grammar;
  }

  
  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  
  private void fireSelectionChanged(TagPath path, boolean on) {
    
    Listener[] ls = (Listener[])listeners.toArray(new Listener[listeners.size()]);
    for (int l=0;l<ls.length;l++)
      ls[l].handleSelection(path,on);
    
  }
  
  
  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  
  public void setPaths(TagPath[] paths, TagPath[] selection) {
    model.setPaths(paths, selection);

    
    for (TagPath path : paths) {
      Object[] treepath = new Object[2];
      treepath[0] = model;
      treepath[1] = new TagPath(path.get(0));
      tree.expandPath(new TreePath(treepath));
    }
    
    
    for (TagPath path : selection) {
      Object[] treepath = new Object[path.length()];
      treepath[0] = model;
      for (int i=1;i<path.length();i++) {
        treepath[i] = new TagPath(path, i);
      }
      tree.expandPath(new TreePath(treepath));
    }
    
  }

  public void setSelected(TagPath path, boolean set) {
    model.setSelected(path, set);
  }
  
  
  public TagPath[] getSelection() {
    return (TagPath[])model.getSelection().toArray(new TagPath[0]);
  }

  
  private class Renderer extends DefaultTreeCellRenderer {

    
    private JPanel        panel = new JPanel();
    private JCheckBox     checkbox = new JCheckBox();

    
    private Renderer() {
      panel.setLayout(new BorderLayout());
      panel.add(checkbox,"West");
      panel.add(this    ,"Center");
      checkbox.setOpaque(false);
      panel.setOpaque(false);
    }
    
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      
      super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
      
      if (value instanceof TagPath) {
        TagPath path = (TagPath)value; 
        setText( path.getLast() + " ("+Gedcom.getName(path.getLast())+")");
        setIcon( grammar.getMeta(path).getImage() );
        checkbox.setSelected(model.getSelection().contains(value));
        panel.invalidate(); 
      }      
      
      return panel;
    }

  } 

  
  private class Model implements TreeModel {
    
    
    private Set<TagPath> paths = new HashSet<TagPath>();

    
    private Set<TagPath> selection = new HashSet<TagPath>();
    
    
    private Map<TagPath,TagPath[]> path2childen = new HashMap<TagPath,TagPath[]>(); 
    
    
    private List<TreeModelListener> tmlisteners = new CopyOnWriteArrayList<TreeModelListener>();
  
    
    public void setPaths(TagPath[] ps, TagPath[] ss) {

      
      selection = new HashSet<TagPath>(Arrays.asList(ss));
      
      
      paths = new HashSet<TagPath>();
      paths.addAll(Arrays.asList(ps));
      paths.addAll(Arrays.asList(ss));
      
      
      TreeModelEvent e = new TreeModelEvent(this, new Object[]{ this });
      for (TreeModelListener listener : tmlisteners)
        listener.treeStructureChanged(e);
      
      
    }
    
    private void setSelected(TagPath path, boolean set) {

      if (!paths.contains(path))
        throw new IllegalArgumentException("path not a choice");
      
      
      if (set)
        selection.add(path);
      else
        selection.remove(path);
      
      
      TreeModelEvent e = new TreeModelEvent(this, new Object[]{ this });
      for (TreeModelListener listener : tmlisteners) 
        listener.treeNodesChanged(e);
      
      fireSelectionChanged(path, set);
      
    }
    
    
    private void toggleSelection(TagPath path) {
      
      if (selection.contains(path))
        setSelected(path, false);
      else
        setSelected(path, true);
    }
    
    
    public void addTreeModelListener(TreeModelListener l) {
      tmlisteners.add(l);
    }
  
    
    public void removeTreeModelListener(TreeModelListener l) {
      tmlisteners.remove(l);
    }

    
    public Object getChild(Object parent, int index) {
      return getChildren(parent)[index];
    }
  
    
    public int getChildCount(Object parent) {
      return getChildren(parent).length;
    }
    
    
    private TagPath[] getChildren(Object node) {
      
      TagPath[] result = path2childen.get(node);
      if (result==null) {
        result = node==this ? getChildrenOfRoot() : getChildrenOfNode((TagPath)node);
      }
      
      return result;
    }
    
    
    private TagPath[] getChildrenOfNode(TagPath path) {
      
      List<TagPath> children = new ArrayList<TagPath>(8);
      for (TagPath p : paths) {
        if (p.length()>path.length()&&p.startsWith(path)) 
          add(new TagPath(p, path.length()+1), children);
      }
      for (TagPath sel : selection) {
        if (sel.length()>path.length()&&sel.startsWith(path)) 
          add(new TagPath(sel, path.length()+1), children);
      }
      
      return TagPath.toArray(children);
    }
    
    
    private TagPath[] getChildrenOfRoot() {
      
      List<TagPath> children = new ArrayList<TagPath>(8);
      for (TagPath path : paths) 
        add(new TagPath(path, 1), children);
      for (TagPath path : selection) 
        add(new TagPath(path, 1), children);
      
      return TagPath.toArray(children);
    }
    
    private void add(TagPath path, List<TagPath> list) {
      if (!list.contains(path))
        list.add(path);
    }
    
    
    public int getIndexOfChild(Object parent, Object child) {
      return 0;
    }
  
    
    public Object getRoot() {
      return this;
    }
  
    
    public boolean isLeaf(Object node) {
      return getChildren(node).length==0;
    }
  
    
    public void valueForPathChanged(TreePath path, Object newValue) {
      
    }
  
    
    public Collection<TagPath> getSelection() {
      return selection;
    }

  } 

  
  public interface Listener {

    
    public void handleSelection(TagPath path, boolean on);

  } 

  
  private class Callback extends MouseAdapter implements TreeWillExpandListener {
    
    public void mousePressed(MouseEvent me) {
      
      TreePath path = tree.getPathForLocation(me.getX(),me.getY());
      if (path==null)
        return;
      
      model.toggleSelection((TagPath)path.getLastPathComponent());
      
    }

    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
      if (event.getPath().getPathCount()<3)
        throw new ExpandVetoException(event);
    }

    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
    }
  } 
  
} 
