package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
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
import net.sourceforge.squirrel_sql.fw.gui.ReadTypeCombo;
import net.sourceforge.squirrel_sql.fw.gui.RightLabel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class DataTypeClob extends BaseDataTypeComponent
	implements IDataTypeComponent
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataTypeClob.class);

	
	private boolean _isNullable;

	
	private JTable _table;

	
	private IRestorableTextComponent _textComponent;

	
	
	
	
	
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();

	
	private static final String thisClassName =
		"net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeClob";


	
	private static int LARGE_COLUMN_DEFAULT_READ_LENGTH = 255;

	
	
	private static boolean propertiesAlreadyLoaded = false;

	
	private static boolean _readClobs = false;

	
	private static boolean _readCompleteClobs = false;

	
	private static int _readClobsSize = LARGE_COLUMN_DEFAULT_READ_LENGTH;

	
	private static boolean _makeNewlinesVisibleInCell = true;

    
	
	public DataTypeClob(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();

		loadProperties();
	}


	
	private static void loadProperties() {

		if (propertiesAlreadyLoaded == false) {
			
			_readClobs = false;	
			String readClobsString = DTProperties.get(thisClassName, "readClobs");
			if (readClobsString != null && readClobsString.equals("true"))
				_readClobs = true;

			_readCompleteClobs = false;	
			String readCompleteClobsString = DTProperties.get(thisClassName, "readCompleteClobs");
			if (readCompleteClobsString != null && readCompleteClobsString.equals("true"))
				_readCompleteClobs = true;
            
			_readClobsSize = LARGE_COLUMN_DEFAULT_READ_LENGTH;	
			String readClobsSizeString = DTProperties.get(thisClassName, "readClobsSize");
			if (readClobsSizeString != null)
				_readClobsSize = Integer.parseInt(readClobsSizeString);

			_makeNewlinesVisibleInCell = true;	
			String makeNewlinesVisibleString = DTProperties.get(thisClassName, "makeNewlinesVisibleInCell");
			if (makeNewlinesVisibleString != null && makeNewlinesVisibleString.equals("false"))
				_makeNewlinesVisibleInCell = false;

			propertiesAlreadyLoaded = true;
		}
	}

	
	public String getClassName() {
		return "net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor";
	}

    
    public static boolean getReadCompleteClob() {
        return _readCompleteClobs;
    }    

    
    public static void setReadCompleteClob(boolean val) {
        _readCompleteClobs = val;
    }
    
	

	
	public String renderObject(Object value) {
		String text = (String)_renderer.renderObject(value);
		if (_makeNewlinesVisibleInCell){
		    text = text.replaceAll("\n", "/\\n");
		}
		return text;
	}

	
	public boolean isEditableInCell(Object originalValue) {
		
		ClobDescriptor cdesc = (ClobDescriptor)originalValue;

		if (wholeClobRead(cdesc)) {
			
			
			if ( cdesc != null && cdesc.getData() != null && cdesc.getData().indexOf('\n') > -1)
					return false;
				else return true;
		}

		
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
						(RestorableJTextField)DataTypeClob.this._textComponent,
						evt, DataTypeClob.this._table);
					CellDataPopup.showDialog(DataTypeClob.this._table,
						DataTypeClob.this._colDef, tableEvt, true);
				}
			}
		});	

		return (JTextField)_textComponent;
	}

	
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		
		if (value.equals("<null>"))
			return null;

		

		
		
		
		
		ClobDescriptor cdesc;
		if (originalValue == null) {
			
			cdesc = new ClobDescriptor(null, value, true, true, 0);
		}
		else {
			
			cdesc = (ClobDescriptor)originalValue;

			
			
			cdesc = new ClobDescriptor(cdesc.getClob(),value,
				true, true, 0);
		}
		return cdesc;

	}

	
	public boolean useBinaryEditingPanel() {
		return false;
	}


	

	
	public boolean isEditableInPopup(Object originalValue) {
		
		
		return wholeClobRead((ClobDescriptor)originalValue);
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

				
				
				
				JTextComponent _theComponent = (JTextComponent)DataTypeClob.this._textComponent;
				String text = _theComponent.getText();


				
				
				

				if ( DataTypeClob.this._isNullable) {

					
					if (text.equals("<null>")) {
						if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							
							DataTypeClob.this._textComponent.restoreText();
							e.consume();
						}
						else {
							
							DataTypeClob.this._textComponent.updateText("");
							
						}
					}
					else {
						
						if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							if (text.length() <= 1 ) {
								
								DataTypeClob.this._textComponent.updateText("<null>");
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

	
	private boolean wholeClobRead(ClobDescriptor cdesc) {
		if (cdesc == null)
			return true;	

		if (cdesc.getWholeClobRead())
			return true;	

		
		try {
			String data = cdesc.getClob().getSubString(1, (int)cdesc.getClob().length());

			
			cdesc.setClobRead(true);
			cdesc.setData(data);
			cdesc.setWholeClobRead(true);
			cdesc.setUserSetClobLimit(0);

			
			 return true;
		}
		catch (Exception ex) {
			cdesc.setClobRead(false);
			cdesc.setWholeClobRead(false);
			cdesc.setData(null);
			
			
			return false;
		}
	}

	

	 
	public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
		throws java.sql.SQLException
	{
		return staticReadResultSet(rs, index);
	}


	public static Object staticReadResultSet(ResultSet rs, int index)
		throws java.sql.SQLException
	{
		
		
		
		
		Clob clob = rs.getClob(index);

		if (rs.wasNull())
			return null;

		
		
		if (_readClobs)
		{
			
			String clobData = null;
			if (clob != null)
			{
				int len = (int)clob.length();
				if (len > 0)
				{
					int charsToRead = len;
					if ( ! _readCompleteClobs)
					{
						charsToRead = _readClobsSize;
					}
					if (charsToRead > len)
					{
						charsToRead = len;
					}
					clobData = clob.getSubString(1, charsToRead);
				}
			}

			
			boolean wholeClobRead = false;
			if (_readCompleteClobs || clobData == null ||
				clobData.length() < _readClobsSize)
			{
				wholeClobRead = true;
			}

			return new ClobDescriptor(clob, clobData, true, wholeClobRead,
				_readClobsSize);
		}
		else
		{
			
			return new ClobDescriptor(clob, null, false, false, 0);
		}
	}


	
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
		if (value == null || ((ClobDescriptor)value).getData() == null)
			return _colDef.getLabel() + " IS NULL";
		else
			return "";	
	}

	
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws java.sql.SQLException {
		if (value == null || ((ClobDescriptor)value).getData() == null) {
			pstmt.setNull(position, _colDef.getSqlType());
		}
		else {
			
			ClobDescriptor cdesc = (ClobDescriptor)value;

			
			
			
			
			
			pstmt.setCharacterStream(position, new StringReader(cdesc.getData()),
				cdesc.getData().length());
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

		 
		 
		 StringBuffer messageBuffer = new StringBuffer();
		 validateAndConvertInPopup(fileText, null, messageBuffer);
		 if (messageBuffer.length() > 0) {
			 
			 throw new IOException(
				 "Text does not represent data of type "+getClassName()+
				 ".  Text was:\n"+fileText);
		 }

		 
		 
		 return fileText;
	}


	 
	 public void exportObject(FileOutputStream outStream, String text)
		 throws IOException {

		 OutputStreamWriter outWriter = new OutputStreamWriter(outStream);

		 
		 StringBuffer messageBuffer = new StringBuffer();
		 validateAndConvertInPopup(text, null, messageBuffer);
		 if (messageBuffer.length() > 0) {
			 
			 throw new IOException(new String(messageBuffer));
		 }

		 
		outWriter.write(text);
		outWriter.flush();
		outWriter.close();
	 }


	

	 
	 public static OkJPanel getControlPanel() {

		

		 
		 
		 loadProperties();

		return new ClobOkJPanel();
	 }



	 
	 private static class ClobOkJPanel extends OkJPanel
	 {
        private static final long serialVersionUID = 6613369906375451603L;

        
		 
		 
		 private JCheckBox _showClobChk = new JCheckBox(s_stringMgr.getString("dataTypeBigDecimal.readContentsOnFirstLoad"));

		 
		 
		 private RightLabel _typeDropLabel = new RightLabel(s_stringMgr.getString("dataTypeBigDecimal.read2"));

		 
		 private ReadTypeCombo _clobTypeDrop = new ReadTypeCombo();

		 
		 private IntegerField _showClobSizeField = new IntegerField(5);

		 
		 private JCheckBox _makeNewlinesVisibleInCellChk =
			 
			 new JCheckBox(s_stringMgr.getString("dataTypeBigDecimal.newlinesAsbackslashN"));


		 public ClobOkJPanel()
		 {

			 
			 
			 _showClobChk.setSelected(_readClobs);
			 _showClobChk.addChangeListener(new ChangeListener()
			 {
				 public void stateChanged(ChangeEvent e)
				 {
					 _clobTypeDrop.setEnabled(_showClobChk.isSelected());
					 _typeDropLabel.setEnabled(_showClobChk.isSelected());
					 _showClobSizeField.setEnabled(_showClobChk.isSelected() &&
						 (_clobTypeDrop.getSelectedIndex() == 0));
				 }
			 });

			 
			 _clobTypeDrop = new ReadTypeCombo();
			 _clobTypeDrop.setSelectedIndex((_readCompleteClobs) ? 1 : 0);
			 _clobTypeDrop.addActionListener(new ActionListener()
			 {
				 public void actionPerformed(ActionEvent e)
				 {
					 _showClobSizeField.setEnabled(_clobTypeDrop.getSelectedIndex() == 0);
				 }
			 });

			 
			 _showClobSizeField = new IntegerField(5);
			 _showClobSizeField.setInt(_readClobsSize);

			 
			 _makeNewlinesVisibleInCellChk.setSelected(_makeNewlinesVisibleInCell);

			 
			 _clobTypeDrop.setEnabled(_readClobs);
			 _typeDropLabel.setEnabled(_readClobs);
			 _showClobSizeField.setEnabled(_readClobs && ! _readCompleteClobs);

			 

			 setLayout(new GridBagLayout());

			 
			 setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("dataTypeClob.typeClob")));
			 final GridBagConstraints gbc = new GridBagConstraints();
			 gbc.fill = GridBagConstraints.HORIZONTAL;
			 gbc.insets = new Insets(4, 4, 4, 4);
			 gbc.anchor = GridBagConstraints.WEST;

			 gbc.gridx = 0;
			 gbc.gridy = 0;

			 gbc.gridwidth = 1;
			 add(_showClobChk, gbc);

			 ++gbc.gridx;
			 add(_typeDropLabel, gbc);

			 ++gbc.gridx;
			 add(_clobTypeDrop, gbc);

			 ++gbc.gridx;
			 add(_showClobSizeField, gbc);

			 ++gbc.gridy;
			 gbc.gridx = 0;
			 gbc.gridwidth = GridBagConstraints.REMAINDER;
			 add(_makeNewlinesVisibleInCellChk, gbc);

		 } 


		 
		 public void ok()
		 {
			 
			 _readClobs = _showClobChk.isSelected();
			 DTProperties.put(
				 thisClassName,
				 "readClobs", Boolean.valueOf(_readClobs).toString());

			 _readCompleteClobs = (_clobTypeDrop.getSelectedIndex() == 0) ? false : true;
			 DTProperties.put(
				 thisClassName,
				 "readCompleteClobs", Boolean.valueOf(_readCompleteClobs).toString());

			 _readClobsSize = _showClobSizeField.getInt();
			 DTProperties.put(
				 thisClassName,
				 "readClobsSize", Integer.toString(_readClobsSize));

			 _makeNewlinesVisibleInCell = _makeNewlinesVisibleInCellChk.isSelected();
			 DTProperties.put(
				 thisClassName,
				 "makeNewlinesVisibleInCell", Boolean.valueOf(_makeNewlinesVisibleInCell).toString());
		 }

	 } 
}
