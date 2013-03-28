package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.util.StringUtil;


public class RuleReference extends AbstractDelegateRule {
	private String name;
	private Properties properties;
	private String message;
	private String description;
	private List<String> examples;
	private String externalInfoUrl;
	private Integer priority;
	private RuleSetReference ruleSetReference;

	public String getOverriddenName() {
		return name;
	}

	@Override
	public void setName(String name) {
		
		if (!isSame(name, super.getName()) || this.name != null) {
			this.name = name;
			super.setName(name);
		}
	}

	public Properties getOverriddenProperties() {
		return properties;
	}

	@Override
	public void addProperty(String name, String property) {
		
		if (!super.hasProperty(name) || !isSame(property, super.getStringProperty(name))) {
			if (this.properties == null) {
				this.properties = new Properties();
			}
			this.properties.put(name, property);
			super.addProperty(name, property);
		}
	}

	@Override
	public void addProperties(Properties properties) {
		
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			addProperty((String)entry.getKey(), (String)entry.getValue());
		}
	}

	public String getOverriddenMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		
		if (!isSame(message, super.getMessage()) || this.message != null) {
			this.message = message;
			super.setMessage(message);
		}
	}

	public String getOverriddenDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		
		if (!isSame(description, super.getDescription()) || this.description != null) {
			this.description = description;
			super.setDescription(description);
		}
	}

	public List<String> getOverriddenExamples() {
		return examples;
	}

	@Override
	public void addExample(String example) {
		
		
		
		
		
		
		
		
				
		
		if (!contains(super.getExamples(), example)) {
			if (this.examples == null) {
				this.examples = new ArrayList<String>(1);
			}
			
			this.examples.clear();
			this.examples.add(example);
			super.addExample(example);
		}
	}

	public String getOverriddenExternalInfoUrl() {
		return externalInfoUrl;
	}

	@Override
	public void setExternalInfoUrl(String externalInfoUrl) {
		
		if (!isSame(externalInfoUrl, super.getExternalInfoUrl()) || this.externalInfoUrl != null) {
			this.externalInfoUrl = externalInfoUrl;
			super.setExternalInfoUrl(externalInfoUrl);
		}
	}

	public Integer getOverriddenPriority() {
		return priority;
	}

	@Override
	public void setPriority(int priority) {
		
		if (priority != super.getPriority() || this.priority != null) {
			this.priority = priority;
			super.setPriority(priority);
		}
	}

	public RuleSetReference getRuleSetReference() {
		return ruleSetReference;
	}

	public void setRuleSetReference(RuleSetReference ruleSetReference) {
		this.ruleSetReference = ruleSetReference;
	}

	private static boolean isSame(String s1, String s2) {
		return StringUtil.isSame(s1, s2, true, false, true);
	}

	private static boolean contains(Collection<String> collection, String s1) {
		for (String s2 : collection) {
			if (isSame(s1, s2)) {
				return true;
			}
		}
		return false;
	}
}
