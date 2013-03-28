
package tests.net.sf.jabref.export.layout.format;

import junit.framework.TestCase;
import net.sf.jabref.export.layout.LayoutFormatter;
import net.sf.jabref.export.layout.format.AuthorLastFirstAbbrCommas;


public class AuthorLastFirstAbbrCommasTest extends TestCase {

	
	public void testFormat() {
			LayoutFormatter a = new AuthorLastFirstAbbrCommas();

			
			assertEquals("", a.format(""));

			
			assertEquals("Someone, V. S.", a.format("Van Something Someone"));

			
			assertEquals("von Neumann, J. and Black Brown, P.", a
				.format("John von Neumann and Black Brown, Peter"));

			
			assertEquals("von Neumann, J., Smith, J. and Black Brown, P.", a
				.format("von Neumann, John and Smith, John and Black Brown, Peter"));

			assertEquals("von Neumann, J., Smith, J. and Black Brown, P.", a
				.format("John von Neumann and John Smith and Black Brown, Peter"));
		
	}

}
