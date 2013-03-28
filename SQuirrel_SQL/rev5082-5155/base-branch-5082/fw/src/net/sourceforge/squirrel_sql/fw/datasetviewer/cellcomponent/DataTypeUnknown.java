package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.event.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.BorderFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;



public class DataTypeUnknown extends BaseDataTypeComponent
	implements IDataTypeComponent
{
	
	private boolean _isNullable;

	
	private JTable _table;
	
	
	private IRestorableTextComponent _textComponent;
	
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataTypeUnknown.class);
	
	
	
	
	
	
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();



	
	private static final String thisClassName =
		"net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeUnknown";

	
	 
	 private static boolean propertiesAlreadyLoaded = false;
	 
		 
	
	private static boolean _readUnknown = false;
	
	

	
	public DataTypeUnknown(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();

		loadProperties();
	}
	
	
	private static void loadProperties() {
		
		
		
		
		if (propertiesAlreadyLoaded == false) {
			
			_readUnknown = false;	
			String readUnknownString = DTProperties.get(
				thisClassName, "readUnknown");
			if (readUnknownString != null && readUnknownString.equals("true"))
				_readUnknown = true;

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
						(RestorableJTextField)DataTypeUnknown.this._textComponent,
						evt, DataTypeUnknown.this._table);
					CellDataPopup.showDialog(DataTypeUnknown.this._table,
						DataTypeUnknown.this._colDef, tableEvt, true);
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
				
				
				
				JTextComponent _theComponent = (JTextComponent)DataTypeUnknown.this._textComponent;
				_beepHelper.beep(_theComponent);
				e.consume();
			}
		}


	
	
	
	 
	 
	public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
		throws java.sql.SQLException {

		String data = null;
		if (_readUnknown)
		{
			
			
			
			
			
			data = rs.getString(index);
		}
		else
		{
			data = s_stringMgr.getString("DataTypeUnknown.unknown",
			                             _colDef.getSqlType() );
		}
		
		if (rs.wasNull())
			return null;
		else return data;
	}

	
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
		if (value == null || value.toString() == null || value.toString().length() == 0)
			return _colDef.getLabel() + " IS NULL";
		else
			return "";
	}
	
	
	
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws java.sql.SQLException {

		throw new java.sql.SQLException("Can not update data of type OTHER");
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
	 	throws IOException {
	 	
		throw new IOException("Can not export data type OTHER");
	 }



		  
	 
	 
	 public static OkJPanel getControlPanel() {
	 	
		
		 
		 
		 
		 loadProperties();
		 
		return new UnknownOkJPanel();
	 }
	 
	 
	 
	 
	 private static class UnknownOkJPanel extends OkJPanel {
        private static final long serialVersionUID = 1L;
        
		
		private JCheckBox _showUnknownChk = new JCheckBox(
			
			s_stringMgr.getString("dataTypeUnknown.readContentsOnLoad"));


		public UnknownOkJPanel() {
		 	 
			
			
			_showUnknownChk.setSelected(_readUnknown);

			
 		
			setBorder(BorderFactory.createTitledBorder(
				
				s_stringMgr.getString("dataTypeUnknown.unknownTypes")));

			add(_showUnknownChk);

		} 
	 
	 
		
		public void ok() {
			
			_readUnknown = _showUnknownChk.isSelected();
			DTProperties.put(
				thisClassName,
				"readUnknown", Boolean.valueOf(_readUnknown).toString());
		}
	 
	 } 
}
