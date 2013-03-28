
 
package org.jfree.chart.urls;

import java.io.Serializable;

import org.jfree.data.general.PieDataset;
import org.jfree.util.ObjectUtilities;


public class StandardPieURLGenerator implements PieURLGenerator, Serializable {

    
    private static final long serialVersionUID = 1626966402065883419L;
    
    
    private String prefix = "index.html";

    
    private String categoryParameterName = "category";
    
    
    private String indexParameterName = "pieIndex";

    
    public StandardPieURLGenerator() {
        this("index.html");
    }

    
    public StandardPieURLGenerator(String prefix) {
        this(prefix, "category");
    }

    
    public StandardPieURLGenerator(String prefix, 
                                   String categoryParameterName) {
        this(prefix, categoryParameterName, "pieIndex");
    }

    
    public StandardPieURLGenerator(String prefix, 
                                   String categoryParameterName, 
                                   String indexParameterName) {
        if (prefix == null) {
            throw new IllegalArgumentException("Null 'prefix' argument.");
        }
        if (categoryParameterName == null) {
            throw new IllegalArgumentException(
                    "Null 'categoryParameterName' argument.");
        }
        this.prefix = prefix;
        this.categoryParameterName = categoryParameterName;
        this.indexParameterName = indexParameterName;
    }

    
    public String generateURL(PieDataset dataset, Comparable key, 
            int pieIndex) {
        String url = this.prefix;
        if (url.indexOf("?") > -1) {
            url += "&amp;" + this.categoryParameterName + "=" 
                    + URLUtilities.encode(key.toString(), "UTF-8");
        }
        else {
            url += "?" + this.categoryParameterName + "=" 
                    + URLUtilities.encode(key.toString(), "UTF-8");
        }
        if (this.indexParameterName != null) {
            url += "&amp;" + this.indexParameterName + "=" 
                   + String.valueOf(pieIndex);
        }
        return url;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardPieURLGenerator)) {
            return false;
        }
        StandardPieURLGenerator that = (StandardPieURLGenerator) obj;
        if (!this.prefix.equals(that.prefix)) {
            return false;
        }
        if (!this.categoryParameterName.equals(that.categoryParameterName)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.indexParameterName, 
                that.indexParameterName)) {
            return false;
        }
        return true;
    }
}
