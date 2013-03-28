package net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCatchStatement;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTType;


public class AvoidCatchingThrowable extends AbstractRule {

    public Object visit(ASTCatchStatement node, Object data) {
        ASTType type = node.findChildrenOfType(ASTType.class).get(0);
        ASTClassOrInterfaceType name = type.findChildrenOfType(ASTClassOrInterfaceType.class).get(0);
        if (name.hasImageEqualTo("Throwable")) {
            addViolation(data, name);
        }
        return super.visit(node, data);
    }
}
