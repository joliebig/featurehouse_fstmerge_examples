

package org.jfree.data;


public interface DomainInfo {

    
    public double getDomainLowerBound(boolean includeInterval);

    
    public double getDomainUpperBound(boolean includeInterval);

    
    public Range getDomainBounds(boolean includeInterval);

}
