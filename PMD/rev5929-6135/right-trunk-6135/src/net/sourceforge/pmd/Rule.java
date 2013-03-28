
package net.sourceforge.pmd;

import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.lang.ast.Node;



public interface Rule {
    
    public static final int LOWEST_PRIORITY = 5;

    
    public static final String[] PRIORITIES = { "High", "Medium High", "Medium", "Medium Low", "Low" };

    
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

    
    int getPriority();

    
    void setPriority(int priority);

    
    String getPriorityName();

    
    @Deprecated
    boolean include();

    
    @Deprecated
    void setInclude(boolean include);

    
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
