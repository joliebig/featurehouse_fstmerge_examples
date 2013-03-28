
package net.sourceforge.squirrel_sql.client.session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MessagePanelTest {

    MessagePanel messagePanelUnderTest = null;
    
    
    @Before
    public void setUp() throws Exception {
        messagePanelUnderTest = new MessagePanel();
    }

    
    @After
    public void tearDown() throws Exception {
        messagePanelUnderTest = null;
    }

    
    @Test (expected = IllegalArgumentException.class)
    public final void testAddToMessagePanelPopup() {
        messagePanelUnderTest.addToMessagePanelPopup(null);
    }

    
    @Test (expected = IllegalArgumentException.class)
    public final void testShowMessageThrowableExceptionFormatter() {
        messagePanelUnderTest.showMessage(null, null);
    }

    
    @Test (expected = IllegalArgumentException.class)
    public final void testShowErrorMessageThrowableExceptionFormatter() {
        messagePanelUnderTest.showErrorMessage(null, null);
    }

    
    @Test (expected = IllegalArgumentException.class)
    public final void testShowMessageString() {
        messagePanelUnderTest.showMessage(null);
    }

    
    @Test (expected = IllegalArgumentException.class)
    public final void testShowWarningMessage() {
        messagePanelUnderTest.showWarningMessage(null);
    }

    
    @Test (expected = IllegalArgumentException.class)
    public final void testShowErrorMessageString() {
        messagePanelUnderTest.showErrorMessage(null);
    }

}
