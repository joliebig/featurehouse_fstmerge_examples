package net.sourceforge.pmd.lang.java.rule.strictexception;

import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;


public class AvoidCatchingThrowableRule extends AbstractJavaRule {

    public Object visit(ASTCatchStatement node, Object data) {
        ASTType type = node.findChildrenOfType(ASTType.class).get(0);
        ASTClassOrInterfaceType name = type.findChildrenOfType(ASTClassOrInterfaceType.class).get(0);
        if (name.hasImageEqualTo("Throwable")) {
            addViolation(data, name);
        }
        return super.visit(node, data);
    }
}
