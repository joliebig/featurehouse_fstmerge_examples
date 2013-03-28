
package genj.util;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;


public class OriginTest extends TestCase {

  
  @SuppressWarnings("deprecation")
  public void testRelative() throws Throwable {
    
    String dir = new File("./gedcom").getCanonicalPath();
    Origin origin = Origin.create(new File(dir, "example.ged").toURL());

    assertEquals(null, origin.calcRelativeLocation("foo.jpg"));
    assertEquals("foo.jpg", origin.calcRelativeLocation(new File(dir, "foo.jpg").toString()));
    assertEquals("foo.jpg", origin.calcRelativeLocation(new File(dir, "../gedcom/foo.jpg").toString()));
    assertEquals("foo.jpg", origin.calcRelativeLocation(new File(dir, "./foo.jpg").toString()));
    assertEquals(null, origin.calcRelativeLocation("/foo.jpg"));
    
    
    assertEquals("question marks are no good", origin.calcRelativeLocation(new File(dir, "question marks are no good").toString()));
    assertEquals(null, origin.calcRelativeLocation(new File(dir, "right?").toString()));
    assertEquals(null, origin.calcRelativeLocation("right?"));
    
    
    origin = Origin.create(new URL("file:/foo/bar/example.ged"));
    assertEquals("foo.jpg", origin.calcRelativeLocation("/foo/bar/foo.jpg"));
    assertEquals(null, origin.calcRelativeLocation("/foo.jpg"));

    
    origin = Origin.create(new URL("file://foo/bar/example.ged"));
    assertEquals("foo.jpg", origin.calcRelativeLocation("/foo/bar/foo.jpg"));
    assertEquals(null, origin.calcRelativeLocation("/foo.jpg"));
    
    
    origin = Origin.create(new URL("file:foo/bar/example.ged"));
    assertEquals("foo.jpg", origin.calcRelativeLocation(System.getProperty("user.dir")+"/foo/bar/foo.jpg"));
    assertEquals(null, origin.calcRelativeLocation("/foo.jpg"));
  }
  
}
