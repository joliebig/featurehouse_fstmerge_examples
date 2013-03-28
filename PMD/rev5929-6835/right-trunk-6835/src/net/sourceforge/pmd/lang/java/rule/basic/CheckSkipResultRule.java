package net.sourceforge.pmd.lang.java.rule.basic;

import java.io.InputStream;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

public class CheckSkipResultRule extends AbstractJavaRule {
    
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!TypeHelper.isA(node.getTypeNode(), InputStream.class)) {
            return data;
        }
        for (NameOccurrence occ: node.getUsages()) {
            NameOccurrence qualifier = occ.getNameForWhichThisIsAQualifier();
            if (qualifier != null && "skip".equals(qualifier.getImage())) {
                JavaNode loc = occ.getLocation();
                if ( loc != null ) {
                    ASTPrimaryExpression exp = loc.getFirstParentOfType(ASTPrimaryExpression.class);
                    while (exp != null) {
                        if (exp.jjtGetParent() instanceof ASTStatementExpression) {
                            
                            
                            addViolation(data, occ.getLocation());
                            break;
                        } else if (exp.jjtGetParent() instanceof ASTExpression &&
                                   exp.jjtGetParent().jjtGetParent() instanceof ASTPrimaryPrefix) {
                            
                            
                            
                            exp = exp.getFirstParentOfType(ASTPrimaryExpression.class);
                        } else {
                            
                            
                            
                            
                            break;
                        }
                    }
                }
            }
        }
        return data;
    }
    
}
