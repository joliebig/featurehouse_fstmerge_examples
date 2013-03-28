
 
package org.jfree.chart.imagemap;


public class StandardToolTipTagFragmentGenerator 
    implements ToolTipTagFragmentGenerator {

    
    public String generateToolTipFragment(String toolTipText) {
        return " title=\"" + toolTipText + "\" alt=\"\"";
    }

}
