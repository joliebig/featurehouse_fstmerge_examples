
package net.sourceforge.squirrel_sql.client.update.gui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.swing.table.TableModel;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Test;

public abstract class AbstractTableModelTest extends BaseSQuirreLJUnit4TestCase
{

	protected TableModel classUnderTest = null;
	
	 
	protected int[] editableColumns = new int[0];

	public AbstractTableModelTest() {
		super();
	}

	@After
   public void tearDown() throws Exception
   {
   	classUnderTest = null;
   	mockHelper.verifyAll();
   }

	@Test
   public void testIsCellEditable()
   {
   	for (int colIdx = 0; colIdx < classUnderTest.getColumnCount(); colIdx++ ) {
   		for (int rowIdx = 0; rowIdx < classUnderTest.getRowCount(); rowIdx++) {
   			if (isEditableColumn(colIdx)) {
   				assertTrue("row="+rowIdx+" col="+colIdx+" is not editable",classUnderTest.isCellEditable(rowIdx, colIdx));
   			} else {
   				assertFalse("row="+rowIdx+" col="+colIdx+" is editable",classUnderTest.isCellEditable(rowIdx, colIdx));
   			}
   			
   		}
   	}
   	
   }

	private boolean isEditableColumn(int colIdx)
   {
   	for (int i = 0; i < editableColumns.length; i++) {
   		if (colIdx == editableColumns[i]) {
   			return true;
   		}
   	}
      return false;
   }

	@Test
   public void testGetValueAt()
   {
   	for (int rowIdx = 0; rowIdx < classUnderTest.getRowCount(); rowIdx++) {
   		for (int colIdx = 0; colIdx < classUnderTest.getColumnCount(); colIdx++) {
   			assertNotNull(classUnderTest.getValueAt(rowIdx, colIdx));
   		}
   	}
   }

	@Test
   public void testGetValueAt_InvalidColumn()
   {
		try {
			classUnderTest.getValueAt(0, classUnderTest.getColumnCount());
			fail("Expected an exception for call to getValue with a column that is one higher that the " +
					"highest column index allowed by this table model");
		} catch (IndexOutOfBoundsException e) {
			
		} catch (IllegalArgumentException e) {
			
		}
		
   }

	@Test
   public void testGetColumnNameInt()
   {
   	assertNotNull(classUnderTest.getColumnName(0));
   }

	@Test
   public void testGetColumnClassInt()
   {
   	for (int i = 0; i < classUnderTest.getColumnCount(); i++) {
   		assertNotNull(classUnderTest.getColumnClass(i));
   	}
   }

}