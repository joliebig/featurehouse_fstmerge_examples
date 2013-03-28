package net.sourceforge.squirrel_sql.fw.id;


import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class UidIdentifierTest extends BaseSQuirreLJUnit4TestCase
{

	@Before
	public void setUp() throws Exception
	{

	}

	@After
	public void tearDown() throws Exception
	{

	}

	@Test
	public void testEquals()
	{
		UidIdentifier uid1 = createIIdentifier();
		uid1.setString("1");
		UidIdentifier uid2 = createIIdentifier();
		uid2.setString("1");
		UidIdentifier uid3 = createIIdentifier();
		uid3.setString("2");
		UidIdentifier uid4 = new UidIdentifier()
		{
			private static final long serialVersionUID = 1L;
		};
		uid4.setString("1");
		new EqualsTester(uid1, uid2, uid3, uid4);
	}

	protected UidIdentifier createIIdentifier()
	{
		return new UidIdentifier();
	}

}
