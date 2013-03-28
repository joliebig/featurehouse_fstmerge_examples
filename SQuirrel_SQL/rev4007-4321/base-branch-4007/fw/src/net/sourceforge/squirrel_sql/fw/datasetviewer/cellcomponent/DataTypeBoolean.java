package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.event.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;




public class DataTypeBoolean extends BaseDataTypeComponent
	implements IDataTypeComponent
{
	
	private boolean _isNullable;

	
	private boolean _isSigned;

	
	private int _scale;

	
	private JTable _table;
	
	
	private IRestorableTextComponent _textComponent;
	
	
	
	
	
	
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();


	
	public DataTypeBoolean(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();
	}
	
	
	public String getClassName() {
		return "java.lang.Boolean";
	}

	
	public boolean areEqual(Object obj1, Object obj2) {
		return ((Boolean)obj1).equals(obj2);
	}

	
	 
	
	public String renderObject(Object value) {
		return (String)_renderer.renderObject(value);
	}
	
	
	public boolean isEditableInCell(Object originalValue) {
		return true;	
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
						(RestorableJTextField)DataTypeBoolean.this._textComponent,
						evt, DataTypeBoolean.this._table);
					CellDataPopup.showDialog(DataTypeBoolean.this._table,
						DataTypeBoolean.this._colDef, tableEvt, true);
				}
			}
		});	

		return (JTextField)_textComponent;
	}

	
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		
		if (value.equals("<null>") || value.equals(""))
			return null;

		
		try {
			Object obj = Boolean.valueOf(value);
			return obj;
		}
		catch (Exception e) {
			messageBuffer.append(e.toString()+"\n");
			
			
			return null;
		}
	}

	
	public boolean useBinaryEditingPanel() {
		return false;
	}
	 

	
	
	
	public boolean isEditableInPopup(Object originalValue) {
		return true;
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
				char c = e.getKeyChar();
				
				
				
				
				JTextComponent _theComponent = (JTextComponent)DataTypeBoolean.this._textComponent;
				String text = _theComponent.getText();

				
				
				if ("TtYy1".indexOf(c) > -1) {
					
					DataTypeBoolean.this._textComponent.updateText("true");
					e.consume();
					return;
				}
				if ("FfNn0".indexOf(c) > -1) {
					
					DataTypeBoolean.this._textComponent.updateText("false");
					e.consume();
					return;
				}

				
				
				
				
				if (c == KeyEvent.VK_TAB || c == KeyEvent.VK_ENTER) {
					
					int index = text.indexOf(c);
                    if (index != -1) {
    					if (index == text.length() -1) {
    						text = text.substring(0, text.length()-1);	
    					}
    					else {
    						text = text.substring(0, index) + text.substring(index+1);
    					}
    					((IRestorableTextComponent)_theComponent).updateText( text);
    					_theComponent.getToolkit().beep();
                    }
					e.consume();
				}

				
				

				if ( DataTypeBoolean.this._isNullable &&
					(c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {

					
					if (text.equals("<null>")) {
						
						DataTypeBoolean.this._textComponent.restoreText();
						e.consume();
					}
					else {
						
						DataTypeBoolean.this._textComponent.updateText("<null>");
						e.consume();
					}
				}
				else {
					
					
					
					
					_theComponent.getToolkit().beep();
					e.consume();
				}
			}
		}


	
	
	
	 
	 
	public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
		throws java.sql.SQLException {
		
		boolean data = rs.getBoolean(index);
		if (rs.wasNull()) {
			return null;
		} else {
		    return Boolean.valueOf(data);
		}
	}

	
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
		if (value == null 
                || value.toString() == null 
                || value.toString().length() == 0)
        {
			return _colDef.getLabel() + " IS NULL";
		
        } else {
            String bitValue = 
                DatabaseSpecificBooleanValue.getBooleanValue(value.toString(),
                                                             md);                   
            return _colDef.getLabel() + "=" + bitValue;
        }
	}
	
	
	
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws java.sql.SQLException {
		if (value == null) {
			pstmt.setNull(position, _colDef.getSqlType());
		}
		else {
			pstmt.setBoolean(position, ((Boolean)value).booleanValue());
		}
	}
	
	
	public Object getDefaultValue(String dbDefaultValue) {
		if (dbDefaultValue != null) {
			
			StringBuffer mbuf = new StringBuffer();
			Object newObject = validateAndConvert(dbDefaultValue, null, mbuf);
			
			
			
			
			if (mbuf.length() == 0)
				return newObject;
		}
		
		
		if (_isNullable) {
			return null;
		}
		
		return Boolean.valueOf(true);
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
}
