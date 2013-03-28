

package net.sf.freecol.server.ai;

import java.util.Comparator;


public class CacheEntryComparator implements Comparator<ProductionCache.Entry> {

    public int compareProduction(ProductionCache.Entry entry1, ProductionCache.Entry entry2) {
        return entry2.getProduction() - entry1.getProduction();
    }

    public int compare(ProductionCache.Entry entry1, ProductionCache.Entry entry2) {

        int production = compareProduction(entry1, entry2);
        if (production != 0) {
            return production;
        } else if (entry1.isExpert()) {
            if (entry2.isExpert()) {
                return 0;
            } else {
                return -1;
            }
        } else if (entry2.isExpert()) {
            return 1;
        } else if (entry1.unitUpgradesToExpert()) {
            if (entry2.unitUpgradesToExpert()) {
                
                return entry2.getUnit().getExperience()
                    - entry1.getUnit().getExperience();
            } else {
                return -1;
            }
        } else if (entry2.unitUpgradesToExpert()) {
            return 1;
        } else if (entry1.unitUpgrades()) {
            if (entry2.unitUpgrades()) {
                
                return entry1.getUnit().getExperience()
                    - entry2.getUnit().getExperience();
            } else {
                return -1;
            }
        } else if (entry2.unitUpgrades()) {
            return 1;
        } else if (entry1.isOtherExpert()) {
            if (entry2.isOtherExpert()) {
                return 0;
            } else {
                
                return 1;
            }
        } else if (entry2.isOtherExpert()) {
            return -1;
        } else {
            return 0;
        }
    }
}

