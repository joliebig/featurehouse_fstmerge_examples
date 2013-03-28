

package org.jfree.data.gantt;

import java.util.Date;

import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.IntervalXYDataset;


public class XYTaskDataset extends AbstractXYDataset
        implements IntervalXYDataset, DatasetChangeListener {

    
    private TaskSeriesCollection underlying;

    
    private double seriesWidth;

    
    private boolean transposed;

    
    public XYTaskDataset(TaskSeriesCollection tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("Null 'tasks' argument.");
        }
        this.underlying = tasks;
        this.seriesWidth = 0.8;
        this.underlying.addChangeListener(this);
    }

    
    public TaskSeriesCollection getTasks() {
        return this.underlying;
    }

    
    public double getSeriesWidth() {
        return this.seriesWidth;
    }

    
    public void setSeriesWidth(double w) {
        if (w <= 0.0) {
            throw new IllegalArgumentException("Requires 'w' > 0.0.");
        }
        this.seriesWidth = w;
        fireDatasetChanged();
    }

    
    public boolean isTransposed() {
        return this.transposed;
    }

    
    public void setTransposed(boolean transposed) {
        this.transposed = transposed;
        fireDatasetChanged();
    }

    
    public int getSeriesCount() {
        return this.underlying.getSeriesCount();
    }

    
    public Comparable getSeriesKey(int series) {
        return this.underlying.getSeriesKey(series);
    }

    
    public int getItemCount(int series) {
        return this.underlying.getSeries(series).getItemCount();
    }

    
    public double getXValue(int series, int item) {
        if (!this.transposed) {
            return getSeriesValue(series);
        }
        else {
            return getItemValue(series, item);
        }
    }

    
    public double getStartXValue(int series, int item) {
        if (!this.transposed) {
            return getSeriesStartValue(series);
        }
        else {
            return getItemStartValue(series, item);
        }
    }

    
    public double getEndXValue(int series, int item) {
        if (!this.transposed) {
            return getSeriesEndValue(series);
        }
        else {
            return getItemEndValue(series, item);
        }
    }

    
    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    
    public Number getStartX(int series, int item) {
        return new Double(getStartXValue(series, item));
    }

    
    public Number getEndX(int series, int item) {
        return new Double(getEndXValue(series, item));
    }

    
    public double getYValue(int series, int item) {
        if (!this.transposed) {
            return getItemValue(series, item);
        }
        else {
            return getSeriesValue(series);
        }
    }

    
    public double getStartYValue(int series, int item) {
        if (!this.transposed) {
            return getItemStartValue(series, item);
        }
        else {
            return getSeriesStartValue(series);
        }
    }

    
    public double getEndYValue(int series, int item) {
        if (!this.transposed) {
            return getItemEndValue(series, item);
        }
        else {
            return getSeriesEndValue(series);
        }
    }

    
    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    
    public Number getStartY(int series, int item) {
        return new Double(getStartYValue(series, item));
    }

    
    public Number getEndY(int series, int item) {
        return new Double(getEndYValue(series, item));
    }

    private double getSeriesValue(int series) {
        return series;
    }

    private double getSeriesStartValue(int series) {
        return series - this.seriesWidth / 2.0;
    }

    private double getSeriesEndValue(int series) {
        return series + this.seriesWidth / 2.0;
    }

    private double getItemValue(int series, int item) {
        TaskSeries s = this.underlying.getSeries(series);
        Task t = s.get(item);
        TimePeriod duration = t.getDuration();
        Date start = duration.getStart();
        Date end = duration.getEnd();
        return (start.getTime() + end.getTime()) / 2.0;
    }

    private double getItemStartValue(int series, int item) {
        TaskSeries s = this.underlying.getSeries(series);
        Task t = s.get(item);
        TimePeriod duration = t.getDuration();
        Date start = duration.getStart();
        return start.getTime();
    }

    private double getItemEndValue(int series, int item) {
        TaskSeries s = this.underlying.getSeries(series);
        Task t = s.get(item);
        TimePeriod duration = t.getDuration();
        Date end = duration.getEnd();
        return end.getTime();
    }


    
    public void datasetChanged(DatasetChangeEvent event) {
        fireDatasetChanged();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYTaskDataset)) {
            return false;
        }
        XYTaskDataset that = (XYTaskDataset) obj;
        if (this.seriesWidth != that.seriesWidth) {
            return false;
        }
        if (this.transposed != that.transposed) {
            return false;
        }
        if (!this.underlying.equals(that.underlying)) {
            return false;
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        XYTaskDataset clone = (XYTaskDataset) super.clone();
        clone.underlying = (TaskSeriesCollection) this.underlying.clone();
        return clone;
    }

}
