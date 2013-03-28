
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractDataTypeComponentTest extends BaseSQuirreLJUnit4TestCase
{

	protected IDataTypeComponent classUnderTest = null;

	protected ColumnDisplayDefinition mockColumnDisplayDefinition =
		mockHelper.createMock(ColumnDisplayDefinition.class);

	protected ISQLDatabaseMetaData mockMetaData = mockHelper.createMock(ISQLDatabaseMetaData.class);

	protected IToolkitBeepHelper mockBeepHelper = mockHelper.createMock(IToolkitBeepHelper.class);

	protected boolean defaultValueIsNull = false;

	protected boolean canDoFileIO = true;

	protected boolean isEditableInCell = true;

	protected boolean isEditableInPopup = true;

	
	protected ColumnDisplayDefinition getMockColumnDisplayDefinition()
	{
		ColumnDisplayDefinition columnDisplayDefinition = 
			mockHelper.createMock("testColumnDisplayDefinition", ColumnDisplayDefinition.class);
		expect(columnDisplayDefinition.isNullable()).andStubReturn(false);
		expect(columnDisplayDefinition.isSigned()).andStubReturn(false);
		expect(columnDisplayDefinition.getPrecision()).andStubReturn(10);
		expect(columnDisplayDefinition.getScale()).andStubReturn(3);
		expect(columnDisplayDefinition.getColumnSize()).andStubReturn(10);
		expect(columnDisplayDefinition.getLabel()).andStubReturn("testLabel");
		return columnDisplayDefinition;
	}

	@Before
	public void setUp() throws Exception
	{
		classUnderTest.setColumnDisplayDefinition(mockColumnDisplayDefinition);
		classUnderTest.setBeepHelper(mockBeepHelper);
		expect(mockColumnDisplayDefinition.getLabel()).andStubReturn("testLabel");
		expect(mockMetaData.getDatabaseProductName()).andStubReturn("testDatabaseProductName");
		expect(mockMetaData.getDatabaseProductVersion()).andStubReturn("testDatabaseProductVersion");
		mockBeepHelper.beep(isA(Component.class));
		expectLastCall().anyTimes();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
		mockHelper.resetAll();
	}

	@Test
	public void testGetClassName() throws Exception
	{
		assertNotNull(classUnderTest.getClassName());
		Class.forName(classUnderTest.getClassName());
	}

	@Test
	public void testCanDoFileIO()
	{
		mockHelper.replayAll();
		if (canDoFileIO)
		{
			assertTrue(classUnderTest.canDoFileIO());
		}
		else
		{
			assertFalse(classUnderTest.canDoFileIO());
		}
		mockHelper.verifyAll();
	}

	@Test
	public void testGetDefaultValue()
	{
		mockHelper.replayAll();
		if (defaultValueIsNull)
		{
			assertNull(classUnderTest.getDefaultValue(null));
		}
		else
		{
			assertNotNull(classUnderTest.getDefaultValue(null));
		}
		mockHelper.verifyAll();
	}

	@Test
	public void testIsEditableInCell()
	{
		Object testObject = getEqualsTestObject();
		
		mockHelper.replayAll();
		if (isEditableInCell)
		{
			assertTrue(classUnderTest.isEditableInCell(testObject));
			assertTrue(classUnderTest.isEditableInCell(null));
		}
		else
		{
			assertFalse(classUnderTest.isEditableInCell(testObject));
			assertFalse(classUnderTest.isEditableInCell(null));

		}
		mockHelper.verifyAll();
	}

	@Test
	public void testIsEditableInPopup()
	{
		Object testObject = getEqualsTestObject();
		mockHelper.replayAll();
		if (isEditableInPopup)
		{
			assertTrue(classUnderTest.isEditableInPopup(testObject));
			assertTrue(classUnderTest.isEditableInPopup(null));
		}
		else
		{
			assertFalse(classUnderTest.isEditableInPopup(testObject));
			assertFalse(classUnderTest.isEditableInPopup(null));
		}
		mockHelper.verifyAll();
	}

	@Test
	public void testNeedToReRead()
	{
		mockHelper.replayAll();
		
		assertFalse(classUnderTest.needToReRead(null));
		mockHelper.verifyAll();
	}

	@Test
	public void testUseBinaryEditingPanel()
	{
		mockHelper.replayAll();
		classUnderTest.useBinaryEditingPanel();
		mockHelper.verifyAll();
	}

	@Test
	public void testAreEqual()
	{
		Object testObject = getEqualsTestObject();
		mockHelper.replayAll();
		assertFalse(classUnderTest.areEqual(testObject, null));
		mockHelper.verifyAll();

	}

	protected abstract Object getEqualsTestObject();

	@Test
	public void testTextComponents()
	{
		JTextField tf = classUnderTest.getJTextField();
		tf.setText("111111111111");
		testKeyListener(tf);
		JTextArea ta = classUnderTest.getJTextArea(null);
		ta.setText("111111111111");
		testKeyListener(ta);
	}

	public void testKeyListener(Component c)
	{
		KeyListener[] listeners = c.getKeyListeners();
		if (listeners.length > 0)
		{
			KeyListener listener = listeners[0];
			KeyEvent e = new KeyEvent(c, -1, 1111111111l, -1, -1, (char) KeyEvent.VK_ENTER);
			
			listener.keyTyped(e);
		}
	}

}