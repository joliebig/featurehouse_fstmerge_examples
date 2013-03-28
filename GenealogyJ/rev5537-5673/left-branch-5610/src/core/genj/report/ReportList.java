
package genj.report;

import genj.util.Registry;
import genj.util.Resources;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


class ReportList extends JTree {

  
  private Callback callback = new Callback();

  
  private ReportSelectionListener selectionListener = null;

  
  private TreeModel treeModel = null;

  
  private TreeModel listModel = null;

  
  private Registry registry;

  
  private static final Resources RESOURCES = Resources.get(ReportView.class);
  
  private boolean byGroup;

  
  public ReportList(Report[] reports, boolean byGroup) {
    
    this.byGroup = byGroup;

    setReports(reports);
    setVisibleRowCount(3);
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    setCellRenderer(callback);
    addTreeSelectionListener(callback);
    addTreeExpansionListener(callback);
    setRootVisible(false);

    
  }

  
  public void setSelection(Report report) {
    if (report == null) {
      clearSelection();
    } else {
      for (int i = 0; i < getRowCount(); i++) {
        TreePath path = getPathForRow(i);
        Object v = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        if (v == report) {
          addSelectionPath(path);
          makeVisible(path);
          break;
        }
      }
    }
  }

  
  public Report getSelection() {
    TreePath selection = getSelectionPath();
    return selection!=null ? (Report)((DefaultMutableTreeNode)selection.getLastPathComponent()).getUserObject() : null;
  }

  
  public void setSelectionListener(ReportSelectionListener listener) {
    selectionListener = listener;
  }

  
  public void setReports(Report[] reports) {
    
    setModel(byGroup ? createTree(reports) : createList(reports));
  }

  
  private void refreshExpanded() {
    for (int i = 0; i < getRowCount(); i++) {
      TreePath path = getPathForRow(i);
      Object v = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
      if (v instanceof Report.Category) {
        Report.Category category = (Report.Category) v;
        if (registry.get("expanded." + category.getName(), true))
          expandPath(path);
        else
          collapsePath(path);
      }
    }
  }

  
  private TreeModel createList(Report[] reports) {
    DefaultMutableTreeNode top = new DefaultMutableTreeNode();
    for (int i = 0; i < reports.length; i++)
      top.add(new DefaultMutableTreeNode(reports[i]));
    return new DefaultTreeModel(top);
  }

  
  private TreeModel createTree(Report[] reports) {
    SortedMap<String, CategoryList> categories = new TreeMap<String, CategoryList>();
    for (int i = 0; i < reports.length; i++) {
      String name = getCategoryText(reports[i].getCategory());
      CategoryList list = (CategoryList) categories.get(name);
      if (list == null) {
        list = new CategoryList(reports[i].getCategory());
        categories.put(name, list);
      }
      list.add(reports[i]);
    }

    DefaultMutableTreeNode top = new DefaultMutableTreeNode();
    for (CategoryList list : categories.values()) {
      DefaultMutableTreeNode cat = new DefaultMutableTreeNode(list.getCategory());
      Report[] reps = list.getReportsInCategory();
      for (int i = 0; i < reps.length; i++)
        cat.add(new DefaultMutableTreeNode(reps[i]));
      top.add(cat);
    }
    return new DefaultTreeModel(top);
  }

  
  private String getCategoryText(Report.Category category) {
    String resourceName = "category." + category.getName();
    String text = RESOURCES.getString(resourceName);
    if (text.equals(resourceName))
      text = category.getName();
    return text;
  }

  
  private class Callback implements TreeCellRenderer, TreeSelectionListener, TreeExpansionListener {
    
    private DefaultTreeCellRenderer defTreeRenderer = new DefaultTreeCellRenderer();

    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int index, boolean hasFocus) {
      defTreeRenderer.getTreeCellRendererComponent(tree, value, isSelected, isExpanded, isLeaf, index, hasFocus);
      Object v = ((DefaultMutableTreeNode) value).getUserObject();
      if (v instanceof Report) {
        Report report = (Report) v;
        defTreeRenderer.setText(report.getName());
        defTreeRenderer.setIcon(report.getImage());
      } else if (v instanceof Report.Category) {
        Report.Category category = (Report.Category) v;
        defTreeRenderer.setText(getCategoryText(category));
        defTreeRenderer.setIcon(category.getImage());
      }

      return defTreeRenderer;
    }

    
    public void valueChanged(TreeSelectionEvent e) {
      Report selection = null;
      TreePath path = getSelectionPath();
      if (path != null) {
        Object v = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        if (v instanceof Report)
          selection = (Report) v;
      }
      if (selectionListener != null)
        selectionListener.valueChanged(selection);
    }

    
    public void treeExpanded(TreeExpansionEvent e) {
      Object v = ((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject();
      if (v instanceof Report.Category) {
        Report.Category category = (Report.Category) v;
        registry.put("expanded." + category.getName(), true);
      }
    }

    
    public void treeCollapsed(TreeExpansionEvent e) {
      Object v = ((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject();
      if (v instanceof Report.Category) {
        Report.Category category = (Report.Category) v;
        registry.put("expanded." + category.getName(), false);
      }
    }
  }

  
  private class CategoryList {
    private Report.Category category;
    private List<Report> reportsInCategory = new ArrayList<Report>();

    public CategoryList(Report.Category category) {
      this.category = category;
    }

    public Report.Category getCategory() {
      return category;
    }

    public Report[] getReportsInCategory() {
      return (Report[]) reportsInCategory.toArray(new Report[reportsInCategory.size()]);
    }

    public void add(Report report) {
      reportsInCategory.add(report);
    }
  }
}
