
package net.sourceforge.pmd;

import java.util.List;
import java.util.Properties;



public interface Rule {
	
	public static final int LOWEST_PRIORITY = 5;

	
	public static final String[] PRIORITIES = { "High", "Medium High",
			"Medium", "Medium Low", "Low" };

	
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

	
	String getExample();

	
	void addExample(String example);

	
	String getExternalInfoUrl();

	
	void setExternalInfoUrl(String externalInfoUrl);

	
	int getPriority();

	
	void setPriority(int priority);

	
	String getPriorityName();

	
	boolean include();

	
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

	
	void addRuleChainVisit(String astNodeName);
	
	
	void start(RuleContext ctx);

	
	void apply(List<?> astCompilationUnits, RuleContext ctx);
	
	
	void end(RuleContext ctx);
}
