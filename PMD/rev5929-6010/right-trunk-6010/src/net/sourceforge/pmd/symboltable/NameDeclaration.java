
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.lang.ast.Node;

public interface NameDeclaration {
    Node getNode();

    String getImage();

    Scope getScope();
}
