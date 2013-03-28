

package org.jfree.chart.event;

import java.util.EventObject;

import org.jfree.chart.JFreeChart;


public class ChartChangeEvent extends EventObject {

    
    private ChartChangeEventType type;

    
    private JFreeChart chart;

    
    public ChartChangeEvent(Object source) {
        this(source, null, ChartChangeEventType.GENERAL);
    }

    
    public ChartChangeEvent(Object source, JFreeChart chart) {
        this(source, chart, ChartChangeEventType.GENERAL);
    }

    
    public ChartChangeEvent(Object source, JFreeChart chart,
                            ChartChangeEventType type) {
        super(source);
        this.chart = chart;
        this.type = type;
    }

    
    public JFreeChart getChart() {
        return this.chart;
    }

    
    public void setChart(JFreeChart chart) {
        this.chart = chart;
    }

    
    public ChartChangeEventType getType() {
        return this.type;
    }

    
    public void setType(ChartChangeEventType type) {
        this.type = type;
    }

}
