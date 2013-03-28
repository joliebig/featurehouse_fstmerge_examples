package net.sourceforge.pmd.lang;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.List;


public class LanguageFilenameFilter implements FilenameFilter {

    private final List<Language> languages;

    
    public LanguageFilenameFilter(Language language) {
	this(Collections.singletonList(language));
    }

    
    public LanguageFilenameFilter(List<Language> languages) {
	this.languages = languages;
    }

    
    public boolean accept(File dir, String name) {
	
	int lastDotIndex = name.lastIndexOf('.');
	if (lastDotIndex < 0) {
	    return false;
	}

	String extension = name.substring(1 + lastDotIndex).toUpperCase();
	for (Language language : languages) {
	    for (String ext : language.getExtensions()) {
		if (extension.equalsIgnoreCase(ext)) {
		    return true;
		}
	    }
	}
	return false;
    }

    public String toString() {
	StringBuilder buffer = new StringBuilder("(Extension is one of: ");
	for (Language language : languages) {
	    List<String> extensions = language.getExtensions();
	    for (int i = 0; i < extensions.size(); i++) {
		if (i > 0) {
		    buffer.append(", ");
		}
		buffer.append(extensions.get(i));
	    }
	}
	buffer.append(")");
	return buffer.toString();
    }
}
