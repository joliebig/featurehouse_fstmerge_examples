
package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorFactory;
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
	    StringBuilder allRulesetFilenames = new StringBuilder();
	    for (Language language : Language.findWithRuleSupport()) {
		    Properties props = new Properties();
		    rulesetsProperties = "rulesets/" + language.getTerseName() + "/rulesets.properties";
		    props.load(ResourceLoader.loadResourceAsStream(rulesetsProperties));
		    String rulesetFilenames = props.getProperty("rulesets.filenames");
		    if (allRulesetFilenames.length() > 0) {
			allRulesetFilenames.append(',');
		    }
		    allRulesetFilenames.append(rulesetFilenames);
	    }
	    return createRuleSets(allRulesetFilenames.toString()).getRuleSetsIterator();
	} catch (IOException ioe) {
	    throw new RuntimeException(
		    "Couldn't find " + rulesetsProperties + "; please ensure that the rulesets directory is on the classpath.  Here's the current classpath: "
			    + System.getProperty("java.class.path"));
	}
    }

    
    public synchronized RuleSets createRuleSets(String ruleSetFileNames) throws RuleSetNotFoundException {
	RuleSets ruleSets = new RuleSets();
	for (StringTokenizer st = new StringTokenizer(ruleSetFileNames, ","); st.hasMoreTokens();) {
	    RuleSet ruleSet = createRuleSet(st.nextToken().trim());
	    ruleSets.addRuleSet(ruleSet);
	}
	return ruleSets;
    }

    
    public synchronized RuleSet createRuleSet(String ruleSetFileName) throws RuleSetNotFoundException {
	return parseRuleSetNode(ruleSetFileName, tryToGetStreamTo(ruleSetFileName));
    }

    
    public RuleSet createRuleSet(InputStream inputStream) {
	return parseRuleSetNode(null, inputStream);
    }

    
    private InputStream tryToGetStreamTo(String name) throws RuleSetNotFoundException {
	InputStream in = ResourceLoader.loadResourceAsStream(name, this.classLoader);
	if (in == null) {
	    throw new RuleSetNotFoundException(
		    "Can't find resource "
			    + name
			    + ".  Make sure the resource is a valid file or URL or is on the CLASSPATH.  Here's the current classpath: "
			    + System.getProperty("java.class.path"));
	}
	return in;
    }

    
    private RuleSet parseRuleSetNode(String fileName, InputStream inputStream) {
	try {
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document document = builder.parse(inputStream);
	    Element ruleSetElement = document.getDocumentElement();

	    RuleSet ruleSet = new RuleSet();
	    ruleSet.setFileName(fileName);
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
			parseRuleNode(ruleSet, node);
		    } else {
			throw new IllegalArgumentException("Unexpected element <" + node.getNodeName()
				+ "> encountered as child of <ruleset> element.");
		    }
		}
	    }

	    return ruleSet;
	} catch (ClassNotFoundException cnfe) {
	    cnfe.printStackTrace();
	    throw new RuntimeException("Couldn't find that class " + cnfe.getMessage());
	} catch (InstantiationException ie) {
	    ie.printStackTrace();
	    throw new RuntimeException("Couldn't find that class " + ie.getMessage());
	} catch (IllegalAccessException iae) {
	    iae.printStackTrace();
	    throw new RuntimeException("Couldn't find that class " + iae.getMessage());
	} catch (ParserConfigurationException pce) {
	    pce.printStackTrace();
	    throw new RuntimeException("Couldn't find that class " + pce.getMessage());
	} catch (RuleSetNotFoundException rsnfe) {
	    rsnfe.printStackTrace();
	    throw new RuntimeException("Couldn't find that class " + rsnfe.getMessage());
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    throw new RuntimeException("Couldn't find that class " + ioe.getMessage());
	} catch (SAXException se) {
	    se.printStackTrace();
	    throw new RuntimeException("Couldn't find that class " + se.getMessage());
	}
    }

    
    private void parseRuleNode(RuleSet ruleSet, Node ruleNode) throws ClassNotFoundException, InstantiationException,
	    IllegalAccessException, RuleSetNotFoundException {
	Element ruleElement = (Element) ruleNode;
	String ref = ruleElement.getAttribute("ref");
	if (ref.endsWith("xml")) {
	    parseRuleSetReferenceNode(ruleSet, ruleElement, ref);
	} else if (StringUtil.isEmpty(ref)) {
	    parseSingleRuleNode(ruleSet, ruleNode);
	} else {
	    parseRuleReferenceNode(ruleSet, ruleNode, ref);
	}
    }

    
    private void parseRuleSetReferenceNode(RuleSet ruleSet, Element ruleElement, String ref)
	    throws RuleSetNotFoundException {

	RuleSetReference ruleSetReference = new RuleSetReference();
	ruleSetReference.setAllRules(true);
	ruleSetReference.setRuleSetFileName(ref);
	NodeList excludeNodes = ruleElement.getChildNodes();
	for (int i = 0; i < excludeNodes.getLength(); i++) {
	    if ((excludeNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
		    && (excludeNodes.item(i).getNodeName().equals("exclude"))) {
		Element excludeElement = (Element) excludeNodes.item(i);
		ruleSetReference.addExclude(excludeElement.getAttribute("name"));
	    }
	}

	RuleSetFactory ruleSetFactory = new RuleSetFactory();
	ruleSetFactory.setClassLoader(this.classLoader);
	RuleSet otherRuleSet = ruleSetFactory.createRuleSet(ref);
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

    
    private void parseSingleRuleNode(RuleSet ruleSet, Node ruleNode) throws ClassNotFoundException,
	    InstantiationException, IllegalAccessException {
	Element ruleElement = (Element) ruleNode;

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

	if (ruleElement.hasAttribute("dfa") && ruleElement.getAttribute("dfa").equals("true")) {
	    rule.setUsesDFA();
	}

	if (ruleElement.hasAttribute("typeResolution") && ruleElement.getAttribute("typeResolution").equals("true")) {
	    rule.setUsesTypeResolution();
	}

	for (int i = 0; i < ruleElement.getChildNodes().getLength(); i++) {
	    Node node = ruleElement.getChildNodes().item(i);
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
		if (node.getNodeName().equals("description")) {
		    rule.setDescription(parseTextNode(node));
		} else if (node.getNodeName().equals("example")) {
		    rule.addExample(parseTextNode(node));
		} else if (node.getNodeName().equals("priority")) {
		    rule.setPriority(RulePriority.valueOf(Integer.parseInt(parseTextNode(node).trim())));
		} else if (node.getNodeName().equals("properties")) {
		    parsePropertiesNode(rule, node);
		} else {
		    throw new IllegalArgumentException("Unexpected element <" + node.getNodeName()
			    + "> encountered as child of <rule> element for Rule " + rule.getName());
		}
	    }
	}
	if (rule.getPriority().compareTo(minimumPriority) <= 0) {
	    ruleSet.addRule(rule);
	}
    }

    
    private void parseRuleReferenceNode(RuleSet ruleSet, Node ruleNode, String ref) throws RuleSetNotFoundException {
	RuleSetFactory ruleSetFactory = new RuleSetFactory();
	ruleSetFactory.setClassLoader(this.classLoader);

	ExternalRuleID externalRuleID = new ExternalRuleID(ref);
	RuleSet externalRuleSet = ruleSetFactory.createRuleSet(externalRuleID.getFilename());
	Rule externalRule = externalRuleSet.getRuleByName(externalRuleID.getRuleName());
	if (externalRule == null) {
	    throw new IllegalArgumentException("Unable to find rule " + externalRuleID.getRuleName()
		    + "; perhaps the rule name is mispelled?");
	}

	if (warnDeprecated && externalRule.isDeprecated()) {
	    if (externalRule instanceof RuleReference) {
		RuleReference ruleReference = (RuleReference) externalRule;
		LOG.warning("Use Rule name " + ruleReference.getRuleSetReference().getRuleSetFileName() + "/"
			+ ruleReference.getName() + " instead of the deprecated Rule name " + externalRuleID
			+ ". Future versions of PMD will remove support for this deprecated Rule name usage.");
	    } else if (externalRule instanceof MockRule) {
		LOG.warning("Discontinue using Rule name " + externalRuleID
			+ " as it has been removed from PMD and no longer functions."
			+ " Future versions of PMD will remove support for this Rule.");
	    } else {
		LOG.warning("Discontinue using Rule name " + externalRuleID
			+ " as it is scheduled for removal from PMD."
			+ " Future versions of PMD will remove support for this Rule.");
	    }
	}

	RuleSetReference ruleSetReference = new RuleSetReference();
	ruleSetReference.setAllRules(false);
	ruleSetReference.setRuleSetFileName(externalRuleID.getFilename());

	RuleReference ruleReference = new RuleReference();
	ruleReference.setRuleSetReference(ruleSetReference);
	ruleReference.setRule(externalRule);

	Element ruleElement = (Element) ruleNode;
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

	if (externalRule.getPriority().compareTo(minimumPriority) <= 0) {
	    ruleSet.addRule(ruleReference);
	}
    }

    
    private static void parsePropertiesNode(Rule rule, Node propertiesNode) {
	for (int i = 0; i < propertiesNode.getChildNodes().getLength(); i++) {
	    Node node = propertiesNode.getChildNodes().item(i);
	    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("property")) {
		parsePropertyNode(rule, node);
	    }
	}
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
		throw new IllegalArgumentException("Cannot set non-existant property '" + name + "' on Rule "
			+ rule.getName());
	    } else {
		Object realValue = propertyDescriptor.valueFrom(value);
		rule.setProperty(propertyDescriptor, realValue);
	    }
	} else {
	    PropertyDescriptor propertyDescriptor = PropertyDescriptorFactory.createPropertyDescriptor(name,
		    description, type, delimiter, min, max, value);
	    rule.definePropertyDescriptor(propertyDescriptor);
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
}
