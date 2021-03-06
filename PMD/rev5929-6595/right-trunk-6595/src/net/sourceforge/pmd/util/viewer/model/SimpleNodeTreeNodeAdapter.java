package net.sourceforge.pmd.util.viewer.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import net.sourceforge.pmd.lang.ast.Node;




public class SimpleNodeTreeNodeAdapter implements TreeNode {
	
    private Node node;
    private List<TreeNode> children;
    private SimpleNodeTreeNodeAdapter parent;

    
    public SimpleNodeTreeNodeAdapter(SimpleNodeTreeNodeAdapter parent, Node node) {
        this.parent = parent;
        this.node = node;
    }

    
    public Node getSimpleNode() {
        return node;
    }


    
    public TreeNode getChildAt(int childIndex) {
        checkChildren();
        return children.get(childIndex);
    }


    
    public int getChildCount() {
        checkChildren();
        return children.size();
    }


    
    public TreeNode getParent() {
        return parent;
    }

    
    public int getIndex(TreeNode node) {
        checkChildren();
        return children.indexOf(node);
    }


    
    public boolean getAllowsChildren() {
        return true;
    }


    

    public boolean isLeaf() {
        checkChildren();
        return children.isEmpty();
    }


    

    public Enumeration<TreeNode> children() {
        return Collections.enumeration(children);
    }


    
    private void checkChildren() {
        if (children == null) {
            children = new ArrayList<TreeNode>(node.jjtGetNumChildren());
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                children.add(new SimpleNodeTreeNodeAdapter(this, node.jjtGetChild(i)));
            }
        }
    }

    
    public String toString() {
        return node.toString();
    }
}

