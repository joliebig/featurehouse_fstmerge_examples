package net.sf.jabref.export.layout.format;

import net.sf.jabref.export.layout.LayoutFormatter;
import net.sf.jabref.Globals;


public class DOIStrip implements LayoutFormatter {

	public String format(String fieldText) {
		
		if (fieldText == null){
			return null;
		}
		
		fieldText = fieldText.trim();
		if (fieldText.length() == 0){
			return fieldText;
		}

		
		if (fieldText.startsWith("10.")) {
			return fieldText;
		}
		
		
		if (fieldText.matches("^doi:/*.*")){
			fieldText = fieldText.replaceFirst("^doi:/*", "");
			return fieldText;
		}

		
		if (fieldText.startsWith(Globals.DOI_LOOKUP_PREFIX)){
			fieldText = fieldText.replaceFirst(Globals.DOI_LOOKUP_PREFIX, "");
			return fieldText;
		}
		
		return fieldText;
	}
}
