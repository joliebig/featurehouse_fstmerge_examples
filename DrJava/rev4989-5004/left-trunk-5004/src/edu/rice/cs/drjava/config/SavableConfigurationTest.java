

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;


public class SavableConfigurationTest extends DrJavaTestCase {
  
  SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
  
  ByteArrayOutputStream outputBytes = null;
  
  public void setUp() throws Exception {
    super.setUp();
    outputBytes = new ByteArrayOutputStream();
  }
  
  
  public void testEmptyConfiguration() throws IOException {  
    SavableConfiguration emptyConfig = new SavableConfiguration(new DefaultOptionMap());
    
    emptyConfig.saveConfiguration(outputBytes, "header");
    
    String outputString = outputBytes.toString();
    String[] lines = outputString.split("\n");
    
    assertTrue("Data exists", outputString.length() > 0);
    assertEquals("Number of lines", 2, lines.length);
    assertEquals("Starts with \"#header\"", "#header", lines[0]);
    try {
      
      String bareDate = lines[1].substring(1, lines[1].length());
      Date readInDate = dateFormat.parse(bareDate);
      assertTrue("Embedded date less than now",
                 readInDate.compareTo(new Date()) <= 0);
    }
    catch (ParseException pe) {
      fail("Could not parse second line into a date."); 
    }
  }
  
  
  public void testNonEmptyConfiguration() throws IOException {
    DefaultOptionMap optionsMap = new DefaultOptionMap();
    optionsMap.setOption(new BooleanOption("tests_are_good", false), true);
    optionsMap.setOption(new IntegerOption("meaning_of_life", 0), 42);
    optionsMap.setOption(new StringOption("yay_strings", "hello?"), "goodbye");
    
    SavableConfiguration nonEmptyConfig = new SavableConfiguration(optionsMap);
    
    nonEmptyConfig.saveConfiguration(outputBytes, "header");                 
    
    String outputString = outputBytes.toString();
    String[] lines = outputString.split("\n");

    assertTrue("Data exists", outputString.length() > 0);
    assertEquals("Number of lines", 5, lines.length);
    assertEquals("Starts with \"#header\"", "#header", lines[0]);
    try {
      
      String bareDate = lines[1].substring(1, lines[1].length());
      Date readInDate = dateFormat.parse(bareDate);
      assertTrue("Embedded date less than now",
                 readInDate.compareTo(new Date()) <= 0);
    }
    catch (ParseException pe) {
      fail("Could not parse second line into a date."); 
    }
    assertEquals("BooleanOption", "tests_are_good = true", lines[2]);
    assertEquals("IntegerOption", "meaning_of_life = 42", lines[3]);
    assertEquals("StringOption", "yay_strings = goodbye", lines[4]);
  }
}