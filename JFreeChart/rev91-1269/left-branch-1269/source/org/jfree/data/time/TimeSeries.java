

package org.jfree.data.time;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesException;
import org.jfree.util.ObjectUtilities;


public class TimeSeries extends Series implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -5032960206869675528L;

    
    protected static final String DEFAULT_DOMAIN_DESCRIPTION = "Time";

    
    protected static final String DEFAULT_RANGE_DESCRIPTION = "Value";

    
    private String domain;

    
    private String range;

    
    protected Class timePeriodClass;

    
    protected List data;

    
    private int maximumItemCount;

    
    private long maximumItemAge;

    
    public TimeSeries(Comparable name) {
        this(name, DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_RANGE_DESCRIPTION,
                Day.class);
    }

    
    public TimeSeries(Comparable name, Class timePeriodClass) {
        this(name, DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_RANGE_DESCRIPTION,
                timePeriodClass);
    }

    
    public TimeSeries(Comparable name, String domain, String range,
                      Class timePeriodClass) {
        super(name);
        this.domain = domain;
        this.range = range;
        this.timePeriodClass = timePeriodClass;
        this.data = new java.util.ArrayList();
        this.maximumItemCount = Integer.MAX_VALUE;
        this.maximumItemAge = Long.MAX_VALUE;
    }

    
    public String getDomainDescription() {
        return this.domain;
    }

    
    public void setDomainDescription(String description) {
        String old = this.domain;
        this.domain = description;
        firePropertyChange("Domain", old, description);
    }

    
    public String getRangeDescription() {
        return this.range;
    }

    
    public void setRangeDescription(String description) {
        String old = this.range;
        this.range = description;
        firePropertyChange("Range", old, description);
    }

    
    public int getItemCount() {
        return this.data.size();
    }

    
    public List getItems() {
        return Collections.unmodifiableList(this.data);
    }

    
    public int getMaximumItemCount() {
        return this.maximumItemCount;
    }

    
    public void setMaximumItemCount(int maximum) {
        if (maximum < 0) {
            throw new IllegalArgumentException("Negative 'maximum' argument.");
        }
        this.maximumItemCount = maximum;
        int count = this.data.size();
        if (count > maximum) {
            delete(0, count - maximum - 1);
        }
    }

    
    public long getMaximumItemAge() {
        return this.maximumItemAge;
    }

    
    public void setMaximumItemAge(long periods) {
        if (periods < 0) {
            throw new IllegalArgumentException("Negative 'periods' argument.");
        }
        this.maximumItemAge = periods;
        removeAgedItems(true);  
    }

    
    public Class getTimePeriodClass() {
        return this.timePeriodClass;
    }

    
    public TimeSeriesDataItem getDataItem(int index) {
        return (TimeSeriesDataItem) this.data.get(index);
    }

    
    public TimeSeriesDataItem getDataItem(RegularTimePeriod period) {
        int index = getIndex(period);
        if (index >= 0) {
            return (TimeSeriesDataItem) this.data.get(index);
        }
        else {
            return null;
        }
    }

    
    public RegularTimePeriod getTimePeriod(int index) {
        return getDataItem(index).getPeriod();
    }

    
    public RegularTimePeriod getNextTimePeriod() {
        RegularTimePeriod last = getTimePeriod(getItemCount() - 1);
        return last.next();
    }

    
    public Collection getTimePeriods() {
        Collection result = new java.util.ArrayList();
        for (int i = 0; i < getItemCount(); i++) {
            result.add(getTimePeriod(i));
        }
        return result;
    }

    
    public Collection getTimePeriodsUniqueToOtherSeries(TimeSeries series) {

        Collection result = new java.util.ArrayList();
        for (int i = 0; i < series.getItemCount(); i++) {
            RegularTimePeriod period = series.getTimePeriod(i);
            int index = getIndex(period);
            if (index < 0) {
                result.add(period);
            }
        }
        return result;

    }

    
    public int getIndex(RegularTimePeriod period) {
        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument.");
        }
        TimeSeriesDataItem dummy = new TimeSeriesDataItem(
              period, Integer.MIN_VALUE);
        return Collections.binarySearch(this.data, dummy);
    }

    
    public Number getValue(int index) {
        return getDataItem(index).getValue();
    }

    
    public Number getValue(RegularTimePeriod period) {

        int index = getIndex(period);
        if (index >= 0) {
            return getValue(index);
        }
        else {
            return null;
        }

    }

    
    public void add(TimeSeriesDataItem item) {
        add(item, true);
    }

    
    public void add(TimeSeriesDataItem item, boolean notify) {
        if (item == null) {
            throw new IllegalArgumentException("Null 'item' argument.");
        }
        if (!item.getPeriod().getClass().equals(this.timePeriodClass)) {
            StringBuffer b = new StringBuffer();
            b.append("You are trying to add data where the time period class ");
            b.append("is ");
            b.append(item.getPeriod().getClass().getName());
            b.append(", but the TimeSeries is expecting an instance of ");
            b.append(this.timePeriodClass.getName());
            b.append(".");
            throw new SeriesException(b.toString());
        }

        
        boolean added = false;
        int count = getItemCount();
        if (count == 0) {
            this.data.add(item);
            added = true;
        }
        else {
            RegularTimePeriod last = getTimePeriod(getItemCount() - 1);
            if (item.getPeriod().compareTo(last) > 0) {
                this.data.add(item);
                added = true;
            }
            else {
                int index = Collections.binarySearch(this.data, item);
                if (index < 0) {
                    this.data.add(-index - 1, item);
                    added = true;
                }
                else {
                    StringBuffer b = new StringBuffer();
                    b.append("You are attempting to add an observation for ");
                    b.append("the time period ");
                    b.append(item.getPeriod().toString());
                    b.append(" but the series already contains an observation");
                    b.append(" for that time period. Duplicates are not ");
                    b.append("permitted.  Try using the addOrUpdate() method.");
                    throw new SeriesException(b.toString());
                }
            }
        }
        if (added) {
            
            if (getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
            }

            removeAgedItems(false);  
                                     
                                     
            if (notify) {
                fireSeriesChanged();
            }
        }

    }

    
    public void add(RegularTimePeriod period, double value) {
        
        add(period, value, true);
    }

    
    public void add(RegularTimePeriod period, double value, boolean notify) {
        
        TimeSeriesDataItem item = new TimeSeriesDataItem(period, value);
        add(item, notify);
    }

    
    public void add(RegularTimePeriod period, Number value) {
        
        add(period, value, true);
    }

    
    public void add(RegularTimePeriod period, Number value, boolean notify) {
        
        TimeSeriesDataItem item = new TimeSeriesDataItem(period, value);
        add(item, notify);
    }

    
    public void update(RegularTimePeriod period, Number value) {
        TimeSeriesDataItem temp = new TimeSeriesDataItem(period, value);
        int index = Collections.binarySearch(this.data, temp);
        if (index >= 0) {
            TimeSeriesDataItem pair = (TimeSeriesDataItem) this.data.get(index);
            pair.setValue(value);
            fireSeriesChanged();
        }
        else {
            throw new SeriesException(
                "TimeSeries.update(TimePeriod, Number):  period does not exist."
            );
        }

    }

    
    public void update(int index, Number value) {
        TimeSeriesDataItem item = getDataItem(index);
        item.setValue(value);
        fireSeriesChanged();
    }

    
    public TimeSeries addAndOrUpdate(TimeSeries series) {
        TimeSeries overwritten = new TimeSeries("Overwritten values from: "
                + getKey(), series.getTimePeriodClass());
        for (int i = 0; i < series.getItemCount(); i++) {
            TimeSeriesDataItem item = series.getDataItem(i);
            TimeSeriesDataItem oldItem = addOrUpdate(item.getPeriod(),
                    item.getValue());
            if (oldItem != null) {
                overwritten.add(oldItem);
            }
        }
        return overwritten;
    }

    
    public TimeSeriesDataItem addOrUpdate(RegularTimePeriod period,
                                          double value) {
        return addOrUpdate(period, new Double(value));
    }

    
    public TimeSeriesDataItem addOrUpdate(RegularTimePeriod period,
                                          Number value) {

        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument.");
        }
        TimeSeriesDataItem overwritten = null;

        TimeSeriesDataItem key = new TimeSeriesDataItem(period, value);
        int index = Collections.binarySearch(this.data, key);
        if (index >= 0) {
            TimeSeriesDataItem existing
                = (TimeSeriesDataItem) this.data.get(index);
            overwritten = (TimeSeriesDataItem) existing.clone();
            existing.setValue(value);
            removeAgedItems(false);  
                                     
                                     
            fireSeriesChanged();
        }
        else {
            this.data.add(-index - 1, new TimeSeriesDataItem(period, value));

            
            if (getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
            }

            removeAgedItems(false);  
                                     
                                     
            fireSeriesChanged();
        }
        return overwritten;

    }

    
    public void removeAgedItems(boolean notify) {
        
        
        if (getItemCount() > 1) {
            long latest = getTimePeriod(getItemCount() - 1).getSerialIndex();
            boolean removed = false;
            while ((latest - getTimePeriod(0).getSerialIndex())
                    > this.maximumItemAge) {
                this.data.remove(0);
                removed = true;
            }
            if (removed && notify) {
                fireSeriesChanged();
            }
        }
    }

    
    public void removeAgedItems(long latest, boolean notify) {

        
        long index = Long.MAX_VALUE;
        try {
            Method m = RegularTimePeriod.class.getDeclaredMethod(
                    "createInstance", new Class[] {Class.class, Date.class,
                    TimeZone.class});
            RegularTimePeriod newest = (RegularTimePeriod) m.invoke(
                    this.timePeriodClass, new Object[] {this.timePeriodClass,
                            new Date(latest), TimeZone.getDefault()});
            index = newest.getSerialIndex();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        
        
        boolean removed = false;
        while (getItemCount() > 0 && (index
                - getTimePeriod(0).getSerialIndex()) > this.maximumItemAge) {
            this.data.remove(0);
            removed = true;
        }
        if (removed && notify) {
            fireSeriesChanged();
        }
    }

    
    public void clear() {
        if (this.data.size() > 0) {
            this.data.clear();
            fireSeriesChanged();
        }
    }

    
    public void delete(RegularTimePeriod period) {
        int index = getIndex(period);
        if (index >= 0) {
            this.data.remove(index);
            fireSeriesChanged();
        }
    }

    
    public void delete(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("Requires start <= end.");
        }
        for (int i = 0; i <= (end - start); i++) {
            this.data.remove(start);
        }
        fireSeriesChanged();
    }

    
    public Object clone() throws CloneNotSupportedException {
        TimeSeries clone = (TimeSeries) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }

    
    public TimeSeries createCopy(int start, int end)
        throws CloneNotSupportedException {

        if (start < 0) {
            throw new IllegalArgumentException("Requires start >= 0.");
        }
        if (end < start) {
            throw new IllegalArgumentException("Requires start <= end.");
        }
        TimeSeries copy = (TimeSeries) super.clone();

        copy.data = new java.util.ArrayList();
        if (this.data.size() > 0) {
            for (int index = start; index <= end; index++) {
                TimeSeriesDataItem item
                    = (TimeSeriesDataItem) this.data.get(index);
                TimeSeriesDataItem clone = (TimeSeriesDataItem) item.clone();
                try {
                    copy.add(clone);
                }
                catch (SeriesException e) {
                    e.printStackTrace();
                }
            }
        }
        return copy;
    }

    
    public TimeSeries createCopy(RegularTimePeriod start, RegularTimePeriod end)
        throws CloneNotSupportedException {

        if (start == null) {
            throw new IllegalArgumentException("Null 'start' argument.");
        }
        if (end == null) {
            throw new IllegalArgumentException("Null 'end' argument.");
        }
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException(
                    "Requires start on or before end.");
        }
        boolean emptyRange = false;
        int startIndex = getIndex(start);
        if (startIndex < 0) {
            startIndex = -(startIndex + 1);
            if (startIndex == this.data.size()) {
                emptyRange = true;  
            }
        }
        int endIndex = getIndex(end);
        if (endIndex < 0) {             
            endIndex = -(endIndex + 1); 
            endIndex = endIndex - 1;    
        }
        if ((endIndex < 0)  || (endIndex < startIndex)) {
            emptyRange = true;
        }
        if (emptyRange) {
            TimeSeries copy = (TimeSeries) super.clone();
            copy.data = new java.util.ArrayList();
            return copy;
        }
        else {
            return createCopy(startIndex, endIndex);
        }

    }

    
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof TimeSeries) || !super.equals(object)) {
            return false;
        }
        TimeSeries s = (TimeSeries) object;
        if (!ObjectUtilities.equal(getDomainDescription(),
                s.getDomainDescription())) {
            return false;
        }

        if (!ObjectUtilities.equal(getRangeDescription(),
                s.getRangeDescription())) {
            return false;
        }

        if (!getClass().equals(s.getClass())) {
            return false;
        }

        if (getMaximumItemAge() != s.getMaximumItemAge()) {
            return false;
        }

        if (getMaximumItemCount() != s.getMaximumItemCount()) {
            return false;
        }

        int count = getItemCount();
        if (count != s.getItemCount()) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (!getDataItem(i).equals(s.getDataItem(i))) {
                return false;
            }
        }
        return true;
    }

    
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.domain != null ? this.domain.hashCode()
                : 0);
        result = 29 * result + (this.range != null ? this.range.hashCode() : 0);
        result = 29 * result + (this.timePeriodClass != null
                ? this.timePeriodClass.hashCode() : 0);
        
        
        int count = getItemCount();
        if (count > 0) {
            TimeSeriesDataItem item = getDataItem(0);
            result = 29 * result + item.hashCode();
        }
        if (count > 1) {
            TimeSeriesDataItem item = getDataItem(count - 1);
            result = 29 * result + item.hashCode();
        }
        if (count > 2) {
            TimeSeriesDataItem item = getDataItem(count / 2);
            result = 29 * result + item.hashCode();
        }
        result = 29 * result + this.maximumItemCount;
        result = 29 * result + (int) this.maximumItemAge;
        return result;
    }

}
