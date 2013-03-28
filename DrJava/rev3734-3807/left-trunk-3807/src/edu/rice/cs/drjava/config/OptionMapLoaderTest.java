

package edu.rice.cs.drjava.config;
import junit.framework.TestCase;
import java.io.*;

import edu.rice.cs.drjava.DrJavaTestCase;


public final class OptionMapLoaderTest extends DrJavaTestCase
  implements OptionConstants
{
  
  public OptionMapLoaderTest(String s) {
    super(s);
  }
  
  public static class StringInputStream extends ByteArrayInputStream {
    public StringInputStream(String s) {
      super(s.getBytes());
    }
  }
  
  
  public static final String OPTION_DOC = 
    "# this is a fake header\n"+
    "this.is.a.real.key = value\n"+
    "indent.level = 1\n"+
    "javac.location = foo\n"+
    "extra.classpath = bam\n\n";
  
  public void testProperConfigSet() throws IOException {
    checkSet(OPTION_DOC,new Integer(1), new File("foo"), 1);
  }
  
  private void checkSet(String set, Integer indent, File javac, int size) throws IOException {
    StringInputStream is = new StringInputStream(set);
    OptionMapLoader loader = new OptionMapLoader(is);
    DefaultOptionMap map = new DefaultOptionMap();
    loader.loadInto(map);
    assertEquals("indent (integer) option",  map.getOption(INDENT_LEVEL),indent);
    assertEquals("JAVAC", map.getOption(JAVAC_LOCATION),javac.getAbsoluteFile());
    assertEquals("size of extra-classpath vector", new Integer(size), 
                 new Integer(map.getOption(EXTRA_CLASSPATH).size()));
  }
  
  public void testEmptyConfigSet() throws IOException {
    checkSet("",INDENT_LEVEL.getDefault(), JAVAC_LOCATION.getDefault(),  EXTRA_CLASSPATH.getDefault().size());
  }
}
