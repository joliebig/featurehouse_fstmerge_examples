package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.Color;
import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;



public class CellComponentFactory {

	
	static HashMap<ColumnDisplayDefinition, IDataTypeComponent> _colDataTypeObjects = 
        new HashMap<ColumnDisplayDefinition, IDataTypeComponent>();
	
	
	 static HashMap<String,IDataTypeComponentFactory> _pluginDataTypeFactories = 
         new HashMap<String,IDataTypeComponentFactory>();

	
	static JTable _table = null;
	
	
	static private ILogger s_log = LoggerController.createLogger(CellComponentFactory.class);
	
	
	public static String getClassName(ColumnDisplayDefinition colDef) {
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		if (dataTypeObject != null)
			return dataTypeObject.getClassName();
		else
			return "java.lang.Object";
	}
	
	
	public static boolean areEqual(ColumnDisplayDefinition colDef,
		Object newValue, Object oldValue) {
			
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		if (dataTypeObject != null)
			return dataTypeObject.areEqual(newValue, oldValue);
		
		
		
		
		
		
		return false;
	}


	
	
	
	public static String renderObject(Object value, ColumnDisplayDefinition colDef)
	{
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.renderObject(value);
		
		
      if(null == value)
      {
         return "<null>";
      }
      else
      {
		   return value.toString();
      }
	}
	
	
	public static TableCellRenderer getTableCellRenderer(ColumnDisplayDefinition colDef)
	{
		return new CellRenderer(getDataTypeObject(null, colDef));
	}
	

	
	static private final class CellRenderer extends DefaultTableCellRenderer implements SquirrelTableCellRenderer
	{
        private static final long serialVersionUID = 1L;
        transient private final IDataTypeComponent _dataTypeObject;

		CellRenderer(IDataTypeComponent dataTypeObject)
		{
			super();

			_dataTypeObject = dataTypeObject;
		}

		
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
				  	
				JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				
				
				if (_dataTypeObject != null &&
					_dataTypeObject.isEditableInCell(value) == false &&
					_dataTypeObject.isEditableInPopup(value) == true) {
					
					
				   setBackground(Color.cyan);
			   }
			   else {
					
					
					
					if (isSelected)
						setBackground(table.getSelectionBackground());
					else
						setBackground(table.getBackground());
			   }	
	

				return label;	  	
		 }
		 
		 
		public void setValue(Object value)
		{		
			
			
			if (_dataTypeObject != null)
				super.setValue(_dataTypeObject.renderObject(value));
			else super.setValue(DefaultColumnRenderer.getInstance().renderObject(value));
		}

		public Object renderValue(Object value)
		{
			if (_dataTypeObject != null)
			{
				return _dataTypeObject.renderObject(value);
			}
			else
			{
				return DefaultColumnRenderer.getInstance().renderObject(value);
			}
		}
	}


	
	public static boolean isEditableInCell(ColumnDisplayDefinition colDef,     
                                           Object originalValue)
	{
		if (colDef.isAutoIncrement()) {
		    return false;
        }
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.isEditableInCell(originalValue);
		
		
		
		return false;
	}

	
	public static boolean needToReRead(ColumnDisplayDefinition colDef, Object originalValue) {
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.needToReRead(originalValue);
		
		
		return false;
	};
	
	
	public static DefaultCellEditor getInCellEditor(
		JTable table, ColumnDisplayDefinition colDef) {


		DefaultCellEditor ed;

		IDataTypeComponent dataTypeObject = getDataTypeObject(table, colDef);
		
		JTextField textField;
		
		
		
		if (dataTypeObject != null)
			textField = dataTypeObject.getJTextField();
		else textField = new RestorableJTextField();
		
		textField.setBackground(Color.yellow);
		
		
		
		textField.setBorder(new EmptyBorder(0,0,0,0));

		ed = new CellEditorUsingRenderer(textField, dataTypeObject);
		ed.setClickCountToStart(1);
		return ed;
	}
	
	
	 public static Object validateAndConvert(
	 	ColumnDisplayDefinition colDef,
	 	Object originalValue,
	 	String inputValue,
	 	StringBuffer messageBuffer) {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		if (dataTypeObject != null) {
			
			return dataTypeObject.validateAndConvert(inputValue, originalValue, messageBuffer);
		}

	 	
	 	
	 	
	 	
	 		
	 	
	 	if (inputValue.equals("<null>"))
	 		return null;
	 	else return inputValue;
	}
	
	
	public static boolean useBinaryEditingPanel(ColumnDisplayDefinition colDef) {
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		if (dataTypeObject != null) {
			
			return dataTypeObject.useBinaryEditingPanel();
		}
		return false;	
	}
	

	
	
	
	public static boolean isEditableInPopup(ColumnDisplayDefinition colDef, 
                                            Object originalValue) {
        if (colDef != null && colDef.isAutoIncrement()) {
            return false;
        }
        
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null) {
			return dataTypeObject.isEditableInPopup(originalValue);
		}
			
		
		
		return false;
	}
	
	
	 public static JTextArea getJTextArea(ColumnDisplayDefinition colDef, Object value) {

		
		
		

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.getJTextArea(value);
		
		
		
		
		
		
		
		
		
		RestorableJTextArea textArea = new RestorableJTextArea();
        if (value != null) {
            textArea.setText(value.toString());
        } else {
            textArea.setText("");
        }
		return textArea;
	}
	
	
	 public static Object validateAndConvertInPopup(
	 	ColumnDisplayDefinition colDef,
	 	Object originalValue,
	 	String inputValue,
	 	StringBuffer messageBuffer) {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		if (dataTypeObject != null) {
			
			return dataTypeObject.validateAndConvertInPopup(inputValue, originalValue, messageBuffer);
		}

	 	
	 	
	 	
	 	
	 		
	 	
	 	if (inputValue.equals("<null>"))
	 		return null;
	 	else return inputValue;
	}


	
	
	
	 

    
    public static Object readResultWithPluginRegisteredDataType(ResultSet rs,
            int sqlType, String sqlTypeName, int index) throws Exception {

        Object result = null;
        String typeNameKey = getRegDataTypeKey(sqlType, sqlTypeName);
        if (_pluginDataTypeFactories.containsKey(typeNameKey)) {
            IDataTypeComponentFactory factory = _pluginDataTypeFactories.get(typeNameKey);
            IDataTypeComponent dtComp = factory.constructDataTypeComponent();
            ColumnDisplayDefinition colDef = new ColumnDisplayDefinition(
                rs, index);
            dtComp.setColumnDisplayDefinition(colDef);
            dtComp.setTable(_table);
            result = dtComp.readResultSet(rs, index, false);
        }
        return result;
    }
	 
	 
	public static Object readResultSet(ColumnDisplayDefinition colDef,
		ResultSet rs, int index, boolean limitDataRead)
		throws java.sql.SQLException {
			
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		if (dataTypeObject != null) {
			
			return dataTypeObject.readResultSet(rs, index, limitDataRead);
		}

		
		
		
		return rs.getObject(index);
	}

	
	public static String getWhereClauseValue(ColumnDisplayDefinition colDef, Object value, ISQLDatabaseMetaData md) {
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		if (dataTypeObject != null) {
			
			return dataTypeObject.getWhereClauseValue(value, md);
		}
		
		
		return null;
	}
	
	
	public static void setPreparedStatementValue(ColumnDisplayDefinition colDef,
		PreparedStatement pstmt, Object value, int position)
		throws java.sql.SQLException {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		
		
		
		
		
		
		if (dataTypeObject != null) {
			
			dataTypeObject.setPreparedStatementValue(pstmt, value, position);
		}
	}
	
	
	static public Object getDefaultValue(ColumnDisplayDefinition colDef, String dbDefaultValue) {
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.getDefaultValue(dbDefaultValue);
		
		
		
		return null;
	}
	
	
	
	
	 
	 
	 
	 public static boolean canDoFileIO(ColumnDisplayDefinition colDef) {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		
		
		if (dataTypeObject == null)
			return false;

		
		return dataTypeObject.canDoFileIO();
	 }
	 
	 
	 public static String importObject(ColumnDisplayDefinition colDef,
	 	FileInputStream inStream)
	 	throws IOException {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		
		
		if (dataTypeObject == null)
			throw new IOException(
				"No internal Data Type class for this column's SQL type");

		
		return dataTypeObject.importObject(inStream);	 		
	 }

	 
	 
	 public static void exportObject(ColumnDisplayDefinition colDef,
	 	FileOutputStream outStream, String text)
	 	throws IOException {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		
		
		if (dataTypeObject == null)
			throw new IOException(
				"No internal Data Type class for this column's SQL type");

		
		dataTypeObject.exportObject(outStream, text);	 		
	 }
	
	
	private static String getRegDataTypeKey(int sqlType, String sqlTypeName) {
	    StringBuilder result = new StringBuilder();
	    result.append(sqlType);
	    result.append(":");
	    result.append(sqlTypeName);
	    return result.toString();
	}
	 
	
	public static void registerDataTypeFactory(
	        IDataTypeComponentFactory factory, int sqlType, String sqlTypeName)
	{
	    String typeName = getRegDataTypeKey(sqlType, sqlTypeName);

	    _pluginDataTypeFactories.put(typeName, factory);
	}
	
	
	
	 
	 
	 public static OkJPanel[] getControlPanels() {
		ArrayList<OkJPanel> panelList = new ArrayList<OkJPanel>();
		
		
		String [] initialClassNameList = {
			net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeBlob.class.getName(),
			net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeClob.class.getName(),
			net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeString.class.getName(),
			net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeOther.class.getName(),
			net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeUnknown.class.getName(),
			net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeDate.class.getName(),
			net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeTime.class.getName(),
			net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeTimestamp.class.getName(),			
			net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.FloatingPointBase.class.getName(),			
			 };


		
		
		ArrayList<String> classNameList = 
            new ArrayList<String>(Arrays.asList(initialClassNameList));
		
		






		
		
		for (int i=0; i< classNameList.size(); i++) {
			String className = classNameList.get(i);
			Class<?>[] parameterTypes = new Class<?>[0];
			try {
				Method panelMethod =
					Class.forName(className).getMethod("getControlPanel", parameterTypes);
					
				OkJPanel panel = (OkJPanel)panelMethod.invoke(null, (Object[])null);
				panelList.add(panel);
			}
			catch (Exception e) {
				s_log.error("Unexpected exception: "+e.getMessage(), e);
			}
		}
		
		return panelList.toArray(new OkJPanel[0]);
	}


	

	
	private static IDataTypeComponent getDataTypeObject(
		JTable table, ColumnDisplayDefinition colDef) {
		
		
		
		if (table != _table) {
			
			_colDataTypeObjects.clear();
			_table = table;
		}
		if (_colDataTypeObjects.containsKey(colDef))
			return _colDataTypeObjects.get(colDef);
		

		
		
		IDataTypeComponent dataTypeComponent = null;
		
		
		if (!_pluginDataTypeFactories.isEmpty()) {
            String typeName = getRegDataTypeKey(colDef.getSqlType(), 
                colDef.getSqlTypeName());
            IDataTypeComponentFactory factory = _pluginDataTypeFactories.get(typeName);
            if (factory != null) {
                dataTypeComponent = factory.constructDataTypeComponent();
                if (colDef != null) {
                    dataTypeComponent.setColumnDisplayDefinition(colDef);
                }
                if (table != null) {
                    dataTypeComponent.setTable(table);
                } else if (_table != null) {
                    dataTypeComponent.setTable(_table);
                }                
            }
		}
		
		
		
		if (dataTypeComponent == null) {	
			switch (colDef.getSqlType())
			{
				case Types.NULL:	
					
					break;

				case Types.BIT:
				case Types.BOOLEAN:
					dataTypeComponent = new DataTypeBoolean(table, colDef);
					break;

				case Types.TIME :
					dataTypeComponent = new DataTypeTime(table, colDef);
					break;

				case Types.DATE :
                    
                    
                    
                    
                    
                    
                    if (DataTypeDate.getReadDateAsTimestamp()) {
                        colDef.setSqlType(Types.TIMESTAMP);
                        colDef.setSqlTypeName("TIMESTAMP");
                        dataTypeComponent = new DataTypeTimestamp(table, colDef);
                    } else {
                        dataTypeComponent = new DataTypeDate(table, colDef);
                    }
					break;

				case Types.TIMESTAMP :
                case -101 : 
                case -102 : 
					dataTypeComponent = new DataTypeTimestamp(table, colDef);
					break;

				case Types.BIGINT :
					dataTypeComponent = new DataTypeLong(table, colDef);
					break;

				case Types.DOUBLE:
				case Types.FLOAT:
					dataTypeComponent = new DataTypeDouble(table, colDef);
					break;
					
				case Types.REAL:
					dataTypeComponent = new DataTypeFloat(table, colDef);
					break;

				case Types.DECIMAL:
				case Types.NUMERIC:
					dataTypeComponent = new DataTypeBigDecimal(table, colDef);
					break;

				case Types.INTEGER:
					
					dataTypeComponent = new DataTypeInteger(table, colDef);
					break;
					
				case Types.SMALLINT:
					dataTypeComponent = new DataTypeShort(table, colDef);
					break;
					
				case Types.TINYINT:
					dataTypeComponent = new DataTypeByte(table, colDef);
					break;

				
				
				
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
				case -9:
                    
                    dataTypeComponent = new DataTypeString(table, colDef);
                    break;
                
                
                case -8:
                    dataTypeComponent = new DataTypeString(table, colDef);
                    
                    
                    
                    colDef.setIsAutoIncrement(true);
                    break;

				case Types.BINARY:
				case Types.VARBINARY:
				case Types.LONGVARBINARY:
					
					dataTypeComponent = new DataTypeBinary(table, colDef);
					break;

				case Types.BLOB:
					dataTypeComponent = new DataTypeBlob(table, colDef);
					break;

				case Types.CLOB:
					dataTypeComponent = new DataTypeClob(table, colDef);
					break;

				case Types.OTHER:
					dataTypeComponent = new DataTypeOther(table, colDef);
					break;

               
            case Types.JAVA_OBJECT:
               dataTypeComponent = new DataTypeJavaObject(table, colDef);
               break;
               

            default:
					
					
					
					dataTypeComponent = new DataTypeUnknown(table, colDef);

			}
		}

		
		_colDataTypeObjects.put(colDef, dataTypeComponent);

		
		
		return dataTypeComponent;
	}
}
