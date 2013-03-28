package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeDate;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

public class ColumnDisplayDefinition
{
	
	private int _displayWidth;

	
	private String _fullTableColumnName;

    
    private String _columnName;
    
	
	private String _label;

	
	private int _sqlType;

	
	private String _sqlTypeName;

	
	private boolean _isNullable;

	
	private int _columnSize;

	
	private int _precision;

	
	private int _scale;

	
	private boolean _isSigned;

	
	private boolean _isCurrency;

   
   private boolean _isAutoIncrement;

   
   private DialectType _dialectType;
    
	
	public ColumnDisplayDefinition(int displayWidth, String label)
	{
		super();
      init(displayWidth,
           null,
           null,
           label,
           Types.NULL,
           null,
           true,
           0,
           0,
           0,
           true,
           false,
           false,
           DialectType.GENERIC);
	}

   
	public ColumnDisplayDefinition(int displayWidth, String fullTableColumnName,
                String columnName, String label, int sqlType, String sqlTypeName,
				boolean isNullable, int columnSize, int precision, int scale,
				boolean isSigned, boolean isCurrency, boolean isAutoIncrement,
				DialectType dialectType) {
		super();
		init(displayWidth, fullTableColumnName, columnName, label, sqlType, 
             sqlTypeName, isNullable, columnSize, precision, scale,
             isSigned, isCurrency, isAutoIncrement, dialectType);
	}

	
	public ColumnDisplayDefinition(ResultSet rs, int idx, DialectType dialectType) throws SQLException {
	    super();
	    ResultSetMetaData md = rs.getMetaData();
	    
	    String columnLabel = md.getColumnLabel(idx);
	    String columnName = md.getColumnName(idx);
	    int displayWidth = columnLabel.length();
	    String fullTableColumnName = 
	        new StringBuilder(md.getTableName(idx))
	                .append(":")
	                .append(columnName)
	                .toString();
	    int sqlType = md.getColumnType(idx);
	    String sqlTypeName = md.getColumnTypeName(idx);
	    boolean isNullable = 
	        md.isNullable(idx) == ResultSetMetaData.columnNullable;
	    int columnSize = md.getColumnDisplaySize(idx);
	    int precision = md.getPrecision(idx);
	    int scale = md.getScale(idx);
        boolean isSigned = md.isSigned(idx);
        boolean isCurrency = md.isCurrency(idx);
        boolean isAutoIncrement = md.isAutoIncrement(idx);
        
        init(displayWidth, fullTableColumnName, columnName, columnLabel, sqlType, 
             sqlTypeName, isNullable, columnSize, precision, scale,
             isSigned, isCurrency, isAutoIncrement, dialectType);	    
	}
	
	
	public int getDisplayWidth()
	{
		return _displayWidth;
	}

	
	public String getFullTableColumnName()
	{
		return _fullTableColumnName;
	}

	
	public String getLabel()
	{
		return _label;
	}

	
	public int getSqlType()
	{
		return _sqlType;
	}

    public void setSqlType(int sqlType) {
        _sqlType = sqlType;
    }
    
	
	public String getSqlTypeName()
	{
		return _sqlTypeName;
	}

    public void setSqlTypeName(String sqlTypeName) {
        _sqlTypeName = sqlTypeName;
    }
    
	
	public boolean isNullable()
	{
		return _isNullable;
	}

	
	public void setIsNullable(boolean isNullable)
	{
		_isNullable = isNullable;
	}

	
	public int getColumnSize()
	{
		return _columnSize;
	}

	
	public int getPrecision()
	{
		return _precision;
	}

	
	public int getScale()
	{
		return _scale;
	}

	
	public boolean isSigned()
	{
		return _isSigned;
	}

	
	public boolean isCurrency()
	{
		return _isCurrency;
	}


	
	public String getClassName()
	{
		return CellComponentFactory.getClassName(this);
	}

	
   private void init(int displayWidth, String fullTableColumnName,
         String columnName, String label, int sqlType, String sqlTypeName,
         boolean isNullable, int columnSize, int precision, int scale,
         boolean isSigned, boolean isCurrency, boolean isAutoIncrement,
         DialectType dialectType) {
      if (label == null) {
         label = " "; 
      }
      _displayWidth = displayWidth;
      if (_displayWidth < label.length()) {
         _displayWidth = label.length();
      }
      _fullTableColumnName = fullTableColumnName;
      _columnName = columnName;
      
      
      _label = label.length() > 0 ? label : " ";

      _sqlType = sqlType;
      _sqlTypeName = sqlTypeName;
      if (sqlType == Types.DATE && DataTypeDate.getReadDateAsTimestamp()) {
         _sqlType = Types.TIMESTAMP;
         _sqlTypeName = "TIMESTAMP";
      }
      _isNullable = isNullable;
      _columnSize = columnSize;
      _precision = precision;
      _scale = scale;
      _isSigned = isSigned;
      _isCurrency = isCurrency;
      _isAutoIncrement = isAutoIncrement;
      _dialectType = dialectType;

   }
    
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[ columnName=");
        result.append(_columnName);
        result.append(", sqlType=");
        result.append(_sqlType);
        result.append(", sqlTypeName=");
        result.append(_sqlTypeName);
        result.append(", dialectType=");        
        result.append(_dialectType == null ? "null" : _dialectType.name());
        result.append(" ]");
        
        return result.toString();
    }

    public void setIsAutoIncrement(boolean autoIncrement) {
        _isAutoIncrement = autoIncrement;
    }
    
    public boolean isAutoIncrement() {
        return _isAutoIncrement;
    }

    
    public void setColumnName(String _columnName) {
        this._columnName = _columnName;
    }

    
    public String getColumnName() {
        return _columnName;
    }

   
   public DialectType getDialectType() {
      return _dialectType;
   }

   
   public void setDialectType(DialectType type) {
      _dialectType = type;
   }
   
   
}
