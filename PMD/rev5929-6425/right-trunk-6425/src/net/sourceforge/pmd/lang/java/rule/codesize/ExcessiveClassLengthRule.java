
package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.rule.design.ExcessiveLengthRule;


public class ExcessiveClassLengthRule extends ExcessiveLengthRule {
    public ExcessiveClassLengthRule() {
        super(ASTClassOrInterfaceDeclaration.class);
    }
}
