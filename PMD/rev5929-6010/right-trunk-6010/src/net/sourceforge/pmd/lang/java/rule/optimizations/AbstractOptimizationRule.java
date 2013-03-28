
package net.sourceforge.pmd.lang.java.rule.optimizations;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.symboltable.NameOccurrence;


public class AbstractOptimizationRule extends AbstractJavaRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    protected boolean assigned(List<NameOccurrence> usages) {
        for (NameOccurrence occ: usages) {
            if (occ.isOnLeftHandSide() || occ.isSelfAssignment()) {
                return true;
            }
            continue;
        }
        return false;
    }

}
