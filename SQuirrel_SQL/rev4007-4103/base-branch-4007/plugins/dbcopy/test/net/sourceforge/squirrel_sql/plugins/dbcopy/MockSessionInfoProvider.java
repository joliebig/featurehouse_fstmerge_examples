
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.MockSession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.MockDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.DBUtil;

public class MockSessionInfoProvider implements SessionInfoProvider {

    ISession sourceSession = null;
    
    ISession destSession = null;
    
    ArrayList<ITableInfo> selectedDatabaseObjects = new ArrayList<ITableInfo>();
    
    IDatabaseObjectInfo destSelectedDatabaseObject = null;
    
    ResourceBundle bundle = null;
    
    String sourceSchema = null;
    String sourceCatalog = null;
    String destSchema=null;
    String destCatalog = null;
    
    boolean dropOnly = false;
    
    public MockSessionInfoProvider(String propertyFile, 
    							   boolean dropOnly) 
    	throws Exception 
    {
        this.dropOnly = dropOnly;
        initialize(propertyFile);
    }
    
    private void initialize(String propertyFile) throws Exception {
        bundle = ResourceBundle.getBundle(propertyFile);
        String sourceDriver = bundle.getString("sourceDriver");
        String sourceJdbcUrl = bundle.getString("sourceJdbcUrl"); 
        String sourceUser = bundle.getString("sourceUser");
        String sourcePass = bundle.getString("sourcePass");
        
        sourceSession = new MockSession(sourceDriver,
                                        sourceJdbcUrl,
                                        sourceUser,
                                        sourcePass);
        sourceSchema = fixCase(bundle.getString("sourceSchema"),
                               sourceSession);
        sourceCatalog = fixCase(bundle.getString("sourceCatalog"),
                				sourceSession);
        String destDriver = bundle.getString("destDriver");
        String destJdbcUrl = bundle.getString("destJdbcUrl"); 
        String destUser = bundle.getString("destUser");
        String destPass = bundle.getString("destPass");
        destSession = new MockSession(destDriver,
                                      destJdbcUrl,
                                      destUser,
                                      destPass);
        destCatalog = fixCase(bundle.getString("destCatalog"),
				  		      destSession);
        destSchema = fixCase(bundle.getString("destSchema"),
				  			 destSession);
        initializeDBObjs();
    }
    
    private void initializeDBObjs() 
        throws SQLException, UserCancelledOperationException 
    {
        List<ITableInfo> tables = getTableNames(sourceSession);
        String destSchema = fixCase(bundle.getString("destSchema"), 
                                    destSession);
        if (tables.size() == 0) {
        	throw new SQLException("No tables found to copy");
        }
        
        for (ITableInfo info : tables) {
        	String sourceTable = fixCase(info.getSimpleName(), sourceSession);
        	if (!shouldIncludeTable(sourceTable)) {
        		continue;
        	}
            dropDestinationTable(sourceTable, destSchema);
            if (!dropOnly) {
            	selectedDatabaseObjects.add(info);
            }
		}
        
        destSelectedDatabaseObject = new MockDatabaseObjectInfo(destSchema, destSchema, destCatalog);
        System.out.println("destSelectedDatabaseObject: "+destSelectedDatabaseObject);
    }
    
    private boolean shouldIncludeTable(String tableName) {
    	boolean result = true;
        
        if (tableName.startsWith("IIE") 
                || tableName.startsWith("iie")) 
        {
            result = false;
        }
        
        if (tableName.startsWith("AXION") 
                || tableName.startsWith("axion")) 
        {
        	result = false;
        }
        
        if (tableName.startsWith("RDB$")) {
        	result = false;
        }
        
        if (tableName.startsWith("sys")) {
        	
        }

    	return result;
    }
    
    private void dropDestinationTable(String tableName, String schema) 
        throws SQLException, UserCancelledOperationException 
    {
        String destTable = fixCase(tableName, destSession);
        if (DialectFactory.isFrontBase(destSession.getMetaData())) {
            DBUtil.dropTable(destTable, schema, null, destSession, true, DialectFactory.DEST_TYPE);
        } else {
            DBUtil.dropTable(destTable, schema, null, destSession, false, DialectFactory.DEST_TYPE);
        }
    }
    
    private List<ITableInfo> getTableNames(ISession sourceSession) throws SQLException {
        List<ITableInfo> result = null;
        String tableStr = bundle.getString("tablesToCopy");
        if ("*".equals(tableStr)) {
            result = getAllTables(sourceSession);
        } else {
        	result = new ArrayList<ITableInfo>();
            String[] tableNames = tableStr.split(",");
            for (int i = 0; i < tableNames.length; i++) {
				String tableName = tableNames[i];
				TableInfo info = new TableInfo(sourceCatalog, 
											   sourceSchema,
											   tableName,
											   "TABLE",
											   "",
											   null);
											   
				result.add(info);
			}
        }
        return result;
    }
    
    private List<ITableInfo> getAllTables(ISession sourceSession) throws SQLException {
        ISQLConnection sourceConn = sourceSession.getSQLConnection();
        SQLDatabaseMetaData data = sourceConn.getSQLMetaData(); 
        ITableInfo[] tableInfos = data.getTables(sourceCatalog, sourceSchema, "%", new String[] {"TABLE"}, null);
        
        ArrayList<ITableInfo> tables = new ArrayList<ITableInfo>();
        for (int i = 0; i < tableInfos.length; i++) {
            String tiSchema = tableInfos[i].getSchemaName();
            if (sourceSchema.equals(tiSchema)
                    || ("".equals(sourceSchema) && tiSchema == null) ) 
            {
                if (tableInfos[i].getDatabaseObjectType() == DatabaseObjectType.TABLE) {
                    System.out.println("Adding table "+tableInfos[i].getSimpleName());
                    tables.add(tableInfos[i]);
                }
            }
        }
        return tables;
    }
    
    private String fixCase(String identifier, ISession session) 
        throws SQLException 
    {
        ISQLConnection con = session.getSQLConnection();
        String result = identifier;
        if (con.getSQLMetaData().getJDBCMetaData().storesUpperCaseIdentifiers()
                && !DialectFactory.isFrontBase(session.getMetaData())) {
            result = identifier.toUpperCase();
        }
        return result;
    }
    
    public void setCopySourceSession(ISession session) {
        sourceSession = session;
    }

    public ISession getCopySourceSession() {
        return sourceSession;
    }

    public IDatabaseObjectInfo[] getSourceSelectedDatabaseObjects() {
        int size = selectedDatabaseObjects.size();
        IDatabaseObjectInfo[] result = new IDatabaseObjectInfo[size];
        Iterator<?> i = selectedDatabaseObjects.iterator();
        int index = 0;
        while (i.hasNext()) { 
            result[index++] = (IDatabaseObjectInfo)i.next();
        }
        return result;
    }

    public void setDestCopySession(ISession session) {
        destSession = session;
    }

    public ISession getCopyDestSession() {
        return destSession;
    }

    
    public IDatabaseObjectInfo getDestSelectedDatabaseObject() {
        return destSelectedDatabaseObject;
    }

    
    public void setDestSelectedDatabaseObject(IDatabaseObjectInfo info) {
        destSelectedDatabaseObject = info;
    }
    
    

}
