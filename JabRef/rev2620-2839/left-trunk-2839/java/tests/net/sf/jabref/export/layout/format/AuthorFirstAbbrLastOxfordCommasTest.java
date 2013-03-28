
package tests.net.sf.jabref.export.layout.format;

import junit.framework.TestCase;
import net.sf.jabref.export.layout.LayoutFormatter;
import net.sf.jabref.export.layout.format.AuthorFirstAbbrLastOxfordCommas;


public class AuthorFirstAbbrLastOxfordCommasTest extends TestCase {

	
	public void testFormat() {
		LayoutFormatter a = new AuthorFirstAbbrLastOxfordCommas();

		
		assertEquals("", a.format(""));

		
		assertEquals("V. S. Someone", a.format("Someone, Van Something"));

		
		assertEquals("J. von Neumann and P. Black Brown", a
			.format("John von Neumann and Black Brown, Peter"));

		
		assertEquals("J. von Neumann, J. Smith, and P. Black Brown", a
			.format("von Neumann, John and Smith, John and Black Brown, Peter"));

		assertEquals("J. von Neumann, J. Smith, and P. Black Brown", a
			.format("John von Neumann and John Smith and Black Brown, Peter"));
	}

}
