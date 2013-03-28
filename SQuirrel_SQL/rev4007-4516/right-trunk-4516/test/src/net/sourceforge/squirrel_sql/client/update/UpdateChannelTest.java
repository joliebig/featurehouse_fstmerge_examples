
package net.sourceforge.squirrel_sql.client.update;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UpdateChannelTest
{

	@Test 
	public void testEnumMethods() {
		for (UpdateChannel action : UpdateChannel.values()) {
			UpdateChannel a = UpdateChannel.valueOf(action.name());
			assertEquals(0, a.compareTo(action));
			assertEquals(action.ordinal(), a.ordinal());
		}
	}

}
