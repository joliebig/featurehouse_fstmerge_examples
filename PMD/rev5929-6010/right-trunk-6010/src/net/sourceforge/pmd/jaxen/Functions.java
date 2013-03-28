package net.sourceforge.pmd.jaxen;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;


public class Functions {

    public static void registerAll() {
	registerGlobal();
	registerLanguages();
    }

    private static void registerGlobal() {
	MatchesFunction.registerSelfInSimpleContext();
    }

    private static void registerLanguages() {
	for (Language language : Language.values()) {
	    for (LanguageVersion languageVersion : language.getVersions()) {
		LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
		if (languageVersionHandler != null) {
		    languageVersionHandler.getXPathFunctionRegister().register();
		}
	    }
	}
    }

}
