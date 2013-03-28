

package org.jfree.data.time.ohlc;

import java.io.Serializable;
import org.jfree.chart.util.HashUtilities;


public class OHLC implements Serializable {

    
    private double open;

    
    private double close;

    
    private double high;

    
    private double low;

    
    public OHLC(double open, double high, double low, double close) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
    }

    
    public double getOpen() {
        return this.open;
    }

    
    public double getClose() {
        return this.close;
    }

    
    public double getHigh() {
        return this.high;
    }

    
    public double getLow() {
        return this.low;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OHLC)) {
            return false;
        }
        OHLC that = (OHLC) obj;
        if (this.open != that.open) {
            return false;
        }
        if (this.close != that.close) {
            return false;
        }
        if (this.high != that.high) {
            return false;
        }
        if (this.low != that.low) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = 193;
        result = HashUtilities.hashCode(result, this.open);
        result = HashUtilities.hashCode(result, this.high);
        result = HashUtilities.hashCode(result, this.low);
        result = HashUtilities.hashCode(result, this.close);
        return result;
    }

}
