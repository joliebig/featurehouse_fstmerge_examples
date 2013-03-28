
package net.sourceforge.pmd;

import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;



public interface Rule {

    
    String VIOLATION_SUPPRESS_REGEX_PROPERTY = "violationSuppressRegex";

    
    String VIOLATION_SUPPRESS_XPATH_PROPERTY = "violationSuppressXPath";

    
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

    
    @Deprecated
    String getExample();

    
    void addExample(String example);

    
    String getExternalInfoUrl();

    
    void setExternalInfoUrl(String externalInfoUrl);

    
    RulePriority getPriority();

    
    void setPriority(RulePriority priority);

    
    Properties getProperties();

    
    void addProperty(String name, String property);

    
    void addProperties(Properties properties);

    
    boolean hasProperty(String name);

    
    boolean getBooleanProperty(String name);

    
    int getIntProperty(String name);

    
    double getDoubleProperty(String name);

    
    String getStringProperty(String name);

    
    
    PropertyDescriptor propertyDescriptorFor(String name);

    
    
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
