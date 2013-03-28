

package org.jfree.chart;

import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.EventObject;

import org.jfree.chart.entity.ChartEntity;


public class ChartMouseEvent extends EventObject implements Serializable {

    
    private static final long serialVersionUID = -682393837314562149L;
    
    
    private JFreeChart chart;

    
    private MouseEvent trigger;

    
    private ChartEntity entity;

    
    public ChartMouseEvent(JFreeChart chart, MouseEvent trigger, 
                           ChartEntity entity) {
        super(chart);
        this.chart = chart;
        this.trigger = trigger;
        this.entity = entity;
    }

    
    public JFreeChart getChart() {
        return this.chart;
    }

    
    public MouseEvent getTrigger() {
        return this.trigger;
    }

    
    public ChartEntity getEntity() {
        return this.entity;
    }

}
