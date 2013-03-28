

package org.jfree.data;

import org.jfree.data.general.DatasetUtilities;


public abstract class DataUtilities {

    
    public static double calculateColumnTotal(Values2D data, int column) {
        double total = 0.0;
        int rowCount = data.getRowCount();
        for (int r = 0; r < rowCount; r++) {
            Number n = data.getValue(r, column);
            if (n != null) {
                total += n.doubleValue();
            }
        }
        return total;
    }

    
    public static double calculateRowTotal(Values2D data, int row) {
        double total = 0.0;
        int columnCount = data.getColumnCount();
        for (int c = 0; c < columnCount; c++) {
            Number n = data.getValue(row, c);
            if (n != null) {
                total += n.doubleValue();
            }
        }
        return total;
    }

    
    public static Number[] createNumberArray(double[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        Number[] result = new Number[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = new Double(data[i]);
        }
        return result;
    }

    
    public static Number[][] createNumberArray2D(double[][] data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        int l1 = data.length;
        Number[][] result = new Number[l1][];
        for (int i = 0; i < l1; i++) {
            result[i] = createNumberArray(data[i]);
        }
        return result;
    }

    
    public static KeyedValues getCumulativePercentages(KeyedValues data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        DefaultKeyedValues result = new DefaultKeyedValues();
        double total = 0.0;
        for (int i = 0; i < data.getItemCount(); i++) {
            Number v = data.getValue(i);
            if (v != null) {
                total = total + v.doubleValue();
            }
        }
        double runningTotal = 0.0;
        for (int i = 0; i < data.getItemCount(); i++) {
            Number v = data.getValue(i);
            if (v != null) {
                runningTotal = runningTotal + v.doubleValue();
            }
            result.addValue(data.getKey(i), new Double(runningTotal / total));
        }
        return result;
    }

}
