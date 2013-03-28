
package net.sourceforge.pmd.lang.java.rule.coupling;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.design.ExcessiveNodeCountRule;
import net.sourceforge.pmd.util.NumericConstants;


public class ExcessiveImportsRule extends ExcessiveNodeCountRule {

    public ExcessiveImportsRule() {
        super(ASTCompilationUnit.class);
    }

    
    public Object visit(ASTImportDeclaration node, Object data) {
        return NumericConstants.ONE;
    }
}
