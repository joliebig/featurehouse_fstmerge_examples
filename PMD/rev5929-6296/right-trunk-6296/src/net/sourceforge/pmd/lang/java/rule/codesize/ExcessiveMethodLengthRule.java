
package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.design.ExcessiveLengthRule;


public class ExcessiveMethodLengthRule extends ExcessiveLengthRule {
    public ExcessiveMethodLengthRule() {
        super(ASTMethodDeclaration.class);
    }
}
