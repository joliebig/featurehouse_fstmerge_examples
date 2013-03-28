
package test.net.sourceforge.pmd.coverage;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sourceforge.pmd.PMD;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class PMDCoverageTest {

    
    @Test
    public void testPmdOptions() {
	runPmd("src/net/sourceforge/pmd/lang/java/rule/design text rulesets/internal/all-java.xml -targetjdk 1.5 -stress -benchmark");
    }

    
    private void runPmd(String commandLine) {
	String args[];
	args = commandLine.split("\\s");

	File f = null;
	try {
	    f = File.createTempFile("pmd", ".txt");
	    int n = args.length;
	    String a[] = new String[n + 2];
	    System.arraycopy(args, 0, a, 0, n);
	    a[n] = "-reportfile";
	    a[n + 1] = f.getAbsolutePath();
	    args = a;

	    PMD.main(args);

	    
	} catch (IOException ioe) {
	    fail("Problem creating temporary file: " + ioe.getLocalizedMessage());
	} finally {
	    f.delete();
	}
    }

    
    private static final String PMD_CONFIG_FILE = "pmd_tests.conf";

    
    @Test
    public void testResourceFileCommands() {
	if (TestDescriptor.inRegressionTestMode()) {
	    
	    return;
	}

	InputStream is = getClass().getResourceAsStream(PMD_CONFIG_FILE);

	if (is != null) {
	    try {
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String l;
		while ((l = r.readLine()) != null) {
		    l = l.trim();
		    if (l.length() == 0 || l.charAt(0) == '#') {
			continue;
		    }

		    runPmd(l);
		}
		r.close();
	    } catch (IOException ioe) {
		fail("Problem reading config file: " + ioe.getLocalizedMessage());
	    }
	} else {
	    fail("Missing config file: " + PMD_CONFIG_FILE);
	}
    }

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(PMDCoverageTest.class);
    }
}
