
import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import net.sourceforge.pmd.build.PmdBuildException;
import net.sourceforge.pmd.build.RuleSetToDocs;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;


public class PmdBuildTest {

    private static String TEST_DIR = "target-test/";
    private static File testDir;

    
    @Before
    public void setUp() throws Exception {
	testDir = new File(TEST_DIR);
	if (! testDir.exists() && ! testDir.mkdir() )
	{
	    throw new PmdBuildException("Can't create " + TEST_DIR);
	}
	else if ( ! testDir.isDirectory() )
	{
	    throw new PmdBuildException("testdir " + TEST_DIR + " exist !");
	}
    }

    @Test
    public void convertRulesetsTest() throws IOException {
	RuleSetToDocs builder = new RuleSetToDocs();
	builder.setRulesDirectory("rulesets");
	builder.setTargetDirectory(TEST_DIR);
	try {
	    builder.convertRulesets();
	} catch (PmdBuildException e) {
	    e.printStackTrace();
	}
    }

    @Test
    public void generateIndexRules() throws IOException, TransformerException {
	RuleSetToDocs builder = new RuleSetToDocs();
	builder.setRulesDirectory("rulesets");
	builder.setTargetDirectory(TEST_DIR);
	try {
	    builder.generateRulesIndex();
	} catch (PmdBuildException e) {
	    e.printStackTrace();
	}
    }


    
    @AfterClass
    public static void tearDown() throws Exception {
	RuleSetToDocs.deleteFile(testDir);
    }


}
