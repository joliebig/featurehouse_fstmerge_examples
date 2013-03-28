package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;



import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


public interface IDataTypeComponent {
    
    public String getClassName();

    
    public boolean areEqual(Object obj1, Object obj2);

    

    
    public String renderObject(Object object);

    
    public boolean isEditableInCell(Object originalValue);

    
    public boolean needToReRead(Object originalValue);

    
    public JTextField getJTextField();

    
    public Object validateAndConvert(String value, Object originalValue,
            StringBuffer messageBuffer);

    
    public boolean useBinaryEditingPanel();

    

    
    public boolean isEditableInPopup(Object originalValue);

    
    public JTextArea getJTextArea(Object value);

    
    public Object validateAndConvertInPopup(String value, Object originalValue,
            StringBuffer messageBuffer);

    

    
    public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
            throws java.sql.SQLException;

    
    public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md);

    
    public void setPreparedStatementValue(PreparedStatement pstmt,
            Object value, int position) throws java.sql.SQLException;

    
    public Object getDefaultValue(String dbDefaultValue);

    

    
    public boolean canDoFileIO();

    
    public String importObject(FileInputStream inStream) throws IOException;

    
    public void exportObject(FileOutputStream outStream, String text)
            throws IOException;

    
    public void setColumnDisplayDefinition(ColumnDisplayDefinition def);

    
    public void setTable(JTable table);
}
