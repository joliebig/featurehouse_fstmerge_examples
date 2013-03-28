package net.sourceforge.squirrel_sql.client.gui.db;


import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class SQLAliasTest extends AbstractSerializableTest
{

	@Before
	public void setUp() throws Exception
	{
		super.serializableToTest = new SQLAlias();
	}

	@After
	public void tearDown() throws Exception
	{
		super.serializableToTest = null;
	}

	@Test
	public void testEqualsObject()
	{
		IIdentifierFactory factory = IdentifierFactory.getInstance();
		IIdentifier id1 = factory.createIdentifier();
		IIdentifier id2 = factory.createIdentifier();
		SQLAlias alias1 = new SQLAlias(id1);
		SQLAlias alias2 = new SQLAlias(id1);
		SQLAlias alias3 = new SQLAlias(id2);
		SQLAlias alias4 = new SQLAlias(id1)
		{
			private static final long serialVersionUID = 1L;
		};
		new EqualsTester(alias1, alias2, alias3, alias4);
	}

	@Test
	public void testIsValid()
	{
		SQLAlias uninitializedAlias = new SQLAlias();
		assertEquals(false, uninitializedAlias.isValid());
	}

}
