package tests.net.sf.jabref.export.layout;

import junit.framework.TestCase;
import net.sf.jabref.export.layout.LayoutFormatter;
import net.sf.jabref.export.layout.format.RTFChars;

public class RTFCharsTest extends TestCase {

	public void testBasicFormat() {

		LayoutFormatter layout = new RTFChars();

		assertEquals("", layout.format(""));

		assertEquals("hallo", layout.format("hallo"));

		
		assertEquals("R\\ulexions sur le timing de la quantit\\u", layout.format("Réflexions sur le timing de la quantité"));

		assertEquals("h\\ulo", layout.format("h\\'allo"));
		assertEquals("h\\ulo", layout.format("h\\'allo"));
	}

	public void testLaTeXHighlighting(){
		
		LayoutFormatter layout = new RTFChars();
		
		assertEquals("{\\i hallo}", layout.format("\\emph{hallo}"));
		assertEquals("{\\i hallo}", layout.format("{\\emph hallo}"));

		assertEquals("{\\i hallo}", layout.format("\\textit{hallo}"));
		assertEquals("{\\i hallo}", layout.format("{\\textit hallo}"));

		assertEquals("{\\b hallo}", layout.format("\\textbf{hallo}"));
		assertEquals("{\\b hallo}", layout.format("{\\textbf hallo}"));
	}
	
	public void testComplicated() {
		LayoutFormatter layout = new RTFChars();

		assertEquals("R\\ulexions sur le timing de la quantit\\u \\u should be \\u", layout.format("Réflexions sur le timing de la quantité \\ae should be æ"));

		assertEquals("h\\ul{\\u\\u}", layout.format("h\\'all\\oe "));
	}

	public void testSpecialCharacters() {

		LayoutFormatter layout = new RTFChars();

		assertEquals("\\u", layout.format("\\'{o}")); 
		assertEquals("\\'f2", layout.format("\\`{o}")); 
		assertEquals("\\'f4", layout.format("\\^{o}")); 
		assertEquals("\\'f6", layout.format("\\\"{o}")); 
		assertEquals("\\u", layout.format("\\~{o}")); 
		assertEquals("\\u", layout.format("\\={o}"));
		assertEquals("\\u", layout.format("\\u{o}"));
		assertEquals("\\u", layout.format("\\c{c}")); 
		assertEquals("{\\u\\u}", layout.format("\\oe"));
		assertEquals("{\\u\\u}", layout.format("\\OE"));
		assertEquals("{\\u\\u}", layout.format("\\ae")); 
		assertEquals("{\\u\\u}", layout.format("\\AE")); 

		assertEquals("", layout.format("\\.{o}")); 
		assertEquals("", layout.format("\\v{o}")); 
		assertEquals("", layout.format("\\H{a}")); 
		assertEquals("", layout.format("\\t{oo}"));
		assertEquals("", layout.format("\\d{o}")); 
		assertEquals("", layout.format("\\b{o}")); 
		assertEquals("", layout.format("\\aa")); 
		assertEquals("", layout.format("\\AA")); 
		assertEquals("", layout.format("\\o")); 
		assertEquals("", layout.format("\\O")); 
		assertEquals("", layout.format("\\l"));
		assertEquals("", layout.format("\\L"));
		assertEquals("", layout.format("\\ss")); 
		assertEquals("", layout.format("?`")); 
		assertEquals("", layout.format("!`")); 

		assertEquals("", layout.format("\\dag"));
		assertEquals("", layout.format("\\ddag"));
		assertEquals("", layout.format("\\S")); 
		assertEquals("", layout.format("\\P")); 
		assertEquals("", layout.format("\\copyright")); 
		assertEquals("", layout.format("\\pounds")); 
	}
}
