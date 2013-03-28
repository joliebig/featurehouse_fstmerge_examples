

package org.jfree.data.function;

import java.io.Serializable;
import java.util.Arrays;
import org.jfree.chart.HashUtilities;


public class PolynomialFunction2D implements Function2D, Serializable {

    
    private double[] coefficients;

    
    public PolynomialFunction2D(double[] coefficients) {
        if (coefficients == null) {
            throw new IllegalArgumentException("Null 'coefficients' argument");
        }
        this.coefficients = (double[]) coefficients.clone();
    }

    
    public double[] getCoefficients() {
        return (double[]) this.coefficients.clone();
    }

    
    public int getOrder() {
        return this.coefficients.length - 1;
    }

    
    public double getValue(double x) {
        double y = 0;
        for(int i = 0; i < coefficients.length; i++){
            y += coefficients[i] * Math.pow(x, i);
        }
        return y;
    }

    
    public boolean equals(Object obj) {
        if (!(obj instanceof PolynomialFunction2D)) {
            return false;
        }
        PolynomialFunction2D that = (PolynomialFunction2D) obj;
        return Arrays.equals(this.coefficients, that.coefficients);
    }

    
    public int hashCode() {
        return HashUtilities.hashCodeForDoubleArray(this.coefficients);
    }

}
