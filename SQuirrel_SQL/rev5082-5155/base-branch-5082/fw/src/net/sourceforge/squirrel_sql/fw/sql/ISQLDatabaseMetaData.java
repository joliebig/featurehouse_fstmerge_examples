package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;


public interface ISQLDatabaseMetaData {

    
    String getUserName() throws SQLException;

    
    String getDatabaseProductName() throws SQLException;

    
    String getDatabaseProductVersion() throws SQLException;

        
    int getDatabaseMajorVersion() throws SQLException;
    
    
    String getDriverName() throws SQLException;

    
    int getJDBCVersion() throws SQLException;

    
    String getIdentifierQuoteString() throws SQLException;

    
    String getCascadeClause() throws SQLException;

    
    String[] getSchemas() throws SQLException;

    
    boolean supportsSchemas() throws SQLException;

    
    boolean supportsSchemasInDataManipulation() throws SQLException;

    
    boolean supportsSchemasInTableDefinitions() throws SQLException;

    
    boolean supportsStoredProcedures() throws SQLException;

    
    boolean supportsSavepoints() throws SQLException;

    
    boolean supportsResultSetType(int type) throws SQLException;

    
    String[] getCatalogs() throws SQLException;

    
    String getURL() throws SQLException;

    
    String getCatalogTerm() throws SQLException;

    
    String getSchemaTerm() throws SQLException;

    
    String getProcedureTerm() throws SQLException;

    
    String getCatalogSeparator() throws SQLException;

    
    boolean supportsCatalogs() throws SQLException;

    
    boolean supportsCatalogsInTableDefinitions() throws SQLException;

    
    boolean supportsCatalogsInDataManipulation() throws SQLException;

    
    boolean supportsCatalogsInProcedureCalls() throws SQLException;

    
    DatabaseMetaData getJDBCMetaData() throws SQLException;

    
    IDataSet getMetaDataSet() throws SQLException;

    
    IDataSet getTypesDataSet() throws DataSetException;

    
    DataTypeInfo[] getDataTypes() throws SQLException;

    
    IProcedureInfo[] getProcedures(String catalog, String schemaPattern,
            String procedureNamePattern, ProgressCallBack progressCallBack)
            throws SQLException;

    
    String[] getTableTypes() throws SQLException;

    
    ITableInfo[] getTables(String catalog, String schemaPattern,
            String tableNamePattern, String[] types,
            ProgressCallBack progressCallBack) throws SQLException;

    
    IUDTInfo[] getUDTs(String catalog, String schemaPattern,
            String typeNamePattern, int[] types) throws SQLException;

    
    String[] getNumericFunctions() throws SQLException;

    
    String[] getStringFunctions() throws SQLException;

    
    String[] getSystemFunctions() throws SQLException;

    
    String[] getTimeDateFunctions() throws SQLException;

    
    String[] getSQLKeywords() throws SQLException;

    BestRowIdentifier[] getBestRowIdentifier(ITableInfo ti) throws SQLException;

    
    IDataSet getColumnPrivilegesDataSet(ITableInfo ti, int[] columnIndices,
            boolean computeWidths) throws DataSetException;

    
    IDataSet getExportedKeysDataSet(ITableInfo ti) throws DataSetException;

    ForeignKeyInfo[] getImportedKeysInfo(String catalog, String schema,
            String tableName) throws SQLException;

    ForeignKeyInfo[] getImportedKeysInfo(ITableInfo ti) throws SQLException;

    IDataSet getImportedKeysDataSet(ITableInfo ti) throws DataSetException;

    ForeignKeyInfo[] getExportedKeysInfo(String catalog, String schema,
            String tableName) throws SQLException;

    ForeignKeyInfo[] getExportedKeysInfo(ITableInfo ti) throws SQLException;

    
    ResultSetDataSet getIndexInfo(ITableInfo ti, int[] columnIndices,
            boolean computeWidths) throws DataSetException;

    
    public List<IndexInfo> getIndexInfo(ITableInfo ti) throws SQLException;
    
    
    IDataSet getPrimaryKey(ITableInfo ti, int[] columnIndices,
            boolean computeWidths) throws DataSetException;

    
    PrimaryKeyInfo[] getPrimaryKey(ITableInfo ti) throws SQLException;

    
    PrimaryKeyInfo[] getPrimaryKey(String catalog, String schema, String table)
            throws SQLException;

    
    IDataSet getProcedureColumnsDataSet(IProcedureInfo ti)
            throws DataSetException;

    
    IDataSet getTablePrivilegesDataSet(ITableInfo ti, int[] columnIndices,
            boolean computeWidths) throws DataSetException;

    
    IDataSet getVersionColumnsDataSet(ITableInfo ti) throws DataSetException;

    
    IDataSet getColumns(ITableInfo ti, int[] columnIndices,
            boolean computeWidths) throws DataSetException;

    
    TableColumnInfo[] getColumnInfo(String catalog, String schema, String table)
            throws SQLException;

    
    TableColumnInfo[] getColumnInfo(ITableInfo ti) throws SQLException;

    
    boolean correctlySupportsSetMaxRows() throws SQLException;

    
    boolean supportsMultipleResultSets() throws SQLException;

    
    boolean storesUpperCaseIdentifiers() throws SQLException;

    
    void clearCache();

   
}
