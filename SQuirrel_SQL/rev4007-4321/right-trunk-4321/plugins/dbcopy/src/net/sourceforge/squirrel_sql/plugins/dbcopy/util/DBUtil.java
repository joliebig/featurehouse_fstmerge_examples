

package net.sourceforge.squirrel_sql.plugins.dbcopy.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.ColTypeMapper;
import net.sourceforge.squirrel_sql.plugins.dbcopy.I18NBaseObject;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.DBCopyPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;

import org.hibernate.MappingException;



public class DBUtil extends I18NBaseObject {

    
    private final static ILogger log = 
        LoggerController.createLogger(DBUtil.class);    
    
    
    private static DBCopyPreferenceBean _prefs = 
        PreferencesManager.getPreferences();
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DBUtil.class);    
    
    
    private static final String TEST_TABLE_NAME = "dbcopytest";
    
    
    private static String lastStatement = null;
    
    private static String lastStatementValues = null;
    
    public static void setPreferences(DBCopyPreferenceBean bean) {
        _prefs = bean;
    }
    
    
    public static String getPKColumnString(ISQLConnection sourceConn,
                                           ITableInfo ti) 
        throws SQLException 
    {
        List<String> pkColumns = getPKColumnList(sourceConn, ti);
        if (pkColumns == null || pkColumns.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder("(");
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
        
    
    
    public static Set<String> getForeignKeySQL(SessionInfoProvider prov,  
                                               ITableInfo ti,
                                               ArrayList<ITableInfo> selectedTableInfos) 
        throws SQLException , UserCancelledOperationException
    {
        HashSet<String> result = new HashSet<String>();
        ForeignKeyInfo[] keys = ti.getImportedKeys();
        if (keys == null) {
            return result;
        }
        for (ForeignKeyInfo fkInfo : keys) {
            String pkTableName = fkInfo.getPrimaryKeyTableName();
            String pkTableCol = fkInfo.getPrimaryKeyColumnName();
            String fkTableName = fkInfo.getForeignKeyTableName();
            String fkTableCol = fkInfo.getForeignKeyColumnName();
            
            
            String fkName = fkInfo.getForeignKeyName();
        

            
            
            
            if (!containsTable(selectedTableInfos, pkTableName)) {
                
                
                
                
                if (log.isDebugEnabled()) {
                    
                    
                    
                    
                    String msg = 
                        s_stringMgr.getString("DBUtil.error.missingtable",
                                              new String[] { fkTableName,
                                                             fkTableCol,
                                                             pkTableName,
                                                             pkTableCol });
                                           
                    log.debug(msg);
                }                    
                continue;
            }

            ISession destSession = prov.getCopyDestSession();
            String destSchema = prov.getDestSelectedDatabaseObject().getSimpleName();
            String destCatalog = prov.getDestSelectedDatabaseObject().getCatalogName();
            if (tableHasForeignKey(destCatalog, 
                                   destSchema, 
                                   ti.getSimpleName(),
                                   fkInfo,
                                   prov))
            {
                if (log.isInfoEnabled()) {
                    log.info(
                        "Skipping FK ("+fkName+") - table "+ti.getSimpleName()+
                        " seems to already have it defined.");
                }
                continue;
            }
            
            String fkTable = getQualifiedObjectName(destSession,  
                                                    destCatalog, 
                                                    destSchema, 
                                                    ti.getSimpleName(), 
                                                    DialectFactory.DEST_TYPE);
            String pkTable = getQualifiedObjectName(destSession, 
                                                    destCatalog, 
                                                    destSchema, 
                                                    pkTableName, 
                                                    DialectFactory.DEST_TYPE);
            StringBuilder tmp = new StringBuilder();
            tmp.append("ALTER TABLE ");
            tmp.append(fkTable);
            tmp.append(" ADD FOREIGN KEY (");
            tmp.append(fkTableCol);
            tmp.append(") REFERENCES ");
            tmp.append(pkTable);
            tmp.append("(");
            tmp.append(pkTableCol);
            tmp.append(")");
            result.add(tmp.toString());
        }
        return result;
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
                prov.getCopyDestSession().getSQLConnection().getSQLMetaData();
            
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
    
    private static boolean containsTable(List<ITableInfo> tableInfos, 
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
    
    
    public static int executeUpdate(ISQLConnection con, 
                                    String SQL,
                                    boolean writeSQL) throws SQLException {
        Statement stmt = null;
        int result = 0;
        try {
            stmt = con.createStatement();
            if (writeSQL) {
                ScriptWriter.write(SQL);
            }
            if (log.isDebugEnabled()) {
                
                String msg = 
                    s_stringMgr.getString("DBUtil.info.executeupdate", SQL);
                log.debug(msg);
            }
            lastStatement = SQL;
            result = stmt.executeUpdate(SQL);
        } finally {
            SQLUtilities.closeStatement(stmt);
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
            if (DialectFactory.isMySQL(session.getMetaData())) {
                stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                           ResultSet.CONCUR_READ_ONLY);
            
                stmt.setFetchSize(Integer.MIN_VALUE);
            } else if (DialectFactory.isTimesTen(session.getMetaData())) {
            	stmt = con.createStatement();
            	int fetchSize = _prefs.getSelectFetchSize();
            	
            	if (fetchSize > 128) {
            		log.info(
            			"executeQuery: TimesTen allows a maximum fetch size of " +
            			"128.  Altering preferred fetch size from "+fetchSize+
            			" to 128.");
            		fetchSize = 128;
            	}
            	stmt.setFetchSize(fetchSize);
            } else { 
                stmt = con.createStatement();
                
                
                
                if (_prefs.getSelectFetchSize() > 0) {
                    stmt.setFetchSize(_prefs.getSelectFetchSize());
                }
            }
        } catch(SQLException e) {
            
            
            SQLUtilities.closeResultSet(rs);
            SQLUtilities.closeStatement(stmt);
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
            SQLUtilities.closeResultSet(rs, true);
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
        
        
        
        if (con.getSQLMetaData().getDriverName().toLowerCase().startsWith("axion")) {
            schema = "";
        }
        String catalog = null;
        
        if (DialectFactory.isMySQL(session.getMetaData())) {
            catalog = schema;
            schema = null;
        }
        
        tableName = tableName.trim();
        ITableInfo[] tis = getTables(session, catalog, schema, tableName);
        if (tis == null || tis.length == 0) {
            if (Character.isUpperCase(tableName.charAt(0))) {
                tableName = tableName.toLowerCase();
            } else {
                tableName = tableName.toUpperCase();
            }
            tis = getTables(session, null, schema, tableName);
            if (tis.length == 0) {
                if (Character.isUpperCase(tableName.charAt(0))) {
                    tableName = tableName.toLowerCase();
                } else {
                    tableName = tableName.toUpperCase();
                }
                tis = getTables(session, null, schema, tableName);
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
    
    public static ITableInfo[] getTables(ISession session, String catalog,
            String schema, String tableName) {
        ITableInfo[] result = new ITableInfo[0];

        try {
            SchemaInfo schemaInfo = session.getSchemaInfo();
            result = schemaInfo.getITableInfos(catalog, schema, tableName);
        } catch (Exception e) {
            log.error("Encountered unexpected exception when attempting to "
                    + "call schemaInfo.getTables with catalog = " + catalog
                    + " schema = " + schema + " tableName = " + tableName);

        }

        if (result == null || result.length == 0) {
            
            
            
            SQLDatabaseMetaData d = session.getSQLConnection().getSQLMetaData();
            result = getTables(d, catalog, schema, tableName);
        }

        return result;
    }

    private static ITableInfo[] getTables(SQLDatabaseMetaData data,
            String catalog, String schema, String tableName) {

        ITableInfo[] result = new ITableInfo[0];

        try {
            result = data.getTables(catalog, schema, tableName, null, null);
        } catch (Exception e) {
            log.error("Encountered unexpected exception when attempting to "
                    + "call SQLDatabaseMetaData.getTables with catalog = "
                    + catalog + " schema = " + schema + " tableName = "
                    + tableName);

        }
        return result;
    }           
    
    
    public static boolean typesAreEquivalent(int sourceType, int destType) {
        boolean result = false;
        if (sourceType == destType) {
            result = true;
        }
        if (sourceType == Types.DECIMAL && destType == Types.NUMERIC) {
            result = true;
        }
        if (sourceType == Types.NUMERIC && destType == Types.DECIMAL) {
            result = true;
        }
        if (sourceType == Types.BOOLEAN && destType == Types.BIT) {
            result = true;
        }
        if (sourceType == Types.BIT && destType == Types.BOOLEAN) {
            result = true;
        }
        return result;
    }
    
    
    private static boolean handleNull(ResultSet rs, 
                                      PreparedStatement ps, 
                                      int index, 
                                      int type)
        throws SQLException 
    {
        boolean result = false;
        if (rs.wasNull()) {
            ps.setNull(index, type);
            result = true;
        }
        return result;
    }
    
    
	public static int replaceOtherDataType(TableColumnInfo colInfo, ISession session) throws MappingException
	{
		int colJdbcType = colInfo.getDataType();
		if (colJdbcType == java.sql.Types.OTHER)
		{
			try
			{
				HibernateDialect dialect = DialectFactory.getDialect(session.getMetaData());
				String typeName = colInfo.getTypeName().toUpperCase();
				int parenIndex = typeName.indexOf("(");
				if (parenIndex != -1)
				{
					typeName = typeName.substring(0, parenIndex);
				}				
				colJdbcType = dialect.getJavaTypeForNativeType(colInfo.getTypeName());
			} catch (Exception e)
			{
				log.error("replaceOtherDataType: unexpected exception - " + e.getMessage());
			}
		}
		return colJdbcType;
	}
    
    
    public static String bindVariable(PreparedStatement ps, 
                                      int sourceColType,
                                      int destColType,
                                      int index, 
                                      ResultSet rs) throws SQLException {
        String result = "null";
        switch (sourceColType) {
            case Types.ARRAY:
                Array arrayVal = rs.getArray(index);
                result = getValue(arrayVal);
                ps.setArray(index, arrayVal);
                break;
            case Types.BIGINT:
                long bigintVal = rs.getLong(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = Long.toString(bigintVal);
                    ps.setLong(index, bigintVal);                    
                }
                break;
            case Types.BINARY:
                result = bindBlobVar(ps, index, rs, destColType);
                break;
            case Types.BIT:
                
                
                
                
                
                
                
                
                
                
                
                
                
                boolean bitValue = rs.getBoolean(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = Boolean.toString(bitValue);
                    ps.setBoolean(index, bitValue);
                }
                break;
            case Types.BLOB:
                result = bindBlobVar(ps, index, rs, destColType);
                break;
            case Types.BOOLEAN:
                boolean booleanValue = rs.getBoolean(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = Boolean.toString(booleanValue);
                    
                    
                    
                    
                    
                    switch (destColType) {
                        case Types.TINYINT:
                        case Types.SMALLINT:
                        case Types.BIGINT:
                        case Types.INTEGER:
                            ps.setInt(index, booleanValue? 1 : 0 );
                            break;
                        case Types.FLOAT:
                            ps.setFloat(index, booleanValue? 1 : 0 );
                            break;
                        case Types.DOUBLE:
                            ps.setDouble(index, booleanValue? 1 : 0 );
                            break;
                        case Types.VARCHAR:
                        case Types.CHAR:
                            ps.setString(index, booleanValue? "1" : "0" );
                            break;
                        default:
                            ps.setBoolean(index, booleanValue);
                            break;
                    }
                }
                break;
            case Types.CHAR:
                String charValue = rs.getString(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = charValue;
                    ps.setString(index, charValue);
                }
                break;
            case Types.CLOB:
                bindClobVar(ps, index, rs, destColType);
                break;
            case Types.DATALINK:
                
                Object datalinkValue = rs.getObject(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = getValue(datalinkValue);
                    ps.setObject(index, datalinkValue);
                }
                break;
            case Types.DATE:
                Date dateValue = rs.getDate(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    
                    
                    result = getValue(dateValue);
                    ps.setDate(index, dateValue);
                }
                break;
            case Types.DECIMAL:
                BigDecimal decimalValue = rs.getBigDecimal(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = getValue(decimalValue);
                    ps.setBigDecimal(index, decimalValue);
                }
                break;
            case Types.DISTINCT:
                
                Object distinctValue = rs.getObject(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = getValue(distinctValue);
                    ps.setObject(index, distinctValue);
                }
                break;
            case Types.DOUBLE:
                double doubleValue = rs.getDouble(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = Double.toString(doubleValue);
                    ps.setDouble(index, doubleValue);
                }
                break;
            case Types.FLOAT:
                
                double floatValue = rs.getDouble(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = Double.toString(floatValue);
                    ps.setDouble(index, floatValue);
                }
                break;
            case Types.INTEGER:
                int integerValue = rs.getInt(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = Integer.toString(integerValue);
                    ps.setInt(index, integerValue);
                }
                break;
            case Types.JAVA_OBJECT:
                Object objectValue = rs.getObject(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = getValue(objectValue);
                    ps.setObject(index, objectValue);
                }
                break;
            case Types.LONGVARBINARY:
                result = bindBlobVar(ps, index, rs, destColType);
                break;
            case Types.LONGVARCHAR:
                String longvarcharValue = rs.getString(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = longvarcharValue;
                    ps.setString(index, longvarcharValue);
                }
                break;
            case Types.NULL:
                
                ps.setNull(index, Types.NULL);
                break;
            case Types.NUMERIC:
                BigDecimal numericValue = rs.getBigDecimal(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = getValue(numericValue);
                    ps.setBigDecimal(index, numericValue);
                }
                break;
            case Types.OTHER:
                
                
                String testValue = rs.getString(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    try {
                        Double.parseDouble(testValue);
                        double numberValue = rs.getDouble(index);
                        ps.setDouble(index, numberValue);                    
                    } catch (SQLException e) {
                        byte[] otherValue = rs.getBytes(index);
                        result = getValue(otherValue);
                        ps.setBytes(index, otherValue);    
                    }
                }
                break;
            case Types.REAL:
                float realValue = rs.getFloat(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = Float.toString(realValue);
                    ps.setFloat(index, realValue);
                }
                break;
            case Types.REF:
                Ref refValue = rs.getRef(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = getValue(refValue);
                    ps.setRef(index, refValue);
                }
                break;
            case Types.SMALLINT:
                short smallintValue = rs.getShort(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = Short.toString(smallintValue);
                    ps.setShort(index, smallintValue);
                }
                break;
            case Types.STRUCT:
                Object structValue = rs.getObject(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = getValue(structValue);
                    ps.setObject(index, structValue);
                }
                break;
            case Types.TIME:
                Time timeValue = rs.getTime(index);
                
                
                if (!handleNull(rs, ps, index, destColType)) {
                    result = getValue(timeValue);
                    ps.setTime(index, timeValue);
                }
                break;
            case Types.TIMESTAMP:
                Timestamp timestampValue = rs.getTimestamp(index);
                
                
                if (!handleNull(rs, ps, index, destColType)) {
                    result = getValue(timestampValue);
                    ps.setTimestamp(index, timestampValue);
                }
                break;
            case Types.TINYINT:
                byte tinyintValue = rs.getByte(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = Byte.toString(tinyintValue);
                    ps.setByte(index, tinyintValue);
                }
                break;
            case Types.VARBINARY:
                result = bindBlobVar(ps, index, rs, destColType);
                break;
            case Types.VARCHAR:
                String varcharValue = rs.getString(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = varcharValue;
                    ps.setString(index, varcharValue);
                }
                break;
            default:
                
                String msg =
                    s_stringMgr.getString("DBUtil.error.unknowntype",
                                          Integer.valueOf(sourceColType));
                log.error(msg);
                
                
                String value = rs.getString(index);
                if (!handleNull(rs, ps, index, destColType)) {
                    result = value;
                    ps.setString(index, value);
                }
                break;
        }
        return result;
    }

    private static String bindClobVar(PreparedStatement ps, 
                                      int index, 
                                      ResultSet rs,
                                      int type) throws SQLException 
    {
        String result = "null";
        if (_prefs.isUseFileCaching()) {
            try {            
                bindClobVarInFile(ps, index, rs, type);
            } catch (Exception e) {
                
                
                
                String msg = s_stringMgr.getString("DBUtil.error.bindclobfailure");
                log.error(msg, e);
                
                result = bindClobVarInMemory(ps, index, rs, type);
            } 
        } else {
            result = bindClobVarInMemory(ps, index, rs, type);
        }
        return result;
    }
    
    private static String bindBlobVar(PreparedStatement ps, 
                                      int index, 
                                      ResultSet rs,
                                      int type) throws SQLException {
        String result = "null";
        if (_prefs.isUseFileCaching()) {
            try {            
                bindBlobVarInFile(ps, index, rs, type);
            } catch (Exception e) {
                
                
                
                String msg = 
                    s_stringMgr.getString("DBUtil.error.bindblobfailure");
                log.error(msg, e);
                
                result = bindBlobVarInMemory(ps, index, rs, type);
            } 
        } else {
            result = bindBlobVarInMemory(ps, index, rs, type);
        }
        return result;
    }
    
    private static String bindClobVarInMemory(PreparedStatement ps, 
                                              int index, 
                                              ResultSet rs,
                                              int type) throws SQLException 
    {
        String clobValue = rs.getString(index);
        if (rs.wasNull()) {
            ps.setNull(index, type);
            return "null";
        }
        String result = getValue(clobValue);
        if (log.isDebugEnabled() && clobValue != null) {
            
            String msg = s_stringMgr.getString("DBUtil.info.bindclobmem",
                                               Integer.valueOf(clobValue.length()));
            log.debug(msg);
        }
        ps.setString(index, clobValue);
        return result;
    }

    
    private static String bindBlobVarInMemory(PreparedStatement ps, 
                                              int index, 
                                              ResultSet rs,
                                              int type) throws SQLException {
        byte[] blobValue = rs.getBytes(index);
        if (rs.wasNull()) {
            ps.setNull(index, type);
            return "null";
        }
        String result = getValue(blobValue);
        if (log.isDebugEnabled() && blobValue != null) {
            
            String msg = 
                s_stringMgr.getString("DBUtil.info.bindblobmem",
                                      Integer.valueOf(blobValue.length));
            log.debug(msg);
        }
        ps.setBytes(index, blobValue);
        return result;
    }
    
    private static void bindClobVarInFile(PreparedStatement ps, 
                                          int index, 
                                          ResultSet rs,
                                          int type)
    throws IOException, SQLException 
    {
        
        InputStream is = rs.getAsciiStream(index);
        if (rs.wasNull()) {
            ps.setNull(index, type);
            return;
        }
        
        
        long millis = System.currentTimeMillis();
        File f = File.createTempFile("clob", ""+millis);
        f.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(f);
        if (log.isDebugEnabled()) {
            
            String msg = s_stringMgr.getString("DBUtil.info.bindclobfile",
                                               f.getAbsolutePath());
            log.debug(msg);
        }
        
        
        byte[] buf = new byte[_prefs.getFileCacheBufferSize()];
        int length = 0;
        int total = 0;
        while ((length = is.read(buf)) >= 0) {
            if (log.isDebugEnabled()) {
                
                String msg =
                    s_stringMgr.getString("DBUtil.info.bindcloblength",
                                          Integer.valueOf(length));
                log.debug(msg);
            }
            fos.write(buf, 0, length);
            total += length;
        }
        fos.close();
        
        
        FileInputStream fis = new FileInputStream(f);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ps.setAsciiStream(index, bis, total);
    }
    
    private static void bindBlobVarInFile(PreparedStatement ps, 
                                          int index, 
                                          ResultSet rs,
                                          int type) 
        throws IOException, SQLException 
    {
        
        InputStream is = rs.getBinaryStream(index);
        if (rs.wasNull()) {
            ps.setNull(index, type);
            return;
        }
        
        long millis = System.currentTimeMillis();
        File f = File.createTempFile("blob", ""+millis);
        f.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(f);
        if (log.isDebugEnabled()) {
            
            String msg = s_stringMgr.getString("DBUtil.info.bindblobfile",
                                               f.getAbsolutePath());
            log.debug(msg);
        }
        
        
        
        byte[] buf = new byte[_prefs.getFileCacheBufferSize()];
        int length = 0;
        int total = 0;
        while ((length = is.read(buf)) >= 0) {
            if (log.isDebugEnabled()) {
                
                String msg =
                    s_stringMgr.getString("DBUtil.info.bindbloblength",
                                          Integer.valueOf(length));
                log.debug(msg);
            }
            fos.write(buf, 0, length);
            total += length;
        }
        fos.close();
        
        
        FileInputStream fis = new FileInputStream(f);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ps.setBinaryStream(index, bis, total);
    }
    
    
    private static String getValue(Object o) {
        if (o != null) {
            return o.toString();
        }
        return "null";
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
            SQLUtilities.closeResultSet(rs);
        }
        return result;
    }

    
    public static boolean isKeyword(ISession session, String data) {
        return session.getSchemaInfo().isKeyword(data);
    }
    
    
    public static void deleteDataInExistingTable(ISession session,
                                                 String catalogName,
                                                 String schemaName, 
                                                 String tableName) 
        throws SQLException, UserCancelledOperationException
    {
        ISQLConnection con = session.getSQLConnection();
        boolean useTrunc = PreferencesManager.getPreferences().isUseTruncate();
        String fullTableName = 
            getQualifiedObjectName(session, 
                                   catalogName, 
                                   schemaName, 
                                   tableName, 
                                   DialectFactory.DEST_TYPE);
        String truncSQL = "TRUNCATE TABLE "+fullTableName;
        String deleteSQL = "DELETE FROM "+fullTableName;
        try {
            if (useTrunc) {
                DBUtil.executeUpdate(con, truncSQL, true);
            } else {
                DBUtil.executeUpdate(con, deleteSQL, true);
            }
        } catch (SQLException e) {
            
            
            
            if (useTrunc) {
                DBUtil.executeUpdate(con, deleteSQL, true);
            } else {
                throw e;
            }
        }
    }    
    
    
    public static void sanityCheckPreferences(ISession destSession) 
        throws MappingException
    {
       
        if (DialectFactory.isFirebird(destSession.getMetaData())) {
            if (!PreferencesManager.getPreferences().isCommitAfterTableDefs()) {
                
                
                
                
                
                
                
                String msg = 
                    s_stringMgr.getString("DBUtil.error.firebirdcommit");
                throw new MappingException(msg);
            }
        }
    }
    
    public static String getCreateTableSql(SessionInfoProvider prov, 
                                           ITableInfo ti) 
        throws SQLException, MappingException, UserCancelledOperationException
    {

        ISession sourceSession = prov.getCopySourceSession();
        String sourceSchema = 
            prov.getSourceSelectedDatabaseObjects()[0].getSchemaName();
        String sourceCatalog = 
            prov.getSourceSelectedDatabaseObjects()[0].getCatalogName();
        String sourceTableName = getQualifiedObjectName(sourceSession, 
                                                        sourceCatalog, 
                                                        sourceSchema,
                                                        ti.getSimpleName(), 
                                                        DialectFactory.SOURCE_TYPE);
        ISession destSession = prov.getCopyDestSession();
        String destSchema = prov.getDestSelectedDatabaseObject().getSimpleName();
        String destCatalog = prov.getDestSelectedDatabaseObject().getCatalogName();
        String destinationTableName = getQualifiedObjectName(destSession, 
                                                             destCatalog, 
                                                             destSchema,
                                                             ti.getSimpleName(), 
                                                             DialectFactory.DEST_TYPE); 
        StringBuilder result = new StringBuilder("CREATE TABLE ");
        result.append(destinationTableName);
        result.append(" ( ");
        result.append("\n");
        TableColumnInfo colInfo = null;
        try {
            ISQLConnection sourceCon = prov.getCopySourceSession().getSQLConnection();
            TableColumnInfo[] colInfoArr = sourceCon.getSQLMetaData().getColumnInfo(ti);
            if (colInfoArr.length == 0) {
                
                
                String msg = 
                    s_stringMgr.getString("DBUtil.error.nocolumns",
                                          new String[] { ti.getSimpleName(),
                                                         ti.getSchemaName() });
                throw new MappingException(msg); 
            }
            for (int i = 0; i < colInfoArr.length; i++) {
                colInfo = colInfoArr[i];
                result.append("\t");
                String columnSql =
                    DBUtil.getColumnSql(prov, colInfo, 
                                        sourceTableName, destinationTableName);
                result.append(columnSql);
                if (i < colInfoArr.length-1) {
                    result.append(",\n");
                }
            }
            
            
            
            
            
            
            
            if (_prefs.isCopyPrimaryKeys()
            		&& !DialectFactory.isAxion(sourceSession.getMetaData())) {
                String pkString = DBUtil.getPKColumnString(sourceCon, ti);
                if (pkString != null) {
                    result.append(",\n\tPRIMARY KEY ");
                    result.append(pkString);
                }
            }
            result.append(")");
        } catch (MappingException e) {
            if (colInfo != null) {
                
                
                String msg = 
                    s_stringMgr.getString("DBUtil.error.maptype",
                                          new String[] { destinationTableName,
                                                         colInfo.getColumnName()});
                log.error(msg, e);
            }
            throw e;
        }
        
        return result.toString();
    }
    
    
    public static String getColumnList(TableColumnInfo[] colInfoArr) 
        throws SQLException 
    {
        StringBuilder result = new StringBuilder();
        
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
    
    
    public static String getColumnSql(SessionInfoProvider prov, 
                                      TableColumnInfo colInfo, 
                                      String sourceTableName,
                                      String destTableName) 
        throws UserCancelledOperationException, MappingException 
    {
        String columnName = colInfo.getColumnName();
        if (_prefs.isCheckKeywords()) {
            checkKeyword(prov.getCopyDestSession(), destTableName, columnName);
        }
        StringBuilder result = new StringBuilder(columnName);
        boolean notNullable = colInfo.isNullable().equalsIgnoreCase("NO");
        String typeName = ColTypeMapper.mapColType(prov.getCopySourceSession(), 
                                                   prov.getCopyDestSession(), 
                                                   colInfo,
                                                   sourceTableName,
                                                   destTableName);
        result.append(" ");
        result.append(typeName);
        if (notNullable) {
            result.append(" NOT NULL");
        } else {
            ISession destSession = prov.getCopyDestSession();
            HibernateDialect d = 
                DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                          destSession.getApplication().getMainFrame(), 
                                          destSession.getMetaData());
            String nullString = d.getNullColumnString().toUpperCase();
            result.append(nullString);
        }
        return result.toString();
    }
    
    
    public static void checkKeyword(ISession session, String table, String column) 
        throws MappingException 
    {
        if (isKeyword(session, column)) {
            String message = getMessage("DBUtil.mappingErrorKeyword",
                                        new String[] { table, column });
            throw new MappingException(message);
        }                   
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
        StringBuilder result = new StringBuilder("select ");
        result.append(columnList);
        result.append(" from ");
        ISession sourceSession = prov.getCopySourceSession();
        
        String tableName = getQualifiedObjectName(sourceSession, 
                                                  ti.getCatalogName(), 
                                                  ti.getSchemaName(), 
                                                  ti.getSimpleName(), 
                                                  DialectFactory.SOURCE_TYPE);
        result.append(tableName);
        return result.toString();
    }
    
    
    
    public static String getInsertSQL(SessionInfoProvider prov, 
                                      String columnList, 
                                      ITableInfo ti,
                                      int columnCount) 
        throws SQLException, UserCancelledOperationException
    {
        StringBuilder result = new StringBuilder();
        result.append("insert into ");
        String destSchema = prov.getDestSelectedDatabaseObject().getSimpleName();
        String destCatalog = prov.getDestSelectedDatabaseObject().getCatalogName();
        ISession destSession = prov.getCopyDestSession();
        result.append(getQualifiedObjectName(destSession, 
                                             destCatalog, 
                                             destSchema,
                                             ti.getSimpleName(), 
                                             DialectFactory.DEST_TYPE));
        result.append(" ( ");
        result.append(columnList);
        result.append(" ) values ( ");
        result.append(getQuestionMarks(columnCount));
        result.append(" )");
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
    {
        String catalog = catalogName;
        String schema = schemaName;
        String object = objectName;
        
        
        
        
        
        if (sessionType == DialectFactory.DEST_TYPE) { 
            catalog = fixCase(session, catalogName);
            schema = fixCase(session, schemaName);
            object = fixCase(session, objectName);
        }
        ISQLDatabaseMetaData md = session.getMetaData();
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
        StringBuilder result = new StringBuilder();
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
            ISQLDatabaseMetaData md = session.getMetaData();
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
        
    
    private static String getQuestionMarks(int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append("?");
            if (i < count-1) {
                result.append(", ");
            }
        }
        return result.toString();
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
            
    public static void validateColumnNames(ITableInfo ti, 
                                           SessionInfoProvider prov) 
        throws MappingException, UserCancelledOperationException
    {
        if (prov == null) {
            return;
        }
        ISession sourceSession = prov.getCopySourceSession();
        ISession destSession = prov.getCopyDestSession();
        if (sourceSession == null || destSession == null) {
            return;
        }
        ISQLConnection sourceCon = sourceSession.getSQLConnection();
        ISQLConnection con = destSession.getSQLConnection();        
        TableColumnInfo[] colInfoArr = null;
        try {
            colInfoArr = sourceCon.getSQLMetaData().getColumnInfo(ti);
        } catch (SQLException e) {
            
            
            return;
        }
        for (int colIdx = 0; colIdx < colInfoArr.length; colIdx++) {
            TableColumnInfo colInfo = colInfoArr[colIdx];
            IDatabaseObjectInfo selectedDestObj = 
                prov.getDestSelectedDatabaseObject();
            String schema = selectedDestObj.getSimpleName();
            String catalog = selectedDestObj.getCatalogName(); 
            String tableName = getQualifiedObjectName(destSession, 
                                                      catalog, 
                                                      schema,
                                                      TEST_TABLE_NAME, 
                                                      DialectFactory.DEST_TYPE); 
            
            StringBuilder sql = 
                new StringBuilder("CREATE TABLE ");
            sql.append(tableName);
            sql.append(" ( ");
            sql.append(colInfo.getColumnName());
            sql.append(" CHAR(10) )");
            boolean cascade = 
                DialectFactory.isFrontBase(destSession.getMetaData());
            try {
                dropTable(TEST_TABLE_NAME, 
                          schema, 
                          catalog, 
                          destSession, 
                          cascade, 
                          DialectFactory.DEST_TYPE);
                DBUtil.executeUpdate(con, sql.toString(), false);
            } catch (SQLException e) {
                String message = getMessage("DBUtil.mappingErrorKeyword",
                                            new String[] { ti.getSimpleName(), 
                                                           colInfo.getColumnName() });
                log.error(message, e);
                throw new MappingException(message);
            } finally {
                dropTable(tableName, 
                          schema, 
                          catalog, 
                          destSession, 
                          cascade, DialectFactory.DEST_TYPE);
            }
            
        }        
    }
    
    public static boolean dropTable(String tableName,
                                    String schemaName,
                                    String catalogName,
                                    ISession session,
                                    boolean cascade, int sessionType) 
        throws UserCancelledOperationException
    {
        boolean result = false;
        ISQLConnection con = session.getSQLConnection();
        String table = getQualifiedObjectName(session, 
                                             catalogName, 
                                             schemaName,
                                             tableName, sessionType);
        String dropsql = "DROP TABLE "+table;
        if (cascade) {
            dropsql += " CASCADE";
        }
        try {
            DBUtil.executeUpdate(con, dropsql, false);
            result = true;
        } catch (SQLException e) {
            
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
        StringBuilder result = new StringBuilder();
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
