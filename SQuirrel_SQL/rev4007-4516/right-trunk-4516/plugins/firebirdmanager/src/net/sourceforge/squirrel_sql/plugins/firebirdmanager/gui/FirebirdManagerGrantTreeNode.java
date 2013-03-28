
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui;

import javax.swing.tree.DefaultMutableTreeNode;


public class FirebirdManagerGrantTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 3032583770856669057L;
	private int treenodeType = 0;

    public FirebirdManagerGrantTreeNode() {
        super();
    }

    public FirebirdManagerGrantTreeNode(Object userObject, int piTreeNodeType) {
        super(userObject);
        treenodeType = piTreeNodeType;
    }

    
    public FirebirdManagerGrantTreeNode(Object userObject) {
        super(userObject);

    }

    
    public FirebirdManagerGrantTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);

    }

    
    
    
    public int getTreenodeType() {
        return treenodeType;
    }
}
