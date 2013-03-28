

package org.jfree.chart.urls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.xy.XYDataset;


public class CustomXYURLGenerator implements XYURLGenerator, Serializable {

    
    private static final long serialVersionUID = -8565933356596551832L;
    
    
    private ArrayList urlSeries = new ArrayList();

    
    public CustomXYURLGenerator() {
        super();
    }

    
    public int getListCount() {
        return this.urlSeries.size();
    }

    
    public int getURLCount(int list) {
        int result = 0;
        List urls = (List) this.urlSeries.get(list);
        if (urls != null) {
            result = urls.size();
        }
        return result;
    }

    
    public String getURL(int series, int item) {
        String result = null;
        if (series < getListCount()) {
            List urls = (List) this.urlSeries.get(series);
            if (urls != null) {
                if (item < urls.size()) {
                    result = (String) urls.get(item);
                }
            }
        }
        return result;
    }

    
    public String generateURL(XYDataset dataset, int series, int item) {
        return getURL(series, item);
    }

    
    public void addURLSeries(List urls) {
        this.urlSeries.add(urls);
    }

    
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }

        if (!(o instanceof CustomXYURLGenerator)) {
            return false;
        }
        CustomXYURLGenerator generator = (CustomXYURLGenerator) o;
        int listCount = getListCount();
        if (listCount != generator.getListCount()) {
            return false;
        }

        for (int series = 0; series < listCount; series++) {
            int urlCount = getURLCount(series);
            if (urlCount != generator.getURLCount(series)) {
                return false;
            }

            for (int item = 0; item < urlCount; item++) {
                String u1 = getURL(series, item);
                String u2 = generator.getURL(series, item);
                if (u1 != null) {
                    if (!u1.equals(u2)) {
                        return false;
                    }
                }
                else {
                    if (u2 != null) {
                        return false;
                    }
                }
            }
        }
        return true;

    }

}
