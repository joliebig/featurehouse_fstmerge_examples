

package org.jfree.data.general;

import java.util.List;

import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;


public class CombinedDataset extends AbstractIntervalXYDataset
                             implements XYDataset, 
                                        OHLCDataset, 
                                        IntervalXYDataset,
                                        CombinationDataset {

    
    private List datasetInfo = new java.util.ArrayList();

    
    public CombinedDataset() {
        super();
    }

    
    public CombinedDataset(SeriesDataset[] data) {
        add(data);
    }

    
    public void add(SeriesDataset data) {
        fastAdd(data);
        DatasetChangeEvent event = new DatasetChangeEvent(this, this);
        notifyListeners(event);
    }

    
    public void add(SeriesDataset[] data) {

        for (int i = 0; i < data.length; i++) {
            fastAdd(data[i]);
        }
        DatasetChangeEvent event = new DatasetChangeEvent(this, this);
        notifyListeners(event);

    }

    
    public void add(SeriesDataset data, int series) {
        add(new SubSeriesDataset(data, series));
    }

    
    private void fastAdd(SeriesDataset data) {
        for (int i = 0; i < data.getSeriesCount(); i++) {
            this.datasetInfo.add(new DatasetInfo(data, i));
        }
    }

    
    
    

    
    public int getSeriesCount() {
        return this.datasetInfo.size();
    }

    
    public Comparable getSeriesKey(int series) {
        DatasetInfo di = getDatasetInfo(series);
        return di.data.getSeriesKey(di.series);
    }

    
    
    

    
    public Number getX(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((XYDataset) di.data).getX(di.series, item);
    }

    
    public Number getY(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((XYDataset) di.data).getY(di.series, item);
    }

    
    public int getItemCount(int series) {
        DatasetInfo di = getDatasetInfo(series);
        return ((XYDataset) di.data).getItemCount(di.series);
    }

    
    
    

    
    public Number getHigh(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((OHLCDataset) di.data).getHigh(di.series, item);
    }

    
    public double getHighValue(int series, int item) {
        double result = Double.NaN;
        Number high = getHigh(series, item);
        if (high != null) {
            result = high.doubleValue();   
        }
        return result;   
    }

    
    public Number getLow(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((OHLCDataset) di.data).getLow(di.series, item);
    }

    
    public double getLowValue(int series, int item) {
        double result = Double.NaN;
        Number low = getLow(series, item);
        if (low != null) {
            result = low.doubleValue();   
        }
        return result;   
    }

    
    public Number getOpen(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((OHLCDataset) di.data).getOpen(di.series, item);
    }

    
    public double getOpenValue(int series, int item) {
        double result = Double.NaN;
        Number open = getOpen(series, item);
        if (open != null) {
            result = open.doubleValue();   
        }
        return result;   
    }

    
    public Number getClose(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((OHLCDataset) di.data).getClose(di.series, item);
    }

    
    public double getCloseValue(int series, int item) {
        double result = Double.NaN;
        Number close = getClose(series, item);
        if (close != null) {
            result = close.doubleValue();   
        }
        return result;   
    }

    
    public Number getVolume(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((OHLCDataset) di.data).getVolume(di.series, item);
    }

    
    public double getVolumeValue(int series, int item) {
        double result = Double.NaN;
        Number volume = getVolume(series, item);
        if (volume != null) {
            result = volume.doubleValue();   
        }
        return result;   
    }

    
    
    

    
    public Number getStartX(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        if (di.data instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) di.data).getStartX(di.series, item);
        }
        else {
            return getX(series, item);
        }
    }

    
    public Number getEndX(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        if (di.data instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) di.data).getEndX(di.series, item);
        }
        else {
            return getX(series, item);
        }
    }

    
    public Number getStartY(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        if (di.data instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) di.data).getStartY(di.series, item);
        }
        else {
            return getY(series, item);
        }
    }

    
    public Number getEndY(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        if (di.data instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) di.data).getEndY(di.series, item);
        }
        else {
            return getY(series, item);
        }
    }

    
    
    

    
    public SeriesDataset getParent() {

        SeriesDataset parent = null;
        for (int i = 0; i < this.datasetInfo.size(); i++) {
            SeriesDataset child = getDatasetInfo(i).data;
            if (child instanceof CombinationDataset) {
                SeriesDataset childParent 
                    = ((CombinationDataset) child).getParent();
                if (parent == null) {
                    parent = childParent;
                }
                else if (parent != childParent) {
                    return null;
                }
            }
            else {
                return null;
            }
        }
        return parent;

    }

    
    public int[] getMap() {

        int[] map = null;
        for (int i = 0; i < this.datasetInfo.size(); i++) {
            SeriesDataset child = getDatasetInfo(i).data;
            if (child instanceof CombinationDataset) {
                int[] childMap = ((CombinationDataset) child).getMap();
                if (childMap == null) {
                    return null;
                }
                map = joinMap(map, childMap);
            }
            else {
                return null;
            }
        }
        return map;
    }

    
    
    

    
    public int getChildPosition(Dataset child) {

        int n = 0;
        for (int i = 0; i < this.datasetInfo.size(); i++) {
            SeriesDataset childDataset = getDatasetInfo(i).data;
            if (childDataset instanceof CombinedDataset) {
                int m = ((CombinedDataset) childDataset)
                    .getChildPosition(child);
                if (m >= 0) {
                    return n + m;
                }
                n++;
            }
            else {
                if (child == childDataset) {
                    return n;
                }
                n++;
            }
        }
        return -1;
    }

    
    
    

    
    private DatasetInfo getDatasetInfo(int series) {
        return (DatasetInfo) this.datasetInfo.get(series);
    }

    
    private int[] joinMap(int[] a, int[] b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        int[] result = new int[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    
    private class DatasetInfo {

        
        private SeriesDataset data;

        
        private int series;

        
        DatasetInfo(SeriesDataset data, int series) {
            this.data = data;
            this.series = series;
        }
    }

}
