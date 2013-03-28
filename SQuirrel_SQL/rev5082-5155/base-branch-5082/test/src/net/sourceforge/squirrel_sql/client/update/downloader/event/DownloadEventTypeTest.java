
package net.sourceforge.squirrel_sql.client.update.downloader.event;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DownloadEventTypeTest
{

	@Test 
	public void testEnumMethods() {
		for (DownloadEventType action : DownloadEventType.values()) {
			DownloadEventType a = DownloadEventType.valueOf(action.name());
			assertEquals(0, a.compareTo(action));
			assertEquals(action.ordinal(), a.ordinal());
		}
	}
	
}
