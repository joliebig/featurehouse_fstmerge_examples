package net.sourceforge.squirrel_sql.client.session.mainpanel;

import static org.easymock.EasyMock.createMock;

import java.awt.Component;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.client.session.ISession;


public class BaseMainPanelTabTest extends BaseSQuirreLTestCase {

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    
    public void testSessionCleanup() {
        MyBaseMainPanelTab tab = new MyBaseMainPanelTab();
        ISession session1 = createMock(ISession.class);
        tab.setSession(session1);
        ISession session2 = createMock(ISession.class);
        tab.sessionEnding(session2);
        assertEquals(session1, tab.getSession());
        tab.sessionEnding(session1);
        assertEquals(null, tab.getSession());
    }
    
    private class MyBaseMainPanelTab extends BaseMainPanelTab {

        
        @Override
        protected void refreshComponent() {
            
        }

        
        public Component getComponent() {
            return null;
        }

        
        public String getHint() {
            return null;
        }

        
        public String getTitle() {
            return null;
        }
        
    }
}
