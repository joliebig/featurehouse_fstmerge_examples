
package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.PropertyDescriptorUtil;
import net.sourceforge.pmd.util.ResourceLoader;
import net.sourceforge.pmd.util.StringUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class RuleSetFactory {

    private static final Logger LOG = Logger.getLogger(RuleSetFactory.class.getName());

    private ClassLoader classLoader = RuleSetFactory.class.getClassLoader();
    private RulePriority minimumPriority = RulePriority.LOW;
    private boolean warnDeprecated = false;

    
    public void setClassLoader(ClassLoader classLoader) {
	this.classLoader = classLoader;
    }

    
    public void setMinimumPriority(RulePriority minimumPriority) {
	this.minimumPriority = minimumPriority;
    }

    
    public void setWarnDeprecated(boolean warnDeprecated) {
	this.warnDeprecated = warnDeprecated;
    }

    
    public Iterator<RuleSet> getRegisteredRuleSets() throws RuleSetNotFoundException {
	String rulesetsProperties = null;
	try {
	    List<RuleSetReferenceId> ruleSetReferenceIds = new ArrayList<RuleSetReferenceId>();
	    for (Language language : Language.findWithRuleSupport()) {
		Properties props = new Properties();
		rulesetsProperties = "rulesets/" + language.getTerseName() + "/rulesets.properties";
		props.load(ResourceLoader.loadResourceAsStream(rulesetsProperties));
		String rulesetFilenames = props.getProperty("rulesets.filenames");
		ruleSetReferenceIds.addAll(RuleSetReferenceId.parse(rulesetFilenames));
	    }
	    return createRuleSets(ruleSetReferenceIds).getRuleSetsIterator();
	} catch (IOException ioe) {
	    throw new RuntimeException("Couldn't find " + rulesetsProperties
		    + "; please ensure that the rulesets directory is on the classpath.  The current classpath is: "
		    + System.getProperty("java.class.path"));
	}
    }

    
    public synchronized RuleSets createRuleSets(String referenceString) throws RuleSetNotFoundException {
	return createRuleSets(RuleSetReferenceId.parse(referenceString));
    }

    
    public synchronized RuleSets createRuleSets(List<RuleSetReferenceId> ruleSetReferenceIds)
	    throws RuleSetNotFoundException {
	RuleSets ruleSets = new RuleSets();
	for (RuleSetReferenceId ruleSetReferenceId : ruleSetReferenceIds) {
	    RuleSet ruleSet = createRuleSet(ruleSetReferenceId);
	    ruleSets.addRuleSet(ruleSet);
	}
	return ruleSets;
    }

    
    public synchronized RuleSet createRuleSet(String referenceString) throws RuleSetNotFoundException {
	List<RuleSetReferenceId> references = RuleSetReferenceId.parse(referenceString);
	if (references.isEmpty()) {
	    throw new RuleSetNotFoundException("No RuleSetReferenceId can be parsed from the string: <"
		    + referenceString + ">");
	}
	return createRuleSet(references.get(0));
    }

    
    public synchronized RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId) throws RuleSetNotFoundException {
	return parseRuleSetNode(ruleSetReferenceId, ruleSetReferenceId.getInputStream(this.classLoader));
    }

    
    private Rule createRule(RuleSetReferenceId ruleSetReferenceId) throws RuleSetNotFoundException {
	if (ruleSetReferenceId.isAllRules()) {
	    throw new IllegalArgumentException("Cannot parse a single Rule from an all Rule RuleSet reference: <"
		    + ruleSetReferenceId + ">.");
	}
	RuleSet ruleSet = createRuleSet(ruleSetReferenceId);
	return ruleSet.getRuleByName(ruleSetReferenceId.getRuleName());
    }

    
    private RuleSet parseRuleSetNode(RuleSetReferenceId ruleSetReferenceId, InputStream inputStream) {
	if (!ruleSetReferenceId.isExternal()) {
	    throw new IllegalArgumentException("Cannot parse a RuleSet from a non-external reference: <"
		    + ruleSetReferenceId + ">.");
	}
	try {
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document document = builder.parse(inputStream);
	    Element ruleSetElement = document.getDocumentElement();

	    RuleSet ruleSet = new RuleSet();
	    ruleSet.setFileName(ruleSetReferenceId.getRuleSetFileName());
	    ruleSet.setName(ruleSetElement.getAttribute("name"));

	    NodeList nodeList = ruleSetElement.getChildNodes();
	    for (int i = 0; i < nodeList.getLength(); i++) {
		Node node = nodeList.item(i);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
		    if (node.getNodeName().equals("description")) {
			ruleSet.setDescription(parseTextNode(node));
		    } else if (node.getNodeName().equals("include-pattern")) {
			ruleSet.addIncludePattern(parseTextNode(node));
		    } else if (node.getNodeName().equals("exclude-pattern")) {
			ruleSet.addExcludePattern(parseTextNode(node));
		    } else if (node.getNodeName().equals("rule")) {
			parseRuleNode(ruleSetReferenceId, ruleSet, node);
		    } else {
			throw new IllegalArgumentException("Unexpected element <" + node.getNodeName()
				+ "> encountered as child of <ruleset> element.");
		    }
		}
	    }

	    return ruleSet;
	} catch (ClassNotFoundException cnfe) {
	    return classNotFoundProblem(cnfe);
	} catch (InstantiationException ie) {
	    return classNotFoundProblem(ie);
	} catch (IllegalAccessException iae) {
	    return classNotFoundProblem(iae);
	} catch (ParserConfigurationException pce) {
	    return classNotFoundProblem(pce);
	} catch (RuleSetNotFoundException rsnfe) {
	    return classNotFoundProblem(rsnfe);
	} catch (IOException ioe) {
	    return classNotFoundProblem(ioe);
	} catch (SAXException se) {
	    return classNotFoundProblem(se);
	}
    }

    private static RuleSet classNotFoundProblem(Exception ex) throws RuntimeException {
	ex.printStackTrace();
	throw new RuntimeException("Couldn't find the class " + ex.getMessage());
    }

    
    private void parseRuleNode(RuleSetReferenceId ruleSetReferenceId, RuleSet ruleSet, Node ruleNode)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, RuleSetNotFoundException {
	Element ruleElement = (Element) ruleNode;
	String ref = ruleElement.getAttribute("ref");
	if (ref.endsWith("xml")) {
	    parseRuleSetReferenceNode(ruleSetReferenceId, ruleSet, ruleElement, ref);
	} else if (StringUtil.isEmpty(ref)) {
	    parseSingleRuleNode(ruleSetReferenceId, ruleSet, ruleNode);
	} else {
	    parseRuleReferenceNode(ruleSetReferenceId, ruleSet, ruleNode, ref);
	}
    }

    
    private void parseRuleSetReferenceNode(RuleSetReferenceId ruleSetReferenceId, RuleSet ruleSet, Element ruleElement,
	    String ref) throws RuleSetNotFoundException {
	RuleSetReference ruleSetReference = new RuleSetReference();
	ruleSetReference.setAllRules(true);
	ruleSetReference.setRuleSetFileName(ref);
	NodeList excludeNodes = ruleElement.getChildNodes();
	for (int i = 0; i < excludeNodes.getLength(); i++) {
	    if (isElementNode(excludeNodes.item(i),"exclude")) {
	    	Element excludeElement = (Element) excludeNodes.item(i);
			ruleSetReference.addExclude(excludeElement.getAttribute("name"));
	    }
	}

	RuleSetFactory ruleSetFactory = new RuleSetFactory();
	ruleSetFactory.setClassLoader(this.classLoader);
	RuleSet otherRuleSet = ruleSetFactory.createRuleSet(RuleSetReferenceId.parse(ref).get(0));
	for (Rule rule : otherRuleSet.getRules()) {
	    if (!ruleSetReference.getExcludes().contains(rule.getName())
		    && rule.getPriority().compareTo(minimumPriority) <= 0 && !rule.isDeprecated()) {
		RuleReference ruleReference = new RuleReference();
		ruleReference.setRuleSetReference(ruleSetReference);
		ruleReference.setRule(rule);
		ruleSet.addRule(ruleReference);
	    }
	}
    }

    
    private void parseSingleRuleNode(RuleSetReferenceId ruleSetReferenceId, RuleSet ruleSet, Node ruleNode)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Element ruleElement = (Element) ruleNode;
	
		
		if (StringUtil.isNotEmpty(ruleSetReferenceId.getRuleName())
			&& !isRuleName(ruleElement, ruleSetReferenceId.getRuleName())) {
		    return;
		}
	
		String attribute = ruleElement.getAttribute("class");
		Class<?> c = classLoader.loadClass(attribute);
		Rule rule = (Rule) c.newInstance();
	
		rule.setName(ruleElement.getAttribute("name"));
	
		if (ruleElement.hasAttribute("language")) {
		    String languageName = ruleElement.getAttribute("language");
		    Language language = Language.findByTerseName(languageName);
		    if (language == null) {
			throw new IllegalArgumentException("Unknown Language '" + languageName + "' for Rule " + rule.getName()
				+ ", supported Languages are "
				+ Language.commaSeparatedTerseNames(Language.findWithRuleSupport()));
		    }
		    rule.setLanguage(language);
		}
	
		Language language = rule.getLanguage();
		if (language == null) {
		    throw new IllegalArgumentException("Rule " + rule.getName()
			    + " does not have a Language; missing 'language' attribute?");
		}
	
		if (ruleElement.hasAttribute("minimumLanguageVersion")) {
		    String minimumLanguageVersionName = ruleElement.getAttribute("minimumLanguageVersion");
		    LanguageVersion minimumLanguageVersion = language.getVersion(minimumLanguageVersionName);
		    if (minimumLanguageVersion == null) {
			throw new IllegalArgumentException("Unknown minimum Language Version '" + minimumLanguageVersionName
				+ "' for Language '" + language.getTerseName() + "' for Rule " + rule.getName()
				+ "; supported Language Versions are: "
				+ LanguageVersion.commaSeparatedTerseNames(language.getVersions()));
		    }
		    rule.setMinimumLanguageVersion(minimumLanguageVersion);
		}
	
		if (ruleElement.hasAttribute("maximumLanguageVersion")) {
		    String maximumLanguageVersionName = ruleElement.getAttribute("maximumLanguageVersion");
		    LanguageVersion maximumLanguageVersion = language.getVersion(maximumLanguageVersionName);
		    if (maximumLanguageVersion == null) {
			throw new IllegalArgumentException("Unknown maximum Language Version '" + maximumLanguageVersionName
				+ "' for Language '" + language.getTerseName() + "' for Rule " + rule.getName()
				+ "; supported Language Versions are: "
				+ LanguageVersion.commaSeparatedTerseNames(language.getVersions()));
		    }
		    rule.setMaximumLanguageVersion(maximumLanguageVersion);
		}
	
		if (rule.getMinimumLanguageVersion() != null && rule.getMaximumLanguageVersion() != null) {
		    throw new IllegalArgumentException("The minimum Language Version '"
			    + rule.getMinimumLanguageVersion().getTerseName()
			    + "' must be prior to the maximum Language Version '"
			    + rule.getMaximumLanguageVersion().getTerseName() + "' for Rule " + rule.getName()
			    + "; perhaps swap them around?");
		}
	
		String since = ruleElement.getAttribute("since");
		if (since.length() > 0) {
		    rule.setSince(since);
		}
		rule.setMessage(ruleElement.getAttribute("message"));
		rule.setRuleSetName(ruleSet.getName());
		rule.setExternalInfoUrl(ruleElement.getAttribute("externalInfoUrl"));
	
		if (hasAttributeSetTrue(ruleElement,"dfa")) {
		    rule.setUsesDFA();
		}
	
		if (hasAttributeSetTrue(ruleElement,"typeResolution")) {
		    rule.setUsesTypeResolution();
		}
	
		final NodeList nodeList = ruleElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
		    Node node = nodeList.item(i);
		    if (node.getNodeType() != Node.ELEMENT_NODE) continue;
		    String nodeName = node.getNodeName();
			if (nodeName.equals("description")) {
			    rule.setDescription(parseTextNode(node));
			} else if (nodeName.equals("example")) {
			    rule.addExample(parseTextNode(node));
			} else if (nodeName.equals("priority")) {
			    rule.setPriority(RulePriority.valueOf(Integer.parseInt(parseTextNode(node).trim())));
			} else if (nodeName.equals("properties")) {
			    parsePropertiesNode(rule, node);
			} else {
			    throw new IllegalArgumentException("Unexpected element <" + nodeName
				    + "> encountered as child of <rule> element for Rule " + rule.getName());
			}
		    
		}
		if (StringUtil.isNotEmpty(ruleSetReferenceId.getRuleName()) || rule.getPriority().compareTo(minimumPriority) <= 0) {
			ruleSet.addRule(rule);
			}
    }

    private static boolean hasAttributeSetTrue(Element element, String attributeId) {
    	return element.hasAttribute(attributeId) && "true".equalsIgnoreCase(element.getAttribute(attributeId));
    }
    
    
    private void parseRuleReferenceNode(RuleSetReferenceId ruleSetReferenceId, RuleSet ruleSet, Node ruleNode, String ref) throws RuleSetNotFoundException {
		Element ruleElement = (Element) ruleNode;
	
		
		if (StringUtil.isNotEmpty(ruleSetReferenceId.getRuleName())
			&& !isRuleName(ruleElement, ruleSetReferenceId.getRuleName())) {
		    return;
		}
	
		RuleSetFactory ruleSetFactory = new RuleSetFactory();
		ruleSetFactory.setClassLoader(this.classLoader);
	
		RuleSetReferenceId otherRuleSetReferenceId = RuleSetReferenceId.parse(ref).get(0);
		if (!otherRuleSetReferenceId.isExternal()) {
		    otherRuleSetReferenceId = new RuleSetReferenceId(ref, ruleSetReferenceId);
		}
		Rule referencedRule = ruleSetFactory.createRule(otherRuleSetReferenceId);
		if (referencedRule == null) {
		    throw new IllegalArgumentException("Unable to find referenced rule "
			    + otherRuleSetReferenceId.getRuleName() + "; perhaps the rule name is mispelled?");
		}
	
		if (warnDeprecated && referencedRule.isDeprecated()) {
		    if (referencedRule instanceof RuleReference) {
			RuleReference ruleReference = (RuleReference) referencedRule;
			LOG.warning("Use Rule name " + ruleReference.getRuleSetReference().getRuleSetFileName() + "/"
				+ ruleReference.getName() + " instead of the deprecated Rule name " + otherRuleSetReferenceId
				+ ". Future versions of PMD will remove support for this deprecated Rule name usage.");
		    } else if (referencedRule instanceof MockRule) {
			LOG.warning("Discontinue using Rule name " + otherRuleSetReferenceId
				+ " as it has been removed from PMD and no longer functions."
				+ " Future versions of PMD will remove support for this Rule.");
		    } else {
			LOG.warning("Discontinue using Rule name " + otherRuleSetReferenceId
				+ " as it is scheduled for removal from PMD."
				+ " Future versions of PMD will remove support for this Rule.");
		    }
		}
	
		RuleSetReference ruleSetReference = new RuleSetReference();
		ruleSetReference.setAllRules(false);
		ruleSetReference.setRuleSetFileName(otherRuleSetReferenceId.getRuleSetFileName());
	
		RuleReference ruleReference = new RuleReference();
		ruleReference.setRuleSetReference(ruleSetReference);
		ruleReference.setRule(referencedRule);
	
		if (ruleElement.hasAttribute("deprecated")) {
		    ruleReference.setDeprecated(Boolean.parseBoolean(ruleElement.getAttribute("deprecated")));
		}
		if (ruleElement.hasAttribute("name")) {
		    ruleReference.setName(ruleElement.getAttribute("name"));
		}
		if (ruleElement.hasAttribute("message")) {
		    ruleReference.setMessage(ruleElement.getAttribute("message"));
		}
		if (ruleElement.hasAttribute("externalInfoUrl")) {
		    ruleReference.setExternalInfoUrl(ruleElement.getAttribute("externalInfoUrl"));
		}
		for (int i = 0; i < ruleElement.getChildNodes().getLength(); i++) {
		    Node node = ruleElement.getChildNodes().item(i);
		    if (node.getNodeType() == Node.ELEMENT_NODE) {
			if (node.getNodeName().equals("description")) {
			    ruleReference.setDescription(parseTextNode(node));
			} else if (node.getNodeName().equals("example")) {
			    ruleReference.addExample(parseTextNode(node));
			} else if (node.getNodeName().equals("priority")) {
			    ruleReference.setPriority(RulePriority.valueOf(Integer.parseInt(parseTextNode(node))));
			} else if (node.getNodeName().equals("properties")) {
			    parsePropertiesNode(ruleReference, node);
			} else {
			    throw new IllegalArgumentException("Unexpected element <" + node.getNodeName()
				    + "> encountered as child of <rule> element for Rule " + ruleReference.getName());
			}
		    }
		}
	
		if (StringUtil.isNotEmpty(ruleSetReferenceId.getRuleName())
			|| referencedRule.getPriority().compareTo(minimumPriority) <= 0) {
		    ruleSet.addRule(ruleReference);
		}
    }

    private static boolean isElementNode(Node node, String name) {
    	return node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(name);
    }
    
    private static void parsePropertiesNode(Rule rule, Node propertiesNode) {
		for (int i = 0; i < propertiesNode.getChildNodes().getLength(); i++) {
		    Node node = propertiesNode.getChildNodes().item(i);
		    if (isElementNode(node, "property")) {
		    	parsePropertyNodeBR(rule, node);
		    }
		}
    }
    
    private static String valueFrom(Node parentNode) {
    	
    	final NodeList nodeList = parentNode.getChildNodes();
    	
	    for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (isElementNode(node, "value")) {
			    return parseTextNode(node);
				}
	    	}
	    return null;
    }
    
    
    @SuppressWarnings("unchecked")
	    private static void parsePropertyNode(Rule rule, Node propertyNode) {
		Element propertyElement = (Element) propertyNode;
		String name = propertyElement.getAttribute("name");
		String description = propertyElement.getAttribute("description");
		String type = propertyElement.getAttribute("type");
		String delimiter = propertyElement.getAttribute("delimiter");
		String min = propertyElement.getAttribute("min");
		String max = propertyElement.getAttribute("max");
		String value = propertyElement.getAttribute("value");
	
		
		if (StringUtil.isEmpty(value)) {
		    for (int i = 0; i < propertyNode.getChildNodes().getLength(); i++) {
			Node node = propertyNode.getChildNodes().item(i);
			if ((node.getNodeType() == Node.ELEMENT_NODE) && node.getNodeName().equals("value")) {
			    value = parseTextNode(node);
			}
		    }
		}
	
		
		if (StringUtil.isEmpty(type)) {
		    PropertyDescriptor propertyDescriptor = rule.getPropertyDescriptor(name);
		    if (propertyDescriptor == null) {
			throw new IllegalArgumentException("Cannot set non-existant property '" + name + "' on Rule " + rule.getName());
		    } else {
			Object realValue = propertyDescriptor.valueFrom(value);
			rule.setProperty(propertyDescriptor, realValue);
		    }
		} else {
		    PropertyDescriptor propertyDescriptor = PropertyDescriptorFactory.createPropertyDescriptor(name,  description, type, delimiter, min, max, value);
		    rule.definePropertyDescriptor(propertyDescriptor);
		}
	}
    
    private static void setValue(Rule rule, PropertyDescriptor desc, String strValue) {
    	Object realValue = desc.valueFrom(strValue);
		rule.setProperty(desc, realValue);
    }
    
    @SuppressWarnings("unchecked")
    private static void parsePropertyNodeBR(Rule rule, Node propertyNode) {
    	
    	Element propertyElement = (Element) propertyNode;
    	String typeId = propertyElement.getAttribute(PropertyDescriptorFields.typeKey);
    	String strValue = propertyElement.getAttribute(PropertyDescriptorFields.valueKey);
    	if (StringUtil.isEmpty(strValue)) strValue = valueFrom(propertyElement);
    	
    	
    	if (StringUtil.isEmpty(typeId)) {
        	String name = propertyElement.getAttribute(PropertyDescriptorFields.nameKey);
        	
    	    PropertyDescriptor propertyDescriptor = rule.getPropertyDescriptor(name);
    	    if (propertyDescriptor == null) {
    	    	throw new IllegalArgumentException("Cannot set non-existant property '" + name + "' on Rule " + rule.getName());
    	    	} else {
    	    		setValue(rule, propertyDescriptor, strValue);
    	    		}
    	    return;
    	}
    	
    	net.sourceforge.pmd.PropertyDescriptorFactory pdFactory = PropertyDescriptorUtil.factoryFor(typeId);
    	if (pdFactory == null) {
    		throw new RuntimeException("No property descriptor factory for type: " + typeId);
    	}
    	    	
    	Map<String, Boolean> valueKeys = pdFactory.expectedFields();
    	Map<String, String> values = new HashMap<String, String>(valueKeys.size());
    	
    	
    	for (Map.Entry<String, Boolean> entry : valueKeys.entrySet()) {
    		String valueStr = propertyElement.getAttribute(entry.getKey());
    		if (entry.getValue() && StringUtil.isEmpty(valueStr)) {
    			System.out.println("Missing required value for: " + entry.getKey());	
    		}
    		values.put(entry.getKey(), valueStr);
    	}
    	try {
	    	PropertyDescriptor desc = pdFactory.createWith(values);
	    	PropertyDescriptorWrapper wrapper = new PropertyDescriptorWrapper(desc);
	    	
	    	rule.definePropertyDescriptor(wrapper);
	    	setValue(rule, desc, strValue);
    		
    	} catch (Exception ex) {
    		System.out.println("oops");		
    	}
    }
    
    
    private static String parseTextNode(Node node) {

		final int nodeCount = node.getChildNodes().getLength();
		if (nodeCount == 0) {
		    return "";
		}
	
		StringBuilder buffer = new StringBuilder();
	
		for (int i = 0; i < nodeCount; i++) {
		    Node childNode = node.getChildNodes().item(i);
		    if (childNode.getNodeType() == Node.CDATA_SECTION_NODE || childNode.getNodeType() == Node.TEXT_NODE) {
			buffer.append(childNode.getNodeValue());
		    }
		}
		return buffer.toString();
    }

    
    private boolean isRuleName(Element ruleElement, String ruleName) {
		if (ruleElement.hasAttribute("name")) {
		    return ruleElement.getAttribute("name").equals(ruleName);
		} else if (ruleElement.hasAttribute("ref")) {
		    RuleSetReferenceId ruleSetReferenceId = RuleSetReferenceId.parse(ruleElement.getAttribute("ref")).get(0);
		    return ruleSetReferenceId.getRuleName() != null && ruleSetReferenceId.getRuleName().equals(ruleName);
		} else {
		    return false;
		}
    }
}
