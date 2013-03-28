package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;


import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;



public class DataTypeBinary extends BaseDataTypeComponent
 	implements IDataTypeComponent
{
	
	private boolean _isNullable;

	
	private JTable _table;
	
	
	private IRestorableTextComponent _textComponent;
	
	
	
	
	
	
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();


	
	public DataTypeBinary(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();
	}
	
	
	public String getClassName() {
		return "[Ljava.lang.Byte";
	}

	
	public boolean areEqual(Object obj1, Object obj2) {
        Byte[] b1 = null;
        Byte[] b2 = null;
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if ( (obj1 != null && obj2 == null) 
                || (obj1 == null && obj2 != null) ) 
        {
            return false;
        }
        if (obj1 instanceof Byte[]) {
            b1 = (Byte[])obj1;
        } else {
            b1 = StringUtilities.getByteArray(obj1.toString().getBytes());
        }
        if (obj2 instanceof Byte[]) {
            b2 = (Byte[])obj2;
        } else {
            b2 = StringUtilities.getByteArray(obj2.toString().getBytes());
        }
		
		for (int i=0; i<b1.length; i++)
			if (b1[i] != b2[i])
				return false;
				
		return true;
	}

	
	 
	
	public String renderObject(Object value) {
		
		
		
		Byte[] useValue;
		if (value instanceof java.lang.String) {
			byte[] bytes = ((String)value).getBytes();
			useValue = new Byte[bytes.length];
			for (int i=0; i<bytes.length; i++) {
				useValue[i] = Byte.valueOf(bytes[i]);
            }
		}
		else useValue = (Byte[])value;
		
		return (String)_renderer.renderObject(
			BinaryDisplayConverter.convertToString(useValue,
			BinaryDisplayConverter.HEX, false));
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
						(RestorableJTextField)DataTypeBinary.this._textComponent,
						evt, DataTypeBinary.this._table);
					CellDataPopup.showDialog(DataTypeBinary.this._table,
						DataTypeBinary.this._colDef, tableEvt, true);
				}
			}
		});	

		return (JTextField)_textComponent;
	}


	
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		
		if (value == null || value.equals("<null>") || value.equals(""))
			return null;

		
		try {
			Object obj = BinaryDisplayConverter.convertToBytes(value,
				BinaryDisplayConverter.HEX, false);
			return obj;
		}
		catch (Exception e) {
			messageBuffer.append(e.toString()+"\n");
			
			
			return null;
		}
	}

	
	public boolean useBinaryEditingPanel() {
		return true;
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
				
				
				
				
				JTextComponent _theComponent = (JTextComponent)DataTypeBinary.this._textComponent;
				String text = _theComponent.getText();
												
				
				
				
				
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

				
				
				

				if ( DataTypeBinary.this._isNullable) {

					
					if (text.equals("<null>")) {
						if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							
							DataTypeBinary.this._textComponent.restoreText();
							e.consume();
						}
						else {
							
							DataTypeBinary.this._textComponent.updateText("");
							
						}
					}
					else {
						
						if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							if (text.length() <= 1 ) {
								
								DataTypeBinary.this._textComponent.updateText("<null>");
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
		
		byte[] data = rs.getBytes(index);
		if (rs.wasNull())
			return null;
		else {
			Byte[] internal = new Byte[data.length];
			for (int i=0; i<data.length; i++) {
				internal[i] = Byte.valueOf(data[i]);
            }
			return internal;
		}
	}


	
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
		if (value == null || value.toString() == null || value.toString().length() == 0)
			return _colDef.getLabel() + " IS NULL";
		else
			
			
			return null;	
	}
	
	
	
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws java.sql.SQLException {
		if (value == null) {
			pstmt.setNull(position, _colDef.getSqlType());
		}
		else {
			Byte[] internal = (Byte[])value;
			byte[] dbValue = new byte[internal.length];
			for (int i=0; i<internal.length; i++)
				dbValue[i] = internal[i].byteValue();
			pstmt.setBytes(position, dbValue);
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
		
		
		return new Byte[0];
	}
	
	
	
	 
	 
	 
	 public boolean canDoFileIO() {
	 	return true;
	 }
	 
	 
	public String importObject(FileInputStream inStream)
	 	throws IOException {
	 	

	 	int fileSize = inStream.available();
	 	
	 	byte[] buf = new byte[fileSize];
	 	
	 	int count = inStream.read(buf);
	 	
	 	if (count != fileSize)
	 		throw new IOException(
	 			"Could read only "+ count +
	 			" bytes from a total file size of " + fileSize +
	 			". Import failed.");
	 	
	 	
	 	Byte[] bBytes = new Byte[count];
	 	for (int i=0; i<count; i++) {
	 		bBytes[i] = Byte.valueOf(buf[i]);
        }
	 	
	 	return BinaryDisplayConverter.convertToString(bBytes,
	 		BinaryDisplayConverter.HEX, false);
	}

	 	 
	 
	 public void exportObject(FileOutputStream outStream, String text)
	 	throws IOException {
	 	
		Byte[] bBytes = BinaryDisplayConverter.convertToBytes(text,
			BinaryDisplayConverter.HEX, false);
	 	
	 	
	 	StringBuffer messageBuffer = new StringBuffer();
	 	validateAndConvertInPopup(text, null, messageBuffer);
	 	if (messageBuffer.length() > 0) {
	 		
	 		throw new IOException(new String(messageBuffer));
	 	}
	 	
	 	
	 	byte[] bytes = new byte[bBytes.length];
	 	for (int i=0; i<bytes.length; i++)
	 		bytes[i] = bBytes[i].byteValue();
	 	
	 	
		outStream.write(bytes);
		outStream.flush();
		outStream.close();
	 }
}
