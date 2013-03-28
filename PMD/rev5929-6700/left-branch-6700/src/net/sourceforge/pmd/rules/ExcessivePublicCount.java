
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.rules.design.ExcessiveNodeCountRule;
import net.sourceforge.pmd.util.NumericConstants;


public class ExcessivePublicCount extends ExcessiveNodeCountRule {

    public ExcessivePublicCount() {
        super(ASTCompilationUnit.class);
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
