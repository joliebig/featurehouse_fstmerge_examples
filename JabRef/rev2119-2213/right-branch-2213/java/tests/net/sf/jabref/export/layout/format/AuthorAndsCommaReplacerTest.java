
package tests.net.sf.jabref.export.layout.format;

import junit.framework.TestCase;
import net.sf.jabref.export.layout.LayoutFormatter;
import net.sf.jabref.export.layout.format.AuthorAndsCommaReplacer;


public class AuthorAndsCommaReplacerTest extends TestCase {

	
	public void testFormat() {

		LayoutFormatter a = new AuthorAndsCommaReplacer();
		
		
		assertEquals("", a.format(""));

		
		assertEquals("Someone, Van Something", a.format("Someone, Van Something"));
		
		
		assertEquals("John von Neumann & Peter Black Brown",
			a.format("John von Neumann and Peter Black Brown"));
	
		
		assertEquals("von Neumann, John, Smith, John & Black Brown, Peter",
				a.format("von Neumann, John and Smith, John and Black Brown, Peter"));
	}
}
