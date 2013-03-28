package net.sourceforge.squirrel_sql.plugins.sessionscript;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

import utils.EasyMockHelper;


public class AliasScriptTest extends BaseSQuirreLJUnit4TestCase {

	AliasScript classUnderTest = new AliasScript();

	EasyMockHelper mockHelper = new EasyMockHelper();
	
	@Test
	public void testGetIdentifier() throws Exception
	{
		classUnderTest.setIdentifier(null);
		assertNull(classUnderTest.getIdentifier());
	}

	@Test
	public void testGetSQL() throws Exception
	{
		classUnderTest.setSQL("aTestString");
		assertEquals("aTestString", classUnderTest.getSQL());
	}

	@Test
	public void testEqualsAndHashcode() {
		IIdentifier id1 = mockHelper.createMock(IIdentifier.class);
		IIdentifier id2 = mockHelper.createMock(IIdentifier.class);
		
		ISQLAlias alias1 = mockHelper.createMock(ISQLAlias.class);
		EasyMock.expect(alias1.getIdentifier()).andStubReturn(id1);
		ISQLAlias alias2 = mockHelper.createMock(ISQLAlias.class);
		EasyMock.expect(alias2.getIdentifier()).andStubReturn(id2);
		
		mockHelper.replayAll();
		
		AliasScript a = new AliasScript(alias1);
		AliasScript b = new AliasScript(alias1);
		AliasScript c = new AliasScript(alias2);
		AliasScript d = new AliasScript(alias1) {
			private static final long serialVersionUID = 1L;
		};

		new EqualsTester(a, b, c, d); 
		
		mockHelper.verifyAll();
	}
}
