package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;



public class DataTypeString extends BaseDataTypeComponent
	implements IDataTypeComponent
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataTypeString.class);

	
	private boolean _isNullable;

	
	private int _columnSize;

	
	private JTable _table;

	
	private IRestorableTextComponent _textComponent;

	
	
	
	
	
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();

	
	private final static int DEFAULT_LIMIT_READ_LENGTH = 100;

	
	private static final String thisClassName =
		"net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeString";

	
	
	private static boolean propertiesAlreadyLoaded = false;

	
	private static boolean _makeNewlinesVisibleInCell = true;

	
	private static boolean _useLongInWhere = true;

	
	private static boolean _limitRead = false;

	
	private static int _limitReadLength = DEFAULT_LIMIT_READ_LENGTH;

	
	private static boolean _limitReadOnSpecificColumns = false;

	
	private static HashMap<String, String> _limitReadColumnNameMap = 
	    new HashMap<String, String>();


	
	public DataTypeString(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();
		_columnSize = colDef.getColumnSize();

		loadProperties();
	}

	
	protected DataTypeString() {
	    
	}
	
	
	private static void loadProperties() {

		if (propertiesAlreadyLoaded == false) {
			
			_makeNewlinesVisibleInCell = true;	
			String makeNewlinesVisibleString = DTProperties.get(thisClassName, "makeNewlinesVisibleInCell");
			if (makeNewlinesVisibleString != null && makeNewlinesVisibleString.equals("false"))
				_makeNewlinesVisibleInCell = false;

			_useLongInWhere = true;	
			String useLongInWhereString = DTProperties.get(thisClassName, "useLongInWhere");
			if (useLongInWhereString != null && useLongInWhereString.equals("false"))
				_useLongInWhere = false;

			_limitRead = false;	
			String limitReadString = DTProperties.get(thisClassName, "limitRead");
			if (limitReadString != null && limitReadString.equals("true"))
				_limitRead = true;

			_limitReadLength = DEFAULT_LIMIT_READ_LENGTH;	
			String limitReadLengthString = DTProperties.get(thisClassName, "limitReadLength");
			if (limitReadLengthString != null)
				_limitReadLength = Integer.parseInt(limitReadLengthString);

			_limitReadOnSpecificColumns = false;	
			String limitReadOnSpecificColumnsString = DTProperties.get(thisClassName, "limitReadOnSpecificColumns");
			if (limitReadOnSpecificColumnsString != null && limitReadOnSpecificColumnsString.equals("true"))
				_limitReadOnSpecificColumns = true;

			
			
			_limitReadColumnNameMap.clear();	

			String nameString = DTProperties.get(thisClassName, "limitReadColumnNames");
			int start = 0;
			int end;
			String name;

			while (nameString != null && start < nameString.length()) {
				end = nameString.indexOf(',', start + 1);
				if (end > -1) {
					name = nameString.substring(start+1, end);
					start = end;
				}
				else {
					name = nameString.substring(start+1);
					start = nameString.length();
				}

				_limitReadColumnNameMap.put(name, null);
			}

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
		String text = (String)_renderer.renderObject(value);
		if (_makeNewlinesVisibleInCell) {
			 text = text.replaceAll("\n", "/\\n");
		}
		return text;
	}

	
	public boolean isEditableInCell(Object originalValue) {
		
		 if (originalValue != null && ((String)originalValue).indexOf('\n') > -1)
			 return false;
		else return true;
	}

	
	public boolean needToReRead(Object originalValue) {
		
		if (_limitRead == false)
			return false;

		
		if (originalValue == null)
			return false;

		
		
		
		if (((String)originalValue).length() < _limitReadLength)
			return false;

		
		
		if (((String)originalValue).length() > _limitReadLength)
			return false;

		
		
		if (_limitReadOnSpecificColumns == false)
			return true;

		
		
		if (_limitReadColumnNameMap.containsKey(_colDef.getLabel()))
			return true;	
		else return false;	
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
						(RestorableJTextField)DataTypeString.this._textComponent,
						evt, DataTypeString.this._table);
					CellDataPopup.showDialog(DataTypeString.this._table,
						DataTypeString.this._colDef, tableEvt, true);
				}
			}
		});	

		return (JTextField)_textComponent;
	}

	
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		
		if (value.equals("<null>"))
			return null;

		
		return value;	
	}

	
	public boolean useBinaryEditingPanel() {
		return false;
	}



	


	
	public boolean isEditableInPopup(Object originalValue) {
		
		
		
		
		return ! needToReRead(originalValue);
	}

	
	 public JTextArea getJTextArea(Object value) {
		_textComponent = new RestorableJTextArea();

		
		
		
		
		
		((RestorableJTextArea)_textComponent).setText((String)_renderer.renderObject(value));

		
		((RestorableJTextArea)_textComponent).addKeyListener(new KeyTextHandler());

		return (RestorableJTextArea)_textComponent;
	 }

	
	public Object validateAndConvertInPopup(String value, Object originalValue, StringBuffer messageBuffer) {
		return validateAndConvert(value, originalValue, messageBuffer);
	}


	


	
	 private class KeyTextHandler extends BaseKeyTextHandler {
		
		public void keyTyped(KeyEvent e) {
			char c = e.getKeyChar();

			
			
			
			JTextComponent _theComponent = (JTextComponent)DataTypeString.this._textComponent;
			String text = _theComponent.getText();

			
			
			

			
			if (DataTypeString.this._columnSize > 0 &&
				text.length()>= DataTypeString.this._columnSize &&
				c != KeyEvent.VK_BACK_SPACE &&
				c != KeyEvent.VK_DELETE) {
				
				e.consume();
				_theComponent.getToolkit().beep();

				
			}

			
			
			

			if ( DataTypeString.this._isNullable) {

				
				if (text.equals("<null>")) {
					if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
						
						DataTypeString.this._textComponent.restoreText();
						e.consume();
					}
					else {
						
						DataTypeString.this._textComponent.updateText("");
						
					}
				}
				else {
					
					
					
					if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
						if (text.length() == 0 ) {
							
							DataTypeString.this._textComponent.updateText("<null>");
							e.consume();
						}
					}
				}
			}
			else {
                
                
                handleNotNullableField(text, c, e, _textComponent);
			}
		}
	}




	

	 
	public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
		throws java.sql.SQLException {

		String data = rs.getString(index);
		if (rs.wasNull())
			return null;
		else {
			
			
			
			if (limitDataRead == true && _limitRead == true
				&& data.length() >= _limitReadLength) {

				
				if (_limitReadOnSpecificColumns == false ||
					(_limitReadOnSpecificColumns == true &&
						_limitReadColumnNameMap.containsKey(_colDef.getLabel()))) {
					
					data = data.substring(0, _limitReadLength);
				}

			}
			return data;

		}
	}

	
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
		
		
		
		if (_colDef.getSqlType() == Types.LONGVARCHAR &&
			_useLongInWhere == false)
			return "";	

		if (value == null || value.toString() == null )
			return _colDef.getLabel() + " IS NULL";
		else {
			
			
			
			if ( ! needToReRead(value))
				return _colDef.getLabel() + "='" + escapeLine(value.toString(), md) + "'";
			else return "";	
		}
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

		
		return "";
	}

	
	static public String escapeLine(String s, ISQLDatabaseMetaData md) {
		String retvalue = s;
		if (s.indexOf ("'") != -1 ) {
			StringBuffer hold = new StringBuffer();
			char c;
			for(int i=0; i < s.length(); i++ ) {
				if ((c=s.charAt(i)) == '\'' ) {
					hold.append ("''");
				}
				else {
					hold.append(c);
				}
			}
			retvalue = hold.toString();
		}
		return DatabaseSpecificEscape.escapeSQL(retvalue, md);
	}

	


	 
	 public boolean canDoFileIO() {
		 return true;
	 }

	 
	public String importObject(FileInputStream inStream)
		 throws IOException {

		 InputStreamReader inReader = new InputStreamReader(inStream);

		 int fileSize = inStream.available();

		 char charBuf[] = new char[fileSize];

		 int count = inReader.read(charBuf, 0, fileSize);

		 if (count != fileSize)
			 throw new IOException(
				 "Could read only "+ count +
				 " chars from a total file size of " + fileSize +
				 ". Import failed.");

		 
		 
		 
		 
		 String fileText;
		 if (charBuf[count-1] == KeyEvent.VK_ENTER)
			 fileText = new String(charBuf, 0, count-1);
		 else fileText = new String(charBuf);

		 
		 if (_columnSize > 0 && fileText.length() > _columnSize)
			 throw new IOException(
				 "File contains "+fileText.length()+
				 " characters which exceeds this column's limit of "+
				 _columnSize+".\nImport Aborted.");

		 return fileText;
	}


	 
	 public void exportObject(FileOutputStream outStream, String text)
		 throws IOException {

		 OutputStreamWriter outWriter = new OutputStreamWriter(outStream);

		 
		 outWriter.write(text);
		outWriter.flush();
		outWriter.close();
	 }


	

	 
	 public static OkJPanel getControlPanel() {

		

		 
		 
		 loadProperties();

		return new ClobOkJPanel();
	 }



	 
	 private static class ClobOkJPanel extends OkJPanel {
		

        private static final long serialVersionUID = -578848466466561988L;

        
		private JCheckBox _makeNewlinesVisibleInCellChk =
			
			new JCheckBox(s_stringMgr.getString("dataTypeString.newlines"));

		
		
		private JCheckBox _useLongInWhereChk =
			
			new JCheckBox(s_stringMgr.getString("dataTypeString.allowLongVarchar"));

		
		private JCheckBox _limitReadChk =
			
			new JCheckBox(s_stringMgr.getString("dataTypeString.limitSize"));

		
		private IntegerField _limitReadLengthTextField =
			new IntegerField(5);

		
		private JCheckBox _limitReadOnSpecificColumnsChk =
			
			new JCheckBox(s_stringMgr.getString("dataTypeString.limitReadOnly"));

		
		private JTextArea _limitReadColumnNameTextArea =
			new JTextArea(5, 12);


		public ClobOkJPanel() {

			

			
			_makeNewlinesVisibleInCellChk.setSelected(_makeNewlinesVisibleInCell);

			
			_useLongInWhereChk.setSelected(_useLongInWhere);

			
			_limitReadChk.setSelected(_limitRead);
			_limitReadChk.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					_limitReadLengthTextField.setEnabled(_limitReadChk.isSelected());
					_limitReadOnSpecificColumnsChk.setEnabled(_limitReadChk.isSelected());
					_limitReadColumnNameTextArea.setEnabled(_limitReadChk.isSelected() &&
						(_limitReadOnSpecificColumnsChk.isSelected()));
				}
			});


			
			_limitReadLengthTextField.setInt(_limitReadLength);

			
			_limitReadOnSpecificColumnsChk.setSelected(_limitReadOnSpecificColumns);
			_limitReadOnSpecificColumnsChk.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					_limitReadColumnNameTextArea.setEnabled(
						_limitReadOnSpecificColumnsChk.isSelected());
				}
			});

			
			Iterator<String> names = _limitReadColumnNameMap.keySet().iterator();
			StringBuffer namesText = new StringBuffer();
			while (names.hasNext()) {
				if (namesText.length() > 0)
					namesText.append("\n" + names.next());
				else namesText.append(names.next());
			}
			_limitReadColumnNameTextArea.setText(namesText.toString());

			
			_limitReadLengthTextField.setEnabled(_limitReadChk.isSelected());
			_limitReadOnSpecificColumnsChk.setEnabled(_limitReadChk.isSelected());
			_limitReadColumnNameTextArea.setEnabled(_limitReadChk.isSelected() &&
				(_limitReadOnSpecificColumnsChk.isSelected()));;

			

			setLayout(new GridBagLayout());

			setBorder(BorderFactory.createTitledBorder(
				
				s_stringMgr.getString("dataTypeString.typeChar")));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;

			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(_makeNewlinesVisibleInCellChk, gbc);

			gbc.gridx = 0;
			gbc.gridy++;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(_useLongInWhereChk, gbc);

			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			add(_limitReadChk, gbc);

			gbc.gridx++;
			gbc.gridwidth = 1;
			add(_limitReadLengthTextField, gbc);

			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			add(_limitReadOnSpecificColumnsChk, gbc);

			gbc.gridx++;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			JScrollPane scrollPane = new JScrollPane();

			
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			scrollPane.setViewportView(_limitReadColumnNameTextArea);
			add(scrollPane, gbc);


		} 


		
		public void ok() {
			
			_makeNewlinesVisibleInCell = _makeNewlinesVisibleInCellChk.isSelected();
			DTProperties.put(thisClassName,
				"makeNewlinesVisibleInCell", Boolean.valueOf(_makeNewlinesVisibleInCell).toString());

			_useLongInWhere = _useLongInWhereChk.isSelected();
			DTProperties.put(thisClassName,
				"useLongInWhere", Boolean.valueOf(_useLongInWhere).toString());

			_limitRead = _limitReadChk.isSelected();
			DTProperties.put(thisClassName,
				"limitRead", Boolean.valueOf(_limitRead).toString());

			_limitReadLength = _limitReadLengthTextField.getInt();
			DTProperties.put(thisClassName,
				"limitReadLength", Integer.toString(_limitReadLength));

			_limitReadOnSpecificColumns = _limitReadOnSpecificColumnsChk.isSelected();
			DTProperties.put(thisClassName,
				"limitReadOnSpecificColumns", Boolean.valueOf(_limitReadOnSpecificColumns).toString());

			

			
			_limitReadColumnNameMap.clear();
			
			String columnNameText = _limitReadColumnNameTextArea.getText();

			int start = 0;
			int end;
			String name;
			String propertyString = "";

			while (start < columnNameText.length()) {
				
				end = columnNameText.indexOf('\n', start+1);
				if (end > -1) {
					name = columnNameText.substring(start, end);
					start = end;
				}
				else {
					name = columnNameText.substring(start);
					start = columnNameText.length();
				}

				
				name = name.trim().toUpperCase();
				if (name.length() == 0)
					continue;	

				_limitReadColumnNameMap.put(name.trim().toUpperCase(), null);

				
				propertyString += "," + name.trim().toUpperCase();
			}	

			DTProperties.put(thisClassName,
				"limitReadColumnNames", propertyString);

		}	

	 } 

}
