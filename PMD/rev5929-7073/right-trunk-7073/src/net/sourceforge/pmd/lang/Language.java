
package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.ecmascript.rule.EcmascriptRuleChainVisitor;
import net.sourceforge.pmd.lang.java.rule.JavaRuleChainVisitor;
import net.sourceforge.pmd.lang.jsp.rule.JspRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.RuleChainVisitor;
import net.sourceforge.pmd.lang.xml.rule.XmlRuleChainVisitor;


public enum Language {

    
    
    CPP("C++", null, "cpp", null, "h", "c", "cpp", "cxx", "cc", "C"),
    FORTRAN("Fortran", null, "fortran", null, "for"),
    ECMASCRIPT("Ecmascript", null, "ecmascript", EcmascriptRuleChainVisitor.class, "js"),
    JAVA("Java", null, "java", JavaRuleChainVisitor.class, "java"),
    JSP("Java Server Pages", "JSP", "jsp", JspRuleChainVisitor.class, "jsp"),
    PHP("PHP: Hypertext Preprocessor", "PHP", "php", null, "php", "class"),
    RUBY("Ruby", null, "ruby", null, "rb", "cgi", "class"),
    XSL("XSL", null, "xsl", XmlRuleChainVisitor.class, "xsl", "xslt"),
    XML("XML", null, "xml", XmlRuleChainVisitor.class, "xml");

    private final String name;
    private final String shortName;
    private final String terseName;
    private final List<String> extensions;
    private final Class<?> ruleChainVisitorClass;
    private final List<LanguageVersion> versions;

    
    private Language(String name, String shortName, String terseName, Class<?> ruleChainVisitorClass,
	    String... extensions) {
	if (name == null) {
	    throw new IllegalArgumentException("Name must not be null.");
	}
	if (terseName == null) {
	    throw new IllegalArgumentException("Terse name must not be null.");
	}
	this.name = name;
	this.shortName = shortName != null ? shortName : name;
	this.terseName = terseName;
	this.ruleChainVisitorClass = ruleChainVisitorClass;
	this.extensions = Collections.unmodifiableList(Arrays.asList(extensions));
	this.versions = new ArrayList<LanguageVersion>();

	
	if (ruleChainVisitorClass != null) {
	    try {
		Object obj = ruleChainVisitorClass.newInstance();
		if (!(obj instanceof RuleChainVisitor)) {
		    throw new IllegalStateException("RuleChainVisitor class <" + ruleChainVisitorClass.getName()
			    + "> does not implement the RuleChainVisitor interface!");
		}
	    } catch (InstantiationException e) {
		throw new IllegalStateException("Unable to invoke no-arg constructor for RuleChainVisitor class <"
			+ ruleChainVisitorClass.getName() + ">!");
	    } catch (IllegalAccessException e) {
		throw new IllegalStateException("Unable to invoke no-arg constructor for RuleChainVisitor class <"
			+ ruleChainVisitorClass.getName() + ">!");
	    }
	}
    }

    
    public String getName() {
	return name;
    }

    
    public String getShortName() {
	return shortName;
    }

    
    public String getTerseName() {
	return terseName;
    }

    
    public List<String> getExtensions() {
	return extensions;
    }

    
    public boolean hasExtension(String extension) {
	if (extension != null) {
	    for (String ext : extensions) {
		if (ext.equalsIgnoreCase(extension)) {
		    return true;
		}
	    }
	}
	return false;
    }

    
    public Class<?> getRuleChainVisitorClass() {
	return ruleChainVisitorClass;
    }

    
    public List<LanguageVersion> getVersions() {
	return versions;
    }

    
    public LanguageVersion getDefaultVersion() {
	init();
	for (LanguageVersion version : getVersions()) {
	    if (version.isDefaultVersion()) {
		return version;
	    }
	}
	throw new IllegalStateException("No default LanguageVersion configured for " + this);
    }

    
    public LanguageVersion getVersion(String version) {
	init();
	for (LanguageVersion languageVersion : getVersions()) {
	    if (languageVersion.getVersion().equals(version)) {
		return languageVersion;
	    }
	}
	return null;
    }

    
    @Override
    public String toString() {
	return "Language [" + name + "]";
    }

    
    public static List<Language> findWithRuleSupport() {
	List<Language> languages = new ArrayList<Language>();
	for (Language language : Language.values()) {
	    if (language.getRuleChainVisitorClass() != null) {
		languages.add(language);
	    }
	}
	return languages;
    }

    
    public static List<Language> findByExtension(String extension) {
	List<Language> languages = new ArrayList<Language>();
	for (Language language : Language.values()) {
	    if (language.hasExtension(extension)) {
		languages.add(language);
	    }
	}
	return languages;
    }

    
    public static Language findByTerseName(String terseName) {
	for (Language language : Language.values()) {
	    if (language.getTerseName().equalsIgnoreCase(terseName)) {
		return language;
	    }
	}
	return null;
    }
    
    
    public static String commaSeparatedTerseNames(List<Language> languages) {
	StringBuilder builder = new StringBuilder();
	for (Language language : languages) {
	    if (builder.length() > 0) {
		builder.append(", ");
	    }
	    builder.append(language.getTerseName());
	}
	return builder.toString();
    }

    private static void init() {
	
	
	LanguageVersion.values();
    }

    
    public static Language getDefaultLanguage() {
	return Language.JAVA;
    }
}
