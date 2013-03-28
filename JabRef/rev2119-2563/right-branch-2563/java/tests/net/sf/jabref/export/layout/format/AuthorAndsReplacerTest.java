
package tests.net.sf.jabref.export.layout.format;

import junit.framework.TestCase;
import net.sf.jabref.export.layout.LayoutFormatter;
import net.sf.jabref.export.layout.format.AuthorAndsReplacer;


public class AuthorAndsReplacerTest extends TestCase {

	
	public void testFormat() {
		LayoutFormatter a = new AuthorAndsReplacer();

		
		assertEquals("", a.format(""));

		
		assertEquals("Someone, Van Something", a.format("Someone, Van Something"));

		
		assertEquals("John Smith & Black Brown, Peter", a
			.format("John Smith and Black Brown, Peter"));

		
		assertEquals("von Neumann, John; Smith, John & Black Brown, Peter", a
			.format("von Neumann, John and Smith, John and Black Brown, Peter"));

		assertEquals("John von Neumann; John Smith & Peter Black Brown", a
			.format("John von Neumann and John Smith and Peter Black Brown"));
	}

}
