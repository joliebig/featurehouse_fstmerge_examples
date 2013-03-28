

package koala.dynamicjava.util;

import junit.framework.*;

public abstract class DynamicJavaTestCase extends TestCase {
  
  protected DynamicJavaTestCase() {
    super();
  }
  
  
  protected DynamicJavaTestCase(String name) {
    super(name);
  }
  
  
  protected void setTigerEnabled(boolean b) {
    TigerUtilities.setTigerEnabled(b);
  }
  
}