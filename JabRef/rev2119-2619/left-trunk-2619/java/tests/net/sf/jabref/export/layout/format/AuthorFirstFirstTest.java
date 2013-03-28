
package tests.net.sf.jabref.export.layout.format;

import junit.framework.TestCase;
import net.sf.jabref.export.layout.format.AuthorFirstFirst;


public class AuthorFirstFirstTest extends TestCase {

	
	public void testFormat() {
		assertEquals("John von Neumann and John Smith and Peter Black Brown, Jr",
			new AuthorFirstFirst()
				.format("von Neumann,,John and John Smith and Black Brown, Jr, Peter"));
	}

}
