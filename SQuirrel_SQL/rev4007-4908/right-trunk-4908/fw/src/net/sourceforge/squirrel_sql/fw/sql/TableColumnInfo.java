package net.sourceforge.squirrel_sql.fw.sql;

public class TableColumnInfo extends DatabaseObjectInfo
{
    static final long serialVersionUID = 3529392685978921375L;
    
    private final String _tableName;
	private final String _columnName;
	private final int _dataType;
	private final String _typeName;
	private final int _columnSize;
	private final int _decimalDigits;
	private final int _radix;
	private final int _isNullAllowed;
	private final String _remarks;
	private final String _defaultValue;
	private final int _octetLength;
	private final int _ordinalPosition;
	private final String _isNullable;

	public TableColumnInfo(String catalog, String schema, String tableName,
							String columnName, int dataType, String typeName,
							int columnSize, int decimalDigits, int radix,
							int isNullAllowed, String remarks, String defaultValue,
							int octetLength, int ordinalPosition,
							String isNullable, ISQLDatabaseMetaData md)
	{
		super(catalog, schema, tableName + '.' + columnName,
				DatabaseObjectType.COLUMN, md);
        _tableName = tableName;
		_columnName = columnName;
		_dataType = dataType;
		_typeName = typeName;
		_columnSize = columnSize;
		_decimalDigits = decimalDigits;
		_radix = radix;
		_isNullAllowed = isNullAllowed;
		_remarks = remarks;
		_defaultValue = defaultValue;
		_octetLength = octetLength;
		_ordinalPosition = ordinalPosition;
		_isNullable = isNullable;
	}

    public TableColumnInfo(String catalog, String schema, String tableName,
            String columnName, int dataType, String typeName,
            int columnSize, int decimalDigits, int radix,
            int isNullAllowed, String remarks, String defaultValue,
            int octetLength, int ordinalPosition,
            String isNullable)
    {
        super(catalog, schema, tableName);
        _tableName = tableName;
        _columnName = columnName;
        _dataType = dataType;
        _typeName = typeName;
        _columnSize = columnSize;
        _decimalDigits = decimalDigits;
        _radix = radix;
        _isNullAllowed = isNullAllowed;
        _remarks = remarks;
        _defaultValue = defaultValue;
        _octetLength = octetLength;
        _ordinalPosition = ordinalPosition;
        _isNullable = isNullable;
    }
    
    public String getTableName() {
        return _tableName;
    }
    
	public String getColumnName()
	{
		return _columnName;
	}

	public int getDataType()
	{
		return _dataType;
	}

	public String getTypeName()
	{
		return _typeName;
	}

	public int getColumnSize()
	{
		return _columnSize;
	}

	public int getDecimalDigits()
	{
		return _decimalDigits;
	}

	public int getRadix()
	{
		return _radix;
	}

	public int isNullAllowed()
	{
		return _isNullAllowed;
	}

	public String getRemarks()
	{
		return _remarks;
	}

	public String getDefaultValue()
	{
		return _defaultValue;
	}

	public int getOctetLength()
	{
		return _octetLength;
	}

	public int getOrdinalPosition()
	{
		return _ordinalPosition;
	}

	public String isNullable()
	{
		return _isNullable;
	}
}
