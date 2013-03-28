

package org.jfree.chart.imagemap;

import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.urls.XYZURLGenerator;


public interface URLTagFragmentGenerator {

    
    public String generateURLFragment(String urlText);

}
