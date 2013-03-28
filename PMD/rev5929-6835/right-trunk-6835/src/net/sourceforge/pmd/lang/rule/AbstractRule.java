
package net.sourceforge.pmd.lang.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.CollectionUtil;



public abstract class AbstractRule implements Rule {
    private Language language;
    private LanguageVersion minimumLanguageVersion;
    private LanguageVersion maximumLanguageVersion;
    private boolean deprecated;
    private String name = getClass().getName();
    private String since;
    private String ruleClass = getClass().getName();
    private String ruleSetName;
    private String message;
    private String description;
    private List<String> examples = new ArrayList<String>();
    private String externalInfoUrl;
    private RulePriority priority = RulePriority.LOW;
    private List<PropertyDescriptor<?>> propertyDescriptors = new ArrayList<PropertyDescriptor<?>>();
    
    private Map<PropertyDescriptor<?>, Object> propertyValuesByDescriptor = new HashMap<PropertyDescriptor<?>, Object>();
    private boolean usesDFA;
    private boolean usesTypeResolution;
    private List<String> ruleChainVisits = new ArrayList<String>();
    
    public AbstractRule() {
	definePropertyDescriptor(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR);
	definePropertyDescriptor(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
    }

    
    public Language getLanguage() {
	return language;
    }

    
    public void setLanguage(Language language) {
	if (this.language != null && this instanceof ImmutableLanguage && !this.language.equals(language)) {
	    throw new UnsupportedOperationException("The Language for Rule class " + this.getClass().getName()
		    + " is immutable and cannot be changed.");
	}
	this.language = language;
    }

    
    public LanguageVersion getMinimumLanguageVersion() {
	return minimumLanguageVersion;
    }

    
    public void setMinimumLanguageVersion(LanguageVersion minimumLanguageVersion) {
	this.minimumLanguageVersion = minimumLanguageVersion;
    }

    
    public LanguageVersion getMaximumLanguageVersion() {
	return maximumLanguageVersion;
    }

    
    public void setMaximumLanguageVersion(LanguageVersion maximumLanguageVersion) {
	this.maximumLanguageVersion = maximumLanguageVersion;
    }

    
    public boolean isDeprecated() {
	return deprecated;
    }

    
    public void setDeprecated(boolean deprecated) {
	this.deprecated = deprecated;
    }

    
    public String getName() {
	return name;
    }

    
    public void setName(String name) {
	this.name = name;
    }

    
    public String getSince() {
	return since;
    }

    
    public void setSince(String since) {
	this.since = since;
    }

    
    public String getRuleClass() {
	return ruleClass;
    }

    
    public void setRuleClass(String ruleClass) {
	this.ruleClass = ruleClass;
    }

    
    public String getRuleSetName() {
	return ruleSetName;
    }

    
    public void setRuleSetName(String ruleSetName) {
	this.ruleSetName = ruleSetName;
    }

    
    public String getMessage() {
	return message;
    }

    
    public void setMessage(String message) {
	this.message = message;
    }

    
    public String getDescription() {
	return description;
    }

    
    public void setDescription(String description) {
	this.description = description;
    }

    
    public List<String> getExamples() {
	
	return examples;
    }

    
    public void addExample(String example) {
	examples.add(example);
    }

    
    public String getExternalInfoUrl() {
	return externalInfoUrl;
    }

    
    public void setExternalInfoUrl(String externalInfoUrl) {
	this.externalInfoUrl = externalInfoUrl;
    }

    
    public RulePriority getPriority() {
	return priority;
    }

    
    public void setPriority(RulePriority priority) {
	this.priority = priority;
    }

    
    public void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) {
	
	for (PropertyDescriptor<?> descriptor : propertyDescriptors) {
	    if (descriptor.name().equals(propertyDescriptor.name())) {
		throw new IllegalArgumentException("There is already a PropertyDescriptor with name '"
			+ propertyDescriptor.name() + "' defined on Rule " + this.getName() + ".");
	    }
	}
	propertyDescriptors.add(propertyDescriptor);
	
	Collections.sort(propertyDescriptors);
    }

    
    public PropertyDescriptor<?> getPropertyDescriptor(String name) {
	for (PropertyDescriptor<?> propertyDescriptor : propertyDescriptors) {
	    if (name.equals(propertyDescriptor.name())) {
		return propertyDescriptor;
	    }
	}
	return null;
    }

    
    public boolean hasDescriptor(PropertyDescriptor<?> descriptor) {
    	
    	if (propertyValuesByDescriptor.isEmpty()) {
    		getPropertiesByPropertyDescriptor();	
    	}
    	
    	return propertyValuesByDescriptor.containsKey(descriptor);
    }
    
    
    public List<PropertyDescriptor<?>> getPropertyDescriptors() {
	return propertyDescriptors;
    }

    
    @SuppressWarnings("unchecked")
    public <T> T getProperty(PropertyDescriptor<T> propertyDescriptor) {
	checkValidPropertyDescriptor(propertyDescriptor);
	T value;
	if (propertyValuesByDescriptor.containsKey(propertyDescriptor)) {
	    value = (T) propertyValuesByDescriptor.get(propertyDescriptor);
	} else {
	    value = propertyDescriptor.defaultValue();
	}
	return value;
    }

    
    public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
	checkValidPropertyDescriptor(propertyDescriptor);
	propertyValuesByDescriptor.put(propertyDescriptor, value);
    }

    private void checkValidPropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) {
	if (!propertyDescriptors.contains(propertyDescriptor)) {
	    throw new IllegalArgumentException("Property descriptor not defined for Rule " + this.getName() + ": " + propertyDescriptor);
	}
    }

    
    public Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor() {
	if (propertyDescriptors.isEmpty()) {
	    return Collections.emptyMap();
	}

	Map<PropertyDescriptor<?>, Object> propertiesByPropertyDescriptor = new HashMap<PropertyDescriptor<?>, Object>(
		propertyDescriptors.size());
	
	propertiesByPropertyDescriptor.putAll(this.propertyValuesByDescriptor);

	
	for (PropertyDescriptor<?> propertyDescriptor : this.propertyDescriptors) {
	    if (!propertiesByPropertyDescriptor.containsKey(propertyDescriptor)) {
		propertiesByPropertyDescriptor.put(propertyDescriptor, propertyDescriptor.defaultValue());
	    }
	}

	return propertiesByPropertyDescriptor;
    }

    
    public boolean usesDefaultValues() {
        
        Map<PropertyDescriptor<?>, Object> valuesByProperty = getPropertiesByPropertyDescriptor();
        if (valuesByProperty.isEmpty()) {
        	return true;
        	}
        
        Iterator<Map.Entry<PropertyDescriptor<?>, Object>> iter = valuesByProperty.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<PropertyDescriptor<?>, Object> entry = iter.next();
            if (!CollectionUtil.areEqual(entry.getKey().defaultValue(), entry.getValue())) {
            	return false;
           		}
        }
        
        return true;
    }
    
    
    public void setUsesDFA() {
	this.usesDFA = true;
    }

    
    public boolean usesDFA() {
	return this.usesDFA;
    }

    
    public void setUsesTypeResolution() {
	this.usesTypeResolution = true;
    }

    
    public boolean usesTypeResolution() {
	return this.usesTypeResolution;
    }

    
    public boolean usesRuleChain() {
	return !getRuleChainVisits().isEmpty();
    }

    
    public List<String> getRuleChainVisits() {
	return ruleChainVisits;
    }

    
    public void addRuleChainVisit(Class<? extends Node> nodeClass) {
	if (!nodeClass.getSimpleName().startsWith("AST")) {
	    throw new IllegalArgumentException("Node class does not start with 'AST' prefix: " + nodeClass);
	}
	addRuleChainVisit(nodeClass.getSimpleName().substring("AST".length()));
    }

    
    public void addRuleChainVisit(String astNodeName) {
	if (!ruleChainVisits.contains(astNodeName)) {
	    ruleChainVisits.add(astNodeName);
	}
    }

    
    public void start(RuleContext ctx) {
	
    }

    
    public void end(RuleContext ctx) {
	
    }

    
    public void addViolation(Object data, Node node) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
		ruleContext, this, node);
    }

    
    public void addViolation(Object data, Node node, String arg) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
		ruleContext, this, node, arg);
    }

    
    public void addViolation(Object data, Node node, Object[] args) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
		ruleContext, this, node, args);
    }

    
    public void addViolationWithMessage(Object data, Node node, String message) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolationWithMessage(
		ruleContext, this, node, message);
    }

    
    public void addViolationWithMessage(Object data, Node node, String message, Object[] args) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolationWithMessage(
		ruleContext, this, node, message, args);
    }

    
    @Override
    public boolean equals(Object o) {
	if (o == null) {
	    return false; 
	}

	if (this == o) {
	    return true; 
	}

	boolean equality = this.getClass().getName().equals(o.getClass().getName());

	if (equality) {
	    Rule that = (Rule) o;
	    equality = this.getName().equals(that.getName()) && this.getPriority().equals(that.getPriority())
		    && this.getPropertiesByPropertyDescriptor().equals(that.getPropertiesByPropertyDescriptor());
	}

	return equality;
    }

    
    @Override
    public int hashCode() {
	Object propertyValues = this.getPropertiesByPropertyDescriptor();
	return this.getClass().getName().hashCode() + (this.getName() != null ? this.getName().hashCode() : 0)
		+ this.getPriority().hashCode() + (propertyValues != null ? propertyValues.hashCode() : 0);
    }
}
