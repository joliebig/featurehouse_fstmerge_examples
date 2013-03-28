package net.sourceforge.pmd.parsers;

import net.sourceforge.pmd.ast.ParseException;

import java.io.Reader;
import java.util.Map;


public interface Parser {

    
    Object parse(Reader source) throws ParseException;

    Map<Integer, String> getExcludeMap();

    void setExcludeMarker(String marker);

}
