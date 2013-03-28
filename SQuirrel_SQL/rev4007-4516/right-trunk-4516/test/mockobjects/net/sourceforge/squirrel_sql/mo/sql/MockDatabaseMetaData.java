
package net.sourceforge.squirrel_sql.mo.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import com.mockobjects.sql.MockSingleRowResultSet;


@SuppressWarnings("unused")
public class MockDatabaseMetaData extends
        com.mockobjects.sql.MockDatabaseMetaData {
	
	private MockResultSet catalogs = null;
	private MockResultSet schemas = null;
	private String keywords = null;
	private MockResultSet typeInfo = null;
	private MockResultSet procedures = null;
	
	private String catalog = "aCatalog";
	private String schema = "aSchema";
	
	private String catalogTerm = "CATALOG";
	private String schemaTerm = "SCHEMA";
	private String procedureTerm = "PROCEDURE";
	
	public MockDatabaseMetaData() {
		
	}
	
	public MockDatabaseMetaData(String currentCatalog,
								String currentSchema) 
	{
		catalog = currentCatalog;
		schema = currentSchema;
	}	
	
    
    public String getIdentifierQuoteString() throws SQLException {
        
        return "\"";
    }

    
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return false;
    }

    
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return false;
    }

    
    public ResultSet getTableTypes() throws SQLException {
        MockSingleRowResultSet rs = new MockSingleRowResultSet();
        rs.addExpectedIndexedValues(new Object[] { "TABLE" });
        return rs;
    }

    
    public String getDatabaseProductName() throws SQLException {
        return "junitDBProductName";
    }

    
    public String getDatabaseProductVersion() throws SQLException {
        return "1.0";
    }
    
    
    public boolean allProceduresAreCallable() throws SQLException {
        
        return false;
    }

    
    public boolean allTablesAreSelectable() throws SQLException {
        
        return false;
    }

    
    public boolean deletesAreDetected(int arg0) throws SQLException {
        
        return false;
    }

    
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        
        return false;
    }

    
    public boolean insertsAreDetected(int arg0) throws SQLException {
        
        return false;
    }

    
    public boolean isCatalogAtStart() throws SQLException {
        
        return false;
    }

    
    public boolean isReadOnly() throws SQLException {
        
        return false;
    }

    
    public boolean locatorsUpdateCopy() throws SQLException {
        
        return false;
    }

    
    public boolean nullPlusNonNullIsNull() throws SQLException {
        
        return false;
    }

    
    public boolean nullsAreSortedAtEnd() throws SQLException {
        
        return false;
    }

    
    public boolean nullsAreSortedAtStart() throws SQLException {
        
        return false;
    }

    
    public boolean nullsAreSortedHigh() throws SQLException {
        
        return false;
    }

    
    public boolean nullsAreSortedLow() throws SQLException {
        
        return false;
    }

    
    public boolean othersDeletesAreVisible(int arg0) throws SQLException {
        
        return false;
    }

    
    public boolean othersInsertsAreVisible(int arg0) throws SQLException {
        
        return false;
    }

    
    public boolean othersUpdatesAreVisible(int arg0) throws SQLException {
        
        return false;
    }

    
    public boolean ownDeletesAreVisible(int arg0) throws SQLException {
        
        return false;
    }

    
    public boolean ownInsertsAreVisible(int arg0) throws SQLException {
        
        return false;
    }

    
    public boolean ownUpdatesAreVisible(int arg0) throws SQLException {
        
        return false;
    }

    
    public void setupDriverName(String arg0) {
        
        super.setupDriverName(arg0);
    }

    
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        
        return false;
    }

    
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        
        return false;
    }

    
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        
        return false;
    }

    
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        
        return false;
    }

    
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        
        return false;
    }

    
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        
        return false;
    }

    
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        
        return false;
    }

    
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        
        return false;
    }

    
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        
        return false;
    }

    
    public boolean supportsANSI92FullSQL() throws SQLException {
        
        return false;
    }

    
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        
        return false;
    }

    
    public boolean supportsBatchUpdates() throws SQLException {
        
        return false;
    }

    
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        
        return false;
    }

    
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        
        return false;
    }

    
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        
        return false;
    }

    
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        
        return false;
    }

    
    public boolean supportsColumnAliasing() throws SQLException {
        
        return false;
    }

    
    public boolean supportsConvert() throws SQLException {
        
        return false;
    }

    
    public boolean supportsConvert(int arg0, int arg1) throws SQLException {
        
        return false;
    }

    
    public boolean supportsCoreSQLGrammar() throws SQLException {
        
        return false;
    }

    
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        
        return false;
    }

    
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        
        return false;
    }

    
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        
        return false;
    }

    
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        
        return false;
    }

    
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        
        return false;
    }

    
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        
        return false;
    }

    
    public boolean supportsFullOuterJoins() throws SQLException {
        
        return false;
    }

    
    public boolean supportsGetGeneratedKeys() throws SQLException {
        
        return false;
    }

    
    public boolean supportsGroupBy() throws SQLException {
        
        return false;
    }

    
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        
        return false;
    }

    
    public boolean supportsGroupByUnrelated() throws SQLException {
        
        return false;
    }

    
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        
        return false;
    }

    
    public boolean supportsLikeEscapeClause() throws SQLException {
        
        return false;
    }

    
    public boolean supportsLimitedOuterJoins() throws SQLException {
        
        return false;
    }

    
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        
        return false;
    }

    
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        
        return false;
    }

    
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        
        return false;
    }

    
    public boolean supportsMultipleOpenResults() throws SQLException {
        
        return false;
    }

    
    public boolean supportsMultipleResultSets() throws SQLException {
        
        return false;
    }

    
    public boolean supportsMultipleTransactions() throws SQLException {
        
        return false;
    }

    
    public boolean supportsNamedParameters() throws SQLException {
        
        return false;
    }

    
    public boolean supportsNonNullableColumns() throws SQLException {
        
        return false;
    }

    
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        
        return false;
    }

    
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        
        return false;
    }

    
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        
        return false;
    }

    
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        
        return false;
    }

    
    public boolean supportsOrderByUnrelated() throws SQLException {
        
        return false;
    }

    
    public boolean supportsOuterJoins() throws SQLException {
        
        return false;
    }

    
    public boolean supportsPositionedDelete() throws SQLException {
        
        return false;
    }

    
    public boolean supportsPositionedUpdate() throws SQLException {
        
        return false;
    }

    
    public boolean supportsResultSetConcurrency(int arg0, int arg1) throws SQLException {
        
        return false;
    }

    
    public boolean supportsResultSetHoldability(int arg0) throws SQLException {
        
        return false;
    }

    
    public boolean supportsResultSetType(int arg0) throws SQLException {
        
        return false;
    }

    
    public boolean supportsSavepoints() throws SQLException {
        
        return false;
    }

    
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        
        return false;
    }

    
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        
        return false;
    }

    
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        
        return false;
    }

    
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        
        return false;
    }

    
    public boolean supportsSelectForUpdate() throws SQLException {
        
        return false;
    }

    
    public boolean supportsStatementPooling() throws SQLException {
        
        return false;
    }

    
    public boolean supportsStoredProcedures() throws SQLException {
        
        return false;
    }

    
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        
        return false;
    }

    
    public boolean supportsSubqueriesInExists() throws SQLException {
        
        return false;
    }

    
    public boolean supportsSubqueriesInIns() throws SQLException {
        
        return false;
    }

    
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        
        return false;
    }

    
    public boolean supportsTableCorrelationNames() throws SQLException {
        
        return false;
    }

    
    public boolean supportsTransactionIsolationLevel(int arg0) throws SQLException {
        
        return false;
    }

    
    public boolean supportsTransactions() throws SQLException {
        
        return false;
    }

    
    public boolean supportsUnion() throws SQLException {
        
        return false;
    }

    
    public boolean supportsUnionAll() throws SQLException {
        
        return false;
    }

    
    public boolean updatesAreDetected(int arg0) throws SQLException {
        
        return false;
    }

    
    public boolean usesLocalFilePerTable() throws SQLException {
        
        return false;
    }

    
    public boolean usesLocalFiles() throws SQLException {
        
        return false;
    }

	public ResultSet getCatalogs() throws SQLException {
		return catalogs;
	}

	public void setCatalogs(String[] catalogNames, SQLDatabaseMetaData md) {
		catalogs = new MockResultSet();
        TableColumnInfo[] cols = new TableColumnInfo[] {
                new TableColumnInfo("aCatalog", 
                                    "aSchema",  
                                    "",         
                                    "",         
                                    1,          
                                    "",         
                                    0,  
                                    0,  
                                    0,  
                                    0,  
                                    "",
                                    "", 
                                    0, 
                                    0, 
                                    "", 
                                    md) };      
        catalogs.setTableColumnInfos(cols);
		for (int i = 0; i < catalogNames.length; i++) {
			catalogs.addRow(new Object[] {catalogNames[i]});
		}
	}

	public ResultSet getSchemas() throws SQLException {
		return schemas;
	}

	public void setSchemas(String[] schemaNames, SQLDatabaseMetaData md) {
		schemas = new MockResultSet(null);
        TableColumnInfo[] cols = new TableColumnInfo[] {
                                    new TableColumnInfo("aCatalog", 
                                                        "aSchema",  
                                                        "",         
                                                        "",         
                                                        1,          
                                                        "",         
                                                        0,  
                                                        0,  
                                                        0,  
                                                        0,  
                                                        "",
                                                        "", 
                                                        0, 
                                                        0, 
                                                        "", 
                                                        md) };      
        schemas.setTableColumnInfos(cols);
        for (int i = 0; i < schemaNames.length; i++) {
			schemas.addRow(new Object[] {schemaNames[i]});
		}
		
	}
    
	public String getSQLKeywords() throws SQLException {
		return "";
	}
	
	public void setSQLKeywords(String[] someKeywords) {
		StringBuffer tmp = new StringBuffer();
		for (int i = 0; i < someKeywords.length; i++) {
			tmp.append(someKeywords[i]);
			if (i < someKeywords.length) {
				tmp.append(",");
			}
		}
		keywords = tmp.toString();
	}
	
	public ResultSet getTypeInfo() throws SQLException {
		return new MockResultSet(null);
	}
	
	public ResultSet getProcedures(String catalog, 
								   String schemaPattern, 
								   String procedureNamrPattern) 
		throws SQLException
	{
		return new MockResultSet(null);
	}
	
	public String getNumericFunctions() throws SQLException {
		return "";
	}

	public String getStringFunctions() throws SQLException {
		return "";
	}

	public ResultSet getTables(String aCatalog, 
							   String schemaPattern, 
							   String tableNamePattern, 
							   String[] types) 
		throws SQLException 
	{
		return new MockResultSet(null);
	}

	public String getTimeDateFunctions() throws SQLException {
		return "";
	}
	
	public void setCatalogTerm(String aCatalogTerm) {
		catalogTerm = aCatalogTerm;
	}
	
    public String getCatalogSeparator() {
        return ".";
    }
    
	public String getCatalogTerm() {
		return catalogTerm;
	}

	
	public void setSchemaTerm(String schemaTerm) {
		this.schemaTerm = schemaTerm;
	}

	
	public String getSchemaTerm() {
		return schemaTerm;
	}

	
	public void setProcedureTerm(String procedureTerm) {
		this.procedureTerm = procedureTerm;
	}

	
	public String getProcedureTerm() {
		return procedureTerm;
	}

	public String getDriverName() throws SQLException {
		return "MockDatabaseDriver";
	}

	
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException
	{
		
		return false;
	}

	
	public ResultSet getClientInfoProperties() throws SQLException
	{
		
		return null;
	}

	
	public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern,
		String columnNamePattern) throws SQLException
	{
		
		return null;
	}

	
	public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
		throws SQLException
	{
		
		return null;
	}

	
	public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException
	{
		
		return null;
	}

}
