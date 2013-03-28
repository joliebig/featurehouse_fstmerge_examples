

package org.jfree.data.general;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.data.DomainInfo;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.KeyedValues;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.function.Function2D;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ArrayUtilities;


public final class DatasetUtilities {
    
    
    private DatasetUtilities() {
        
    }

    
    public static double calculatePieDatasetTotal(PieDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        List keys = dataset.getKeys();
        double totalValue = 0;
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable current = (Comparable) iterator.next();
            if (current != null) {
                Number value = dataset.getValue(current);
                double v = 0.0;
                if (value != null) {
                    v = value.doubleValue();
                }
                if (v > 0) {
                    totalValue = totalValue + v;
                }
            }
        }
        return totalValue;
    }

    
    public static PieDataset createPieDatasetForRow(CategoryDataset dataset, 
                                                    Comparable rowKey) {
        int row = dataset.getRowIndex(rowKey);
        return createPieDatasetForRow(dataset, row);
    }

    
    public static PieDataset createPieDatasetForRow(CategoryDataset dataset, 
                                                    int row) {
        DefaultPieDataset result = new DefaultPieDataset();
        int columnCount = dataset.getColumnCount();
        for (int current = 0; current < columnCount; current++) {
            Comparable columnKey = dataset.getColumnKey(current);
            result.setValue(columnKey, dataset.getValue(row, current));
        }
        return result;
    }

    
    public static PieDataset createPieDatasetForColumn(CategoryDataset dataset,
                                                       Comparable columnKey) {
        int column = dataset.getColumnIndex(columnKey);
        return createPieDatasetForColumn(dataset, column);
    }

    
    public static PieDataset createPieDatasetForColumn(CategoryDataset dataset, 
                                                       int column) {
        DefaultPieDataset result = new DefaultPieDataset();
        int rowCount = dataset.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Comparable rowKey = dataset.getRowKey(i);
            result.setValue(rowKey, dataset.getValue(i, column));
        }
        return result;
    }

    
    public static PieDataset createConsolidatedPieDataset(PieDataset source, 
                                                          Comparable key,
                                                          double minimumPercent)
    {
        return DatasetUtilities.createConsolidatedPieDataset(
            source, key, minimumPercent, 2
        );
    }

    
    public static PieDataset createConsolidatedPieDataset(PieDataset source,
                                                          Comparable key,
                                                          double minimumPercent,
                                                          int minItems) {
        
        DefaultPieDataset result = new DefaultPieDataset();
        double total = DatasetUtilities.calculatePieDatasetTotal(source);

        
        List keys = source.getKeys();
        ArrayList otherKeys = new ArrayList();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable currentKey = (Comparable) iterator.next();
            Number dataValue = source.getValue(currentKey);
            if (dataValue != null) {
                double value = dataValue.doubleValue();
                if (value / total < minimumPercent) {
                    otherKeys.add(currentKey);
                }
            }
        }

        
        iterator = keys.iterator();
        double otherValue = 0;
        while (iterator.hasNext()) {
            Comparable currentKey = (Comparable) iterator.next();
            Number dataValue = source.getValue(currentKey);
            if (dataValue != null) {
                if (otherKeys.contains(currentKey) 
                    && otherKeys.size() >= minItems) {
                    
                    otherValue += dataValue.doubleValue();
                }
                else {
                    
                    result.setValue(currentKey, dataValue);
                }
            }
        }
        
        if (otherKeys.size() >= minItems) {
            result.setValue(key, otherValue);
        }
        return result;
    }

    
    public static CategoryDataset createCategoryDataset(String rowKeyPrefix,
                                                        String columnKeyPrefix,
                                                        double[][] data) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; c++) {
                String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(new Double(data[r][c]), rowKey, columnKey);
            }
        }
        return result;

    }

    
    public static CategoryDataset createCategoryDataset(String rowKeyPrefix,
                                                        String columnKeyPrefix,
                                                        Number[][] data) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; c++) {
                String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(data[r][c], rowKey, columnKey);
            }
        }
        return result;

    }

    
    public static CategoryDataset createCategoryDataset(Comparable[] rowKeys,
                                                        Comparable[] columnKeys,
                                                        double[][] data) {

        
        if (rowKeys == null) {
            throw new IllegalArgumentException("Null 'rowKeys' argument.");
        }
        if (columnKeys == null) {
            throw new IllegalArgumentException("Null 'columnKeys' argument.");
        }
        if (ArrayUtilities.hasDuplicateItems(rowKeys)) {
            throw new IllegalArgumentException("Duplicate items in 'rowKeys'.");
        }
        if (ArrayUtilities.hasDuplicateItems(columnKeys)) {
            throw new IllegalArgumentException(
                "Duplicate items in 'columnKeys'."
            );
        }
        if (rowKeys.length != data.length) {
            throw new IllegalArgumentException(
                "The number of row keys does not match the number of rows in "
                + "the data array."
            );
        }
        int columnCount = 0;
        for (int r = 0; r < data.length; r++) {
            columnCount = Math.max(columnCount, data[r].length);
        }
        if (columnKeys.length != columnCount) {
            throw new IllegalArgumentException(
                "The number of column keys does not match the number of "
                + "columns in the data array."
            );
        }
        
        
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            Comparable rowKey = rowKeys[r];
            for (int c = 0; c < data[r].length; c++) {
                Comparable columnKey = columnKeys[c];
                result.addValue(new Double(data[r][c]), rowKey, columnKey);
            }
        }
        return result;

    }

    
    public static CategoryDataset createCategoryDataset(Comparable rowKey, 
                                                        KeyedValues rowData) {

        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        if (rowData == null) {
            throw new IllegalArgumentException("Null 'rowData' argument.");
        }
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int i = 0; i < rowData.getItemCount(); i++) {
            result.addValue(rowData.getValue(i), rowKey, rowData.getKey(i));
        }
        return result;

    }

    
    public static XYDataset sampleFunction2D(Function2D f, 
                                             double start, 
                                             double end, 
                                             int samples,
                                             Comparable seriesKey) {

        if (f == null) {
            throw new IllegalArgumentException("Null 'f' argument.");   
        }
        if (seriesKey == null) {
            throw new IllegalArgumentException("Null 'seriesKey' argument.");
        }
        if (start >= end) {
            throw new IllegalArgumentException("Requires 'start' < 'end'.");
        }
        if (samples < 2) {
            throw new IllegalArgumentException("Requires 'samples' > 1");
        }

        XYSeries series = new XYSeries(seriesKey);
        double step = (end - start) / samples;
        for (int i = 0; i <= samples; i++) {
            double x = start + (step * i);
            series.add(x, f.getValue(x));
        }
        XYSeriesCollection collection = new XYSeriesCollection(series);
        return collection;

    }

    
    public static boolean isEmptyOrNull(PieDataset dataset) {

        if (dataset == null) {
            return true;
        }

        int itemCount = dataset.getItemCount();
        if (itemCount == 0) {
            return true;
        }

        for (int item = 0; item < itemCount; item++) {
            Number y = dataset.getValue(item);
            if (y != null) {
                double yy = y.doubleValue();
                if (yy > 0.0) {
                    return false;
                }
            }
        }

        return true;

    }

    
    public static boolean isEmptyOrNull(CategoryDataset dataset) {

        if (dataset == null) {
            return true;
        }

        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        if (rowCount == 0 || columnCount == 0) {
            return true;
        }

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                if (dataset.getValue(r, c) != null) {
                    return false;
                }

            }
        }

        return true;

    }

    
    public static boolean isEmptyOrNull(XYDataset dataset) {

        boolean result = true;

        if (dataset != null) {
            for (int s = 0; s < dataset.getSeriesCount(); s++) {
                if (dataset.getItemCount(s) > 0) {
                    result = false;
                    continue;
                }
            }
        }

        return result;

    }

    
    public static Range findDomainBounds(XYDataset dataset) {
        return findDomainBounds(dataset, true);
    }

    
    public static Range findDomainBounds(XYDataset dataset, 
                                         boolean includeInterval) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        Range result = null;
        
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            result = info.getDomainBounds(includeInterval);
        }
        else {
            result = iterateDomainBounds(dataset, includeInterval);
        }
        return result;
        
    }

    
    public static Range iterateDomainBounds(XYDataset dataset) {
        return iterateDomainBounds(dataset, true);
    }

    
    public static Range iterateDomainBounds(XYDataset dataset, 
                                            boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");   
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        double lvalue;
        double uvalue;
        if (includeInterval && dataset instanceof IntervalXYDataset) {
            IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    lvalue = intervalXYData.getStartXValue(series, item);
                    uvalue = intervalXYData.getEndXValue(series, item);
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        else {
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    lvalue = dataset.getXValue(series, item);
                    uvalue = lvalue;
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum > maximum) {
            return null;
        }
        else {
            return new Range(minimum, maximum);
        }
    }
    
    
    public static Range findRangeBounds(CategoryDataset dataset) {
        return findRangeBounds(dataset, true);
    }
    
    
    public static Range findRangeBounds(CategoryDataset dataset, 
                                        boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            result = info.getRangeBounds(includeInterval);
        }
        else {
            result = iterateCategoryRangeBounds(dataset, includeInterval);
        }
        return result;
    }
    
    
    public static Range findRangeBounds(XYDataset dataset) {
        return findRangeBounds(dataset, true);
    }
    
    
    public static Range findRangeBounds(XYDataset dataset, 
                                        boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            result = info.getRangeBounds(includeInterval);
        }
        else {
            result = iterateXYRangeBounds(dataset);
        }
        return result;
    }
    
    
    public static Range iterateCategoryRangeBounds(CategoryDataset dataset, 
            boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        boolean interval = includeInterval 
                           && dataset instanceof IntervalCategoryDataset;
        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Number lvalue;
                Number uvalue;
                if (interval) {
                    IntervalCategoryDataset icd 
                        = (IntervalCategoryDataset) dataset;
                    lvalue = icd.getStartValue(row, column);
                    uvalue = icd.getEndValue(row, column);
                }
                else {
                    lvalue = dataset.getValue(row, column);
                    uvalue = lvalue;
                }
                if (lvalue != null) {
                    minimum = Math.min(minimum, lvalue.doubleValue());
                }
                if (uvalue != null) {
                    maximum = Math.max(maximum, uvalue.doubleValue());
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        else {
            return new Range(minimum, maximum);
        }
    }
    
    
    public static Range iterateXYRangeBounds(XYDataset dataset) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; series++) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
                double lvalue;
                double uvalue;
                if (dataset instanceof IntervalXYDataset) {
                    IntervalXYDataset intervalXYData 
                        = (IntervalXYDataset) dataset;
                    lvalue = intervalXYData.getStartYValue(series, item);
                    uvalue = intervalXYData.getEndYValue(series, item);
                }
                else if (dataset instanceof OHLCDataset) {
                    OHLCDataset highLowData = (OHLCDataset) dataset;
                    lvalue = highLowData.getLowValue(series, item);
                    uvalue = highLowData.getHighValue(series, item);
                }
                else {
                    lvalue = dataset.getYValue(series, item);
                    uvalue = lvalue;
                }
                if (!Double.isNaN(lvalue)) {
                    minimum = Math.min(minimum, lvalue);
                }
                if (!Double.isNaN(uvalue)) {     
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        else {
            return new Range(minimum, maximum);
        }
    }

    
    public static Number findMinimumDomainValue(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Number result = null;
        
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            return new Double(info.getDomainLowerBound(true));
        }
        else {
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData 
                            = (IntervalXYDataset) dataset;
                        value = intervalXYData.getStartXValue(series, item);
                    }
                    else {
                        value = dataset.getXValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                result = null;
            }
            else {
                result = new Double(minimum);
            }
        }

        return result;
    }
    
    
    public static Number findMaximumDomainValue(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Number result = null;
        
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            return new Double(info.getDomainUpperBound(true));
        }

        
        else {
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData 
                            = (IntervalXYDataset) dataset;
                        value = intervalXYData.getEndXValue(series, item);
                    }
                    else {
                        value = dataset.getXValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        maximum = Math.max(maximum, value);
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                result = null;
            }
            else {
                result = new Double(maximum);
            }

        }
        
        return result;
    }

    
    public static Number findMinimumRangeValue(CategoryDataset dataset) {

        
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return new Double(info.getRangeLowerBound(true));
        }

        
        else {
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = dataset.getRowCount();
            int itemCount = dataset.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int item = 0; item < itemCount; item++) {
                    Number value;
                    if (dataset instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd 
                            = (IntervalCategoryDataset) dataset;
                        value = icd.getStartValue(series, item);
                    }
                    else {
                        value = dataset.getValue(series, item);
                    }
                    if (value != null) {
                        minimum = Math.min(minimum, value.doubleValue());
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }

        }

    }

    
    public static Number findMinimumRangeValue(XYDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return new Double(info.getRangeLowerBound(true));
        }

        
        else {
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData 
                            = (IntervalXYDataset) dataset;
                        value = intervalXYData.getStartYValue(series, item);
                    }
                    else if (dataset instanceof OHLCDataset) {
                        OHLCDataset highLowData = (OHLCDataset) dataset;
                        value = highLowData.getLowValue(series, item);
                    }
                    else {
                        value = dataset.getYValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }

        }

    }

    
    public static Number findMaximumRangeValue(CategoryDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return new Double(info.getRangeUpperBound(true));
        }

        
        else {

            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = dataset.getRowCount();
            int itemCount = dataset.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int item = 0; item < itemCount; item++) {
                    Number value;
                    if (dataset instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd 
                            = (IntervalCategoryDataset) dataset;
                        value = icd.getEndValue(series, item);
                    }
                    else {
                        value = dataset.getValue(series, item);
                    }
                    if (value != null) {
                        maximum = Math.max(maximum, value.doubleValue());
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }

    }

    
    public static Number findMaximumRangeValue(XYDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return new Double(info.getRangeUpperBound(true));
        }

        
        else  {

            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData 
                            = (IntervalXYDataset) dataset;
                        value = intervalXYData.getEndYValue(series, item);
                    }
                    else if (dataset instanceof OHLCDataset) {
                        OHLCDataset highLowData = (OHLCDataset) dataset;
                        value = highLowData.getHighValue(series, item);
                    }
                    else {
                        value = dataset.getYValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        maximum = Math.max(maximum, value);
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }

    }

    
    public static Range findStackedRangeBounds(CategoryDataset dataset) {
        return findStackedRangeBounds(dataset, 0.0);
    }

    
    public static Range findStackedRangeBounds(CategoryDataset dataset, 
            double base) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; item++) {
            double positive = base;
            double negative = base;
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; series++) {
                Number number = dataset.getValue(series, item);
                if (number != null) {
                    double value = number.doubleValue();
                    if (value > 0.0) {
                        positive = positive + value;
                    }
                    if (value < 0.0) {
                        negative = negative + value;  
                        
                    }
                }
            }
            minimum = Math.min(minimum, negative);
            maximum = Math.max(maximum, positive);
        }
        if (minimum <= maximum) {
            result = new Range(minimum, maximum);
        }
        return result;

    }

    
    public static Range findStackedRangeBounds(CategoryDataset dataset,
                                               KeyToGroupMap map) {
    
        Range result = null;
        if (dataset != null) {
            
            
            int[] groupIndex = new int[dataset.getRowCount()];
            for (int i = 0; i < dataset.getRowCount(); i++) {
                groupIndex[i] = map.getGroupIndex(
                    map.getGroup(dataset.getRowKey(i))
                );   
            }
            
            
            int groupCount = map.getGroupCount();
            double[] minimum = new double[groupCount];
            double[] maximum = new double[groupCount];
            
            int categoryCount = dataset.getColumnCount();
            for (int item = 0; item < categoryCount; item++) {
                double[] positive = new double[groupCount];
                double[] negative = new double[groupCount];
                int seriesCount = dataset.getRowCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = dataset.getValue(series, item);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value > 0.0) {
                            positive[groupIndex[series]] 
                                 = positive[groupIndex[series]] + value;
                        }
                        if (value < 0.0) {
                            negative[groupIndex[series]] 
                                 = negative[groupIndex[series]] + value;
                                 
                        }
                    }
                }
                for (int g = 0; g < groupCount; g++) {
                    minimum[g] = Math.min(minimum[g], negative[g]);
                    maximum[g] = Math.max(maximum[g], positive[g]);
                }
            }
            for (int j = 0; j < groupCount; j++) {
                result = Range.combine(
                    result, new Range(minimum[j], maximum[j])
                );
            }
        }
        return result;

    }

    
    public static Number findMinimumStackedRangeValue(CategoryDataset dataset) {

        Number result = null;
        if (dataset != null) {
            double minimum = 0.0;
            int categoryCount = dataset.getRowCount();
            for (int item = 0; item < categoryCount; item++) {
                double total = 0.0;

                int seriesCount = dataset.getColumnCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = dataset.getValue(series, item);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value < 0.0) {
                            total = total + value;  
                            
                        }
                    }
                }
                minimum = Math.min(minimum, total);

            }
            result = new Double(minimum);
        }
        return result;

    }

    
    public static Number findMaximumStackedRangeValue(CategoryDataset dataset) {

        Number result = null;

        if (dataset != null) {
            double maximum = 0.0;
            int categoryCount = dataset.getColumnCount();
            for (int item = 0; item < categoryCount; item++) {
                double total = 0.0;
                int seriesCount = dataset.getRowCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = dataset.getValue(series, item);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value > 0.0) {
                            total = total + value;
                        }
                    }
                }
                maximum = Math.max(maximum, total);
            }
            result = new Double(maximum);
        }

        return result;

    }

    
    public static Range findStackedRangeBounds(TableXYDataset dataset) {
        return findStackedRangeBounds(dataset, 0.0);
    }
    
    
    public static Range findStackedRangeBounds(TableXYDataset dataset, 
                                               double base) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        double minimum = base;
        double maximum = base;
        for (int itemNo = 0; itemNo < dataset.getItemCount(); itemNo++) {
            double positive = base;
            double negative = base;
            int seriesCount = dataset.getSeriesCount();
            for (int seriesNo = 0; seriesNo < seriesCount; seriesNo++) {
                double y = dataset.getYValue(seriesNo, itemNo);
                if (!Double.isNaN(y)) {
                    if (y > 0.0) {
                        positive += y;
                    }
                    else {
                        negative += y;
                    }
                }
            }
            if (positive > maximum) {
                maximum = positive;
            } 
            if (negative < minimum) {
                minimum = negative;
            } 
        }
        if (minimum <= maximum) {
            return new Range(minimum, maximum);
        }
        else {
            return null;   
        }
    }
    
    
    public static double calculateStackTotal(TableXYDataset dataset, int item) {
        double total = 0.0;
        int seriesCount = dataset.getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
            double value = dataset.getYValue(s, item);
            if (!Double.isNaN(value)) {
                total = total + value;
            }
        }
        return total;
    }

    
    public static Range findCumulativeRangeBounds(CategoryDataset dataset) {
        
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        
        boolean allItemsNull = true; 
                                     
        double minimum = 0.0;
        double maximum = 0.0;
        for (int row = 0; row < dataset.getRowCount(); row++) {
            double runningTotal = 0.0;
            for (int column = 0; column < dataset.getColumnCount() - 1; 
                 column++) {
                Number n = dataset.getValue(row, column);
                if (n != null) {
                    allItemsNull = false;
                    double value = n.doubleValue();
                    runningTotal = runningTotal + value;
                    minimum = Math.min(minimum, runningTotal);
                    maximum = Math.max(maximum, runningTotal);
                }
            }    
        }
        if (!allItemsNull) {
            return new Range(minimum, maximum);
        }
        else {
            return null;
        }
        
    }

}
