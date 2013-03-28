

package org.jfree.chart.urls;

import java.io.Serializable;

import org.jfree.data.category.CategoryDataset;
import org.jfree.util.ObjectUtilities;


public class StandardCategoryURLGenerator implements CategoryURLGenerator,
        Cloneable, Serializable {

    
    private static final long serialVersionUID = 2276668053074881909L;

    
    private String prefix = "index.html";

    
    private String seriesParameterName = "series";

    
    private String categoryParameterName = "category";

    
    public StandardCategoryURLGenerator() {
        super();
    }

    
    public StandardCategoryURLGenerator(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Null 'prefix' argument.");
        }
        this.prefix = prefix;
    }

    
    public StandardCategoryURLGenerator(String prefix,
                                        String seriesParameterName,
                                        String categoryParameterName) {

        if (prefix == null) {
            throw new IllegalArgumentException("Null 'prefix' argument.");
        }
        if (seriesParameterName == null) {
            throw new IllegalArgumentException(
                    "Null 'seriesParameterName' argument.");
        }
        if (categoryParameterName == null) {
            throw new IllegalArgumentException(
                    "Null 'categoryParameterName' argument.");
        }
        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.categoryParameterName = categoryParameterName;

    }

    
    public String generateURL(CategoryDataset dataset, int series,
                              int category) {
        String url = this.prefix;
        Comparable seriesKey = dataset.getRowKey(series);
        Comparable categoryKey = dataset.getColumnKey(category);
        boolean firstParameter = url.indexOf("?") == -1;
        url += firstParameter ? "?" : "&amp;";
        url += this.seriesParameterName + "=" + URLUtilities.encode(
                seriesKey.toString(), "UTF-8");
        url += "&amp;" + this.categoryParameterName + "="
                + URLUtilities.encode(categoryKey.toString(), "UTF-8");
        return url;
    }

    
    public Object clone() throws CloneNotSupportedException {
        
        
        
        return super.clone();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardCategoryURLGenerator)) {
            return false;
        }
        StandardCategoryURLGenerator that = (StandardCategoryURLGenerator) obj;
        if (!ObjectUtilities.equal(this.prefix, that.prefix)) {
            return false;
        }

        if (!ObjectUtilities.equal(this.seriesParameterName,
                that.seriesParameterName)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.categoryParameterName,
                that.categoryParameterName)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result;
        result = (this.prefix != null ? this.prefix.hashCode() : 0);
        result = 29 * result
            + (this.seriesParameterName != null
                    ? this.seriesParameterName.hashCode() : 0);
        result = 29 * result
            + (this.categoryParameterName != null
                    ? this.categoryParameterName.hashCode() : 0);
        return result;
    }

}
