

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;
import java.awt.Font;


public final class FontOptionTest extends DrJavaTestCase {

  public void testParse() {
    FontOption fo = new FontOption("font.test1", Font.decode(null));
    assertEquals(new Font("monospaced", 0, 12), fo.parse("monospaced-12"));
    assertEquals(new Font("sansserif", 1, 10), fo.parse("sansserif-BOLD-10"));
    assertEquals(new Font("sansserif", 3, 10), fo.parse("sansserif-BOLDITALIC-10"));

    
    Font f = fo.parse("true");
    assertNotNull(f);
  }

  public void testFormat() {
    FontOption fO1 = new FontOption("font.test2", Font.decode(null));

    assertEquals("monospaced-12",  fO1.format(new Font("monospaced", 0, 12)));
    assertEquals("sansserif-BOLD-10", fO1.format(new Font("sansserif", 1, 10)));
    assertEquals("sansserif-BOLDITALIC-10", fO1.format(new Font("sansserif", 3, 10)));
  }
}
