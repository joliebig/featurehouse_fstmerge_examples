
package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.rule.design.ExcessiveNodeCountRule;
import net.sourceforge.pmd.util.NumericConstants;


public class ExcessivePublicCountRule extends ExcessiveNodeCountRule {

    public ExcessivePublicCountRule() {
        super(ASTCompilationUnit.class);
        setProperty(MINIMUM_DESCRIPTOR, 45d);
    }

    
    public Object visit(ASTMethodDeclarator node, Object data) {
        return this.getTallyOnAccessType((AccessNode) node.jjtGetParent());
    }

    
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (node.isFinal() && node.isStatic()) {
            return NumericConstants.ZERO;
        } 
        return this.getTallyOnAccessType(node);
    }

    
    private Integer getTallyOnAccessType(AccessNode node) {
        if (node.isPublic()) {
            return NumericConstants.ONE;
        }
        return NumericConstants.ZERO;
    }
}
