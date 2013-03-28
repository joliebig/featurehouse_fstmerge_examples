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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.gui.RightLabel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.ThreadSafeDateFormat;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;



public class DataTypeDate extends BaseDataTypeComponent
	implements IDataTypeComponent
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataTypeDate.class);

   
   private static ILogger s_log = LoggerController.createLogger(DataTypeDate.class);

	
	private boolean _isNullable;

	
	private JTable _table;

	
	private IRestorableTextComponent _textComponent;

	
	
	
	
	
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();

	
	private static final String thisClassName =
		"net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeDate";


	
	private static int DEFAULT_LOCALE_FORMAT = DateFormat.SHORT;

	
	 
	 private static boolean propertiesAlreadyLoaded = false;

	 
	 
	 private static boolean useJavaDefaultFormat = true;

	 
	 private static int localeFormat = DEFAULT_LOCALE_FORMAT;

	 
	 private static boolean lenient = true;

     
     private static boolean readDateAsTimestamp = false;
     
	 
	 
	 private static ThreadSafeDateFormat dateFormat = 
	     new ThreadSafeDateFormat(localeFormat);
    private boolean _renderExceptionHasBeenLogged;


   
   public DataTypeDate(JTable table, ColumnDisplayDefinition colDef) {
      _table = table;
      _colDef = colDef;
      _isNullable = colDef.isNullable();

      loadProperties();
   }

	
	private static void loadProperties() {

		
		
		
		if (propertiesAlreadyLoaded == false) {
            propertiesAlreadyLoaded = true;
			
			useJavaDefaultFormat =true;	
			String useJavaDefaultFormatString = DTProperties.get(
				thisClassName, "useJavaDefaultFormat");
			if (useJavaDefaultFormatString != null && useJavaDefaultFormatString.equals("false"))
				useJavaDefaultFormat =false;

			
			localeFormat =DateFormat.SHORT;	
			String localeFormatString = DTProperties.get(
				thisClassName, "localeFormat");
			if (localeFormatString != null)
				localeFormat = Integer.parseInt(localeFormatString);

			
			lenient = true;	
			String lenientString = DTProperties.get(
				thisClassName, "lenient");
			if (lenientString != null && lenientString.equals("false"))
				lenient =false;
            
            
            
            
            readDateAsTimestamp = false;
            String readDateAsTimestampString = 
                DTProperties.get(thisClassName, "readDateAsTimestamp");
            if (readDateAsTimestampString != null && 
                    readDateAsTimestampString.equals("true")) 
            {
                readDateAsTimestamp = true;
            }
		}
	}

    public static boolean getReadDateAsTimestamp() {
        propertiesAlreadyLoaded = false;
        loadProperties();
        return readDateAsTimestamp;
    }
    
	
	public String getClassName() {
		return "java.sql.Date";
	}

	
	public boolean areEqual(Object obj1, Object obj2) {
		return ((Date)obj1).equals(obj2);
	}

	

	
	public String renderObject(Object value) {
		
		if (useJavaDefaultFormat == true || value == null)
			return (String)_renderer.renderObject(value);

		
		try
		{
		    return (String)_renderer.renderObject(dateFormat.format(value));
		}
		catch (Exception e)
		{
		    if(false == _renderExceptionHasBeenLogged)
		    {
		        _renderExceptionHasBeenLogged = true;
		        s_log.error("Could not format \"" + value + "\" as date type", e);
		    }
		    return (String) _renderer.renderObject(value);
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
						(RestorableJTextField)DataTypeDate.this._textComponent,
						evt, DataTypeDate.this._table);
					CellDataPopup.showDialog(DataTypeDate.this._table,
						DataTypeDate.this._colDef, tableEvt, true);
				}
			}
		});	

		return (JTextField)_textComponent;
	}

	
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		
		if (value.equals("<null>") || value.equals(""))
			return null;

		
		try {
			if (useJavaDefaultFormat) {
				Object obj = Date.valueOf(value);
				return obj;
			}
			else {
				
				java.util.Date javaDate = dateFormat.parse(value);
				java.sql.Date sqlDate = new java.sql.Date(javaDate.getTime());
				return sqlDate;
			}
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

				
				
				
				JTextComponent _theComponent = (JTextComponent)DataTypeDate.this._textComponent;
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


				
				
				

				if ( DataTypeDate.this._isNullable) {

					
					if (text.equals("<null>")) {
						if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							
							DataTypeDate.this._textComponent.restoreText();
							e.consume();
						}
						else {
							
							DataTypeDate.this._textComponent.updateText("");
							
						}
					}
					else {
						
						if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							if (text.length() <= 1 ) {
								
								DataTypeDate.this._textComponent.updateText("<null>");
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
		throws java.sql.SQLException 
    {
	    
	    return staticReadResultSet(rs, index, limitDataRead);
    }

     
    public static Object staticReadResultSet(ResultSet rs, int index, boolean limitDataRead)
        throws java.sql.SQLException 
    {
        loadProperties();
        Object data = null;

        data = rs.getDate(index);

        if (rs.wasNull()) {
            return null;
        } else {
            return data;
        }
    }
    
	
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md)
	{
		if (value == null || value.toString() == null || value.toString().length() == 0)
		{
			return _colDef.getLabel() + " IS NULL";
		}
		else
		{
            
            boolean hasTimeComponent = (value.toString().indexOf(":") != -1);
            
            
            boolean hasDateComponent = (value.toString().indexOf("-") != -1);
            
            if (hasTimeComponent && hasDateComponent) {
                
                return _colDef.getLabel() + "={ts '" + value.toString() + "'}";
            } else if (hasTimeComponent) {
                
                return _colDef.getLabel() + "={t '" + value.toString() + "'}";
            } else {
                if (DialectFactory.isOracle(md)) {
                    
                    
                    return "trunc(" + _colDef.getLabel() + ")={d '" + value.toString() + "'}";
                } else {
                    return _colDef.getLabel() + "={d '" + value.toString() + "'}";
                }
            }               
		}
	}


	
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws java.sql.SQLException {
		if (value == null) {
			pstmt.setNull(position, _colDef.getSqlType());
		}
		else {
			pstmt.setDate(position, ((Date)value));
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

		
		return new Date( new java.util.Date().getTime());
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

		return new DateOkJPanel();
	 }

	
	public static class DateFormatTypeCombo extends JComboBox
	{
        private static final long serialVersionUID = 1L;

        public DateFormatTypeCombo()
		{
			
			addItem(s_stringMgr.getString("dataTypeDate.full", DateFormat.getDateInstance(DateFormat.FULL).format(new java.util.Date())));
			
			addItem(s_stringMgr.getString("dataTypeDate.long", DateFormat.getDateInstance(DateFormat.LONG).format(new java.util.Date())));
			
			addItem(s_stringMgr.getString("dataTypeDate.medium", DateFormat.getDateInstance(DateFormat.MEDIUM).format(new java.util.Date())));
			
			addItem(s_stringMgr.getString("dataTypeDate.short", DateFormat.getDateInstance(DateFormat.SHORT).format(new java.util.Date())));
		}

		public void setSelectedIndex(int option) {
			if (option == DateFormat.SHORT)
				super.setSelectedIndex(3);
			else if (option == DateFormat.MEDIUM)
				super.setSelectedIndex(2);
			else if (option == DateFormat.LONG)
				super.setSelectedIndex(1);
			else super.setSelectedIndex(0);
		}

		public int getValue() {
			if (getSelectedIndex() == 3)
				return DateFormat.SHORT;
			else if (getSelectedIndex() == 2)
				return DateFormat.MEDIUM;
			else if (getSelectedIndex() == 1)
				return DateFormat.LONG;
			else return DateFormat.FULL;
		}
	}


	 
	 private static class DateOkJPanel extends OkJPanel
	 {
        private static final long serialVersionUID = 1L;

        
		 
		 private JCheckBox useJavaDefaultFormatChk = new JCheckBox(
			 
			 s_stringMgr.getString("dataTypeDate.useDefaultFormat", new java.sql.Date(new java.util.Date().getTime()).toString()));

		 
		 
		 private RightLabel dateFormatTypeDropLabel = new RightLabel(s_stringMgr.getString("dataTypeDate.orlocaleIndependent"));

		 
		 private DateFormatTypeCombo dateFormatTypeDrop = new DateFormatTypeCombo();

		 
		 
		 private JCheckBox lenientChk = new JCheckBox(s_stringMgr.getString("dataTypeDate.allowInexact"));

         
         
         private JCheckBox readdDateAsTimestampChk = 
             new JCheckBox(s_stringMgr.getString("dataTypeDate.readDateAsTimestamp"));
         
		 public DateOkJPanel()
		 {

			 
			 
			 useJavaDefaultFormatChk.setSelected(useJavaDefaultFormat);
			 useJavaDefaultFormatChk.addChangeListener(new ChangeListener()
			 {
				 public void stateChanged(ChangeEvent e)
				 {
					 dateFormatTypeDrop.setEnabled(! useJavaDefaultFormatChk.isSelected());
					 dateFormatTypeDropLabel.setEnabled(! useJavaDefaultFormatChk.isSelected());
					 lenientChk.setEnabled(! useJavaDefaultFormatChk.isSelected());
				 }
			 });

			 
			 dateFormatTypeDrop = new DateFormatTypeCombo();
			 dateFormatTypeDrop.setSelectedIndex(localeFormat);

			 
			 lenientChk.setSelected(lenient);
             
             
             readdDateAsTimestampChk.setSelected(readDateAsTimestamp);

			 
			 dateFormatTypeDrop.setEnabled(! useJavaDefaultFormatChk.isSelected());
			 dateFormatTypeDropLabel.setEnabled(! useJavaDefaultFormatChk.isSelected());
			 lenientChk.setEnabled(! useJavaDefaultFormatChk.isSelected());

             

			 setLayout(new GridBagLayout());

			 
			 setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("dataTypeDate.typeDate")));
			 final GridBagConstraints gbc = new GridBagConstraints();
			 gbc.fill = GridBagConstraints.HORIZONTAL;
			 gbc.insets = new Insets(4, 4, 4, 4);
			 gbc.anchor = GridBagConstraints.WEST;

			 gbc.gridx = 0;
			 gbc.gridy = 0;

			 gbc.gridwidth = GridBagConstraints.REMAINDER;
			 add(useJavaDefaultFormatChk, gbc);

			 gbc.gridwidth = 1;
			 gbc.gridx = 0;
			 ++gbc.gridy;
			 add(dateFormatTypeDropLabel, gbc);

			 ++gbc.gridx;
			 add(dateFormatTypeDrop, gbc);

			 gbc.gridx = 0;
			 ++gbc.gridy;
			 add(lenientChk, gbc);

             gbc.gridx = 0;
             ++gbc.gridy;
             add(readdDateAsTimestampChk, gbc);             
		 } 


		 
		 public void ok()
		 {
			 
			 useJavaDefaultFormat = useJavaDefaultFormatChk.isSelected();
			 DTProperties.put(thisClassName,
			                  "useJavaDefaultFormat", 
			                  Boolean.valueOf(useJavaDefaultFormat).toString());


			 localeFormat = dateFormatTypeDrop.getValue();
			 dateFormat = new ThreadSafeDateFormat(localeFormat);	
			 DTProperties.put(thisClassName,
			                  "localeFormat", 
			                  Integer.toString(localeFormat));

			 lenient = lenientChk.isSelected();
			 dateFormat.setLenient(lenient);
			 DTProperties.put(thisClassName,
			                  "lenient", 
			                  Boolean.valueOf(lenient).toString());
             
             readDateAsTimestamp = readdDateAsTimestampChk.isSelected();
             DTProperties.put(thisClassName,
                              "readDateAsTimestamp",
                              Boolean.valueOf(readDateAsTimestamp).toString());
		 }

	 } 
}
