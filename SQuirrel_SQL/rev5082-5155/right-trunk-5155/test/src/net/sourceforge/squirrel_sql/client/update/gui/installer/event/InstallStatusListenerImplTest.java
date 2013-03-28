
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.gui.installer.ProgressDialogController;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InstallStatusListenerImplTest extends BaseSQuirreLJUnit4TestCase
{

	InstallStatusListenerImpl classUnderTest = null;
	
	ProgressDialogController mockController = mockHelper.createMock(ProgressDialogController.class);
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new InstallStatusListenerImpl(mockController);
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testHandleInitChangelistStartedStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.INIT_CHANGELIST_STARTED);
		int numFilesToUpdate = 10;
		evt.setNumFilesToUpdate(numFilesToUpdate);
		mockController.showProgressDialog(isA(String.class), isA(String.class), eq(numFilesToUpdate));
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}

	@Test
	public void testHandleFileInitChangelistStartedStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.FILE_INIT_CHANGELIST_STARTED);
		evt.setArtifactName("testArtifactName");
		mockController.setDetailMessage(isA(String.class));
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}
	
	@Test
	public void testHandleFileInitChangeListCompleteStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.FILE_INIT_CHANGELIST_COMPLETE);
		mockController.incrementProgress();
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}
	
	@Test
	public void testHandleInitChangelistCompleteStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.INIT_CHANGELIST_COMPLETE);
		mockController.setDetailMessage(isA(String.class));
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}
	
	
	@Test
	public void testHandleBackupStartedStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.BACKUP_STARTED);
		int numFilesToUpdate = 10;
		evt.setNumFilesToUpdate(numFilesToUpdate);
		mockController.resetProgressDialog(isA(String.class), isA(String.class), eq(numFilesToUpdate));
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}

	@Test
	public void testHandleFileBackupStartedStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.FILE_BACKUP_STARTED);
		evt.setArtifactName("testArtifactName");
		mockController.setDetailMessage(isA(String.class));
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}
	
	@Test
	public void testHandleFileBackupCompleteStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.FILE_BACKUP_COMPLETE);
		mockController.incrementProgress();
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}
	
	@Test
	public void testHandleBackupCompleteStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.BACKUP_COMPLETE);
		mockController.setDetailMessage(isA(String.class));
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}
	
	@Test
	public void testHandleInstallStartedStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.INSTALL_STARTED);
		int numFilesToUpdate = 10;
		evt.setNumFilesToUpdate(numFilesToUpdate);
		mockController.resetProgressDialog(isA(String.class), isA(String.class), eq(numFilesToUpdate));
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}

	@Test
	public void testHandleFileInstallStartedStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.FILE_INSTALL_STARTED);
		evt.setArtifactName("testArtifactName");
		mockController.setDetailMessage(isA(String.class));
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}

	@Test
	public void testHandleFileInstallCompleteStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.FILE_INSTALL_COMPLETE);
		mockController.incrementProgress();
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}

	@Test
	public void testHandleInstallCompleteStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.INSTALL_COMPLETE);
		mockController.hideProgressDialog();
		
		mockHelper.replayAll();
		classUnderTest.handleInstallStatusEvent(evt);
		mockHelper.verifyAll();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testNullArgumentConstructor() {
		classUnderTest = new InstallStatusListenerImpl(null);
	}
}
