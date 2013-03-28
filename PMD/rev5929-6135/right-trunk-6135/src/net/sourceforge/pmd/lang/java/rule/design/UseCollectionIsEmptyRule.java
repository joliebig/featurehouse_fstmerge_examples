
package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.rule.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;


public class UseCollectionIsEmptyRule extends AbstractInefficientZeroCheck {
    
    public boolean appliesToClassName(String name){
        return CollectionUtil.isCollectionType(name, true);
    }
    
    
    public boolean isTargetMethod(NameOccurrence occ) {
        if (occ.getNameForWhichThisIsAQualifier() != null) {
            if (occ.getLocation().getImage().endsWith(".size")) {
                return true;
            }
        }
        return false;
    }
}
