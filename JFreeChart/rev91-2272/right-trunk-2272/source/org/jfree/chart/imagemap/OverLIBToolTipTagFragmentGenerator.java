

package org.jfree.chart.imagemap;


public class OverLIBToolTipTagFragmentGenerator
        implements ToolTipTagFragmentGenerator {

    
    public OverLIBToolTipTagFragmentGenerator() {
        super();
    }

    
    public String generateToolTipFragment(String toolTipText) {
        return " onMouseOver=\"return overlib('"
                + ImageMapUtilities.javascriptEscape(toolTipText)
                + "');\" onMouseOut=\"return nd();\"";
    }

}
