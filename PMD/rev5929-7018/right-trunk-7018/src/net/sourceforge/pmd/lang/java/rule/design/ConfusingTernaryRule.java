
package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;


public class ConfusingTernaryRule extends AbstractJavaRule {

    public Object visit(ASTIfStatement node, Object data) {
        
        if (node.jjtGetNumChildren() == 3) {
            Node inode = node.jjtGetChild(0);
            if (inode instanceof ASTExpression &&
                    inode.jjtGetNumChildren() == 1) {
        	Node jnode = inode.jjtGetChild(0);
                if (isMatch(jnode)) {
                    addViolation(data, node);
                }
            }
        }
        return super.visit(node, data);
    }

    public Object visit(ASTConditionalExpression node, Object data) {
        
        if (node.jjtGetNumChildren() > 0) {
            Node inode = node.jjtGetChild(0);
            if (isMatch(inode)) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }

    
    private static boolean isMatch(Node node) {
        return
                isUnaryNot(node) ||
                isNotEquals(node) ||
                isConditionalWithAllMatches(node) ||
                isParenthesisAroundMatch(node);
    }

    private static boolean isUnaryNot(Node node) {
        
        return
                node instanceof ASTUnaryExpressionNotPlusMinus &&
                "!".equals(node.getImage());
    }

    private static boolean isNotEquals(Node node) {
        
        return
                node instanceof ASTEqualityExpression &&
                "!=".equals(node.getImage());
    }

    private static boolean isConditionalWithAllMatches(Node node) {
        
        if (!(node instanceof ASTConditionalAndExpression) &&
                !(node instanceof ASTConditionalOrExpression)) {
            return false;
        }
        int n = node.jjtGetNumChildren();
        if (n <= 0) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            Node inode = node.jjtGetChild(i);
            
            if (!isMatch(inode)) {
                return false;
            }
        }
        
        return true;
    }

    private static boolean isParenthesisAroundMatch(Node node) {
        
        if (!(node instanceof ASTPrimaryExpression) ||
                (node.jjtGetNumChildren() != 1)) {
            return false;
        }
        Node inode = node.jjtGetChild(0);
        if (!(inode instanceof ASTPrimaryPrefix) ||
                (inode.jjtGetNumChildren() != 1)) {
            return false;
        }
        Node jnode = inode.jjtGetChild(0);
        if (!(jnode instanceof ASTExpression) ||
                (jnode.jjtGetNumChildren() != 1)) {
            return false;
        }
        Node knode = jnode.jjtGetChild(0);
        
        return isMatch(knode);
    }
}
