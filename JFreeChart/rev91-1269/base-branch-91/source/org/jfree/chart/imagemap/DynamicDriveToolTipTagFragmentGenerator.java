
 
package org.jfree.chart.imagemap;


public class DynamicDriveToolTipTagFragmentGenerator 
    implements ToolTipTagFragmentGenerator {

    
    protected String title = "";

    
    protected int style = 1;

    
    public DynamicDriveToolTipTagFragmentGenerator() {
        super();
    }

    
    public DynamicDriveToolTipTagFragmentGenerator(String title, int style) {
        this.title = title;
        this.style = style;
    }

    
    public String generateToolTipFragment(String toolTipText) {
        return " onMouseOver=\"return stm(['" + this.title + "','" 
            + toolTipText + "'],Style[" + this.style + "]);\"" 
            + " onMouseOut=\"return htm();\"";
    }

}
