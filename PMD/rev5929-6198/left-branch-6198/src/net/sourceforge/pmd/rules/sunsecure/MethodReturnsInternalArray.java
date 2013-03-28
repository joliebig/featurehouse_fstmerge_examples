
package net.sourceforge.pmd.rules.sunsecure;

import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;

import java.util.List;


public class MethodReturnsInternalArray extends AbstractSunSecureRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration method, Object data) {
        if (!method.getResultType().returnsArray()) {
            return data;
        }
        List<ASTReturnStatement> returns = method.findChildrenOfType(ASTReturnStatement.class);
        ASTTypeDeclaration td = method.getFirstParentOfType(ASTTypeDeclaration.class);
        for (ASTReturnStatement ret: returns) {
            final String vn = getReturnedVariableName(ret);
            if (!isField(vn, td)) {
                continue;
            }
            if (ret.findChildrenOfType(ASTPrimarySuffix.class).size() > 2) {
                continue;
            }
            if (!ret.findChildrenOfType(ASTAllocationExpression.class).isEmpty()) {
                continue;
            }
            if (!isLocalVariable(vn, method)) {
                addViolation(data, ret, vn);
            } else {
                
                final ASTPrimaryPrefix pp = ret.getFirstChildOfType(ASTPrimaryPrefix.class);
                if (pp != null && pp.usesThisModifier()) {
                    final ASTPrimarySuffix ps = ret.getFirstChildOfType(ASTPrimarySuffix.class);
                    if (ps.hasImageEqualTo(vn)) {
                        addViolation(data, ret, vn);
                    }
                }
            }
        }
        return data;
    }


}
