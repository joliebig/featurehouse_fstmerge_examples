
package net.sourceforge.squirrel_sql.client.update.downloader.event;


import static net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadEventType.DOWNLOAD_STARTED;
import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DownloadStatusEventTest extends BaseSQuirreLJUnit4TestCase
{

	private DownloadStatusEvent classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testGetType() {
		classUnderTest = new DownloadStatusEvent(DOWNLOAD_STARTED);
		assertEquals(DOWNLOAD_STARTED, classUnderTest.getType());
	}
	
	@Test
	public void testGetSetFilename() {
		classUnderTest = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_COMPLETED);
		String filename = "somefilename";
		classUnderTest.setFilename(filename);
		assertEquals(filename, classUnderTest.getFilename());
	}
	
	@Test
	public void testGetSetFileCountTotal() {
		classUnderTest = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_COMPLETED);
		classUnderTest.setFileCountTotal(100);
		assertEquals(100, classUnderTest.getFileCountTotal());
	}
	
	@Test
	public void testGetSetException() {
		classUnderTest = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_COMPLETED);
		Exception expected = new Exception("something exceptional");
		classUnderTest.setException(expected);
		assertEquals(expected, classUnderTest.getException());
	}
	
	@Test
	public void testToString() {
		classUnderTest = new DownloadStatusEvent(DOWNLOAD_STARTED);
		Assert.assertNotNull(classUnderTest.toString());
	}
	
}
