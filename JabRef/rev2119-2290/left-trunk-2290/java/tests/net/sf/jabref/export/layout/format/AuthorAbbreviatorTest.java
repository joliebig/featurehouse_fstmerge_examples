
package tests.net.sf.jabref.export.layout.format;

import junit.framework.TestCase;
import net.sf.jabref.export.layout.LayoutFormatter;
import net.sf.jabref.export.layout.format.AuthorAbbreviator;
import net.sf.jabref.export.layout.format.AuthorLastFirstAbbreviator;


public class AuthorAbbreviatorTest extends TestCase {

	public void testFormat() {

		LayoutFormatter a = new AuthorLastFirstAbbreviator();
		LayoutFormatter b = new AuthorAbbreviator();

		assertEquals(b.format(""), a.format(""));
		assertEquals(b.format("Someone, Van Something"), a.format("Someone, Van Something"));
		assertEquals(b.format("Smith, John"), a.format("Smith, John"));
		assertEquals(b.format("von Neumann, John and Smith, John and Black Brown, Peter"), a
			.format("von Neumann, John and Smith, John and Black Brown, Peter"));

	}

}
