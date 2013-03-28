
package tests.net.sf.jabref.export.layout.format;

import junit.framework.TestCase;
import net.sf.jabref.export.layout.LayoutFormatter;
import net.sf.jabref.export.layout.format.AuthorFirstLastOxfordCommas;


public class AuthorFirstLastOxfordCommasTest extends TestCase {

	
	public void testFormat() {
		LayoutFormatter a = new AuthorFirstLastOxfordCommas();

		
		assertEquals("", a.format(""));

		
		assertEquals("Van Something Someone", a.format("Someone, Van Something"));

		
		assertEquals("John von Neumann and Peter Black Brown", a
			.format("John von Neumann and Peter Black Brown"));

		
		assertEquals("John von Neumann, John Smith, and Peter Black Brown", a
			.format("von Neumann, John and Smith, John and Black Brown, Peter"));

		assertEquals("John von Neumann, John Smith, and Peter Black Brown", a
			.format("John von Neumann and John Smith and Black Brown, Peter"));
	}


}
