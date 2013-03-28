
package net.sourceforge.squirrel_sql.client.mainframe.action;

import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.awt.Component;
import java.awt.Frame;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.IDialogUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SavePreferencesCommandTest {

    Frame mockFrame = createMock(Frame.class);
    IApplication mockApplication = createMock(IApplication.class);
    IDialogUtils mockDialogUtils = createMock(IDialogUtils.class);
    
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    
    
    @Test (expected = IllegalArgumentException.class)
    public final void testSavePreferencesCommandNullApp() {
        new SavePreferencesCommand(null, mockFrame);
    }

    @Test (expected = IllegalArgumentException.class)
    public final void testSavePreferencesCommandNullFrame() {
        new SavePreferencesCommand(mockApplication, null);
    }

    
    
    @Test 
    public final void testExecute() {
        mockApplication.saveApplicationState();
        mockDialogUtils.showOk(isA(Component.class), isA(String.class));
        
        replay(mockFrame);
        replay(mockApplication);
        replay(mockDialogUtils);
        
        
        SavePreferencesCommand command = 
            new SavePreferencesCommand(mockApplication, mockFrame);
        command.setDialogUtils(mockDialogUtils);
        command.execute();
        
        verify(mockFrame);
        verify(mockApplication);
        verify(mockDialogUtils);
        
    }

}
