

package org.jfree.chart.imagemap;


public class StandardToolTipTagFragmentGenerator
        implements ToolTipTagFragmentGenerator {

    
    public StandardToolTipTagFragmentGenerator() {
        super();
    }

    
    public String generateToolTipFragment(String toolTipText) {
        return " title=\"" + ImageMapUtilities.htmlEscape(toolTipText)
            + "\" alt=\"\"";
    }

}
