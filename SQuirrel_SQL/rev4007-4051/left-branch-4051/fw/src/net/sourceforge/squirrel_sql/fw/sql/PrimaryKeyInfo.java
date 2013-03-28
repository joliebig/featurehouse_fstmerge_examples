package net.sourceforge.squirrel_sql.fw.sql;




public class PrimaryKeyInfo extends DatabaseObjectInfo
{
    static final long serialVersionUID = 4785889679696720264L;

    
    private String columnName = null;
    
    
    private short keySequence;
    
    
    private String tableName = null;
    
    
    PrimaryKeyInfo() {
        super(null, null, null, null, null);
    }
    
    
	public PrimaryKeyInfo(String catalog, 
                   String schema,
                   String aTableName,
                   String aColumnName, 
                   short aKeySequence, 
                   String aPrimaryKeyName,
                   ISQLDatabaseMetaData md)
	{
		super(catalog, schema, aPrimaryKeyName, DatabaseObjectType.PRIMARY_KEY, md);
        columnName = aColumnName;
        tableName = aTableName;
        keySequence = aKeySequence;
	}

    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    
    public String getColumnName() {
        return columnName;
    }

    
    public void setKeySequence(short keySequence) {
        this.keySequence = keySequence;
    }

    
    public short getKeySequence() {
        return keySequence;
    }

    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    
    public String getTableName() {
        return tableName;
    }

    public String getQualifiedColumnName() {
        if (tableName != null && !"".equals(tableName)) {
            return tableName + "." + columnName;
        }
        return columnName;
    }
}
