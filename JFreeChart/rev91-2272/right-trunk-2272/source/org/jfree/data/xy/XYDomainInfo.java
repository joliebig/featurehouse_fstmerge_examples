

package org.jfree.data.xy;

import java.util.List;
import org.jfree.data.Range;


public interface XYDomainInfo {

    
    public Range getDomainBounds(List visibleSeriesKeys,
            boolean includeInterval);

}