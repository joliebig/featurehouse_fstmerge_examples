
package net.sourceforge.pmd;

import java.util.List;
import java.util.Map;
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

    
    boolean getBooleanProperty(PropertyDescriptor key);
    
    
    boolean[] getBooleanProperties(PropertyDescriptor key);
    
    
    int getIntProperty(String name);

    
    int getIntProperty(PropertyDescriptor key);
    
    
    int[] getIntProperties(PropertyDescriptor key);
    
    
    double getDoubleProperty(String name);

    
    double getDoubleProperty(PropertyDescriptor key);
    
    
    double[] getDoubleProperties(PropertyDescriptor key);
    
    
    String getStringProperty(String name);

    
    String getStringProperty(PropertyDescriptor key);
    
    
    String[] getStringProperties(PropertyDescriptor key);
    
    
    void setProperty(PropertyDescriptor key, Object value);
    
    
    void setProperties(PropertyDescriptor key, Object[] values);
    
    
    Map<PropertyDescriptor, Object> propertyValuesByDescriptor();
    
    
    
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
