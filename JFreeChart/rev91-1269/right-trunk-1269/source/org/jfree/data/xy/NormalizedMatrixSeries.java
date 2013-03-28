
 
package org.jfree.data.xy;



public class NormalizedMatrixSeries extends MatrixSeries {
    
    
    public static final double DEFAULT_SCALE_FACTOR = 1.0;

    
    private double m_scaleFactor = DEFAULT_SCALE_FACTOR;

    
    private double m_totalSum;

    
    public NormalizedMatrixSeries(String name, int rows, int columns) {
        super(name, rows, columns);

        
        this.m_totalSum = Double.MIN_VALUE;
    }

    
    public Number getItem(int itemIndex) {
        int i = getItemRow(itemIndex);
        int j = getItemColumn(itemIndex);

        double mij = get(i, j) * this.m_scaleFactor;
        Number n = new Double(mij / this.m_totalSum);

        return n;
    }

    
    public void setScaleFactor(double factor) {
        this.m_scaleFactor = factor;
        
    }


    
    public double getScaleFactor() {
        return this.m_scaleFactor;
    }


    
    public void update(int i, int j, double mij) {
        this.m_totalSum -= get(i, j);
        this.m_totalSum += mij;

        super.update(i, j, mij);
    }

    
    public void zeroAll() {
        this.m_totalSum = 0;
        super.zeroAll();
    }
}
