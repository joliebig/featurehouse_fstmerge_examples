package net.sourceforge.squirrel_sql.plugins.dbdiff.util;

public class AbstractDifference {

    public static enum DiffType { 
        TABLE_COLUMN_COUNT,
        TABLE_PK_NAME,
        TABLE_PK_COLUMN,
        TABLE_FK_NAME,
        TABLE_FK_COLUMN,
        TABLE_FK_COUNT,
        TABLE_INDEX_NAME,
        TABLE_INDEX_COLUMNS,
        TABLE_INDEX_UNIQUENESS,
        COLUMN_TYPE, 
        COLUMN_LENGTH, 
        COLUMN_PRECISION, 
        COLUMN_SCALE,
        COLUMN_DEFAULT_VALUE,
        COLUMN_NULLABILITY,
        COLUMN_COMMENT
    } 
    
    
    protected String _catalog1;
    
    
    protected String _schema1;
    
    
    protected String _tableName1;

    
    protected String _catalog2;
    
    
    protected String _schema2;
    
    
    protected String _tableName2;
    
    
    protected DiffType _differenceType;
    
     
    protected Object _differenceVal1;
    
    
    protected Object _differenceVal2;
    
    
    public void setDifferenceType(DiffType _differenceType) {
        this._differenceType = _differenceType;
    }

    
    public DiffType getDifferenceType() {
        return _differenceType;
    }

    
    public void setTableName1(String _tableName) {
        this._tableName1 = _tableName;
    }

    
    public String getTableName1() {
        return _tableName1;
    }

    
    public void setSchema1(String _schema) {
        this._schema1 = _schema;
    }

    
    public String getSchema1() {
        return _schema1;
    }

    
    public void setCatalog1(String _catalog1) {
        this._catalog1 = _catalog1;
    }

    
    public String getCatalog1() {
        return _catalog1;
    }

    
    public void setCatalog2(String _catalog2) {
        this._catalog2 = _catalog2;
    }

    
    public String getCatalog2() {
        return _catalog2;
    }

    
    public void setSchema2(String _schema2) {
        this._schema2 = _schema2;
    }

    
    public String getSchema2() {
        return _schema2;
    }

    
    public void setTableName2(String _tableName2) {
        this._tableName2 = _tableName2;
    }

    
    public String getTableName2() {
        return _tableName2;
    }

    
    public void setDifferenceVal1(Object _differenceVal1) {
        this._differenceVal1 = _differenceVal1;
    }

    
    public Object getDifferenceVal1() {
        return _differenceVal1;
    }

    
    public void setDifferenceVal2(Object _differenceVal2) {
        this._differenceVal2 = _differenceVal2;
    }

    
    public Object getDifferenceVal2() {
        return _differenceVal2;
    }
    
    
}
