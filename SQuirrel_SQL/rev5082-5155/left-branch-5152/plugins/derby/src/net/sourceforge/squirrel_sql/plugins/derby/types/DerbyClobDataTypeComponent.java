package net.sourceforge.squirrel_sql.plugins.derby.types;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseKeyTextHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DefaultColumnRenderer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IRestorableTextComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableJTextArea;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableJTextField;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilitiesImpl;


public class DerbyClobDataTypeComponent extends BaseDataTypeComponent implements
      IDataTypeComponent {

   
   private boolean _isNullable;

   
   private IRestorableTextComponent _textComponent;

   
   private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();

   private IOUtilities ioUtils = new IOUtilitiesImpl();
   
   
   public DerbyClobDataTypeComponent() {
   }

	
   @Override
   public String getClassName() {
      return "net.sourceforge.squirrel_sql.plugins.derby.types.DerbyClobDescriptor";
   }

   
   public static boolean getReadCompleteClob() {
      return true;
   }

   
   public static void setReadCompleteClob(boolean val) {
      
   }

   
   public boolean areEqual(Object obj1, Object obj2) {
      if (obj1 == obj2) {
         return true;
      }

      
      
      
      
      
      
      if (obj1 != null)
         return ((DerbyClobDescriptor) obj1).equals((DerbyClobDescriptor) obj2);
      else
         return ((DerbyClobDescriptor) obj2).equals((DerbyClobDescriptor) obj1);
   }

   

   
   @Override
   public String renderObject(Object value) {
      return (String) _renderer.renderObject(value);
   }

   
   public boolean isEditableInCell(Object originalValue) {
      
      DerbyClobDescriptor cdesc = (DerbyClobDescriptor) originalValue;

      
      
      if (cdesc != null && cdesc.getData() != null
            && cdesc.getData().indexOf('\n') > -1) {
         return false;
      } else {
         return true;
      }
   }

   
   public boolean needToReRead(Object originalValue) {
      return false;
   }

   
   @Override
   public void setColumnDisplayDefinition(ColumnDisplayDefinition def) {
      super.setColumnDisplayDefinition(def);

      this._isNullable = def.isNullable();
   }

   
   @Override
   public Object validateAndConvert(String value, Object originalValue,
         StringBuffer messageBuffer) {
      
      if (value.equals("<null>")) {
         return null;
      }

      

      
      
      
      
      
      
      DerbyClobDescriptor cdesc;
      if (originalValue == null) {
         
         cdesc = new DerbyClobDescriptor(value);
      } else {
         
         cdesc = (DerbyClobDescriptor) originalValue;

         
         
         
         cdesc = new DerbyClobDescriptor(value);
      }
      return cdesc;

   }

   
   public boolean useBinaryEditingPanel() {
      return false;
   }

   

   
   public boolean isEditableInPopup(Object originalValue) {
      return true;
   }

   
   @Override
   public Object validateAndConvertInPopup(String value, Object originalValue,
         StringBuffer messageBuffer) {
      return validateAndConvert(value, originalValue, messageBuffer);
   }

   
   @Override
   protected KeyListener getKeyListener(IRestorableTextComponent component) {
       return new KeyTextHandler();
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
						(RestorableJTextField)DerbyClobDataTypeComponent.this._textComponent,
						evt, DerbyClobDataTypeComponent.this._table);
					CellDataPopup.showDialog(DerbyClobDataTypeComponent.this._table,
						DerbyClobDataTypeComponent.this._colDef, tableEvt, true);
				}
			}
		});	

		return (JTextField)_textComponent;
	}
   
	
	 public JTextArea getJTextArea(Object value) {
		_textComponent = new RestorableJTextArea();

		
		
		
		
		
		((RestorableJTextArea)_textComponent).setText((String)_renderer.renderObject(value));

		
		((RestorableJTextArea)_textComponent).addKeyListener(new KeyTextHandler());

		return (RestorableJTextArea)_textComponent;
	 }
	
   
   

   
   private class KeyTextHandler extends BaseKeyTextHandler {
      public void keyTyped(KeyEvent e) {
         char c = e.getKeyChar();

         
         
         
         JTextComponent _theComponent = (JTextComponent) DerbyClobDataTypeComponent.this._textComponent;
         String text = _theComponent.getText();

         
         
         
         

         if (DerbyClobDataTypeComponent.this._isNullable) {

            
            if (text.equals("<null>")) {
               if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
                  
                  DerbyClobDataTypeComponent.this._textComponent.restoreText();
                  e.consume();
               } else {
                  
                  DerbyClobDataTypeComponent.this._textComponent.updateText("");
                  
               }
            } else {
               
               if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
                  if (text.length() <= 1) {
                     
                     
                     DerbyClobDataTypeComponent.this._textComponent.updateText("<null>");
                     e.consume();
                  }
               }
            }
         } else {
            
            
            handleNotNullableField(text, c, e, _textComponent);
         }
      }
   }

   

   
   public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
         throws java.sql.SQLException {
      return staticReadResultSet(rs, index);
   }

   
   public static Object staticReadResultSet(ResultSet rs, int index)
         throws java.sql.SQLException {

      Clob clob = rs.getClob(index);

      if (rs.wasNull()) {
         return null;
      }
      
      String clobData = clob.getSubString(1, (int)clob.length());
      return new DerbyClobDescriptor(clobData);
   }

   
   public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
      if (value == null || ((DerbyClobDescriptor) value).getData() == null)
         return _colDef.getLabel() + " IS NULL";
      else
         return ""; 
   }

   
   public void setPreparedStatementValue(PreparedStatement pstmt, Object value,
         int position) throws java.sql.SQLException {
      if (value == null || ((DerbyClobDescriptor) value).getData() == null) {
         pstmt.setNull(position, _colDef.getSqlType());
      } else {
         
         DerbyClobDescriptor cdesc = (DerbyClobDescriptor) value;

         pstmt.setCharacterStream(position,
                                  new StringReader(cdesc.getData()),
                                  cdesc.getData().length());
      }
   }

   
   public Object getDefaultValue(String dbDefaultValue) {
      if (dbDefaultValue != null) {
         
         StringBuffer mbuf = new StringBuffer();
         Object newObject = validateAndConvert(dbDefaultValue, null, mbuf);

         
         
         
         if (mbuf.length() == 0) {
            return newObject;
         }
      }

      
      if (_isNullable) {
         return null;
      }

      
      return null;
   }

   

   
   public boolean canDoFileIO() {
      return true;
   }

   
   @Override
   public String importObject(FileInputStream inStream) throws IOException {

      InputStreamReader inReader = new InputStreamReader(inStream);

      int fileSize = inStream.available();

      char charBuf[] = new char[fileSize];

      int count = inReader.read(charBuf, 0, fileSize);

      if (count != fileSize)
         throw new IOException("Could read only " + count
               + " chars from a total file size of " + fileSize
               + ". Import failed.");

      
      
      
      
      String fileText;
      if (charBuf[count - 1] == KeyEvent.VK_ENTER)
         fileText = new String(charBuf, 0, count - 1);
      else
         fileText = new String(charBuf);

      
      
      StringBuffer messageBuffer = new StringBuffer();
      validateAndConvertInPopup(fileText, null, messageBuffer);
      if (messageBuffer.length() > 0) {
         
         throw new IOException("Text does not represent data of type "
               + getClassName() + ".  Text was:\n" + fileText);
      }

      
      
      return fileText;
   }

   
   @Override
   public void exportObject(FileOutputStream outStream, String text)
         throws IOException {

      OutputStreamWriter outWriter = null;
      try {
         outWriter = new OutputStreamWriter(outStream);

         
         StringBuffer messageBuffer = new StringBuffer();
         validateAndConvertInPopup(text, null, messageBuffer);
         if (messageBuffer.length() > 0) {
            
            throw new IOException(new String(messageBuffer));
         }

         
         outWriter.write(text);
         outWriter.flush();

      } finally {
      	ioUtils.closeWriter(outWriter);
      }
   }

}
