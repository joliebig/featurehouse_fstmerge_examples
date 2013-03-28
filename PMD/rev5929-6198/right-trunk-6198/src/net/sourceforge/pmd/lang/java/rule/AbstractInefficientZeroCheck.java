package net.sourceforge.pmd.lang.java.rule;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;


public abstract class AbstractInefficientZeroCheck extends AbstractJavaRule {

    public abstract boolean appliesToClassName(String name);

    public abstract boolean isTargetMethod(NameOccurrence occ);

    public Object visit(ASTVariableDeclaratorId node, Object data) {
	Node nameNode = node.getTypeNameNode();
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
            Node expr = occ.getLocation().jjtGetParent().jjtGetParent().jjtGetParent();
            if ((expr instanceof ASTEqualityExpression ||
                    (expr instanceof ASTRelationalExpression && ">".equals(expr.getImage())))
                && isCompareZero(expr)) {
                addViolation(data, occ.getLocation());
            }
        }
        return data;
    }

    
    private boolean isCompareZero(Node equality) {
        return (checkComparison(equality, 0) || checkComparison(equality, 1));

    }

    
    private boolean checkComparison(Node equality, int i) {
	Node target = equality.jjtGetChild(i).jjtGetChild(0);
        if (target.jjtGetNumChildren() == 0) {
            return false;
        }
        target = target.jjtGetChild(0);
        return (target instanceof ASTLiteral && "0".equals(target.getImage()));
    }

}