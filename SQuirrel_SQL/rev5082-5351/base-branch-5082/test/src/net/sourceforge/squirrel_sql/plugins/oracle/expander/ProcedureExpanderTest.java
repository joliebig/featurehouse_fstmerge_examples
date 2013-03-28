
package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;

import java.sql.DatabaseMetaData;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

import org.junit.Before;

public class ProcedureExpanderTest extends AbstractINodeExpanderTest
{

	@Before
	public void setUp() throws Exception
	{

		IProcedureInfo mockProcedureInfo = mockHelper.createMock(IProcedureInfo.class);
		expect(mockProcedureInfo.getProcedureType()).andStubReturn(DatabaseMetaData.procedureNoResult);
		IProcedureInfo[] procedures = new IProcedureInfo[] { mockProcedureInfo };
		expect(
			mockSchemaInfo.getStoredProceduresInfos((String)isNull(), eq(TEST_SCHEMA_NAME), isA(ObjFilterMatcher.class)))
			.andStubReturn(procedures);
		classUnderTest = new ProcedureExpander();
	}
}
