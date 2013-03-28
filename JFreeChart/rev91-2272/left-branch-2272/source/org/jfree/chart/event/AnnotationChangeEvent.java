

package org.jfree.chart.event;

import org.jfree.chart.annotations.Annotation;


public class AnnotationChangeEvent extends ChartChangeEvent {

    
    private Annotation annotation;

    
    public AnnotationChangeEvent(Object source, Annotation annotation) {
        super(source);
        this.annotation = annotation;
    }

    
    public Annotation getAnnotation() {
        return this.annotation;
    }

}
