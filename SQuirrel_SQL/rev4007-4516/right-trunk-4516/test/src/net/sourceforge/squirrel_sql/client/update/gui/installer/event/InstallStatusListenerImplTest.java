
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InstallStatusListenerImplTest extends BaseSQuirreLJUnit4TestCase
{

	InstallStatusListenerImpl classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new InstallStatusListenerImpl();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testHandleInstallStatusEvent()
	{
		InstallStatusEvent evt = new InstallStatusEvent(InstallEventType.INSTALL_STARTED);
		classUnderTest.handleInstallStatusEvent(evt);
	}

}
