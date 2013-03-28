
package net.sourceforge.squirrel_sql.fw.dialects;

import static java.sql.Types.VARCHAR;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Types;
import java.util.Vector;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.MappingException;
import org.junit.Test;

import utils.EasyMockHelper;

public abstract class AbstractDialectExtTest extends BaseSQuirreLJUnit4TestCase
{

	protected HibernateDialect classUnderTest = null;

	protected EasyMockHelper mockHelper = new EasyMockHelper();

	private TableColumnInfo mockColumnInfo = mockHelper.createMock(TableColumnInfo.class);

	private DatabaseObjectQualifier mockQualifier = mockHelper.createMock(DatabaseObjectQualifier.class);

	private SqlGenerationPreferences mockPrefs = mockHelper.createMock(SqlGenerationPreferences.class);

	public AbstractDialectExtTest()
	{
		super();
		disableLogging(org.hibernate.dialect.Dialect.class);
	}

	private void setCommonExpectations()
	{
		expect(mockColumnInfo.getTableName()).andStubReturn("aTestTableName");
		expect(mockColumnInfo.getColumnName()).andStubReturn("aTestColumnName");
		expect(mockColumnInfo.getRemarks()).andStubReturn("aRemark");
		expect(mockColumnInfo.getDataType()).andStubReturn(VARCHAR);
		expect(mockColumnInfo.getColumnSize()).andStubReturn(1024);
		expect(mockColumnInfo.getDecimalDigits()).andStubReturn(0);
		expect(mockColumnInfo.getDefaultValue()).andStubReturn(null);
		expect(mockColumnInfo.isNullable()).andStubReturn("YES");
		expect(mockQualifier.getSchema()).andStubReturn("aTestSchema");
		expect(mockQualifier.getCatalog()).andStubReturn("aTestCatalog");
		expect(mockPrefs.isQualifyTableNames()).andStubReturn(true);
		expect(mockPrefs.isQuoteIdentifiers()).andStubReturn(true);
		expect(mockPrefs.isQuoteColumnNames()).andStubReturn(true);
		expect(mockPrefs.isQuoteConstraintNames()).andStubReturn(true);
		mockPrefs.setQuoteColumnNames(false);
		expectLastCall().anyTimes();
		mockPrefs.setQuoteConstraintNames(false);
		expectLastCall().anyTimes();
	}

	@Test
	public void testSupportsProduct()
	{
		assertFalse(classUnderTest.supportsProduct(null, null));
		assertFalse(classUnderTest.supportsProduct("FOOBAR", "1"));

	}

	@Test
	public void testGetLengthFunction()
	{
		for (String type : JDBCTypeMapper.getJdbcTypeList())
		{
			int dataType = JDBCTypeMapper.getJdbcType(type);
			String lengthFunction = classUnderTest.getLengthFunction(dataType);
			assertNotNull(lengthFunction);
			assertFalse("".equals(lengthFunction));
		}

	}

	@Test
	public void testCanPasteTo() throws Exception
	{
		IDatabaseObjectInfo mockDboInfo = mockHelper.createMock(IDatabaseObjectInfo.class);
		boolean canPasteTo = false;
		DatabaseObjectType t = DatabaseObjectType.DATABASE_TYPE_DBO;
		for (Field f : t.getClass().getFields())
		{
			if (Modifier.isPublic(f.getModifiers()))
			{
				Object o = f.get(t);
				expect(mockDboInfo.getDatabaseObjectType()).andStubReturn((DatabaseObjectType) (o));
				mockHelper.replayAll();
				canPasteTo = canPasteTo || classUnderTest.canPasteTo(mockDboInfo);
				mockHelper.resetAll();
			}
		}
		String displayName = classUnderTest.getDisplayName();
		assertTrue("Dialect (" + displayName + ") can't paste to any object", canPasteTo);

	}

	@Test
	public void testGetMaxFunction()
	{
		String displayName = classUnderTest.getDisplayName();
		assertNotNull("Dialect (" + displayName + ") doesn't return a valid max function",
			classUnderTest.getMaxFunction());
		assertFalse("Dialect (" + displayName + ") doesn't return a valid max function",
			"".equals(classUnderTest.getMaxFunction()));
	}

	@Test
	public void testGetPrecisionDigits()
	{
		for (String type : JDBCTypeMapper.getJdbcTypeList())
		{
			int dataType = JDBCTypeMapper.getJdbcType(type);
			assertTrue(classUnderTest.getPrecisionDigits(10, dataType) > 0);
		}
	}

	@Test
	public void testGetMaxPrecision()
	{
		for (String type : JDBCTypeMapper.getJdbcTypeList())
		{
			int dataType = JDBCTypeMapper.getJdbcType(type);
			assertTrue(classUnderTest.getMaxPrecision(dataType) >= 0);
		}
	}

	@Test
	public void testGetMaxScale()
	{
		for (String type : JDBCTypeMapper.getJdbcTypeList())
		{
			int dataType = JDBCTypeMapper.getJdbcType(type);
			assertTrue(classUnderTest.getMaxScale(dataType) >= 0);
		}
	}

	@Test
	public void testGetColumnLength()
	{
		for (String type : JDBCTypeMapper.getJdbcTypeList())
		{
			int dataType = JDBCTypeMapper.getJdbcType(type);
			assertTrue(classUnderTest.getColumnLength(10, dataType) >= 0);
		}
	}

	@Test
	public void testGetDialectType()
	{
		assertNotNull(classUnderTest.getDialectType());
	}

	@Test
	public void testgetAddForeignKeyConstraintSQL()
	{
		setCommonExpectations();
		mockHelper.replayAll();
		try
		{
			Vector<String[]> localRefCols = new Vector<String[]>();
			localRefCols.add(new String[] { "aCol", "bCol" });
			String[] sql =
				classUnderTest.getAddForeignKeyConstraintSQL("localTableName", "refTableName", "constraintName",
					true, true, true, true, "fkIndexName", localRefCols, "updateAction", "onDeleteAction",
					mockQualifier, mockPrefs);

			if (classUnderTest.supportsAddForeignKeyConstraint())
			{
				assertNotNull("supportsAddForeignKeyConstraint == true, but sql returned was null", sql);
				assertTrue(sql.length != 0);
			}
			else
			{
				fail("Expected dialect (" + classUnderTest.getDisplayName()
					+ ") to throw UnsupportedOperationException when trying to retrieve SQL for adding "
					+ "a foreign key constraint");
			}
		}
		catch (UnsupportedOperationException e)
		{
			if (classUnderTest.supportsAddForeignKeyConstraint())
			{
				failForUnsupported("supportsAddForeignKeyConstraint", e);
			}
		}
		mockHelper.verifyAll();

	}

	@Test
	public void testGetColumnDefaultAlterSQL()
	{
		setCommonExpectations();
		mockHelper.replayAll();
		try
		{
			String sql = classUnderTest.getColumnDefaultAlterSQL(mockColumnInfo, mockQualifier, mockPrefs);

			if (classUnderTest.supportsAlterColumnDefault())
			{
				assertNotNull("supportsAlterColumnDefault == true, but sql returned was null", sql);
			}
			else
			{
				fail("Expected dialect (" + classUnderTest.getDisplayName()
					+ ") to throw UnsupportedOperationException when trying to retrieve SQL for altering "
					+ "a column default");
			}
		}
		catch (UnsupportedOperationException e)
		{
			if (classUnderTest.supportsAlterColumnDefault())
			{
				failForUnsupported("supportsAlterColumnDefault", e);
			}
		}
		mockHelper.verifyAll();

	}

	@Test
	public void testGetColumnDropSQL()
	{
		setCommonExpectations();
		mockHelper.replayAll();
		try
		{
			String sql =
				classUnderTest.getColumnDropSQL("aTestTableName", "aTestColumnName", mockQualifier, mockPrefs);
			if (classUnderTest.supportsDropColumn())
			{
				assertNotNull("supportsDropColumn == true, but sql returned was null", sql);
			}
			else
			{
				fail("Expected dialect (" + classUnderTest.getDisplayName()
					+ ") to throw UnsupportedOperationException when trying to retrieve SQL for dropping a column");
			}
		}
		catch (UnsupportedOperationException e)
		{
			if (classUnderTest.supportsDropColumn())
			{
				failForUnsupported("supportsDropColumn", e);
			}
		}
		mockHelper.verifyAll();
	}

	@Test
	public void testGetColumnNullAlter()
	{
		setCommonExpectations();

		mockHelper.replayAll();

		try
		{
			String[] sql = classUnderTest.getColumnNullableAlterSQL(mockColumnInfo, mockQualifier, mockPrefs);
			if (classUnderTest.supportsAlterColumnNull())
			{
				assertNotNull("supportsAlterColumnNull == true, but sql returned was null", sql);
				assertTrue(sql.length != 0);
			}
			else
			{
				fail("Expected dialect ("
					+ classUnderTest.getDisplayName()
					+ ") to throw UnsupportedOperationException when trying to retrieve SQL for modify a column's nullability");
			}
		}
		catch (UnsupportedOperationException e)
		{
			if (classUnderTest.supportsAlterColumnNull())
			{
				failForUnsupported("supportsAlterColumnNull", e);
			}
		}

		mockHelper.verifyAll();
	}

	@Test
	public void testGetColumnNameAlter()
	{
		setCommonExpectations();

		TableColumnInfo mockToInfo = mockHelper.createMock(TableColumnInfo.class);
		expect(mockToInfo.getColumnName()).andStubReturn("aNewColumnName");

		mockHelper.replayAll();

		try
		{
			String sql =
				classUnderTest.getColumnNameAlterSQL(mockColumnInfo, mockToInfo, mockQualifier, mockPrefs);
			if (classUnderTest.supportsRenameColumn())
			{
				assertNotNull("supportsRenameColumn == true, but sql returned was null", sql);
			}
			else
			{
				fail("Expected dialect ("
					+ classUnderTest.getDisplayName()
					+ ") to throw UnsupportedOperationException when trying to retrieve SQL for re-naming a column");
			}
		}
		catch (UnsupportedOperationException e)
		{
			if (classUnderTest.supportsRenameColumn())
			{
				failForUnsupported("supportsRenameColumn", e);
			}
		}

		mockHelper.verifyAll();
	}

	@Test
	public void testAddColumnQualifiedNamesQuotedIdentifiers()
	{
		setCommonExpectations();
		mockHelper.replayAll();

		try
		{
			String[] sql = classUnderTest.getAddColumnSQL(mockColumnInfo, mockQualifier, mockPrefs);
			if (!classUnderTest.supportsAddColumn())
			{
				fail("Expected dialect (" + classUnderTest.getDisplayName()
					+ ") to throw UnsupportedOperationException when trying to retrieve SQL for adding a column");
			}
			assertNotNull(sql);
			assertTrue(sql.length != 0);
		}
		catch (UnsupportedOperationException e)
		{
			if (classUnderTest.supportsAddColumn())
			{
				failForUnsupported("supportsAddColumn", e);
			}
		}
		mockHelper.verifyAll();
	}

	@Test
	public void testGetTypeNameInt()
	{
		testAllTypes(classUnderTest);
	}

	@Test
	public void testGetCreateSequenceSQL()
	{
		setCommonExpectations();
		mockHelper.replayAll();
		try
		{
			String sql =
				classUnderTest.getCreateSequenceSQL("sSequenceName", "1", "1", "2000", "1", "20", true,
					mockQualifier, mockPrefs);
			if (classUnderTest.supportsSequence())
			{
				assertNotNull("supportsSequence == true, but sql returned was null", sql);
			}
			else
			{
				fail("Expected dialect (" + classUnderTest.getDisplayName()
					+ ") to throw UnsupportedOperationException when trying to create a sequence");
			}
		}
		catch (UnsupportedOperationException e)
		{
			if (classUnderTest.supportsSequence())
			{
				failForUnsupported("supportsSequence", e);
			}
		}
		mockHelper.verifyAll();
	}

	@Test
	public void testGetAlterSequenceSQL()
	{
		setCommonExpectations();
		mockHelper.replayAll();
		try
		{
			String[] sql =
				classUnderTest.getAlterSequenceSQL("sSequenceName", "1", "1", "2000", "1", "20", true,
					mockQualifier, mockPrefs);

			if (classUnderTest.supportsSequence() && classUnderTest.supportsAlterSequence())
			{
				assertNotNull("supportsSequence == true, but sql returned was null", sql);
				assertTrue(sql.length != 0);
			}
			else
			{
				fail("Expected dialect (" + classUnderTest.getDisplayName()
					+ ") to throw UnsupportedOperationException when trying to alter a sequence");
			}
		}
		catch (UnsupportedOperationException e)
		{
			if (classUnderTest.supportsSequence())
			{
				failForUnsupported("supportsSequence", e);
			}
		}
		mockHelper.verifyAll();
	}

	

	private void failForUnsupported(String supportsMethod, UnsupportedOperationException e)
	{
		String message = e.getMessage();
		
		
		if (!message.equals("Not yet implemented"))
		{
			fail("For dialect (" + classUnderTest.getDisplayName() + ") " + supportsMethod
				+ " was true, but still got an UnsupportedOperationException: " + e.getMessage());
		}
	}

	private void testAllTypes(HibernateDialect d)
	{
		try
		{
			Field[] fields = java.sql.Types.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				Integer jdbcType = field.getInt(null);
				testType(jdbcType, d);
			}
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	private void testType(int type, HibernateDialect dialect)
	{
		try
		{
			dialect.getTypeName(type, 10, 0, 0);
		}
		catch (MappingException e)
		{
			
			
			
			
			
			if (type != Types.NULL && type != Types.DATALINK && type != Types.OTHER && type != Types.JAVA_OBJECT
				&& type != Types.DISTINCT && type != Types.STRUCT && type != Types.ARRAY && type != Types.REF
				
				&& type != -8 
				&& type != -9 
				&& type != -15 
				&& type != -16 
				&& type != 2011 
				&& type != 2009 
			)
			{
				fail("Dialect (" + classUnderTest.getDisplayName() + ") has no mapping for type: " + type + "="
					+ JDBCTypeMapper.getJdbcTypeName(type));
			}
		}
	}

}