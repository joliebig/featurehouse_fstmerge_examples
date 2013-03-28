

package genj.gedcom;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class GedcomConstructorsTest extends TestCase { 
  
  private final static Set<Class<? extends Property>> exceptions = new HashSet<Class<? extends Property>>();
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    exceptions.add(PropertyForeignXRef.class);
  }
  
  public void testConstructors() {
    
    Pattern p = Pattern.compile("(\\w*).class");

    String type = null;
    File fs = new File("build/classes/core/genj/gedcom");
    for (File f : fs.listFiles()) {
      Matcher m = p.matcher(f.getName());
      if (m.matches()) try {
        type = "genj.gedcom."+m.group(1);
        Class<?> c = Class.forName(type);
        
        if (exceptions.contains(c))
          continue;
          
        if (Entity.class.isAssignableFrom(c)) try {
          c.getDeclaredConstructor(String.class, String.class);
          continue;
        } catch (NoSuchMethodException e) {
          fail("entity "+type+" without tag constructor");
        }

        if (Property.class.isAssignableFrom(c)) try {
          c.getDeclaredConstructor(String.class);
        } catch (NoSuchMethodException e) {
          fail("property "+type+" without tag constructor");
        }
        
      } catch (ClassNotFoundException e) {
        
      }
    }
    
  }
}
