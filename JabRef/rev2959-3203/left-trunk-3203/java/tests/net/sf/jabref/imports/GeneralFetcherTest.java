package tests.net.sf.jabref.imports;

import javax.swing.JButton;
import javax.swing.JTextField;

import tests.net.sf.jabref.TestUtils;

import net.sf.jabref.JabRef;
import net.sf.jabref.JabRefFrame;
import net.sf.jabref.SidePaneManager;
import net.sf.jabref.imports.ACMPortalFetcher;
import net.sf.jabref.imports.GeneralFetcher;
import junit.framework.TestCase;


public class GeneralFetcherTest extends TestCase {
	static JabRefFrame jrf;
	static SidePaneManager spm;
	static GeneralFetcher gf;
	static ACMPortalFetcher acmpf;

	
	public void testResetButton() throws Exception {
		String testString = "test string";
		JTextField tf = (JTextField) TestUtils.getChildNamed(gf, "tf");
		assertNotNull(tf); 
		tf.setText(testString);
		tf.postActionEvent(); 
		assertEquals(testString, tf.getText());
		JButton reset = (JButton) TestUtils.getChildNamed(gf, "reset");
		assertNotNull(reset); 
		reset.doClick(); 
		assertEquals("", tf.getText());
	}

	
	public void setUp() {
		JabRef.main(new String[0]);
		jrf = JabRef.singleton.jrf;
		spm = jrf.sidePaneManager;
		acmpf = new ACMPortalFetcher();
		gf = new GeneralFetcher(spm, jrf, acmpf);
	}

	public void tearDown() {
		gf = null;
		acmpf = null;
		spm = null;
		jrf = null;
	}

}
