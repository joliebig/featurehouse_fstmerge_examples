
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;


public class LongClassRule extends ExcessiveLengthRule {
    public LongClassRule() {
        super(ASTClassOrInterfaceDeclaration.class);
    }
}
