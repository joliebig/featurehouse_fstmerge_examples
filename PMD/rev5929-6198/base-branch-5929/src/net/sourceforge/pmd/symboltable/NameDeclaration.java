
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

public interface NameDeclaration {
    SimpleNode getNode();

    String getImage();

    Scope getScope();
}
