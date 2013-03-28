package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.List;


public abstract class AbstractPoorMethodCall extends AbstractRule {
    
    
    
    protected abstract String targetTypename();
    
    
    protected abstract String[] methodNames();
    
    
    protected abstract boolean isViolationArgument(Node arg);

    
    private boolean isNotedMethod(NameOccurrence occurrence) {
        
        if (occurrence == null) return false;
        
        String methodCall = occurrence.getImage();      
        String[] methodNames = methodNames();
        
        for (int i=0; i<methodNames.length; i++) {
            if (methodCall.indexOf(methodNames[i]) != -1) return true;
        }
        return false;
    }
        
    
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        
        if (!node.getNameDeclaration().getTypeImage().equals(targetTypename())) {
            return data;
        }
        
        for (NameOccurrence occ: node.getUsages()) {
            if (isNotedMethod(occ.getNameForWhichThisIsAQualifier())) {
                SimpleNode parent = (SimpleNode)occ.getLocation().jjtGetParent().jjtGetParent();
                if (parent instanceof ASTPrimaryExpression) {
                    
                    List additives = parent.findChildrenOfType(ASTAdditiveExpression.class);
                    if (!additives.isEmpty()) {
                        return data;
                    }
                    List literals = parent.findChildrenOfType(ASTLiteral.class);
                    for (int l=0; l<literals.size(); l++) {
                        ASTLiteral literal = (ASTLiteral)literals.get(l);
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

