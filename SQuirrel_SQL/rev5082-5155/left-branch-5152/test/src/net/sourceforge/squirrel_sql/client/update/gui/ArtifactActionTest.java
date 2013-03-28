
package net.sourceforge.squirrel_sql.client.update.gui;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArtifactActionTest
{

	@Test 
	public void testEnumMethods() {
		for (ArtifactAction action : ArtifactAction.values()) {
			ArtifactAction a = ArtifactAction.valueOf(action.name());
			assertEquals(0, a.compareTo(action));
			assertEquals(action.ordinal(), a.ordinal());
		}
	}
	
}
