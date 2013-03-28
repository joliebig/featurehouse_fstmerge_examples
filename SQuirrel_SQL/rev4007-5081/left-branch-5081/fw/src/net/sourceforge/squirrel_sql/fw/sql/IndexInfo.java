
package net.sourceforge.squirrel_sql.fw.sql;


public class IndexInfo extends DatabaseObjectInfo {
    
    private static final long serialVersionUID = 4146807206360206252L;

    public static enum IndexType {
        STATISTIC,
        CLUSTERED,
        HASHED,
        OTHER
    }
    
    public static enum SortOrder {
        ASC,
        DESC,
        NONE
    }
    
    
    private String columnName = null;
    
    private String tableName = null;
    
    private boolean nonUnique = false;
    
    private String indexQualifier = null;
    
    private IndexType indexType = null;
    
    private short ordinalPosition;
    
    private SortOrder sortOrder = null;
    
    private int cardinality;
    
    private int pages;
    
    private String filterCondition = null;
    
    public IndexInfo(String catalog, 
                     String schema, 
                     String indexName,
                     String tableName,
                     String columnName,
                     boolean nonUnique,
                     String indexQualifier,
                     IndexType indexType,
                     short ordinalPosition,
                     SortOrder sortOrder,
                     int cardinality,
                     int pages,
                     String filterCondition,
                     ISQLDatabaseMetaData md) 
    {
        super(catalog, schema, indexName, DatabaseObjectType.INDEX, md);
        this.tableName = tableName;
        this.columnName = columnName;
        this.nonUnique = nonUnique;
        this.indexType = indexType;
        this.ordinalPosition = ordinalPosition;
        this.sortOrder = sortOrder;
        this.cardinality = cardinality;
        this.pages = pages;
        this.filterCondition = filterCondition;
    }

    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    
    public String getColumnName() {
        return columnName;
    }

    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    
    public String getTableName() {
        return tableName;
    }

    
    public void setNonUnique(boolean nonUnique) {
        this.nonUnique = nonUnique;
    }

    
    public boolean isNonUnique() {
        return nonUnique;
    }

    
    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    
    public IndexType getIndexType() {
        return indexType;
    }

    
    public void setOrdinalPosition(short ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    
    public short getOrdinalPosition() {
        return ordinalPosition;
    }

    
    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    
    public SortOrder getSortOrder() {
        return sortOrder;
    }

    
    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    
    public int getCardinality() {
        return cardinality;
    }

    
    public void setPages(int pages) {
        this.pages = pages;
    }

    
    public int getPages() {
        return pages;
    }

    
    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition;
    }

    
    public String getFilterCondition() {
        return filterCondition;
    }

    
    public void setIndexQualifier(String indexQualifier) {
        this.indexQualifier = indexQualifier;
    }

    
    public String getIndexQualifier() {
        return indexQualifier;
    }
}
