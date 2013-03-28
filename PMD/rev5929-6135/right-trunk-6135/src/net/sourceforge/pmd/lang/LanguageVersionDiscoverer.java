
package net.sourceforge.pmd.lang;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LanguageVersionDiscoverer {
    private Map<Language, LanguageVersion> languageToLanguageVersion = new HashMap<Language, LanguageVersion>();

    
    public LanguageVersion setDefaultLanguageVersion(LanguageVersion languageVersion) {
	LanguageVersion currentLanguageVersion = languageToLanguageVersion.put(languageVersion.getLanguage(),
		languageVersion);
	if (currentLanguageVersion == null) {
	    currentLanguageVersion = languageVersion.getLanguage().getDefaultVersion();
	}
	return currentLanguageVersion;
    }

    
    public LanguageVersion getDefaultLanguageVersion(Language language) {
	LanguageVersion languageVersion = languageToLanguageVersion.get(language);
	if (languageVersion == null) {
	    languageVersion = language.getDefaultVersion();
	}
	return languageVersion;
    }

    
    public LanguageVersion getDefaultLanguageVersionForFile(File sourceFile) {
	return getDefaultLanguageVersionForFile(sourceFile.getName());
    }

    
    public LanguageVersion getDefaultLanguageVersionForFile(String fileName) {
	List<Language> languages = getLanguagesForFile(fileName);
	LanguageVersion languageVersion = null;
	if (!languages.isEmpty()) {
	    languageVersion = getDefaultLanguageVersion(languages.get(0));
	}
	return languageVersion;
    }

    
    public List<Language> getLanguagesForFile(File sourceFile) {
	return getLanguagesForFile(sourceFile.getName());
    }

    
    public List<Language> getLanguagesForFile(String fileName) {
	String extension = getExtension(fileName);
	return Language.findByExtension(extension);
    }

    
    private String getExtension(String fileName) {
	String extension = null;
	int extensionIndex = 1 + fileName.lastIndexOf('.');
	if (extensionIndex > 0) {
	    extension = fileName.substring(extensionIndex);
	}
	return extension;
    }
}
