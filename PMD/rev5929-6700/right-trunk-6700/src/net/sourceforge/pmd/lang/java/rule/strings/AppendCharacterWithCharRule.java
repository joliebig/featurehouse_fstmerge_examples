
package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;


public class AppendCharacterWithCharRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTLiteral node, Object data) {
        ASTBlockStatement bs = node.getFirstParentOfType(ASTBlockStatement.class);
        if (bs == null) {
            return data;
        }

        if (node.isSingleCharacterStringLiteral()) {
            if (!InefficientStringBufferingRule.isInStringBufferOperation(node, 8, "append")) {
                return data;
            }
            addViolation(data, node);
        }
        return data;
    }
}
