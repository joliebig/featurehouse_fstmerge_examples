
package net.sourceforge.squirrel_sql.fw.sql;

import static org.easymock.EasyMock.expect;

import java.sql.Types;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

import com.gargoylesoftware.base.testing.EqualsTester;

public class UDTInfoTest extends BaseSQuirreLJUnit4TestCase
{
	private static final String TEST_SCHEMA = "aSchema";

	private static final String TEST_CATALOG = "aCatalog";

	EasyMockHelper mockHelper = new EasyMockHelper();

	SQLDatabaseMetaData mockMetaData = mockHelper.createMock(SQLDatabaseMetaData.class);

	@Before
	public void setUp() throws Exception
	{
		expect(mockMetaData.supportsSchemasInDataManipulation()).andStubReturn(true);
		expect(mockMetaData.getDatabaseProductName()).andStubReturn("oracle");
		expect(mockMetaData.getDatabaseProductVersion()).andStubReturn("10g");
		expect(mockMetaData.supportsCatalogsInDataManipulation()).andStubReturn(true);
		expect(mockMetaData.supportsSchemasInTableDefinitions()).andStubReturn(true);
		expect(mockMetaData.getCatalogSeparator()).andStubReturn(".");
		expect(mockMetaData.getIdentifierQuoteString()).andStubReturn("'");
		
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testEqualsAndHashcode()
	{
		mockHelper.replayAll();
		
		
		UDTInfo a =
			new UDTInfo(TEST_CATALOG, TEST_SCHEMA, "userDefinedType", "java.lang.String", "" + Types.VARCHAR,
				"UDT-A", mockMetaData);

		
		UDTInfo a2 =
			new UDTInfo(TEST_CATALOG, TEST_SCHEMA, "userDefinedType", "java.lang.String", "" + Types.VARCHAR,
				"UDT-A", mockMetaData);

		
		UDTInfo c =
			new UDTInfo(TEST_CATALOG, TEST_SCHEMA, "userDefinedType2", "java.lang.Object", "" + Types.VARBINARY,
				"UDT-C", mockMetaData);

		
		UDTInfo d =
			new UDTInfo(TEST_CATALOG, TEST_SCHEMA, "userDefinedType2", "java.lang.Object", "" + Types.VARBINARY,
				"UDT-C", mockMetaData)
			{
				private static final long serialVersionUID = 1L;
			};

		new EqualsTester(a, a2, c, d);
		
		UDTInfo a3 = new UDTInfo(TEST_CATALOG, TEST_SCHEMA, "userDefinedType", null, "" + Types.VARCHAR,
			"UDT-A", mockMetaData);

		new EqualsTester(a, a2, a3, d);
		
		UDTInfo a4 = new UDTInfo(TEST_CATALOG, TEST_SCHEMA, "userDefinedType", "java.lang.String", null,
			"UDT-A", mockMetaData);
		
		new EqualsTester(a, a2, a4, d);
		
		UDTInfo a5 =
			new UDTInfo(TEST_CATALOG, TEST_SCHEMA, "userDefinedType", "java.lang.String", "" + Types.VARCHAR,
				"No Remarks", mockMetaData);
		
		new EqualsTester(a, a2, a5, d);
		
		mockHelper.verifyAll();
	}

}
