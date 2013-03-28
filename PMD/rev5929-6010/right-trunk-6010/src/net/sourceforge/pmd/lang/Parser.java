
package net.sourceforge.pmd.lang;

import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ParseException;



public interface Parser {
    
    TokenManager getTokenManager(String fileName, Reader source);

    
    Node parse(String fileName, Reader source) throws ParseException;

    
    Map<Integer, String> getExcludeMap();

    
    String getExcludeMarker();

    
    void setExcludeMarker(String marker);
}
