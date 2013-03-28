

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.util.ArrayList;


public final class ForcedChoiceOptionTest extends DrJavaTestCase {
  
  public ForcedChoiceOptionTest(String name) { super(name); }
  
  public void testGetName() {
    ForcedChoiceOption fco = new ForcedChoiceOption("javadoc_access",
                                                    "protected",
                                                    null);
    
    assertEquals("javadoc_access", fco.getName());
  }
  
  public void testParse() {
    ArrayList<String> aList = new ArrayList<String>(4);
    
    aList.add("public");
    aList.add("protected");
    aList.add("package");
    aList.add("private");
    ForcedChoiceOption fco = new ForcedChoiceOption("javadoc_access",
                                                    "protected",
                                                    aList);
    
    assertTrue("Parsing \"private\"", "private".equals(fco.parse("private")));
    try { fco.parse("Private"); fail(); }
    catch (OptionParseException e) { }
    
    try { fco.parse("true"); fail(); }
    catch (OptionParseException e) { }
    
    try { fco.parse(".33"); fail(); }
    catch (OptionParseException e) { }
  }
  
  public void testFormat() {
    ForcedChoiceOption fco = new ForcedChoiceOption("javadoc_access",
                                                    "protected",
                                                    null);
    
    assertTrue("Formatting \"private\"", "private".equals(fco.format(new String("private"))));
    assertTrue("Formatting \"public\"", "public".equals(fco.format(new String("public"))));
  }
}
