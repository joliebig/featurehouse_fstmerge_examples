
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.ast.ASTMethodDeclaration;


public class LongMethodRule extends ExcessiveLengthRule {
    public LongMethodRule() {
        super(ASTMethodDeclaration.class);
    }
}
