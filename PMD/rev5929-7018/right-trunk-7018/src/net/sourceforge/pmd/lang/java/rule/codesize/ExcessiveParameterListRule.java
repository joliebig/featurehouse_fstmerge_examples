
package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.rule.design.ExcessiveNodeCountRule;
import net.sourceforge.pmd.util.NumericConstants;


public class ExcessiveParameterListRule extends ExcessiveNodeCountRule {
    public ExcessiveParameterListRule() {
        super(ASTFormalParameters.class);
        setProperty(MINIMUM_DESCRIPTOR, 10d);
    }

    
    public Object visit(ASTFormalParameter node, Object data) {
        return NumericConstants.ONE;
    }
}
