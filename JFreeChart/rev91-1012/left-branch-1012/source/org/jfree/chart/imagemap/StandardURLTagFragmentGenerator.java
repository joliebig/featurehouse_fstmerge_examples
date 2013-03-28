
 
package org.jfree.chart.imagemap;


public class StandardURLTagFragmentGenerator 
        implements URLTagFragmentGenerator {

    
    public StandardURLTagFragmentGenerator() {
        super();
    }
    
    
    public String generateURLFragment(String urlText) {
        
        return " href=\"" + urlText + "\"";
    }

}
