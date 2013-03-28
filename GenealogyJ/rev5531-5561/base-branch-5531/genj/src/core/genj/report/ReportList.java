
package genj.report;

import genj.util.Registry;
import genj.util.Resources;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JScrollPane;
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


 class ReportList extends JScrollPane
{
    public static final int VIEW_LIST = 0;
    public static final int VIEW_TREE = 1;

    
    private Report[] reports;

    
    private JTree tree = null;

    
    private int viewType;

    
    private Report selection = null;

    
    private Callback callback = new Callback();

    
    private ReportSelectionListener selectionListener = null;

    
    private TreeModel treeModel = null;

    
    private TreeModel listModel = null;

    
    private Registry registry;

    
    private static final Resources RESOURCES = Resources.get(ReportView.class);

    
    public ReportList(Report[] reports, int viewType, Registry registry) {
        this.reports = reports;
        this.viewType = viewType;
        this.registry = registry;

        tree = new JTree();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(callback);
        tree.addTreeSelectionListener(callback);
        tree.addTreeExpansionListener(callback);
        tree.setRootVisible(false);
        setViewportView(tree);

        refreshView();
    }

    
    public void setViewType(int viewType) {
        this.viewType = viewType;
        refreshView();
    }

    
    public int getViewType() {
        return viewType;
    }

    
    public void setSelection(Report report) {
        selection = report;
        if (selection == null) {
            tree.clearSelection();
        } else {
            for (int i = 0; i < tree.getRowCount(); i++) {
                TreePath path = tree.getPathForRow(i);
                Object v = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
                if (v == selection) {
                    tree.addSelectionPath(path);
                    tree.makeVisible(path);
                    break;
                }
            }
        }
    }

    
    public Report getSelection() {
        return selection;
    }

    
    public void setSelectionListener(ReportSelectionListener listener) {
        selectionListener = listener;
    }

    
    public void setReports(Report[] reports) {
        this.reports = reports;
        listModel = null;
        treeModel = null;
        refreshView();
    }

    
    private void refreshView() {
        Report oldSelection = getSelection();
        if (viewType == VIEW_LIST) {
            if (listModel == null)
                listModel = createList();
            tree.setModel(listModel);
        } else {
            if (treeModel == null)
                treeModel = createTree();
            tree.setModel(treeModel);
            refreshExpanded();
        }
        setSelection(oldSelection);
    }

    
    private void refreshExpanded() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            TreePath path = tree.getPathForRow(i);
            Object v = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
            if (v instanceof Report.Category) {
                Report.Category category = (Report.Category)v;
                if (registry.get("expanded." + category.getName(), true))
                    tree.expandPath(path);
                else
                    tree.collapsePath(path);
            }
        }
    }

    
    private TreeModel createList() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode();
        for (int i = 0; i < reports.length; i++)
            top.add(new DefaultMutableTreeNode(reports[i]));
        return new DefaultTreeModel(top);
    }

    
    private TreeModel createTree() {
        SortedMap categories = new TreeMap();
        for (int i = 0; i < reports.length; i++) {
            String name = getCategoryText(reports[i].getCategory());
            CategoryList list = (CategoryList)categories.get(name);
            if (list == null) {
                list = new CategoryList(reports[i].getCategory());
                categories.put(name, list);
            }
            list.add(reports[i]);
        }

        DefaultMutableTreeNode top = new DefaultMutableTreeNode();
        Iterator iterator = categories.entrySet().iterator();
        while (iterator.hasNext()) {
            CategoryList list = (CategoryList)((Map.Entry)iterator.next()).getValue();
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

    
    private class Callback implements TreeCellRenderer, TreeSelectionListener,
        TreeExpansionListener
    {
        
        private DefaultTreeCellRenderer defTreeRenderer = new DefaultTreeCellRenderer();

        
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean isSelected, boolean isExpanded, boolean isLeaf,
                int index, boolean hasFocus) {
            defTreeRenderer.getTreeCellRendererComponent(tree, value, isSelected,
                    isExpanded, isLeaf, index, hasFocus);
            Object v = ((DefaultMutableTreeNode)value).getUserObject();
            if (v instanceof Report) {
                Report report = (Report)v;
                defTreeRenderer.setText(report.getName());
                defTreeRenderer.setIcon(report.getImage());
            } else if (v instanceof Report.Category) {
                Report.Category category = (Report.Category)v;
                defTreeRenderer.setText(getCategoryText(category));
                defTreeRenderer.setIcon(category.getImage());
            }

            return defTreeRenderer;
        }

        
        public void valueChanged(TreeSelectionEvent e) {
            selection = null;
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                Object v = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
                if (v instanceof Report)
                    selection = (Report)v;
            }
            if (selectionListener != null)
                selectionListener.valueChanged(selection);
        }

        
        public void treeExpanded(TreeExpansionEvent e) {
            Object v = ((DefaultMutableTreeNode)e.getPath()
                    .getLastPathComponent()).getUserObject();
            if (v instanceof Report.Category) {
                Report.Category category = (Report.Category)v;
                registry.put("expanded." + category.getName(), true);
            }
        }

        
        public void treeCollapsed(TreeExpansionEvent e) {
            Object v = ((DefaultMutableTreeNode)e.getPath()
                    .getLastPathComponent()).getUserObject();
            if (v instanceof Report.Category) {
                Report.Category category = (Report.Category)v;
                registry.put("expanded." + category.getName(), false);
            }
        }
    }

    
    private class CategoryList
    {
        private Report.Category category;
        private List reportsInCategory = new ArrayList();

        public CategoryList(Report.Category category) {
            this.category = category;
        }

        public Report.Category getCategory() {
            return category;
        }

        public Report[] getReportsInCategory() {
            return (Report[])reportsInCategory.toArray(new Report[reportsInCategory.size()]);
        }

        public void add(Report report) {
            reportsInCategory.add(report);
        }
    }
}
