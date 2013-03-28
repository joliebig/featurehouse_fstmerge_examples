package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;


public abstract class AbstractDataType extends BaseSQuirreLTestCase {

	protected IDataTypeComponent iut = null;
		
	public abstract void testTextComponents();

	public AbstractDataType() {
		super();
	}

	protected ColumnDisplayDefinition getColDef() {
		return new ColumnDisplayDefinition(10, "aLabel");
	}
	
	protected void testTextComponents(IDataTypeComponent dtc) {
		JTextField tf = dtc.getJTextField();
		tf.setText("111111111111");
		testKeyListener(tf);
		JTextArea ta = dtc.getJTextArea(null);
		ta.setText("111111111111");
		testKeyListener(ta);		
	}

	protected void testKeyListener(Component c) {
		KeyListener[] listeners = c.getKeyListeners();
		if (listeners.length > 0) {
			KeyListener listener = listeners[0];
			KeyEvent e = new KeyEvent(c, -1, 1111111111l, -1, -1, (char)KeyEvent.VK_ENTER);
			
			listener.keyTyped(e);
		}
	}

}