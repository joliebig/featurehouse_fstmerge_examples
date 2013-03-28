
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.rules.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;


public class UseCollectionIsEmpty extends AbstractInefficientZeroCheck {
    
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
