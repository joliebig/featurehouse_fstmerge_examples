package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTRelationalExpression;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.List;


public abstract class AbstractInefficientZeroCheck extends AbstractRule {

    public abstract boolean appliesToClassName(String name);

    public abstract boolean isTargetMethod(NameOccurrence occ);

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        SimpleNode nameNode = node.getTypeNameNode();
        if (nameNode instanceof ASTPrimitiveType) {
            return data;
        }
        if (!appliesToClassName(node.getNameDeclaration().getTypeImage())) {
            return data;
        }

        List<NameOccurrence> declars = node.getUsages();
        for (NameOccurrence occ: declars) {
            if (!isTargetMethod(occ)) {
                continue;
            }
            SimpleNode expr = (SimpleNode) occ.getLocation().jjtGetParent().jjtGetParent().jjtGetParent();
            if ((expr instanceof ASTEqualityExpression ||
                    (expr instanceof ASTRelationalExpression && ">".equals(expr.getImage())))
                && isCompareZero(expr)) {
                addViolation(data, occ.getLocation());
            }
        }
        return data;
    }

    
    private boolean isCompareZero(SimpleNode equality) {
        return (checkComparison(equality, 0) || checkComparison(equality, 1));

    }

    
    private boolean checkComparison(SimpleNode equality, int i) {
        SimpleNode target = (SimpleNode) equality.jjtGetChild(i).jjtGetChild(0);
        if (target.jjtGetNumChildren() == 0) {
            return false;
        }
        target = (SimpleNode) target.jjtGetChild(0);
        return (target instanceof ASTLiteral && "0".equals(target.getImage()));
    }

}