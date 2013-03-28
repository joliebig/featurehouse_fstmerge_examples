package net.sourceforge.pmd.lang.java.rule;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;


public abstract class AbstractPoorMethodCall extends AbstractJavaRule {
    

    
    protected abstract String targetTypename();

    
    protected abstract String[] methodNames();

    
    protected abstract boolean isViolationArgument(Node arg);

    
    private boolean isNotedMethod(NameOccurrence occurrence) {

        if (occurrence == null) {
            return false;
        }

        String methodCall = occurrence.getImage();
        String[] methodNames = methodNames();

        for (String element : methodNames) {
            if (methodCall.indexOf(element) != -1) {
                return true;
            }
        }
        return false;
    }

    
    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!node.getNameDeclaration().getTypeImage().equals(targetTypename())) {
            return data;
        }

        for (NameOccurrence occ : node.getUsages()) {
            if (isNotedMethod(occ.getNameForWhichThisIsAQualifier())) {
                Node parent = occ.getLocation().jjtGetParent().jjtGetParent();
                if (parent instanceof ASTPrimaryExpression) {
                    
                    if (parent.hasDescendantOfType(ASTAdditiveExpression.class)) {
                        return data;
                    }
                    List<ASTLiteral> literals = parent.findDescendantsOfType(ASTLiteral.class);
                    for (int l = 0; l < literals.size(); l++) {
                        ASTLiteral literal = literals.get(l);
                        if (isViolationArgument(literal)) {
                            addViolation(data, occ.getLocation());
                        }
                    }
                }
            }
        }
        return data;
    }
}
