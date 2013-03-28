

package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;


public class XYAnnotationEntity extends ChartEntity
                                implements Serializable {

    
    private static final long serialVersionUID = 2340334068383660799L;

    
    private int rendererIndex;

    
    public XYAnnotationEntity(Shape hotspot, int rendererIndex,
                              String toolTipText, String urlText) {
        super(hotspot, toolTipText, urlText);
        this.rendererIndex = rendererIndex;
    }

    
    public int getRendererIndex() {
        return this.rendererIndex;
    }

    
    public void setRendererIndex(int index) {
        this.rendererIndex = index;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYAnnotationEntity)) {
            return false;
        }
        XYAnnotationEntity that = (XYAnnotationEntity) obj;
        if (this.rendererIndex != that.rendererIndex) {
            return false;
        }
        return true;
    }

}
