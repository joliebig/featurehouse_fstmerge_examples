package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.SQLException;

import com.mockobjects.sql.MockConnection2;

public class MockSQLDatabaseMetaData extends SQLDatabaseMetaData 
                                     implements ISQLDatabaseMetaData {

    static MockConnection2 conn = new MockConnection2();
    static ISQLConnection sqlConn = new SQLConnection(conn, null, null);
    
    
    public MockSQLDatabaseMetaData() {
        super(sqlConn);
    }


    
    @Override
    public synchronized ForeignKeyInfo[] getExportedKeysInfo(ITableInfo ti) throws SQLException {
        throw new SQLException("Simulated Unsupported API Method");
    }


    
    @Override
    public synchronized ForeignKeyInfo[] getImportedKeysInfo(ITableInfo ti) throws SQLException {
        throw new SQLException("Simulated Unsupported API Method");
    }
    
    
    
}
