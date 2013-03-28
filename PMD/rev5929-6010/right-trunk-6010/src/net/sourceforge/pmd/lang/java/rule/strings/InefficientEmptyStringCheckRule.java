package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.symboltable.NameOccurrence;


public class InefficientEmptyStringCheckRule extends AbstractInefficientZeroCheck {

    
    public boolean isTargetMethod(NameOccurrence occ) {
        if (occ.getNameForWhichThisIsAQualifier() != null
                && occ.getNameForWhichThisIsAQualifier().getImage().indexOf("trim") != -1) {
            Node pExpression = occ.getLocation().jjtGetParent().jjtGetParent();
            if (pExpression.jjtGetNumChildren() >= 3
                    && "length".equals(pExpression.jjtGetChild(2).getImage())) {
                return true;
            }
        }
        return false;
    }

    public boolean appliesToClassName(String name) {
        return "String".equals(name);
    }

}