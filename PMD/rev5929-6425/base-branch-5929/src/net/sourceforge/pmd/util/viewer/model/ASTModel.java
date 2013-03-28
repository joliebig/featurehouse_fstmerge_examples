package net.sourceforge.pmd.util.viewer.model;


import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.pmd.ast.SimpleNode;




public class ASTModel implements TreeModel {
	
    private SimpleNode root;
    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>(1);

    
    public ASTModel(SimpleNode root) {
        this.root = root;
    }

    
    public Object getChild(Object parent, int index) {
        return ((SimpleNode) parent).jjtGetChild(index);
    }

    
    public int getChildCount(Object parent) {
        return ((SimpleNode) parent).jjtGetNumChildren();
    }

    
    public int getIndexOfChild(Object parent, Object child) {
        SimpleNode node = ((SimpleNode) parent);
        for (int i = 0; i < node.jjtGetNumChildren(); i++)
            if (node.jjtGetChild(i).equals(child)) {
                return i;
            }
        return -1;
    }

    
    public boolean isLeaf(Object node) {
        return ((SimpleNode) node).jjtGetNumChildren() == 0;
    }

    
    public Object getRoot() {
        return root;
    }

    
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException();
    }

    
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }


    
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }


    protected void fireTreeModelEvent(TreeModelEvent e) {
        for (TreeModelListener listener : listeners) {
            listener.treeNodesChanged(e);
        }
    }

}

