package tests.net.sf.jabref.imports;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexEntryType;
import net.sf.jabref.Globals;
import net.sf.jabref.JabRefPreferences;
import net.sf.jabref.imports.IsiImporter;


public class IsiImporterTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();

		if (Globals.prefs == null) {
			Globals.prefs = JabRefPreferences.getInstance();
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testIsRecognizedFormat() throws IOException {

        IsiImporter importer = new IsiImporter();
		assertTrue(importer.isRecognizedFormat(IsiImporterTest.class
			.getResourceAsStream("IsiImporterTest1.isi")));

		assertTrue(importer.isRecognizedFormat(IsiImporterTest.class
			.getResourceAsStream("IsiImporterTestINSPEC.isi")));

		assertTrue(importer.isRecognizedFormat(IsiImporterTest.class
			.getResourceAsStream("IsiImporterTestWOS.isi")));

		assertTrue(importer.isRecognizedFormat(IsiImporterTest.class
			.getResourceAsStream("IsiImporterTestMedline.isi")));
	}

	public void testProcessSubSup() {

		HashMap hm = new HashMap();
		hm.put("title", "/sub 3/");
		IsiImporter.processSubSup(hm);
		assertEquals("$_3$", hm.get("title"));

		hm.put("title", "/sub   3   /");
		IsiImporter.processSubSup(hm);
		assertEquals("$_3$", hm.get("title"));

		hm.put("title", "/sub 31/");
		IsiImporter.processSubSup(hm);
		assertEquals("$_{31}$", hm.get("title"));

		hm.put("abstract", "/sub 3/");
		IsiImporter.processSubSup(hm);
		assertEquals("$_3$", hm.get("abstract"));

		hm.put("review", "/sub 31/");
		IsiImporter.processSubSup(hm);
		assertEquals("$_{31}$", hm.get("review"));

		hm.put("title", "/sup 3/");
		IsiImporter.processSubSup(hm);
		assertEquals("$^3$", hm.get("title"));

		hm.put("title", "/sup 31/");
		IsiImporter.processSubSup(hm);
		assertEquals("$^{31}$", hm.get("title"));

		hm.put("abstract", "/sup 3/");
		IsiImporter.processSubSup(hm);
		assertEquals("$^3$", hm.get("abstract"));

		hm.put("review", "/sup 31/");
		IsiImporter.processSubSup(hm);
		assertEquals("$^{31}$", hm.get("review"));

		hm.put("title", "/sub $Hello/");
		IsiImporter.processSubSup(hm);
		assertEquals("$_{\\$Hello}$", hm.get("title"));
	}

	public void testImportEntries() throws IOException {
		IsiImporter importer = new IsiImporter();

		List entries = importer.importEntries(IsiImporterTest.class
			.getResourceAsStream("IsiImporterTest1.isi"));
		assertEquals(1, entries.size());
		BibtexEntry entry = (BibtexEntry) entries.get(0);
		assertEquals("Optical properties of MgO doped LiNbO$_3$ single crystals", entry
			.getField("title"));
		assertEquals(
			"James Brown and James Marc Brown and Brown, J. M. and Brown, J. and Brown, J. M. and Brown, J.",
			entry.getField("author"));

		assertEquals(BibtexEntryType.ARTICLE, entry.getType());
		assertEquals("Optical Materials", entry.getField("journal"));
		assertEquals("2006", entry.getField("year"));
		assertEquals("28", entry.getField("volume"));
		assertEquals("5", entry.getField("number"));
		assertEquals("467--72", entry.getField("pages"));

		
	}

	public void testImportEntriesINSPEC() throws IOException {
		IsiImporter importer = new IsiImporter();

		List entries = importer.importEntries(IsiImporterTest.class
			.getResourceAsStream("IsiImporterTestInspec.isi"));

		assertEquals(2, entries.size());
		BibtexEntry a = (BibtexEntry) entries.get(0);
		BibtexEntry b = (BibtexEntry) entries.get(1);

		if (a.getField("title").equals(
			"Optical and photoelectric spectroscopy of photorefractive Sn$_2$P$_2$S$_6$ crystals")) {
			BibtexEntry tmp = a;
			a = b;
			b = tmp;
		}

		
		assertEquals(
			"Second harmonic generation of continuous wave ultraviolet light and production of beta -BaB$_2$O$_4$ optical waveguides",
			a.getField("title"));
		assertEquals(BibtexEntryType.ARTICLE, a.getType());

		assertEquals("Degl'Innocenti, R. and Guarino, A. and Poberaj, G. and Gunter, P.", a
			.getField("author"));
		assertEquals("Applied Physics Letters", a.getField("journal"));
		assertEquals("2006", a.getField("year"));
		assertEquals("#jul#", a.getField("month"));
		assertEquals("89", a.getField("volume"));
		assertEquals("4", a.getField("number"));

		

		
		
		
		

		
		assertEquals("We report on the generation of continuous-wave (cw) ultraviolet"
			+ " (UV) laser light at lambda =278 nm by optical frequency doubling of"
			+ " visible light in beta -BaB$_2$O$_4$ waveguides. Ridge-type "
			+ "waveguides were produced by He$^+$ implantation, photolithography"
			+ " masking, and plasma etching. The final waveguides have core dimension"
			+ " of a few mu m$^2$ and show transmission losses of 5 dB/cm at 532 nm "
			+ "and less than 10 dB/cm at 266 nm. In our first experiments, a second "
			+ "harmonic power of 24 mu W has been generated at 278 nm in an 8 mm long "
			+ "waveguide pumped by 153 mW at 556 nm.".replaceFirst("266", "\n"), a.getField(
			"abstract").toString());
		
		assertEquals("Aip", a.getField("publisher"));
		
		
		
		
		
		
		

		
		assertEquals(
			"Optical and photoelectric spectroscopy of photorefractive Sn$_2$P$_2$S$_6$ crystals",
			b.getField("title").toString());
		assertEquals(BibtexEntryType.ARTICLE, b.getType());
	}

	public void testImportEntriesWOS() throws IOException {
		IsiImporter importer = new IsiImporter();

		List entries = importer.importEntries(IsiImporterTest.class
			.getResourceAsStream("IsiImporterTestWOS.isi"));

		assertEquals(2, entries.size());
		BibtexEntry a = (BibtexEntry) entries.get(0);
		BibtexEntry b = (BibtexEntry) entries.get(1);

		if (a.getField("title").equals(
			"Optical waveguides in Sn2P2S6 by low fluence MeV He+ ion implantation")) {
			BibtexEntry tmp = a;
			a = b;
			b = tmp;
		}

		assertEquals("Optical and photoelectric spectroscopy of photorefractive Sn2P2S6 crystals",
			a.getField("title"));
		assertEquals("Optical waveguides in Sn2P2S6 by low fluence MeV He+ ion implantation", b
			.getField("title"));

		assertEquals("Journal Of Physics-Condensed Matter", a.getField("journal"));
	}

	public void testIsiAuthorsConvert() {
		assertEquals(
			"James Brown and James Marc Brown and Brown, J. M. and Brown, J. and Brown, J. M. and Brown, J.",
			IsiImporter
				.isiAuthorsConvert("James Brown and James Marc Brown and Brown, J.M. and Brown, J. and Brown, J.M. and Brown, J."));

		assertEquals(
			"Joffe, Hadine and Hall, Janet E. and Gruber, Staci and Sarmiento, Ingrid A. and Cohen, Lee S. and Yurgelun-Todd, Deborah and Martin, Kathryn A.",
			IsiImporter
				.isiAuthorsConvert("Joffe, Hadine; Hall, Janet E; Gruber, Staci; Sarmiento, Ingrid A; Cohen, Lee S; Yurgelun-Todd, Deborah; Martin, Kathryn A"));

	}

	public void testMonthConvert(){
		
		assertEquals("#jun#", IsiImporter.parseMonth("06"));
		assertEquals("#jun#", IsiImporter.parseMonth("JUN"));
		assertEquals("#jun#", IsiImporter.parseMonth("jUn"));
		assertEquals("#may#", IsiImporter.parseMonth("MAY-JUN"));
		assertEquals("#jun#", IsiImporter.parseMonth("2006 06"));
		assertEquals("#jun#", IsiImporter.parseMonth("2006 06-07"));
		assertEquals("#jul#", IsiImporter.parseMonth("2006 07 03"));
		assertEquals("#may#", IsiImporter.parseMonth("2006 May-Jun"));
	}
	
	public void testIsiAuthorConvert() {
		assertEquals("James Brown", IsiImporter.isiAuthorConvert("James Brown"));
		assertEquals("James Marc Brown", IsiImporter.isiAuthorConvert("James Marc Brown"));
		assertEquals("Brown, J. M.", IsiImporter.isiAuthorConvert("Brown, J.M."));
		assertEquals("Brown, J.", IsiImporter.isiAuthorConvert("Brown, J."));
		assertEquals("Brown, J. M.", IsiImporter.isiAuthorConvert("Brown, JM"));
		assertEquals("Brown, J.", IsiImporter.isiAuthorConvert("Brown, J"));
		assertEquals("Brown, James", IsiImporter.isiAuthorConvert("Brown, James"));
		assertEquals("Hall, Janet E.", IsiImporter.isiAuthorConvert("Hall, Janet E"));
		assertEquals("", IsiImporter.isiAuthorConvert(""));
	}

	public void testGetExtensions() {
		
	}

	public void testGetIsCustomImporter() {
		IsiImporter importer = new IsiImporter();
		assertEquals(false, importer.getIsCustomImporter());
	}

	public void testImportIEEEExport() throws IOException {
		IsiImporter importer = new IsiImporter();

		List entries = importer.importEntries(IsiImporterTest.class
			.getResourceAsStream("IEEEImport1.txt"));

		assertEquals(1, entries.size());
		BibtexEntry a = (BibtexEntry) entries.get(0);
		
		assertEquals(a.getType().getName(), BibtexEntryType.ARTICLE, a.getType());
		assertEquals("Geoscience and Remote Sensing Letters, IEEE", a.getField("journal"));
		assertEquals(
			"Improving Urban Road Extraction in High-Resolution " +
			"Images Exploiting Directional Filtering, Perceptual " +
			"Grouping, and Simple Topological Concepts",
			a.getField("title"));

		assertEquals("4", a.getField("volume"));
		assertEquals("3", a.getField("number"));
		
		assertEquals("1545-598X", a.getField("SN"));  

		assertEquals("387--391", a.getField("pages"));

		assertEquals("Gamba, P. and Dell'Acqua, F. and Lisini, G.", (String) a.getField("author"));

		assertEquals("2006", a.getField("year"));

		assertEquals("Perceptual grouping, street extraction, urban remote sensing", (String)a.getField("keywords"));

		assertEquals("In this letter, the problem of detecting urban road " +
				"networks from high-resolution optical/synthetic aperture " +
				"radar (SAR) images is addressed. To this end, this letter " +
				"exploits a priori knowledge about road direction " +
				"distribution in urban areas. In particular, this letter " +
				"presents an adaptive filtering procedure able to capture the " +
				"predominant directions of these roads and enhance the " +
				"extraction results. After road element extraction, to both " +
				"discard redundant segments and avoid gaps, a special " +
				"perceptual grouping algorithm is devised, exploiting " +
				"colinearity as well as proximity concepts. Finally, the road " +
				"network topology is considered, checking for road " +
				"intersections and regularizing the overall patterns using " +
				"these focal points. The proposed procedure was tested on a " +
				"pair of very high resolution images, one from an optical " +
				"sensor and one from a SAR sensor. The experiments show an " +
				"increase in both the completeness and the quality indexes " +
				"for the extracted road network.", (String)a.getField("abstract"));
		
	}
	
	public void testImportEntriesMedline() throws IOException {
		IsiImporter importer = new IsiImporter();

		List entries = importer.importEntries(IsiImporterTest.class
			.getResourceAsStream("IsiImporterTestMedline.isi"));

		assertEquals(2, entries.size());
		BibtexEntry a = (BibtexEntry) entries.get(0);
		BibtexEntry b = (BibtexEntry) entries.get(1);

		if (((String) a.getField("title")).startsWith("Estrogen")) {
			BibtexEntry tmp = a;
			a = b;
			b = tmp;
		}

		
		assertEquals(
			"Effects of modafinil on cognitive performance and alertness during sleep deprivation.",
			a.getField("title"));

		assertEquals("Wesensten, Nancy J.", (String) a.getField("author"));
		assertEquals("Curr Pharm Des", a.getField("journal"));
		assertEquals("2006", a.getField("year"));
		assertEquals(null, a.getField("month"));
		assertEquals("12", a.getField("volume"));
		assertEquals("20", a.getField("number"));
		assertEquals("2457--71", a.getField("pages"));
		assertEquals(BibtexEntryType.ARTICLE, a.getType());

		
		assertEquals(
			"Estrogen therapy selectively enhances prefrontal cognitive processes: a randomized, double-blind, placebo-controlled study with functional magnetic resonance imaging in perimenopausal and recently postmenopausal women.",
			b.getField("title").toString());
		assertEquals(
			"Joffe, Hadine and Hall, Janet E. and Gruber, Staci and Sarmiento, Ingrid A. and Cohen, Lee S. and Yurgelun-Todd, Deborah and Martin, Kathryn A.",
			(String) b.getField("author"));
		assertEquals("2006", b.getField("year"));
		assertEquals("#may#", b.getField("month"));
		assertEquals("13", b.getField("volume"));
		assertEquals("3", b.getField("number"));
		assertEquals("411--22", b.getField("pages"));
		assertEquals(BibtexEntryType.ARTICLE, b.getType());
	}
}
