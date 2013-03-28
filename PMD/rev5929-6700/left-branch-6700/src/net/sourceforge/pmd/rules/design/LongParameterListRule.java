
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.util.NumericConstants;



public class LongParameterListRule extends ExcessiveNodeCountRule {
    public LongParameterListRule() {
        super(ASTFormalParameters.class);
    }

    
    public Object visit(ASTFormalParameter node, Object data) {
        return NumericConstants.ONE;
    }
}
