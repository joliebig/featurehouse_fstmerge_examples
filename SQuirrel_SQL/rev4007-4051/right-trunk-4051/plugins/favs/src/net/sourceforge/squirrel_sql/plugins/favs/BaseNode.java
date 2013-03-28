package net.sourceforge.squirrel_sql.plugins.favs;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.squirrel_sql.fw.persist.ValidationException;

abstract class BaseNode extends DefaultMutableTreeNode {
    protected BaseNode(String title, boolean children) {
        super(title, children);
    }

    abstract void setName(String name) throws ValidationException;

    abstract String getName();
}