package tests.net.sf.jabref.bst;

import net.sf.jabref.bst.BibtexWidth;
import net.sf.jabref.bst.Warn;
import junit.framework.TestCase;


public class BibtexWidthTest extends TestCase {

	public void assertBibtexWidth(final int i, final String string) {
		assertEquals(i, BibtexWidth.width(string, new Warn() {
			public void warn(String s) {
				fail("Should not Warn! Width should be " + i + " for " + string);
			}
		}));
	}

	public void testWidth() {

		assertBibtexWidth(278, "i");

		assertBibtexWidth(1639, "0I~ "); 

		assertBibtexWidth(2612, "Hi Hi ");

		assertBibtexWidth(778, "{\\oe}");

		assertBibtexWidth(3390, "Hi {\\oe   }Hi ");
		
		assertBibtexWidth(444, "{\\'e}");
		
		assertBibtexWidth(19762, "Ulrich {\\\"{U}}nderwood and Ned {\\~N}et and Paul {\\={P}}ot");

		assertBibtexWidth(7861, "{\\'{E}}douard Masterly");
		
		assertBibtexWidth(30514, "Jonathan Meyer and Charles Louis Xavier Joseph de la Vall{\\'e}e Poussin");
	
	}
	

	public void testGetCharWidth() {
		assertEquals(500, BibtexWidth.getCharWidth('0'));
		assertEquals(361, BibtexWidth.getCharWidth('I'));
		assertEquals(500, BibtexWidth.getCharWidth('~'));
		assertEquals(500, BibtexWidth.getCharWidth('}'));
		assertEquals(278, BibtexWidth.getCharWidth(' '));
	}
}
