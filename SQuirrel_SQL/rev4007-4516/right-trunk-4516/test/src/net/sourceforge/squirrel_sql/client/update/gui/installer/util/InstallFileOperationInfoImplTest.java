
package net.sourceforge.squirrel_sql.client.update.gui.installer.util;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class InstallFileOperationInfoImplTest extends BaseSQuirreLJUnit4TestCase 
{

	private InstallFileOperationInfoImpl classUnderTest = null;
	
	EasyMockHelper mockHelper = new EasyMockHelper();
	
	FileWrapper mockFileToInstall = mockHelper.createMock("mockFileToInstall", FileWrapper.class);
	FileWrapper mockInstallDir = mockHelper.createMock("mockInstallDir", FileWrapper.class);
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new InstallFileOperationInfoImpl(mockFileToInstall, mockInstallDir);
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testGetSetFileToInstall()
	{
		FileWrapper mockNewFileToInstall = mockHelper.createMock(FileWrapper.class);
		
		mockHelper.replayAll();
		assertEquals(mockFileToInstall, classUnderTest.getFileToInstall());
		classUnderTest.setFileToInstall(mockNewFileToInstall);
		assertEquals(mockNewFileToInstall, classUnderTest.getFileToInstall());
		mockHelper.verifyAll();
		
	}

	@Test
	public void testGetSetInstallDir()
	{
		FileWrapper mockNewInstallDir = mockHelper.createMock(FileWrapper.class);
		
		mockHelper.replayAll();
		assertEquals(mockInstallDir, classUnderTest.getInstallDir());
		classUnderTest.setInstallDir(mockNewInstallDir);
		assertEquals(mockNewInstallDir, classUnderTest.getInstallDir());
		mockHelper.verifyAll();
	}

	@Test
	public void testGetSetPlugin()
	{
		mockHelper.replayAll();
		assertFalse(classUnderTest.isPlugin());
		classUnderTest.setPlugin(true);
		assertTrue(classUnderTest.isPlugin());
		mockHelper.verifyAll();
	}

}
