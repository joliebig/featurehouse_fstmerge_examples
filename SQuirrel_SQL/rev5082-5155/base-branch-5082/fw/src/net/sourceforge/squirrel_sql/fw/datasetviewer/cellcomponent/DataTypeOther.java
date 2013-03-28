package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.event.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.text.JTextComponent;
import javax.swing.JCheckBox;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;


public class DataTypeOther extends BaseDataTypeComponent
	implements IDataTypeComponent
{
	
	private boolean _isNullable;

	
	private JTable _table;
	
	
	private IRestorableTextComponent _textComponent;
	
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataTypeOther.class);	

	
	
	
	
	
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();	


	
	private static final String thisClassName =
		"net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeOther";

	
	 
	 private static boolean propertiesAlreadyLoaded = false;
	 
		 
	
	private static boolean _readSQLOther = false;
	

	
	public DataTypeOther(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();

		loadProperties();
	}
	
	
	private static void loadProperties() {
		
		
		
		
		if (propertiesAlreadyLoaded == false) {
			
			_readSQLOther = false;	
			String readSQLOtherString = DTProperties.get(
				thisClassName, "readSQLOther");
			if (readSQLOtherString != null && readSQLOtherString.equals("true"))
				_readSQLOther = true;

			propertiesAlreadyLoaded = true;
		}
	}
	
	
	public String getClassName() {
		return "java.lang.String";
	}

	
	public boolean areEqual(Object obj1, Object obj2) {
		return ((String)obj1).equals(obj2);
	}
	

	
	 
	 	
	
	public String renderObject(Object value) {
		return (String)_renderer.renderObject(value);
	}
	
	
	public boolean isEditableInCell(Object originalValue) {
		return false;
	}

	
	public boolean needToReRead(Object originalValue) {
		
		
		return false;
	}
		
	
	public JTextField getJTextField() {
		_textComponent = new RestorableJTextField();
		
		
		((RestorableJTextField)_textComponent).addKeyListener(new KeyTextHandler());
				
		
		
		
		
		
		
		((RestorableJTextField)_textComponent).addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					MouseEvent tableEvt = SwingUtilities.convertMouseEvent(
						(RestorableJTextField)DataTypeOther.this._textComponent,
						evt, DataTypeOther.this._table);
					CellDataPopup.showDialog(DataTypeOther.this._table,
						DataTypeOther.this._colDef, tableEvt, true);
				}
			}
		});	

		return (JTextField)_textComponent;
	}
	
	
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		return null;
	}

	
	public boolean useBinaryEditingPanel() {
		return false;
	}
	 
	
	
	

	
	
	public boolean isEditableInPopup(Object originalValue) {
		return false;
	}
	
	
	 public JTextArea getJTextArea(Object value) {
		_textComponent = new RestorableJTextArea();
	
		
		
		((RestorableJTextArea)_textComponent).setText(renderObject(value));
		
		
		((RestorableJTextArea)_textComponent).addKeyListener(new KeyTextHandler());
		
		return (RestorableJTextArea)_textComponent;
	 }

	
	public Object validateAndConvertInPopup(String value, Object originalValue, StringBuffer messageBuffer) {
		return validateAndConvert(value, originalValue, messageBuffer);
	}


	

	
	
	 private class KeyTextHandler extends KeyAdapter {
		
		public void keyTyped(KeyEvent e) {
			
			
			
			JTextComponent _theComponent = (JTextComponent)DataTypeOther.this._textComponent;
			e.consume();
			_beepHelper.beep(_theComponent);
		}
	}


	
	
	
	 
	 
	public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
		throws java.sql.SQLException {

			
		String data = null;
		if (_readSQLOther)
		{
			
			
			
			
			
			data = rs.getString(index);
		}
		else
		{
			data = s_stringMgr.getString("DataTypeOther.other");
		}
		
		if (rs.wasNull())
			return null;
		else return data;

		




	}

	
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
		if (value == null || value.toString() == null )
			return _colDef.getLabel() + " IS NULL";
		else
			return _colDef.getLabel() + "='" + value.toString() + "'";
	}
	
	
	
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws java.sql.SQLException {
		if (value == null) {
			pstmt.setNull(position, _colDef.getSqlType());
		}
		else {
			pstmt.setString(position, ((String)value));
		}
	}
	
	
	public Object getDefaultValue(String dbDefaultValue) {
		if (dbDefaultValue != null) {
			
			StringBuffer mbuf = new StringBuffer();
			Object newObject = validateAndConvert(dbDefaultValue, null, mbuf);
			
			
			
			
			if (mbuf.length() == 0)
				return newObject;
		}
		
		
		if (_isNullable)
			return null;
		
		
		
		return null;
	}
	
	
	
	 
	 
	 
	 public boolean canDoFileIO() {
	 	return false;
	 }
	 
	 
	public String importObject(FileInputStream inStream)
	 	throws IOException {
	 	
		throw new IOException("Can not import data type OTHER");
	}

	 	 
	 
	 public void exportObject(FileOutputStream outStream, String text)
	 	throws IOException 	 {	
	 	
		throw new IOException("Can not export data type OTHER");
	 }	
	 

		  
	 
	 
	 public static OkJPanel getControlPanel() {
	 	
		
		 
		 
		 
		 loadProperties();
		 
		return new SQLOtherOkJPanel();
	 }
	 
	 
	 
	 
	 private static class SQLOtherOkJPanel extends OkJPanel {

	     private static final long serialVersionUID = 9034966488591013288L;
	     
	     
	     private JCheckBox _showSQLOtherChk = new JCheckBox(
	             
	             s_stringMgr.getString("dataTypeOther.readContentsWhenLoaded"));


		public SQLOtherOkJPanel() {
		 	 
			
			
			_showSQLOtherChk.setSelected(_readSQLOther);

			
 		
			
			setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("dataTypeOther.sqlOtherType")));

			add(_showSQLOtherChk);

		} 
	 
	 
		
		public void ok() {
			
			_readSQLOther = _showSQLOtherChk.isSelected();
			DTProperties.put(
				thisClassName,
				"readSQLOther", Boolean.valueOf(_readSQLOther).toString());
		}
	 
	 } 
}
