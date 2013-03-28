
package net.sourceforge.squirrel_sql.fw.dialects;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DialectTypeTest
{

	@Test 
	public void testEnumMethods() {
		for (DialectType action : DialectType.values()) {
			DialectType a = DialectType.valueOf(action.name());
			assertEquals(0, a.compareTo(action));
			assertEquals(action.ordinal(), a.ordinal());
		}
	}

}
