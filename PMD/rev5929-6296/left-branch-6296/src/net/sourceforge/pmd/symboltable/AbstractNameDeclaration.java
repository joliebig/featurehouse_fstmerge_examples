
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

public abstract class AbstractNameDeclaration implements NameDeclaration {

    protected SimpleNode node;

    public AbstractNameDeclaration(SimpleNode node) {
        this.node = node;
    }

    public SimpleNode getNode() {
        return node;
    }

    public String getImage() {
        return node.getImage();
    }

    public Scope getScope() {
        return node.getScope();
    }
}
