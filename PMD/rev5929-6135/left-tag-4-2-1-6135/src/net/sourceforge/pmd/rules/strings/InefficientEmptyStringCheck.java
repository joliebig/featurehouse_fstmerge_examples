package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.rules.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.symboltable.NameOccurrence;


public class InefficientEmptyStringCheck extends AbstractInefficientZeroCheck {

    
    public boolean isTargetMethod(NameOccurrence occ) {
        if (occ.getNameForWhichThisIsAQualifier() != null
                && occ.getNameForWhichThisIsAQualifier().getImage().indexOf("trim") != -1) {
            Node pExpression = occ.getLocation().jjtGetParent().jjtGetParent();
            if (pExpression.jjtGetNumChildren() >= 3
                    && "length".equals(((SimpleNode) pExpression.jjtGetChild(2)).getImage())) {
                return true;
            }
        }
        return false;
    }

    public boolean appliesToClassName(String name) {
        return "String".equals(name);
    }

}