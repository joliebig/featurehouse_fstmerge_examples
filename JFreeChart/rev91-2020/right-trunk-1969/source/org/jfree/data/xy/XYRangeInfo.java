

package org.jfree.data.xy;

import java.util.List;
import org.jfree.data.Range;


public interface XYRangeInfo {

    
    public Range getRangeBounds(List visibleSeriesKeys, Range xRange,
            boolean includeInterval);

}