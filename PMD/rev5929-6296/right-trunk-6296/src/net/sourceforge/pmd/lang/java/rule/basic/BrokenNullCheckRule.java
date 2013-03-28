package net.sourceforge.pmd.lang.java.rule.basic;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class BrokenNullCheckRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        ASTExpression expression = (ASTExpression)node.jjtGetChild(0);

        ASTConditionalAndExpression conditionalAndExpression = expression.getFirstDescendantOfType(ASTConditionalAndExpression.class);
        if (conditionalAndExpression != null) {
            checkForViolations(node, data, conditionalAndExpression);
        }

        ASTConditionalOrExpression conditionalOrExpression = expression.getFirstDescendantOfType(ASTConditionalOrExpression.class);
        if (conditionalOrExpression != null) {
            checkForViolations(node, data, conditionalOrExpression);
        }

        return super.visit(node, data);
    }


    private void checkForViolations(ASTIfStatement node, Object data, Node conditionalExpression) {
        ASTEqualityExpression equalityExpression = conditionalExpression.getFirstChildOfType(ASTEqualityExpression.class);
        if (equalityExpression == null) {
            return;
        }
        if (conditionalExpression instanceof ASTConditionalAndExpression &&
                !"==".equals(equalityExpression.getImage())) {
            return;
        }
        if (conditionalExpression instanceof ASTConditionalOrExpression &&
                !"!=".equals(equalityExpression.getImage())) {
            return;
        }
        ASTNullLiteral nullLiteral = equalityExpression.getFirstDescendantOfType(ASTNullLiteral.class);
        if (nullLiteral == null) {
            return;     
        }
        
        if (conditionalExpression.hasDescendantOfType(ASTAssignmentOperator.class)) {
            return;
        }

        
        ASTPrimaryExpression nullCompareExpression = findNullCompareExpression(equalityExpression);
        if (nullCompareExpression == null) {
            return;     
        }

        
        for (int i = 0; i < conditionalExpression.jjtGetNumChildren(); i++) {
            Node conditionalSubnode = conditionalExpression.jjtGetChild(i);

            
            ASTEqualityExpression nullEqualityExpression = nullLiteral.getFirstParentOfType(ASTEqualityExpression.class);
            if (conditionalSubnode.equals(nullEqualityExpression)) {
                continue;
            }
            ASTPrimaryExpression conditionalPrimaryExpression;
            if (conditionalSubnode instanceof ASTPrimaryExpression) {
                conditionalPrimaryExpression = (ASTPrimaryExpression)conditionalSubnode;
            } else {
                
                conditionalPrimaryExpression = conditionalSubnode
                    .getFirstDescendantOfType(ASTPrimaryExpression.class);
            }

            if (primaryExpressionsAreEqual(nullCompareExpression, conditionalPrimaryExpression)) {
                addViolation(data, node);   
            }

        }
    }

    private boolean primaryExpressionsAreEqual(ASTPrimaryExpression nullCompareVariable, ASTPrimaryExpression expressionUsage) {
        List<String> nullCompareNames = new ArrayList<String>();
        findExpressionNames(nullCompareVariable, nullCompareNames);

        List<String> expressionUsageNames = new ArrayList<String>();
        findExpressionNames(expressionUsage, expressionUsageNames);

        for (int i = 0; i < nullCompareNames.size(); i++) {
            if (expressionUsageNames.size() == i) {
                return false;   
            }

            String nullCompareExpressionName = nullCompareNames.get(i);
            String expressionUsageName       = expressionUsageNames.get(i);

            
            if (!nullCompareExpressionName.equals(expressionUsageName) &&
                    !expressionUsageName.startsWith(nullCompareExpressionName + ".")) {
                return false;   
            }
        }

        return true;
    }


    
    private void findExpressionNames(Node nullCompareVariable, List<String> results) {
        for (int i = 0; i < nullCompareVariable.jjtGetNumChildren(); i++) {
            Node child = nullCompareVariable.jjtGetChild(i);

            if (child.getClass().equals(ASTName.class)) {                   
                results.add( ((ASTName)child).getImage() );
            } else if (child.getClass().equals(ASTLiteral.class)) {         
                String literalImage = ((ASTLiteral)child).getImage();
                
                if (literalImage != null) {
                    results.add( literalImage );
                }
            } else if (child.getClass().equals(ASTPrimarySuffix.class)) {   
                String name = ((ASTPrimarySuffix)child).getImage();
                if (name != null && !name.equals("")) {
                    results.add(name);
                }
            } else if (child.getClass().equals(ASTClassOrInterfaceType.class)) {    
                String name = ((ASTClassOrInterfaceType)child).getImage();
                results.add(name);
            }

            if (child.jjtGetNumChildren() > 0) {
                findExpressionNames(child, results);
            }
        }
    }

    private ASTPrimaryExpression findNullCompareExpression(ASTEqualityExpression equalityExpression) {
        List<ASTPrimaryExpression> primaryExpressions = equalityExpression.findDescendantsOfType(ASTPrimaryExpression.class);
        for (ASTPrimaryExpression primaryExpression: primaryExpressions) {
            List<ASTPrimaryPrefix> primaryPrefixes = primaryExpression.findDescendantsOfType(ASTPrimaryPrefix.class);
            for (ASTPrimaryPrefix primaryPrefix: primaryPrefixes) {
                if (primaryPrefix.hasDescendantOfType(ASTName.class)) {
                    
                    return primaryExpression;
                }
            }
        }
        return null;  
    }

}
