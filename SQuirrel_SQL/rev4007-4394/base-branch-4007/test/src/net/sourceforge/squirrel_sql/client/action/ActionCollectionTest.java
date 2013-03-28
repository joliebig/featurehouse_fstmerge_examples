
package net.sourceforge.squirrel_sql.client.action;

import static org.easymock.classextension.EasyMock.replay;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.test.TestUtil;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActionCollectionTest extends BaseSQuirreLJUnit4TestCase {

    ActionCollection actionCollectionUnderTest = null;
    IApplication mockApplication = null;
    Action mockAction = null;
    
    @Before
    public void setUp() throws Exception {
        mockApplication = TestUtil.getEasyMockApplication(false);
        mockAction = EasyMock.createMock(Action.class);
        
        replay(mockAction);
        
        actionCollectionUnderTest = new ActionCollection(mockApplication);
    }

    @After
    public void tearDown() throws Exception {
        actionCollectionUnderTest = null;
    }

    

    @Test (expected = IllegalArgumentException.class )
    public final void testAddNull() {
        actionCollectionUnderTest.add(null);            
    }
    
    @Test (expected = IllegalArgumentException.class )
    public final void testEnableAction() {
        actionCollectionUnderTest.enableAction(null, true);            
    }
    
}
