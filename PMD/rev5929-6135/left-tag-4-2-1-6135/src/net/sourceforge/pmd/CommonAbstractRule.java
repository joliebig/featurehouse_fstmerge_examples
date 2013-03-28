package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;





public abstract class CommonAbstractRule implements Rule {

	
	private static final boolean IN_OLD_PROPERTY_MODE = true;

	private String name = getClass().getName();
	private String since;
	private String ruleClass = getClass().getName();
	private String ruleSetName;
	private String message;
	private String description;
	private List<String> examples = new ArrayList<String>();
	private String externalInfoUrl;
	private int priority = LOWEST_PRIORITY;
	
	private boolean include;
	private Properties properties = new Properties();
	private boolean usesDFA;
	private boolean usesTypeResolution;
	private List<String> ruleChainVisits = new ArrayList<String>();

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

	
	public String getExample() {
		if (examples.isEmpty()) {
			return null;
		} else {
			
			return examples.get(examples.size() - 1);
		}
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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getPriorityName() {
		return PRIORITIES[getPriority() - 1];
	}

	
	public boolean include() {
		return include;
	}

	
	public void setInclude(boolean include) {
		this.include = include;
	}

	
	public Properties getProperties() {
		
		return properties;
	}

	
	public void addProperty(String name, String value) {
		getProperties().setProperty(name, value);
	}

	
	public void addProperties(Properties properties) {
		getProperties().putAll(properties);
	}

	
	public boolean hasProperty(String name) {
		return IN_OLD_PROPERTY_MODE ? 
		getProperties().containsKey(name)
				: propertiesByName().containsKey(name);
	}

	
	public boolean getBooleanProperty(String name) {
		return Boolean.parseBoolean(getProperties().getProperty(name));
	}

	public boolean getBooleanProperty(PropertyDescriptor descriptor) {

		return ((Boolean)getProperty(descriptor)).booleanValue();
	}

	
	public boolean[] getBooleanProperties(PropertyDescriptor descriptor) {
		Boolean[] values = (Boolean[])getProperties(descriptor);
		boolean[] bools = new boolean[values.length];
		for (int i = 0; i < bools.length; i++)
			bools[i] = values[i].booleanValue();
		return bools;
	}

	
	public int getIntProperty(String name) {
		return Integer.parseInt(getProperties().getProperty(name));
	}

	public int getIntProperty(PropertyDescriptor descriptor) {

		return ((Number)getProperty(descriptor)).intValue();
	}

	
	public int[] getIntProperties(PropertyDescriptor descriptor) {
		Number[] values = (Number[])getProperties(descriptor);
		int[] ints = new int[values.length];
		for (int i = 0; i < ints.length; i++)
			ints[i] = values[i].intValue();
		return ints;
	}

	
	public double getDoubleProperty(String name) {
		return Double.parseDouble(getProperties().getProperty(name));
	}

	public double getDoubleProperty(PropertyDescriptor descriptor) {
		return ((Number)getProperty(descriptor)).doubleValue();
	}

	
	public double[] getDoubleProperties(PropertyDescriptor descriptor) {
		Number[] values = (Number[])getProperties(descriptor);
		double[] doubles = new double[values.length];
		for (int i = 0; i < doubles.length; i++)
			doubles[i] = values[i].doubleValue();
		return doubles;
	}

	
	public String getStringProperty(String name) {
		return getProperties().getProperty(name);
	}

	public String getStringProperty(PropertyDescriptor descriptor) {
		return (String)getProperty(descriptor);
	}

	public String[] getStringProperties(PropertyDescriptor descriptor) {
		return (String[])getProperties(descriptor);
	}

	public Class[] getTypeProperties(PropertyDescriptor descriptor) {
		return (Class[])getProperties(descriptor);
	}

	public Class getTypeProperty(PropertyDescriptor descriptor) {
		return (Class)getProperty(descriptor);
	}

	private Object getProperty(PropertyDescriptor descriptor) {
		if (descriptor.maxValueCount() > 1) {
			propertyGetError(descriptor, true);
		}
		String rawValue = getProperties().getProperty(descriptor.name());
		return rawValue == null || rawValue.length() == 0 ? descriptor
				.defaultValue() : descriptor.valueFrom(rawValue);
	}

	public void setProperty(PropertyDescriptor descriptor, Object value) {
		if (descriptor.maxValueCount() > 1) {
			propertySetError(descriptor, true);
		}
		getProperties().setProperty(descriptor.name(),
				descriptor.asDelimitedString(value));
	}

	private Object[] getProperties(PropertyDescriptor descriptor) {
		if (descriptor.maxValueCount() == 1) {
			propertyGetError(descriptor, false);
		}
		String rawValue = getProperties().getProperty(descriptor.name());
		return rawValue == null || rawValue.length() == 0 ? (Object[])descriptor
				.defaultValue()
				: (Object[])descriptor.valueFrom(rawValue);
	}

	public void setProperties(PropertyDescriptor descriptor, Object[] values) {
		if (descriptor.maxValueCount() == 1) {
			propertySetError(descriptor, false);
		}
		getProperties().setProperty(descriptor.name(),
				descriptor.asDelimitedString(values));
	}

	
	protected Map<String, PropertyDescriptor> propertiesByName() {
		return Collections.emptyMap();
	}

	public PropertyDescriptor propertyDescriptorFor(String name) {
		PropertyDescriptor descriptor = propertiesByName().get(name);
		if (descriptor == null) {
			throw new IllegalArgumentException("Unknown property: " + name);
		}
		return descriptor;
	}

	private void propertyGetError(PropertyDescriptor descriptor,
			boolean requestedSingleValue) {

		if (requestedSingleValue) {
			throw new RuntimeException(
					"Cannot retrieve a single value from a multi-value property field");
		}
		throw new RuntimeException(
				"Cannot retrieve multiple values from a single-value property field");
	}

	private void propertySetError(PropertyDescriptor descriptor,
			boolean setSingleValue) {

		if (setSingleValue) {
			throw new RuntimeException(
					"Cannot set a single value within a multi-value property field");
		}
		throw new RuntimeException(
				"Cannot set multiple values within a single-value property field");
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

	public void addRuleChainVisit(String astNodeName) {
		if (!ruleChainVisits.contains(astNodeName)) {
			ruleChainVisits.add(astNodeName);
		}
	}

	public void start(RuleContext ctx) {
		
	}

	public void end(RuleContext ctx) {
		
	}

	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false; 
		}

		if (this == o) {
			return true; 
		}

		boolean equality = this.getClass().getName().equals(
				o.getClass().getName());

		if (equality) {
			Rule that = (Rule)o;
			equality = this.getName().equals(that.getName())
					&& this.getPriority() == that.getPriority()
					&& this.getProperties().equals(that.getProperties());
		}

		return equality;
	}

	
	@Override
	public int hashCode() {
		return this.getClass().getName().hashCode()
				+ (this.getName() != null ? this.getName().hashCode() : 0)
				+ this.getPriority()
				+ (this.getProperties() != null ? this.getProperties()
						.hashCode() : 0);
	}

	protected static Map<String, PropertyDescriptor> asFixedMap(
			PropertyDescriptor[] descriptors) {
		Map<String, PropertyDescriptor> descriptorsByName = new HashMap<String, PropertyDescriptor>(
				descriptors.length);
		for (PropertyDescriptor descriptor : descriptors) {
			descriptorsByName.put(descriptor.name(), descriptor);
		}
		return Collections.unmodifiableMap(descriptorsByName);
	}

	protected static Map<String, PropertyDescriptor> asFixedMap(
			PropertyDescriptor descriptor) {
		return asFixedMap(new PropertyDescriptor[] { descriptor });
	}
}
