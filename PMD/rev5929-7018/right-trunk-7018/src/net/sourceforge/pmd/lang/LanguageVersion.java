
package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.cpp.CppHandler;
import net.sourceforge.pmd.lang.ecmascript.Ecmascript3Handler;
import net.sourceforge.pmd.lang.java.Java13Handler;
import net.sourceforge.pmd.lang.java.Java14Handler;
import net.sourceforge.pmd.lang.java.Java15Handler;
import net.sourceforge.pmd.lang.java.Java16Handler;
import net.sourceforge.pmd.lang.java.Java17Handler;
import net.sourceforge.pmd.lang.jsp.JspHandler;
import net.sourceforge.pmd.lang.xml.XmlHandler;


public enum LanguageVersion {

    
    
    CPP(Language.CPP, "", new CppHandler(), true),
    FORTRAN(Language.FORTRAN, "", null, true),
    ECMASCRIPT(Language.ECMASCRIPT, "3", new Ecmascript3Handler(), true),
    JAVA_13(Language.JAVA, "1.3", new Java13Handler(), false),
    JAVA_14(Language.JAVA, "1.4", new Java14Handler(), false),
    JAVA_15(Language.JAVA, "1.5", new Java15Handler(), true),
    JAVA_16(Language.JAVA, "1.6", new Java16Handler(), false),
    JAVA_17(Language.JAVA, "1.7", new Java17Handler(), false),
    JSP(Language.JSP, "", new JspHandler(), true),
    PHP(Language.PHP, "", null, true),
    RUBY(Language.RUBY, "", null, true),
    XSL(Language.XSL,"",new XmlHandler(),true),
    XML(Language.XML, "", new XmlHandler(), true);

    private final Language language;
    private final String version;
    private final LanguageVersionHandler languageVersionHandler;
    private final boolean defaultVersion;

    
    private LanguageVersion(Language language, String version, LanguageVersionHandler languageVersionHandler,
	    boolean defaultVersion) {
	if (language == null) {
	    throw new IllegalArgumentException("Language must not be null.");
	}
	if (version == null) {
	    throw new IllegalArgumentException("Version must not be null.");
	}
	this.language = language;
	this.version = version;
	this.languageVersionHandler = languageVersionHandler;
	this.defaultVersion = defaultVersion;

	
	if (defaultVersion) {
	    for (LanguageVersion languageVersion : language.getVersions()) {
		if (languageVersion.isDefaultVersion()) {
		    throw new IllegalArgumentException(languageVersion.getLanguage() + " already has default "
			    + languageVersion + ", not " + version);
		}
	    }
	}
	language.getVersions().add(this);
	
	Collections.sort(language.getVersions());
    }

    
    public Language getLanguage() {
	return language;
    }

    
    public String getVersion() {
	return version;
    }

    
    public String getName() {
	return version.length() > 0 ? language.getName() + ' ' + version : language.getName();
    }

    
    public String getShortName() {
	return version.length() > 0 ? language.getShortName() + ' ' + version : language.getShortName();
    }

    
    public String getTerseName() {
	return version.length() > 0 ? language.getTerseName() + ' ' + version : language.getTerseName();
    }

    
    public LanguageVersionHandler getLanguageVersionHandler() {
	return languageVersionHandler;
    }

    
    public boolean isDefaultVersion() {
	return defaultVersion;
    }

    
    @Override
    public String toString() {
	return "LanguageVersion[" + language.getName() + " " + version + ']';
    }

    
    public static LanguageVersion findByTerseName(String terseName) {
	for (LanguageVersion languageVersion : LanguageVersion.values()) {
	    if (terseName.equals(languageVersion.getTerseName())) {
		return languageVersion;
	    }
	}
	return null;
    }

    
    
    public static List<LanguageVersion> findVersionsForLanguageTerseName(String languageTerseName) {
	List<LanguageVersion> versionsAvailable = new ArrayList<LanguageVersion>(0);
	for (LanguageVersion languageVersion : LanguageVersion.values()) {	    
	    if (languageVersion.getLanguage().getTerseName().equals(languageTerseName)) {
		versionsAvailable.add(languageVersion);
	    }
	}
	return versionsAvailable;
    }
    

    
    public static String commaSeparatedTerseNames(List<LanguageVersion> languageVersions) {
    	
    	if (languageVersions == null || languageVersions.isEmpty()) {
    		return "";
    	}
    	
    	StringBuilder builder = new StringBuilder();
    	builder.append(languageVersions.get(0).getTerseName());
    	for (int i=1; i<languageVersions.size(); i++) {
    		builder.append(", ").append(languageVersions.get(i).getTerseName());
    	}
    	return builder.toString();
    }

    
    public static LanguageVersion getDefaultVersion() {
	return LanguageVersion.JAVA_15;
    }
}
