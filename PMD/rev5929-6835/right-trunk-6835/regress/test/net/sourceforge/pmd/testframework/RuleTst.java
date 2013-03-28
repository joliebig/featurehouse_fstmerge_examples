
package test.net.sourceforge.pmd.testframework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class RuleTst {
    public static final LanguageVersion DEFAULT_LANGUAGE_VERSION = LanguageVersion.JAVA_15;
    public static final Language DEFAULT_LANGUAGE = DEFAULT_LANGUAGE_VERSION.getLanguage();

    
    public Rule findRule(String ruleSet, String ruleName) {
        try {
            Rule rule = new RuleSetFactory().createRuleSets(ruleSet).getRuleByName(ruleName);
            if (rule == null) {
                fail("Rule " + ruleName + " not found in ruleset " + ruleSet);
            }
            rule.setRuleSetName(ruleSet);
            return rule;
        } catch (RuleSetNotFoundException e) {
            e.printStackTrace();
            fail("Couldn't find ruleset " + ruleSet);
            return null;
        }
    }


    
    @SuppressWarnings("unchecked")
    public void runTest(TestDescriptor test) {
        Rule rule = test.getRule();

        if (test.getReinitializeRule()) {
            rule = findRule(rule.getRuleSetName(), rule.getName());
        }

        Map<PropertyDescriptor<?>, Object> oldProperties = rule.getPropertiesByPropertyDescriptor();
        try {
            int res;
            try {
        	
                if (test.getProperties() != null) {
                    for (Map.Entry<Object, Object> entry : test.getProperties().entrySet()) {
                	String propertyName = (String)entry.getKey();
                	String strValue = (String)entry.getValue();
                	PropertyDescriptor propertyDescriptor = rule.getPropertyDescriptor(propertyName);
                	if (propertyDescriptor == null) {
                            throw new IllegalArgumentException("No such property '" + propertyName + "' on Rule " + rule.getName());
                	}
                	Object value = propertyDescriptor.valueFrom(strValue);
                	rule.setProperty(propertyDescriptor, value);
                    }
                }

                res = processUsingStringReader(test.getCode(), rule, test.getLanguageVersion()).size();
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RuntimeException('"' + test.getDescription() + "\" failed", t);
            }
            assertEquals('"' + test.getDescription() + "\" resulted in wrong number of failures,",
                    test.getNumberOfProblemsExpected(), res);
        } finally {
            
            
            
            for (Map.Entry entry: oldProperties.entrySet()) {
        	rule.setProperty((PropertyDescriptor)entry.getKey(), entry.getValue());
            }
        }
    }

    private Report processUsingStringReader(String code, Rule rule,
   			LanguageVersion languageVersion) throws PMDException {
        Report report = new Report();
        runTestFromString(code, rule, report, languageVersion);
        return report;
    }

    
    public void runTestFromString(String code, Rule rule, Report report, LanguageVersion languageVersion) throws PMDException {
        PMD p = new PMD();
        p.getConfiguration().setDefaultLanguageVersion(languageVersion);
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        ctx.setSourceCodeFilename("n/a");
        ctx.setLanguageVersion(languageVersion);
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        p.processFile(new StringReader(code), new RuleSets(rules), ctx);
    }

    
    protected String getCleanRuleName(Rule rule) {
        String fullClassName = rule.getClass().getName();
        if (fullClassName.equals(rule.getName())) {
            
            String packageName = rule.getClass().getPackage().getName();
            return fullClassName.substring(packageName.length()+1);
        } else {
            return rule.getName();  
        }
    }

    
    public TestDescriptor[] extractTestsFromXml(Rule rule) {
        String testsFileName = getCleanRuleName(rule);

        return extractTestsFromXml(rule, testsFileName);
    }

    public TestDescriptor[] extractTestsFromXml(Rule rule, String testsFileName) {
        return extractTestsFromXml(rule, testsFileName, "xml/");
    }
    
    public TestDescriptor[] extractTestsFromXml(Rule rule, String testsFileName, String baseDirectory) {
        String testXmlFileName = baseDirectory + testsFileName + ".xml";
        InputStream inputStream = getClass().getResourceAsStream(testXmlFileName);
        if (inputStream == null) {
            throw new RuntimeException("Couldn't find " + testXmlFileName);
        }

        Document doc;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(inputStream);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            throw new RuntimeException("Couldn't parse " + testXmlFileName + ", due to: " + pce.getMessage());
        } catch (FactoryConfigurationError fce) {
            fce.printStackTrace();
            throw new RuntimeException("Couldn't parse " + testXmlFileName + ", due to: " + fce.getMessage());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Couldn't parse " + testXmlFileName + ", due to: " + ioe.getMessage());
        } catch (SAXException se) {
            se.printStackTrace();
            throw new RuntimeException("Couldn't parse " + testXmlFileName + ", due to: " + se.getMessage());
        }

        return parseTests(rule, doc);
    }

    private TestDescriptor[] parseTests(Rule rule, Document doc) {
        Element root = doc.getDocumentElement();
        NodeList testCodes = root.getElementsByTagName("test-code");

        TestDescriptor[] tests = new TestDescriptor[testCodes.getLength()];
        for (int i = 0; i < testCodes.getLength(); i++) {
            Element testCode = (Element)testCodes.item(i);

            boolean reinitializeRule = true;
            Node reinitializeRuleAttribute = testCode.getAttributes().getNamedItem("reinitializeRule");
            if (reinitializeRuleAttribute != null) {
                String reinitializeRuleValue = reinitializeRuleAttribute.getNodeValue();
                if ("false".equalsIgnoreCase(reinitializeRuleValue) ||
                        "0".equalsIgnoreCase(reinitializeRuleValue)) {
                    reinitializeRule = false;
                }
            }

            boolean isRegressionTest = true;
            Node regressionTestAttribute = testCode.getAttributes().getNamedItem("regressionTest");
            if (regressionTestAttribute != null) {
                String reinitializeRuleValue = regressionTestAttribute.getNodeValue();
                if ("false".equalsIgnoreCase(reinitializeRuleValue)) {
                    isRegressionTest = false;
                }
            }

            NodeList ruleProperties = testCode.getElementsByTagName("rule-property");
            Properties properties = new Properties();
            for (int j = 0; j < ruleProperties.getLength(); j++) {
                Node ruleProperty = ruleProperties.item(j);
                String propertyName = ruleProperty.getAttributes().getNamedItem("name").getNodeValue();
                properties.setProperty(propertyName, parseTextNode(ruleProperty));
            }
            int expectedProblems = Integer.parseInt(getNodeValue(testCode, "expected-problems", true));
            String description = getNodeValue(testCode, "description", true);
            String code = getNodeValue(testCode, "code", false);
            if (code == null) {
                
                NodeList coderefs = testCode.getElementsByTagName("code-ref");
                if (coderefs.getLength()==0) {
                    throw new RuntimeException("Required tag is missing from the test-xml. Supply either a code or a code-ref tag");
                }
                Node coderef = coderefs.item(0);
                String referenceId = coderef.getAttributes().getNamedItem("id").getNodeValue();
                NodeList codeFragments = root.getElementsByTagName("code-fragment");
                for (int j = 0; j < codeFragments.getLength(); j++) {
                    String fragmentId = codeFragments.item(j).getAttributes().getNamedItem("id").getNodeValue();
                    if (referenceId.equals(fragmentId)) {
                        code = parseTextNode(codeFragments.item(j));
                    }
                }

                if (code==null) {
                    throw new RuntimeException("No matching code fragment found for coderef");
                }
            }

            String languageVersionString = getNodeValue(testCode, "source-type", false);
            if (languageVersionString == null) {
                tests[i] = new TestDescriptor(code, description, expectedProblems, rule);
            } else {
            	 LanguageVersion languageVersion = LanguageVersion.findByTerseName(languageVersionString);
                if (languageVersion != null) {
                    tests[i] = new TestDescriptor(code, description, expectedProblems, rule, languageVersion);
                } else {
                    throw new RuntimeException("Unknown LanguageVersion for test: " + languageVersionString);
                }
            }
            tests[i].setReinitializeRule(reinitializeRule);
            tests[i].setRegressionTest(isRegressionTest);
            tests[i].setProperties(properties);
        }
        return tests;
    }

    private String getNodeValue(Element parentElm, String nodeName, boolean required) {
        NodeList nodes = parentElm.getElementsByTagName(nodeName);
        if (nodes == null || nodes.getLength() == 0) {
            if (required) {
                throw new RuntimeException("Required tag is missing from the test-xml: " + nodeName);
            } else {
                return null;
            }
        }
        Node node = nodes.item(0);
        return parseTextNode(node);
    }

    private static String parseTextNode(Node exampleNode) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < exampleNode.getChildNodes().getLength(); i++) {
            Node node = exampleNode.getChildNodes().item(i);
            if (node.getNodeType() == Node.CDATA_SECTION_NODE
                    || node.getNodeType() == Node.TEXT_NODE) {
                buffer.append(node.getNodeValue());
            }
        }
        return buffer.toString().trim();
    }

    
    public void runTestFromString(String code, Rule rule, Report report) throws PMDException {
        runTestFromString(code, rule, report, DEFAULT_LANGUAGE_VERSION);
    }
}
