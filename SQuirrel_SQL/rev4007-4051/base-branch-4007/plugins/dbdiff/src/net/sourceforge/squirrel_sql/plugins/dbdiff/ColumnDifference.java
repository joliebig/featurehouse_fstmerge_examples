package net.sourceforge.squirrel_sql.plugins.dbdiff;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;



public class ColumnDifference {

    private String tableName = "";
    private String columnName = "";

    private int col1Type = -1;
    private int col2Type = -1;
    
    private int col1Length = 0;
    private int col2Length = 0;
    
    private boolean col1IsNullable = false;
    private boolean col2IsNullable = false;
    
    private boolean col1Exists = true;
    private boolean col2Exists = true;
    
    private String col1remarks = "";
    private String col2remarks = "";
    
    public ColumnDifference() {}
    
    public void setColumns(TableColumnInfo c1, TableColumnInfo c2) {
        if (c1 == null || c2 == null) {
            throw new IllegalArgumentException("c1, c2 cannot be null");
        }
        if (!c1.getTableName().equals(c2.getTableName())) {
            throw new IllegalArgumentException(
                "Columns to be compared must be from the same table");
        }
        if (!c1.getColumnName().equals(c2.getColumnName())) {
            throw new IllegalArgumentException(
                "Columns to be compared must have the same column name");
        }
        setColumn1(c1);
        setColumn2(c2);
    }

    public void setColumn1(TableColumnInfo c1) {
        col1Type = c1.getDataType();
        col1Length = c1.getColumnSize();
        col1IsNullable = c1.isNullable().equalsIgnoreCase("NO") ? false : true;
        tableName = c1.getTableName();
        columnName = c1.getColumnName();        
        col1remarks = c1.getRemarks();
    }

    public void setColumn2(TableColumnInfo c2) {
        col2Type = c2.getDataType();
        col2Length = c2.getColumnSize();
        col2IsNullable = c2.isNullable().equalsIgnoreCase("NO") ? false : true;
        tableName = c2.getTableName();
        columnName = c2.getColumnName();    
        col2remarks = c2.getRemarks();
    }
    
    public int getCol1Type() {
        return col1Type;
    }
    
    public int getCol1Length() {
        return col1Length;
    }
    
    public boolean col1AllowsNull() {
        return col1IsNullable;
    }

    public int getCol2Type() {
        return col2Type;
    }
    
    public int getCol2Length() {
        return col2Length;
    }
    
    public boolean col2AllowsNull() {
        return col2IsNullable;
    }
    
    public String getCol1Remarks() {
        return col1remarks;
    }
    
    public String getCol2Remarks() {
        return col2remarks;
    }
    
    
    public boolean execute() {
        if (!col1Exists || !col2Exists) {
            return true;
        }
        if (col1Type != col2Type) {
            return true;
        }
        if (col1Length != col2Length) {
            return true;
        }
        if (col1IsNullable != col2IsNullable) {
            return true;
        }
        if (!remarksEqual()) {
            return true;
        }
        return false;
    }

    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    
    public String getTableName() {
        return tableName;
    }

    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    
    public String getColumnName() {
        return columnName;
    }

    
    public void setCol1Exists(boolean col1Exists) {
        this.col1Exists = col1Exists;
    }

    
    public boolean isCol1Exists() {
        return col1Exists;
    }

    
    public void setCol2Exists(boolean col2Exists) {
        this.col2Exists = col2Exists;
    }

    
    public boolean isCol2Exists() {
        return col2Exists;
    }
    
    public boolean remarksEqual() {
        if (col1remarks == null && col2remarks == null) {
            return true;
        }
        if ((col1remarks == null && col2remarks != null) 
                || (col1remarks != null && col2remarks == null)) {
            return false;
        }
        return col1remarks.equals(col2remarks);
    }
    
    public boolean typesEqual() {
        return col1Type == col2Type;
    }
    
    public boolean lengthsEqual() {
        return col1Length == col2Length;
    }
    
    public boolean nullableEqual() {
        return col1IsNullable == col2IsNullable;
    }
    
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("TABLE: ");
        result.append(tableName);
        result.append(" COLUMN: ");
        result.append(columnName);
        result.append("\n");
        result.append("source colType: ");
        result.append(col1Type);
        result.append("\n");
        
        result.append("dest colType: ");
        result.append(col2Type);
        result.append("\n");

        result.append("source colLength: ");
        result.append(col1Length);
        result.append("\n");

        result.append("dest colLength: ");
        result.append(col2Length);
        result.append("\n");

        result.append("source IsNullable: ");
        result.append(col1IsNullable);
        result.append("\n");

        result.append("dest IsNullable: ");
        result.append(col2IsNullable);
        result.append("\n");
        
        return result.toString();
    }
}
