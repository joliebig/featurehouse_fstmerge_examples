
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfo;
import net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfoFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;


public class InstallFileOperationInfoFactoryImplTest extends BaseSQuirreLJUnit4TestCase
{

	EasyMockHelper mockHelper = new EasyMockHelper(); 
	
	InstallFileOperationInfoFactoryImpl classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new InstallFileOperationInfoFactoryImpl();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testCreate()
	{
		FileWrapper fileToCopy = mockHelper.createMock("fileToCopy", FileWrapper.class);
		FileWrapper installDir = mockHelper.createMock("installDir", FileWrapper.class);
		InstallFileOperationInfo info = classUnderTest.create(fileToCopy, installDir);
		assertNotNull(info);
		assertEquals(fileToCopy, info.getFileToInstall());
		assertEquals(installDir, info.getInstallDir());
	}

	@Test (expected = IllegalArgumentException.class)
	public void testCreate_NullFileToCopy()
	{
		FileWrapper fileToCopy = null;
		FileWrapper installDir = mockHelper.createMock("installDir", FileWrapper.class);
		classUnderTest.create(fileToCopy, installDir);
	}
	
	public void testCreate_NullInstallDir() {
		FileWrapper fileToCopy = mockHelper.createMock("fileToCopy", FileWrapper.class);
		FileWrapper installDir = null;
		classUnderTest.create(fileToCopy, installDir);
	}
}
