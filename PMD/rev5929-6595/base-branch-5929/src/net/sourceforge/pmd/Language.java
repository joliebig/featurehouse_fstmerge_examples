package net.sourceforge.pmd;

import java.util.HashMap;
import java.util.Map;


public final class Language {
    private static Map<String, Language> mapNameOnRuleLanguage = new HashMap<String, Language>();

    private static final String JSP_RULE_LANGUAGE_NAME = "jsp";
    private static final String JAVA_RULE_LANGUAGE_NAME = "java";

    public static final Language JAVA = new Language(JAVA_RULE_LANGUAGE_NAME);
    public static final Language JSP = new Language(JSP_RULE_LANGUAGE_NAME);


    
    public static Language getByName(String name) {
        return mapNameOnRuleLanguage.get(name);
    }

    private String name;

    
    private Language(String name) {
        this.name = name;
        mapNameOnRuleLanguage.put(name, this);
    }

    
    public String getName() {
        return name;
    }

    
    public boolean equals(Object obj) {
        if (obj instanceof Language) {
            return ((Language) obj).getName().equals(name);
        } else {
            return false;
        }
    }

    
    public int hashCode() {
        return name.hashCode();
    }

    
    public String toString() {
        return "Language [" + name + "]";
    }
}
