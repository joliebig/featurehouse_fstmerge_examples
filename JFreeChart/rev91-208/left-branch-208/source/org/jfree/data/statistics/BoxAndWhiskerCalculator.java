

package org.jfree.data.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public abstract class BoxAndWhiskerCalculator {
    
    
    public static BoxAndWhiskerItem calculateBoxAndWhiskerStatistics(
                                        List values) {
        return calculateBoxAndWhiskerStatistics(values, true); 
    }

    
    public static BoxAndWhiskerItem calculateBoxAndWhiskerStatistics(
            List values, boolean stripNullAndNaNItems) {
        
        if (values == null) { 
            throw new IllegalArgumentException("Null 'values' argument.");
        }
        
        List vlist;
        if (stripNullAndNaNItems) {        
            vlist = new ArrayList(values.size());
            Iterator iterator = values.listIterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj instanceof Number) {
                    Number n = (Number) obj;
                    double v = n.doubleValue();
                    if (!Double.isNaN(v)) {
                        vlist.add(n);
                    }
                }
            }
        }
        else {
            vlist = values;
        }
        Collections.sort(vlist);
        
        double mean = Statistics.calculateMean(vlist, false);
        double median = Statistics.calculateMedian(vlist, false);
        double q1 = calculateQ1(vlist);
        double q3 = calculateQ3(vlist);
        
        double interQuartileRange = q3 - q1;
        
        double upperOutlierThreshold = q3 + (interQuartileRange * 1.5);
        double lowerOutlierThreshold = q1 - (interQuartileRange * 1.5);
        
        double upperFaroutThreshold = q3 + (interQuartileRange * 2.0);
        double lowerFaroutThreshold = q1 - (interQuartileRange * 2.0);

        double minRegularValue = Double.POSITIVE_INFINITY;
        double maxRegularValue = Double.NEGATIVE_INFINITY;
        double minOutlier = Double.POSITIVE_INFINITY;
        double maxOutlier = Double.NEGATIVE_INFINITY;
        List outliers = new ArrayList();
        
        Iterator iterator = vlist.iterator();
        while (iterator.hasNext()) {
            Number number = (Number) iterator.next();
            double value = number.doubleValue();
            if (value > upperOutlierThreshold) {
                outliers.add(number);
                if (value > maxOutlier && value <= upperFaroutThreshold) {
                    maxOutlier = value;
                }
            }
            else if (value < lowerOutlierThreshold) {
                outliers.add(number);                    
                if (value < minOutlier && value >= lowerFaroutThreshold) {
                    minOutlier = value;
                }
            }
            else {
                minRegularValue = Math.min(minRegularValue, value);
                maxRegularValue = Math.max(maxRegularValue, value);
            }
            minOutlier = Math.min(minOutlier, minRegularValue);
            maxOutlier = Math.max(maxOutlier, maxRegularValue);
        }
        
        return new BoxAndWhiskerItem(new Double(mean), new Double(median),
                new Double(q1), new Double(q3), new Double(minRegularValue),
                new Double(maxRegularValue), new Double(minOutlier),
                new Double(maxOutlier), outliers);
        
    }

    
    public static double calculateQ1(List values) {
        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        }
        
        double result = Double.NaN;
        int count = values.size();
        if (count > 0) {
            if (count % 2 == 1) {
                if (count > 1) {
                    result = Statistics.calculateMedian(values, 0, count / 2);
                }
                else {
                    result = Statistics.calculateMedian(values, 0, 0);
                }
            }
            else {
                result = Statistics.calculateMedian(values, 0, count / 2 - 1);
            }
            
        }
        return result;
    }
    
    
    public static double calculateQ3(List values) {
        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        }
        double result = Double.NaN;
        int count = values.size();
        if (count > 0) {
            if (count % 2 == 1) {
                if (count > 1) {
                    result = Statistics.calculateMedian(values, count / 2, 
                            count - 1);
                }
                else {
                    result = Statistics.calculateMedian(values, 0, 0);
                }
            }
            else {
                result = Statistics.calculateMedian(values, count / 2, 
                        count - 1);
            }
        }
        return result;
    }
    
}
