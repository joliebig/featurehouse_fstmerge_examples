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



public class DataTypeLong extends BaseDataTypeComponent
	implements IDataTypeComponent
{
	
	private boolean _isNullable;

	
	private boolean _isSigned;

	
	private int _scale;

	
	private JTable _table;
	
	
	private IRestorableTextComponent _textComponent;
	
	
	
	
	
	
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();


	
	public DataTypeLong(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();
		_isSigned = colDef.isSigned();
		_scale = colDef.getScale();
	}
	
	
	public String getClassName() {
		return "java.lang.Long";
	}

	
	public boolean areEqual(Object obj1, Object obj2) {
		return (obj1).equals(obj2);
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
						(RestorableJTextField)DataTypeLong.this._textComponent,
						evt, DataTypeLong.this._table);
					CellDataPopup.showDialog(DataTypeLong.this._table,
						DataTypeLong.this._colDef, tableEvt, true);
				}
			}
		});	

		return (JTextField)_textComponent;
	}

	
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		
		if (value.equals("<null>") || value.equals(""))
			return null;

		
		try {
			Object obj = new Long(value);
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

		
	
	
	 private class KeyTextHandler extends BaseKeyTextHandler {
	 	public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				
				
				
				
				JTextComponent _theComponent = (JTextComponent)DataTypeLong.this._textComponent;
				String text = _theComponent.getText();
	
				
				if ( ! DataTypeLong.this._isSigned && c == '-') {
					
					_theComponent.getToolkit().beep();
					e.consume();
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

				if ( ! ( Character.isDigit(c) ||
					(c == '-') ||
					(c == KeyEvent.VK_BACK_SPACE) ||
					(c == KeyEvent.VK_DELETE) ) ) {
					_theComponent.getToolkit().beep();
					e.consume();
				}

				
				if (DataTypeLong.this._scale > 0 &&
					text.length() == DataTypeLong.this._scale &&
					c != KeyEvent.VK_BACK_SPACE &&
					c != KeyEvent.VK_DELETE) {
					
					e.consume();
					_theComponent.getToolkit().beep();
				}

				
				
				

				if ( DataTypeLong.this._isNullable) {

					
					if (text.equals("<null>")) {
						if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							
							DataTypeLong.this._textComponent.restoreText();
							e.consume();
						}
						else {
							
							DataTypeLong.this._textComponent.updateText("");
							
						}
					}
					else {
						
						if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							if (text.length() <= 1 ) {
								
								DataTypeLong.this._textComponent.updateText("<null>");
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
		
		long data = rs.getLong(index);
		if (rs.wasNull()) {
			return null;
        } else {
            return Long.valueOf(data);
        }
	}

	
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
		if (value == null || value.toString() == null || value.toString().length() == 0)
			return _colDef.getLabel() + " IS NULL";
		else
			return _colDef.getLabel() + "=" + value.toString();
	}
	
	
	
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws java.sql.SQLException {
		if (value == null) {
			pstmt.setNull(position, _colDef.getSqlType());
		}
		else {
			pstmt.setLong(position, ((Long)value).longValue());
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
		
		
		return Long.valueOf(0);
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
