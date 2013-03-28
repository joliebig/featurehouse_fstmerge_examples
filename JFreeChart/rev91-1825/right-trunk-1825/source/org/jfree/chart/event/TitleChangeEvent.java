

package org.jfree.chart.event;

import org.jfree.chart.title.Title;


public class TitleChangeEvent extends ChartChangeEvent {

    
    private Title title;

    
    public TitleChangeEvent(Title title) {
        super(title);
        this.title = title;
    }

    
    public Title getTitle() {
        return this.title;
    }

}
