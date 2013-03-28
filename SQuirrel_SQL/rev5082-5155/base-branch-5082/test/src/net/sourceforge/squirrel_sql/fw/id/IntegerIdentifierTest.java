package net.sourceforge.squirrel_sql.fw.id;


import junit.framework.Assert;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class IntegerIdentifierTest extends AbstractSerializableTest
{

	private IntegerIdentifier classUnderTest = null;
	
	private int value = 10;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new IntegerIdentifier(value);
		super.serializableToTest = new IntegerIdentifier(value); 
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
		super.serializableToTest = null;
	}

	@Test
	public void testEquals()
	{
		IntegerIdentifier uid1 = new IntegerIdentifier(1);

		IntegerIdentifier uid2 = new IntegerIdentifier(1);

		IntegerIdentifier uid3 = new IntegerIdentifier(2);

		IntegerIdentifier uid4 = new IntegerIdentifier(1)
		{
			private static final long serialVersionUID = 1L;
		};

		new EqualsTester(uid1, uid2, uid3, uid4);
	}

	@Test
	public void testSetString()
	{
		Assert.assertEquals(value, classUnderTest.hashCode());
		classUnderTest.setString("5");
		Assert.assertEquals(5, classUnderTest.hashCode());
	}
}
