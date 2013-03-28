

package org.jfree.chart.editor;

import org.jfree.chart.JFreeChart;


public class ChartEditorManager {

    
    static ChartEditorFactory factory = new DefaultChartEditorFactory();

    
    private ChartEditorManager() {
        
    }

    
    public static ChartEditorFactory getChartEditorFactory() {
        return factory;
    }

    
    public static void setChartEditorFactory(ChartEditorFactory f) {
        if (f == null) {
            throw new IllegalArgumentException("Null 'f' argument.");
        }
        factory = f;
    }

    
    public static ChartEditor getChartEditor(JFreeChart chart) {
        return factory.createEditor(chart);
    }
}
