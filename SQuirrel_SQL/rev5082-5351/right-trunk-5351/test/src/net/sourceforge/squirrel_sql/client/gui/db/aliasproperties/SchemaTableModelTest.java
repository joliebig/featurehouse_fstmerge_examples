
package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import static net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_DONT_CACHE;
import static org.easymock.EasyMock.expect;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties;
import net.sourceforge.squirrel_sql.client.update.gui.AbstractTableModelTest;

import org.junit.Before;

public class SchemaTableModelTest extends AbstractTableModelTest
{

	@Before
	public void setUp() throws Exception
	{
		SQLAliasSchemaDetailProperties mockProps = mockHelper.createMock(SQLAliasSchemaDetailProperties.class);
		expect(mockProps.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(mockProps.getTable()).andStubReturn(SCHEMA_LOADING_ID_LOAD_DONT_CACHE);
		expect(mockProps.getProcedure()).andStubReturn(SCHEMA_LOADING_ID_LOAD_DONT_CACHE);
		expect(mockProps.getView()).andStubReturn(SCHEMA_LOADING_ID_LOAD_DONT_CACHE);
		SQLAliasSchemaDetailProperties[] props = new SQLAliasSchemaDetailProperties[] { mockProps };
		classUnderTest = new SchemaTableModel(props);
		super.editableColumns = new int[] { 1, 2, 3 };
		mockHelper.replayAll();
	}

}
