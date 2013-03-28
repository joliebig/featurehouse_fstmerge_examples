

package net.sourceforge.squirrel_sql.plugins.dbdiff.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.I18NBaseObject;
import net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider;

import org.hibernate.MappingException;



public class DBUtil extends I18NBaseObject {

    
    private final static ILogger log = 
        LoggerController.createLogger(DBUtil.class);    
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DBUtil.class);    
        
    
    private static String lastStatement = null;
    
    private static String lastStatementValues = null;
    
    
    public static String getPKColumnString(ISQLConnection sourceConn,
                                           ITableInfo ti) 
        throws SQLException 
    {
        List<String> pkColumns = getPKColumnList(sourceConn, ti);
        if (pkColumns == null || pkColumns.size() == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer("(");
        Iterator<String> i = pkColumns.iterator();
        while (i.hasNext()) {
            String columnName = i.next();
            sb.append(columnName);
            if (i.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    
    private static List<String> getPKColumnList(ISQLConnection sourceConn,
                                        ITableInfo ti) 
        throws SQLException 
    {
        ArrayList<String> pkColumns = new ArrayList<String>();
        DatabaseMetaData md = sourceConn.getConnection().getMetaData();
        ResultSet rs = null;
        if (md.supportsCatalogsInTableDefinitions()) {
            rs = md.getPrimaryKeys(ti.getCatalogName(), null, ti.getSimpleName());
        } else if (md.supportsSchemasInTableDefinitions()) {
            rs = md.getPrimaryKeys(null, ti.getSchemaName(), ti.getSimpleName());
        } else {
            rs = md.getPrimaryKeys(null, null, ti.getSimpleName());
        }
        while (rs.next()) {
            String keyColumn = rs.getString(4);
            if (keyColumn != null) {
                pkColumns.add(keyColumn);
            }
        }
        if (pkColumns.size() == 0) {
            return null;
        }        
        return pkColumns;
    }
            
    public static boolean tableHasForeignKey(String destCatalog, 
                                             String destSchema, 
                                             String destTableName,
                                             ForeignKeyInfo fkInfo,
                                             SessionInfoProvider prov)
    {
        boolean result = false;
        try {
            SQLDatabaseMetaData md = 
                prov.getDiffDestSession().getSQLConnection().getSQLMetaData();
            
            ITableInfo[] tables = md.getTables(destCatalog, 
                                                destSchema, 
                                                destTableName, 
                                                new String[] {"TABLE"}, 
                                                null);
            if (tables != null && tables.length == 1) {
                ForeignKeyInfo[] fks = SQLUtilities.getImportedKeys(tables[0], md);
                for (ForeignKeyInfo existingKey: fks) {
                    if (areEqual(existingKey, fkInfo)) {
                        result = true;
                        break;
                    }
                }
            } else {
                log.error(
                    "Couldn't find an exact match for destination table "+
                    destTableName+" in schema "+destSchema+" and catalog "+
                    destCatalog+". Skipping FK constraint");
            }
        } catch (SQLException e) {
            log.error("Unexpected exception while attempting to determine if " +
                      "a table ("+destTableName+") has a particular foreign " +
                      "key");
        }
        return result;
    }
    
    private static boolean areEqual(ForeignKeyInfo fk1, ForeignKeyInfo fk2) {
        String fk1FKColumn = fk1.getForeignKeyColumnName();
        String fk2FKColumn = fk2.getForeignKeyColumnName();
        String fk1PKColumn = fk1.getPrimaryKeyColumnName();
        String fk2PKColumn = fk2.getPrimaryKeyColumnName();
        String fk1FKTable = fk1.getForeignKeyTableName();
        String fk2FKTable = fk2.getForeignKeyTableName();
        String fk1PKTable = fk1.getPrimaryKeyTableName();
        String fk2PKTable = fk2.getPrimaryKeyTableName();
        
        if (!fk1PKColumn.equals(fk2PKColumn)) {
            return false;
        }        
        if (!fk1FKColumn.equals(fk2FKColumn)) {
            return false;
        }
        if (!fk1PKTable.equals(fk2PKTable)) {
            return false;
        }
        if (!fk1FKTable.equals(fk2FKTable)) {
            return false;
        }        
        return true;
    }
    
    public static boolean containsTable(List<ITableInfo> tableInfos, 
                                        String table) 
    {
        boolean result = false;
        for (ITableInfo ti : tableInfos) {
            if (table.equalsIgnoreCase(ti.getSimpleName())) {
                result = true;
                break;
            }            
        }
        return result;
    }
    

    
    public static ResultSet executeQuery(ISession session, 
                                         String sql) 
        throws SQLException 
    {
    	ISQLConnection sqlcon = session.getSQLConnection(); 
        if (sqlcon == null || sql == null) {
            return null;
        }
        Statement stmt = null;
        ResultSet rs = null;

        Connection con = sqlcon.getConnection();
        try {
            stmt = con.createStatement();
        } catch(SQLException e) {
            
            
            if (stmt != null) { 
                try {stmt.close();} catch (SQLException ex) { }
            }
            throw e;
        }
        if (log.isDebugEnabled()) {
            
            String msg = 
                s_stringMgr.getString("DBUtil.info.executequery", sql);
            log.debug(msg);
        }
        try {
            lastStatement = sql;
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            
            
            if (stmt != null) { 
                try {stmt.close();} catch (SQLException ex) { }
            }
            throw e;            
        }
 
        return rs;
    }    
    
    
    public static void closeResultSet(ResultSet rs) {
        if (rs == null) {
            return;
        }
        try {
            Statement stmt = rs.getStatement();
            closeStatement(stmt);
        } catch (Exception e) {  }
    }

    
    
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                ResultSet rs = stmt.getResultSet();
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {}
            try { stmt.close(); } catch (SQLException e) {}
        }
    }
    
        
    private static int getTableCount(ISession session, String tableName) {
        int result = -1;
        ResultSet rs = null;
        try {
            String sql = "select count(*) from "+tableName;            
            rs = executeQuery(session, sql);
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (Exception e) {
            
        } finally {
            closeResultSet(rs);
        }
        return result;        
    }
    
    
    public static int getTableCount(ISession session, 
                                    String catalog,
                                    String schema, 
                                    String tableName,
                                    int sessionType) 
        throws UserCancelledOperationException
    {
        String table = getQualifiedObjectName(session, 
                                              catalog, 
                                              schema,
                                              tableName, 
                                              sessionType);
        return getTableCount(session, table);
    }
    
    public static ITableInfo getTableInfo(ISession session,
                                          String schema,
                                          String tableName) 
        throws SQLException, MappingException, UserCancelledOperationException 
    {
        ISQLConnection con = session.getSQLConnection();
        SchemaInfo schemaInfo = session.getSchemaInfo();
        
        
        
        if (con.getSQLMetaData().getDriverName().toLowerCase().startsWith("axion")) {
            schema = "";
        }
        String catalog = null;
        
        if (DialectFactory.isMySQL(session.getMetaData())) {
            catalog = schema;
            schema = null;
        }
        
        tableName = tableName.trim();
        ITableInfo[] tis = schemaInfo.getITableInfos(catalog, schema, tableName);
        
        
        
        if (tis == null || tis.length == 0) {
            if (Character.isUpperCase(tableName.charAt(0))) {
                tableName = tableName.toLowerCase();
            } else {
                tableName = tableName.toUpperCase();
            }
            tis = schemaInfo.getITableInfos(null, schema, tableName);
            if (tis.length == 0) {
                if (Character.isUpperCase(tableName.charAt(0))) {
                    tableName = tableName.toLowerCase();
                } else {
                    tableName = tableName.toUpperCase();
                }
                tis = schemaInfo.getITableInfos(null, schema, tableName);
            }
        }
        if (tis.length == 0) {
            
            
            String msg = 
                s_stringMgr.getString("DBUtil.error.tablenotfound",
                                      new String[] { tableName,
                                                     schema });
            throw new MappingException(msg);
        }
        if (tis.length > 1) {
        	if (log.isDebugEnabled()) {
        		log.debug(
        			"DBUtil.getTableInfo: found "+tis.length+" that matched "+
        			"catalog="+catalog+" schema="+schema+" tableName="+
        			tableName);
        	}
        }
        return tis[0];
    }
            
    
    public static int replaceOtherDataType(TableColumnInfo colInfo) 
    	throws MappingException 
    {
    	int colJdbcType = colInfo.getDataType();
        if (colJdbcType == java.sql.Types.OTHER) {
            String typeName = colInfo.getTypeName().toUpperCase();
            int parenIndex = typeName.indexOf("(");
            if (parenIndex != -1) {
                typeName = typeName.substring(0,parenIndex);
            }
            colJdbcType = JDBCTypeMapper.getJdbcType(typeName);
            if (colJdbcType == Types.NULL) {
                throw new MappingException(
                        "Encoutered jdbc type OTHER (1111) and couldn't map "+
                        "the database-specific type name ("+typeName+
                        ") to a jdbc type");
            }
        }
        return colJdbcType;
    }
    
    
    public static int getColumnType(ISQLConnection con, 
                                    ITableInfo ti, 
                                    String columnName) throws SQLException { 
        int result = -1;
        if (ti != null) {
            TableColumnInfo[] tciArr = con.getSQLMetaData().getColumnInfo(ti);
            for (int i=0; i < tciArr.length; i++) {
                if (tciArr[i].getColumnName().equalsIgnoreCase(columnName)) {
                    result = tciArr[i].getDataType();
                    break;
                }
            }
        }
        return result;
    }
        
    public static int[] getColumnTypes(ISQLConnection con, 
                                       ITableInfo ti, 
                                       String[] colNames) 
        throws SQLException 
    {
        TableColumnInfo[] tciArr = con.getSQLMetaData().getColumnInfo(ti);
        int[] result = new int[tciArr.length];
        for (int i=0; i < tciArr.length; i++) {
            boolean found = false;
            for (int j=0; j < colNames.length && !found; j++) {
                String columnName = colNames[j];
                if (tciArr[i].getColumnName().equalsIgnoreCase(columnName)) {
                    result[i] = tciArr[i].getDataType();
                    found = true;
                }
            }
        }
        return result;
    }
    
    public static boolean tableHasPrimaryKey(ISQLConnection con,
                                             ITableInfo ti) 
        throws SQLException
    {
        boolean result = false;
        ResultSet rs = null;
        try {
            DatabaseMetaData md = con.getConnection().getMetaData();
            String cat = ti.getCatalogName();
            String schema = ti.getSchemaName();
            String tableName = ti.getSimpleName();
            rs = md.getPrimaryKeys(cat, schema, tableName);
            if (rs.next()) {
                result = true;
            }
        } finally { 
            closeResultSet(rs);
        }
        return result;
    }

    
    public static String getColumnList(TableColumnInfo[] colInfoArr) 
        throws SQLException 
    {
        StringBuffer result = new StringBuffer();
        
        for (int i = 0; i < colInfoArr.length; i++) {
            TableColumnInfo colInfo = colInfoArr[i];
            String columnName = colInfo.getColumnName();
            result.append(columnName);
            if (i < colInfoArr.length-1) {
                result.append(", ");
            }
        }       
        return result.toString();
    }    
    
            
    
    public static String getColumnName(ISQLConnection sourceConn, 
                                       ITableInfo ti, 
                                       int column) 
    throws SQLException 
    {
        TableColumnInfo[] infoArr = sourceConn.getSQLMetaData().getColumnInfo(ti);
        TableColumnInfo colInfo = infoArr[column];
        return colInfo.getColumnName();
    }
        
    
    public static String[] getColumnNames(ISQLConnection sourceConn, 
                                          ITableInfo ti) 
        throws SQLException 
    {
        TableColumnInfo[] infoArr = sourceConn.getSQLMetaData().getColumnInfo(ti);
        String[] result = new String[infoArr.length];
        for (int i = 0; i < result.length; i++) {
            TableColumnInfo colInfo = infoArr[i];
            result[i] = colInfo.getColumnName();
        }
        return result;
    }
    
    
    public static String getSelectQuery(SessionInfoProvider prov, 
                                        String columnList, 
                                        ITableInfo ti) 
        throws SQLException, UserCancelledOperationException 
    {
        StringBuffer result = new StringBuffer("select ");
        result.append(columnList);
        result.append(" from ");
        ISession sourceSession = prov.getDiffSourceSession();
        
        
        
        
        String tableName = getQualifiedObjectName(sourceSession, 
                                                  ti.getCatalogName(), 
                                                  ti.getSchemaName(), 
                                                  ti.getSimpleName(), 
                                                  DialectFactory.SOURCE_TYPE);
        result.append(tableName);
        return result.toString();
    }
    
    
    public static boolean isBinaryType(TableColumnInfo columnInfo) {
        boolean result = false;
        int type = columnInfo.getDataType();
        if (type == Types.BINARY
                || type == Types.BLOB
                || type == Types.LONGVARBINARY
                || type == Types.VARBINARY)
        {
            result = true;
        }
        return result;
    }  
    
    
    public static String getQualifiedObjectName(ISession session,
                                                String catalogName,
                                                String schemaName,
                                                String objectName, 
                                                int sessionType) 
        throws UserCancelledOperationException
    {
        String catalog = fixCase(session, catalogName);
        String schema = fixCase(session, schemaName);
        String object = fixCase(session, objectName);
        SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
        boolean useSchema = true;
        boolean useCatalog = true;
        try {
        	useCatalog = md.supportsCatalogsInTableDefinitions();
        } catch (SQLException e) {
        	log.info("Encountered unexpected exception while attempting to " +
        			"determine if catalogs are used in table definitions");
        }
        try {        
        	useSchema = md.supportsSchemasInTableDefinitions();
        } catch (SQLException e) {
        	log.info("Encountered unexpected exception while attempting to " +
        			"determine if schemas are used in table definitions");
        }
        if (!useCatalog && !useSchema) {
        	return object;
        }
        if ((catalog == null || catalog.equals("")) && 
                (schema == null || schema.equals(""))) {
            return object;
        }
        StringBuffer result = new StringBuffer();
        if (useCatalog && catalog != null && !catalog.equals("")) {
            result.append(catalog);
            result.append(getCatSep(session));
        }
        if (useSchema && schema != null && !schema.equals("")) {
            result.append(schema);
            result.append(".");
        }
        result.append(object);
        return result.toString();
    }    
    
    public static String getCatSep(ISession session) {
        String catsep = ".";
        try {
            SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
            catsep = md.getCatalogSeparator();
        } catch (SQLException e) {
            log.error("getCatSep: Unexpected Exception - "+e.getMessage(), e);
        }
        return catsep;
    }
    
    
    public static String fixCase(ISession session, String identifier)  
    {
        if (identifier == null || identifier.equals("")) {
            return identifier;
        }
        try {
            DatabaseMetaData md = 
            	session.getSQLConnection().getConnection().getMetaData();
        	
        	
        	
        	if (md.storesMixedCaseIdentifiers()) {
        		return identifier;
        	}
        	
            if (md.storesUpperCaseIdentifiers()) {
                return identifier.toUpperCase();
            } else {
                return identifier.toLowerCase();
            }
        } catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.debug("fixCase: unexpected exception: "+e.getMessage());
            }
            return identifier;
        }
    }    
            
    
    public static int getColumnCount(ISQLConnection sourceConn, ITableInfo ti) 
        throws SQLException 
    {
        return sourceConn.getSQLMetaData().getColumnInfo(ti).length;
    }
    
    
    public static int getColumnType(ISQLConnection con, ITableInfo ti, int column) 
        throws SQLException 
    {
        TableColumnInfo[] infoArr = con.getSQLMetaData().getColumnInfo(ti);
        TableColumnInfo colInfo = infoArr[column];
        return colInfo.getDataType();
    }
    
    public static int[] getColumnTypes(ISQLConnection con, ITableInfo ti) 
        throws SQLException 
    {
        TableColumnInfo[] infoArr = con.getSQLMetaData().getColumnInfo(ti);
        int[] result = new int[infoArr.length];
        for (int i = 0; i < result.length; i++) {
            TableColumnInfo colInfo = infoArr[i];
            result[i] = colInfo.getDataType();
        }
        return result;
    }
        
    public static boolean sameDatabaseType(ISession session1, 
                                           ISession session2) 
    {
        boolean result = false;
        String driver1ClassName = session1.getDriver().getDriverClassName();
        String driver2ClassName = session2.getDriver().getDriverClassName();
        if (driver1ClassName.equals(driver2ClassName)) {
            result = true; 
        }
        return result;
    }

    
    public static String getMaxColumnLengthSQL(ISession sourceSession, 
                                               TableColumnInfo colInfo,
                                               String tableName, 
                                               boolean tableNameIsQualified)
        throws UserCancelledOperationException
    {
        StringBuffer result = new StringBuffer();
        HibernateDialect dialect = 
            DialectFactory.getDialect(DialectFactory.SOURCE_TYPE, 
                                      sourceSession.getApplication().getMainFrame(), 
                                      sourceSession.getMetaData());
        String lengthFunction = dialect.getLengthFunction(colInfo.getDataType());
        if (lengthFunction == null) {
            log.error("Length function is null for dialect="+
                      dialect.getClass().getName()+". Using 'length'");
            lengthFunction = "length";
        }
        String maxFunction = dialect.getMaxFunction();
        if (maxFunction == null) {
            log.error("Max function is null for dialect="+
                      dialect.getClass().getName()+". Using 'max'");
            maxFunction = "max";
        }
        result.append("select ");
        result.append(maxFunction);
        result.append("(");
        result.append(lengthFunction);
        result.append("(");
        result.append(colInfo.getColumnName());
        result.append(")) from ");
        String table = tableName;
        if (!tableNameIsQualified) {
            table = getQualifiedObjectName(sourceSession, 
                                              colInfo.getCatalogName(), 
                                              colInfo.getSchemaName(),
                                              tableName, 
                                              DialectFactory.SOURCE_TYPE);
        }
        result.append(table);
        return result.toString();
    }

    
    public static void setLastStatement(String lastStatement) {
        DBUtil.lastStatement = lastStatement;
    }

    
    public static String getLastStatement() {
        return lastStatement;
    }
    
    public static void setLastStatementValues(String values) {
        lastStatementValues = values;
    }
    
    public static String getLastStatementValues() {
        return lastStatementValues;
    }
}
