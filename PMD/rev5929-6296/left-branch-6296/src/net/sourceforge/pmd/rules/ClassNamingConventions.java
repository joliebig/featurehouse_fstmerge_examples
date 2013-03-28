
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;

public class ClassNamingConventions extends AbstractRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (Character.isLowerCase(node.getImage().charAt(0))) {
            addViolation(data, node);
        }
        return data;
    }
}
