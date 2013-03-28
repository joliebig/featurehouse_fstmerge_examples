package net.sourceforge.squirrel_sql.plugins.dataimport.prefs;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class DataImportPreferenceBeanTest extends BaseSQuirreLJUnit4TestCase {

	DataImportPreferenceBean classUnderTest = new DataImportPreferenceBean();

	@Test
	public void testIsUseTruncate() throws Exception
	{
		classUnderTest.setUseTruncate(true);
		assertEquals(true, classUnderTest.isUseTruncate());
	}

}
