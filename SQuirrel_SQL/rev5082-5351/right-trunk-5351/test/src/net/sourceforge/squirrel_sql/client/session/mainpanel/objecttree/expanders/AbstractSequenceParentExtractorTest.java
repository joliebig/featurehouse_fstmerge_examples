
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ISequenceParentExtractor;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public abstract class AbstractSequenceParentExtractorTest extends BaseSQuirreLJUnit4TestCase
{
	protected ISequenceParentExtractor classUnderTest = null;
	
	protected static final String TEST_LIKE_MATCH_STRING = "test like match string";
	
	
	
	PreparedStatement mockPreparedStatement = mockHelper.createMock(PreparedStatement.class);
	IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);
	ObjFilterMatcher mockObjFilterMatcher = mockHelper.createMock(ObjFilterMatcher.class);

	public AbstractSequenceParentExtractorTest()
	{
		super();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testGetSequenceParentQuery()
	{
		mockHelper.replayAll();
		String sql = classUnderTest.getSequenceParentQuery();
		assertNotNull(sql);
		assertTrue(sql.length() > 0);
		mockHelper.verifyAll();
	}

	@Test
	public void testBindParameters() throws Exception
	{
		expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		
		
		
		mockPreparedStatement.setString(EasyMock.anyInt(), isA(String.class));
		EasyMock.expectLastCall().anyTimes();
		
		expect(mockObjFilterMatcher.getSqlLikeMatchString()).andReturn(TEST_LIKE_MATCH_STRING);
	
		mockHelper.replayAll();
		classUnderTest.bindParameters(mockPreparedStatement, mockDatabaseObjectInfo, mockObjFilterMatcher);
		mockHelper.verifyAll();
	}

}