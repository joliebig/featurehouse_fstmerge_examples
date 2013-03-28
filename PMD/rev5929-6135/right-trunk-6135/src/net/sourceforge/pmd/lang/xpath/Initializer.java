
package net.sourceforge.pmd.lang.xpath;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;


public class Initializer {

    
    public static void initialize() {
	initializeGlobal();
	initializeLanguages();
    }

    private static void initializeGlobal() {
	MatchesFunction.registerSelfInSimpleContext();
    }

    private static void initializeLanguages() {
	for (Language language : Language.values()) {
	    for (LanguageVersion languageVersion : language.getVersions()) {
		LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
		if (languageVersionHandler != null) {
		    languageVersionHandler.getXPathHandler().initialize();
		}
	    }
	}
    }
}
