

package org.jfree.chart.annotations;

import org.jfree.chart.event.AnnotationChangeListener;


public interface Annotation {

    
    public void addChangeListener(AnnotationChangeListener listener);

    
    public void removeChangeListener(AnnotationChangeListener listener);

}
