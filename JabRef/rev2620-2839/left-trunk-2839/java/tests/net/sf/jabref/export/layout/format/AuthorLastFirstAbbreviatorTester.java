package tests.net.sf.jabref.export.layout.format;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.jabref.export.layout.LayoutFormatter;
import net.sf.jabref.export.layout.format.AuthorLastFirstAbbreviator;


public class AuthorLastFirstAbbreviatorTester extends TestCase {

	
	public void testOneAuthorSimpleName() {
		String name = "Lastname, Name";
		
		AuthorLastFirstAbbreviator ab = new AuthorLastFirstAbbreviator();
		
		String result = ab.format(name);
		
		
		String expectedResult = "Lastname, N.";

		
		Assert.assertEquals("Abbreviator Test", result, expectedResult);
	}

	
	public void testOneAuthorCommonName() {
		String name = "Lastname, Name Middlename";
		
		AuthorLastFirstAbbreviator ab = new AuthorLastFirstAbbreviator();
		
		String result = ab.format(name);
		
		
		String expectedResult = "Lastname, N. M.";
		
		
		Assert.assertEquals("Abbreviator Test", result, expectedResult);
	}

	
	public void testTwoAuthorsCommonName() {
		String name = "Lastname, Name Middlename and Sobrenome, Nome Nomedomeio";
		
		AuthorLastFirstAbbreviator ab = new AuthorLastFirstAbbreviator();
		
		String result = ab.format(name);
		
		
		String expectedResult = "Lastname, N. M. and Sobrenome, N. N.";
		
		
		Assert.assertEquals("Abbreviator Test", result, expectedResult);
	}


	
	public void testJrAuthor(){
		String name = "Other, Jr., Anthony N.";
		assertEquals("Other, A. N.", abbreviate(name));
	}

	public void testFormat() {

		LayoutFormatter a = new AuthorLastFirstAbbreviator();
		
		assertEquals("", a.format(""));
		assertEquals("Someone, V. S.", a.format("Someone, Van Something"));
		assertEquals("Smith, J.", a.format("Smith, John"));
		assertEquals("von Neumann, J. and Smith, J. and Black Brown, P.",
				a.format("von Neumann, John and Smith, John and Black Brown, Peter"));
		
	}
	
	protected String abbreviate(String name) {
		return (new AuthorLastFirstAbbreviator()).format(name);
	}
	
}
