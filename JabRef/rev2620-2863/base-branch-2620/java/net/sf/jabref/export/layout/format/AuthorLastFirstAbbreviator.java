package net.sf.jabref.export.layout.format;

import net.sf.jabref.export.layout.LayoutFormatter;


public class AuthorLastFirstAbbreviator implements LayoutFormatter {

	
	public String format(String fieldText) {

        
        return (new AuthorAbbreviator()).format(fieldText);

	}
}
