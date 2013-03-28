package net.sourceforge.pmd.lang.java.rule.strictexception;

import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;


public class AvoidCatchingThrowableRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        ASTType type = node.getFirstDescendantOfType(ASTType.class);
        ASTClassOrInterfaceType name = type.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        if (name.hasImageEqualTo("Throwable")) {
            addViolation(data, name);
        }
        return super.visit(node, data);
    }
}
