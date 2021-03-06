package net.sf.jabref.export.layout.format;

import net.sf.jabref.export.layout.AbstractParamLayoutFormatter;

import java.util.List;
import java.util.ArrayList;


public class WrapContent extends AbstractParamLayoutFormatter {

    
    private String before = null, after = null;


    public void setArgument(String arg) {
	String[] parts = parseArgument(arg);
        if (parts.length < 2)
	   return;
        before = parts[0];
        after = parts[1];
    }

    public String format(String fieldText) {
	if (before == null)
	    return "";
    	if (fieldText.length() == 0)
	    return "";
	else
	    return new StringBuilder(before).append(fieldText).append(after).toString();    
    }
}
