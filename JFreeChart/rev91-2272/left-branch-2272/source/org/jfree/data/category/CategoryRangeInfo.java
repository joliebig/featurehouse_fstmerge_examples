

package org.jfree.data.category;

import java.util.List;
import org.jfree.data.Range;


public interface CategoryRangeInfo {

    
    public Range getRangeBounds(List visibleSeriesKeys,
            boolean includeInterval);

}