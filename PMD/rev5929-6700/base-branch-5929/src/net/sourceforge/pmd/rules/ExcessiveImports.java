
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.rules.design.ExcessiveNodeCountRule;
import net.sourceforge.pmd.util.NumericConstants;


public class ExcessiveImports extends ExcessiveNodeCountRule {

    public ExcessiveImports() {
        super(ASTCompilationUnit.class);
    }

    
    public Object visit(ASTImportDeclaration node, Object data) {
        return NumericConstants.ONE;
    }
}
