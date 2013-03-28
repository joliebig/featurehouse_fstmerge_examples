import net.sourceforge.pmd.ant.PmdBuildTask;

import org.apache.tools.ant.BuildException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class AntTaskTest {

    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    
    @Before
    public void setUp() throws Exception {
    }

    
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void antTask() {
	PmdBuildTask task = new PmdBuildTask();
	task.setRulesDirectory("rulesets");
	task.setTarget("target-test");
	try {
	    task.execute();
	} catch (BuildException e) {
	    e.printStackTrace();
	}

    }
}
