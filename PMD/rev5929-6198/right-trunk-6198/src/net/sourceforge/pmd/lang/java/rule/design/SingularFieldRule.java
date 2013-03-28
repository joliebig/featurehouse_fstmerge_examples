
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.ast.Node;


public class SingularFieldRule extends AbstractJavaRule {
	
	
    private static final PropertyDescriptor CHECK_INNER_CLASSES = new BooleanProperty(
			"CheckInnerClasses", "Check inner classes", false, 1.0f);
    private static final PropertyDescriptor DISALLOW_NOT_ASSIGNMENT = new BooleanProperty(
			"DisallowNotAssignment", "Disallow violations where the first usage is not an assignment", false, 1.0f);
	
    public Object visit(ASTFieldDeclaration node, Object data) {
    	boolean checkInnerClasses = getBooleanProperty(CHECK_INNER_CLASSES);
    	boolean disallowNotAssignment = getBooleanProperty(DISALLOW_NOT_ASSIGNMENT);
    	
        if (node.isPrivate() && !node.isStatic()) {
            List<ASTVariableDeclaratorId> list = node.findChildrenOfType(ASTVariableDeclaratorId.class);
            ASTVariableDeclaratorId declaration = list.get(0);
            List<NameOccurrence> usages = declaration.getUsages();
            Node decl = null;
            boolean violation = true;
            for (int ix = 0; ix < usages.size(); ix++) {
                NameOccurrence no = usages.get(ix);
                Node location = no.getLocation();

                ASTPrimaryExpression primaryExpressionParent = location.getFirstParentOfType(ASTPrimaryExpression.class);
                if (ix==0 && !disallowNotAssignment) {
                	if (primaryExpressionParent.getFirstParentOfType(ASTIfStatement.class) != null) {
                		
                		
                		
                		violation = false;
	                	break;		
                	}
                	
                	
                	Node potentialStatement = primaryExpressionParent.jjtGetParent();
	                boolean assignmentToField = no.getImage().equals(location.getImage());	
					if (!assignmentToField || !isInAssignment(potentialStatement)) {
	                	violation = false;
	                	break;		
	                } else {
	                	if (usages.size() > ix + 1) {
	                	    Node secondUsageLocation = usages.get(ix + 1).getLocation();
	                		
	                		List<ASTStatementExpression> parentStatements = secondUsageLocation.getParentsOfType(ASTStatementExpression.class);
	                		for (ASTStatementExpression statementExpression : parentStatements) {
	                			if (statementExpression != null && statementExpression.equals(potentialStatement)) {
		                			
		                			violation = false;
		    	                	break;		
		                		}
							}
	                		
	                	}
	                }
                }
                
                if (!checkInnerClasses) {
	                
	                ASTClassOrInterfaceDeclaration clazz = location.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
	                if (clazz!= null && clazz.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class) != null) {
	                	violation = false;
	                	break;			
	                }
                }
                
                if (primaryExpressionParent.jjtGetParent() instanceof ASTSynchronizedStatement) {
                	
                	violation = false;
                	break;			
                }
                
                Node method = location.getFirstParentOfType(ASTMethodDeclaration.class);
                if (method == null) {
                    method = location.getFirstParentOfType(ASTConstructorDeclaration.class);
                    if (method == null) {
                    	method = location.getFirstParentOfType(ASTInitializer.class);
                    	if (method == null) {
                    		continue;
                    	}
                    }
                }

                if (decl == null) {
                    decl = method;
                    continue;
                } else if (decl != method) {
                    violation = false;
                    break;			
                }
                
                
            }

            if (violation && !usages.isEmpty()) {
                addViolation(data, node, new Object[] { declaration.getImage() });
            }
        }
        return data;
    }

	private boolean isInAssignment(Node potentialStatement) {
		if (potentialStatement instanceof ASTStatementExpression) {
			ASTStatementExpression statement = (ASTStatementExpression)potentialStatement;
			List<ASTAssignmentOperator> assignments = new ArrayList<ASTAssignmentOperator>();
			statement.findChildrenOfType(ASTAssignmentOperator.class, assignments, false);
			if (assignments.isEmpty() || !"=".equals(assignments.get(0).getImage())) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}
