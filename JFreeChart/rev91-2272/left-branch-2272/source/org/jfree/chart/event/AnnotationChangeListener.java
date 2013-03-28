

package org.jfree.chart.event;

import java.util.EventListener;

import org.jfree.chart.annotations.Annotation;


public interface AnnotationChangeListener extends EventListener {

    
    public void annotationChanged(AnnotationChangeEvent event);

}
