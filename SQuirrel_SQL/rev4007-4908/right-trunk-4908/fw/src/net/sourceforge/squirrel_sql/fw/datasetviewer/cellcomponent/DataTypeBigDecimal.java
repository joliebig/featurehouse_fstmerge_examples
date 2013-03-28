package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;



public class DataTypeBigDecimal extends FloatingPointBase
	implements IDataTypeComponent
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataTypeBigDecimal.class);

   
   private static ILogger s_log = LoggerController.createLogger(DataTypeBigDecimal.class);



   
	private ColumnDisplayDefinition _colDef;

	
	private boolean _isNullable;

	
	private int _precision;

	
	private int _scale;

	
	private JTable _table;

	
	private IRestorableTextComponent _textComponent;

	
	
	
	
	
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();


	
	private NumberFormat _numberFormat;
   private boolean _renderExceptionHasBeenLogged;


   
   public DataTypeBigDecimal(JTable table, ColumnDisplayDefinition colDef) {
      _table = table;
      _colDef = colDef;
      _isNullable = colDef.isNullable();
      _precision = colDef.getPrecision();
      _scale = colDef.getScale();

      _numberFormat = NumberFormat.getInstance();

      
      
      
      _numberFormat.setMaximumFractionDigits(5);      
      
      _numberFormat.setMinimumFractionDigits(0);

   }

	
	public String getClassName() {
		return "java.math.BigDecimal";
	}

	
	public boolean areEqual(Object obj1, Object obj2) {
		return obj1.equals(obj2);
	}

	

	
	public String renderObject(Object value) {


		

		if (value == null || useJavaDefaultFormat)
		{
			return (String)_renderer.renderObject(value);
		}
		else
		{

         try
         {
            return (String)_renderer.renderObject(_numberFormat.format(value));
         }
         catch (Exception e)
         {
            if(false == _renderExceptionHasBeenLogged)
            {
               _renderExceptionHasBeenLogged = true;
               s_log.error("Could not format \"" + value + "\" as number type", e);
            }
            return (String) _renderer.renderObject(value);
         }


      }

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
						(RestorableJTextField)DataTypeBigDecimal.this._textComponent,
						evt, DataTypeBigDecimal.this._table);
					CellDataPopup.showDialog(DataTypeBigDecimal.this._table,
						DataTypeBigDecimal.this._colDef, tableEvt, true);
				}
			}
		});	

		return (JTextField)_textComponent;
	}

	
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer)
	{
		
		if (value.equals("<null>") || value.equals(""))
			return null;

		
		try
		{
			BigDecimal obj;

			if(useJavaDefaultFormat)
			{
				obj = new BigDecimal(value);
			}
			else
			{
				obj = new BigDecimal("" + _numberFormat.parse(value));
			}


			
			
			
			if (_scale >= 0 && obj.scale() > _scale)
			{
				Object[] args = new Object[]{
                        Integer.valueOf(obj.scale()), 
                        Integer.valueOf(_scale)
                };
				
                
                
				String msg = s_stringMgr.getString("dataTypeBigDecimal.scaleEceeded", args);

				messageBuffer.append(msg);
				return null;
			}

			
			
			
			
			int objPrecision = value.length();
			
			if (value.indexOf("+") > -1 || value.indexOf("-") > -1)
				objPrecision--;
			if (value.indexOf(".") > -1)
				objPrecision--;

			
			
			if (_precision > 0 && objPrecision > _precision)
			{
				Object[] args = new Object[]{
                        Integer.valueOf(objPrecision), 
                        Integer.valueOf(_precision)
                };
				
                
                
				String msg = s_stringMgr.getString("dataTypeBigDecimal.precisionEceeded", args);

				messageBuffer.append(msg);
				return null;
			}
			return obj;
		}
		catch (Exception e)
		{
			messageBuffer.append(e.toString() + "\n");
			
			
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

				
				
				
                JTextComponent _theComponent = (JTextComponent)DataTypeBigDecimal.this._textComponent;
				String text = _theComponent.getText();

				
				
				
				
				if (c == KeyEvent.VK_TAB || c == KeyEvent.VK_ENTER) {
					
					int index = text.indexOf(c);

					if (-1 != index)
					{
						if (index == text.length() - 1)
						{
							text = text.substring(0, text.length() - 1);	
						}
						else
						{
							text = text.substring(0, index) + text.substring(index + 1);
						}
						((IRestorableTextComponent) _theComponent).updateText(text);
						_theComponent.getToolkit().beep();
						e.consume();
					}
				}

				if ( (c == '-' || c=='+') &&
					! (text.equals("<null>") || text.length() == 0)) {
					
					_theComponent.getToolkit().beep();
					e.consume();
				}

				if ( ! ( Character.isDigit(c) ||
					(c == '-') || (c == '+') ||
					(c == '.') || (c == ',') ||  
					(c == KeyEvent.VK_BACK_SPACE) ||
					(c == KeyEvent.VK_DELETE) ) ) {
					_theComponent.getToolkit().beep();
					e.consume();
				}

				
				
				

				if ( DataTypeBigDecimal.this._isNullable) {

					
					if (text.equals("<null>")) {
						if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							
							DataTypeBigDecimal.this._textComponent.restoreText();
							e.consume();
						}
						else {
							
							DataTypeBigDecimal.this._textComponent.updateText("");
							
						}
					}
					else {
						
						if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							if (text.length() <= 1 ) {
								
								DataTypeBigDecimal.this._textComponent.updateText("<null>");
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

		BigDecimal data = rs.getBigDecimal(index);
		if (rs.wasNull())
			return null;
		else return data;
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
			pstmt.setBigDecimal(position, (BigDecimal)value);
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

		
		return new BigDecimal(0);
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
		 if (messageBuffer.length() > 0) {;
			 
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
