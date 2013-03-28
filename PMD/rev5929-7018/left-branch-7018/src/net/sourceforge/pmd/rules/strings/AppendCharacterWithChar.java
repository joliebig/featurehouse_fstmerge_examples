
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTLiteral;


public class AppendCharacterWithChar extends AbstractRule {

    public Object visit(ASTLiteral node, Object data) {
        ASTBlockStatement bs = node.getFirstParentOfType(ASTBlockStatement.class);
        if (bs == null) {
            return data;
        }

        if (node.isSingleCharacterStringLiteral()) {
            if (!InefficientStringBuffering.isInStringBufferOperation(node, 8, "append")) {
                return data;
            }
            addViolation(data, node);
        }
        return data;
    }
}
