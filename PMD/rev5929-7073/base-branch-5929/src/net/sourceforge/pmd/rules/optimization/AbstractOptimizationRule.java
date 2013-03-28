
package net.sourceforge.pmd.rules.optimization;

import java.util.List;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;


public class AbstractOptimizationRule extends AbstractRule {

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
