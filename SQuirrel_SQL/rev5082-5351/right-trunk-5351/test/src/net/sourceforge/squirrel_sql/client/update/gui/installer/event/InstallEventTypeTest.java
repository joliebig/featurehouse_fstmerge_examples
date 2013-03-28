
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InstallEventTypeTest
{

	@Test 
	public void testEnumMethods() {
		for (InstallEventType action : InstallEventType.values()) {
			InstallEventType a = InstallEventType.valueOf(action.name());
			assertEquals(0, a.compareTo(action));
			assertEquals(action.ordinal(), a.ordinal());
		}
	}

	
}
