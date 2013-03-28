
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


public class PathTreeWidget extends JScrollPane {

  
  private List    listeners = new ArrayList();
  
  
  private Gedcom  gedcom;
  
  
  private JTree   tree;
  
  
  private Model model = new Model();

  
  public PathTreeWidget() {

    
    tree = new JTree(model);
    tree.setShowsRootHandles(false);
    tree.setRootVisible(false);
    tree.setCellRenderer(new Renderer());
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addMouseListener(new Selector());

    
    setMinimumSize(new Dimension(160, 160));
    setPreferredSize(new Dimension(160, 160));
    getViewport().setView(tree);

    
  }

  
  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  
  private void expandRows() {
    for (int i=0;i<tree.getRowCount();i++) {
      tree.expandRow(i);
    }
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
    expandRows();
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
        setText( path.getLast() );
        setIcon( Grammar.V55.getMeta(path).getImage() );
        checkbox.setSelected(model.getSelection().contains(value));
        panel.invalidate(); 
      }      
      
      return panel;
    }

  } 

  
  private class Model implements TreeModel {
    
    
    private TagPath[] paths = new TagPath[0];

    
    private Set selection = new HashSet();
    
    
    private Map path2childen = new HashMap(); 
    
    
    private List tmlisteners = new ArrayList();
  
    
    public void setPaths(TagPath[] ps, TagPath[] ss) {

      
      selection = new HashSet(Arrays.asList(ss));
      
      
      paths = ps;
      
      
      TreeModelEvent e = new TreeModelEvent(this, new Object[]{ this });
      TreeModelListener[] ls = getListenerSnapshot();
      for (int l=0;l<ls.length;l++) ls[l].treeStructureChanged(e);
      
      
    }
    
    
    private void toggleSelection(TagPath path) {
      
      
      boolean removed = selection.remove(path);
      if (!removed) selection.add(path);
      
      fireSelectionChanged(path, !removed);
      
      
      TreeModelEvent e = new TreeModelEvent(this, new Object[]{ this });
      TreeModelListener[] ls = getListenerSnapshot();
      for (int l=0;l<ls.length;l++) ls[l].treeNodesChanged(e);
    }
    
    
    private TreeModelListener[] getListenerSnapshot() {
      return (TreeModelListener[])tmlisteners.toArray(new TreeModelListener[listeners.size()]);
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
      
      TagPath[] result = (TagPath[])path2childen.get(node);
      if (result==null) {
        result = node==this ? getChildrenOfRoot() : getChildrenOfNode((TagPath)node);
      }
      
      return result;
    }
    
    
    private TagPath[] getChildrenOfNode(TagPath path) {
      
      List<TagPath> children = new ArrayList<TagPath>(8);
      for (int p=0;p<paths.length;p++) {
        if (paths[p].length()>path.length()&&paths[p].startsWith(path)) 
          add(new TagPath(paths[p], path.length()+1), children);
      }
      for (Iterator ss=selection.iterator();ss.hasNext();) {
        TagPath sel = (TagPath)ss.next();
        if (sel.length()>path.length()&&sel.startsWith(path)) 
          add(new TagPath(sel, path.length()+1), children);
      }
      
      return TagPath.toArray(children);
    }
    
    
    private TagPath[] getChildrenOfRoot() {
      
      List children = new ArrayList(8);
      for (int p=0;p<paths.length;p++) 
        add(new TagPath(paths[p], 1), children);
      for (Iterator ss=selection.iterator();ss.hasNext();) 
        add(new TagPath((TagPath)ss.next(), 1), children);
      
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
  
    
    public Collection getSelection() {
      return selection;
    }

  } 

  
  public interface Listener {

    
    public void handleSelection(TagPath path, boolean on);

  } 

  
  private class Selector extends MouseAdapter {
    
    public void mousePressed(MouseEvent me) {
      
      TreePath path = tree.getPathForLocation(me.getX(),me.getY());
      if (path==null)
        return;
      
      model.toggleSelection((TagPath)path.getLastPathComponent());
      
    }
  } 
  
} 
