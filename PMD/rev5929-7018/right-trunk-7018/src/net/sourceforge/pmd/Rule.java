
package net.sourceforge.pmd;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;



public interface Rule {

    
    StringProperty VIOLATION_SUPPRESS_REGEX_DESCRIPTOR = new StringProperty("violationSuppressRegex",
	    "Suppress violations with messages matching a regular expression", null, Integer.MAX_VALUE - 1);

    
    StringProperty VIOLATION_SUPPRESS_XPATH_DESCRIPTOR = new StringProperty("violationSuppressXPath",
	    "Suppress violations on nodes which match a given relative XPath expression.", null, Integer.MAX_VALUE - 2);

    
    Language getLanguage();

    
    void setLanguage(Language language);

    
    LanguageVersion getMinimumLanguageVersion();

    
    void setMinimumLanguageVersion(LanguageVersion minimumLanguageVersion);

    
    LanguageVersion getMaximumLanguageVersion();

    
    void setMaximumLanguageVersion(LanguageVersion maximumLanguageVersion);

    
    boolean isDeprecated();

    
    void setDeprecated(boolean deprecated);

    
    String getName();

    
    void setName(String name);

    
    String getSince();

    
    void setSince(String since);

    
    String getRuleClass();

    
    void setRuleClass(String ruleClass);

    
    String getRuleSetName();

    
    void setRuleSetName(String name);

    
    String getMessage();

    
    void setMessage(String message);

    
    String getDescription();

    
    void setDescription(String description);

    
    List<String> getExamples();

    
    void addExample(String example);

    
    String getExternalInfoUrl();

    
    void setExternalInfoUrl(String externalInfoUrl);

    
    RulePriority getPriority();

    
    void setPriority(RulePriority priority);

    
    void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) throws IllegalArgumentException;

    
    PropertyDescriptor<?> getPropertyDescriptor(String name);

    
    List<PropertyDescriptor<?>> getPropertyDescriptors();

    
    <T> T getProperty(PropertyDescriptor<T> propertyDescriptor);

    
    <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value);

    
    Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor();

    
    boolean hasDescriptor(PropertyDescriptor<?> descriptor);
    
    
    boolean usesDefaultValues();
    
    
    void setUsesDFA();

    
    
    boolean usesDFA();

    
    
    void setUsesTypeResolution();

    
    
    boolean usesTypeResolution();

    
    
    boolean usesRuleChain();

    
    List<String> getRuleChainVisits();

    
    void addRuleChainVisit(Class<? extends Node> nodeClass);

    
    void addRuleChainVisit(String astNodeName);

    
    void start(RuleContext ctx);

    
    void apply(List<? extends Node> nodes, RuleContext ctx);

    
    void end(RuleContext ctx);
}
