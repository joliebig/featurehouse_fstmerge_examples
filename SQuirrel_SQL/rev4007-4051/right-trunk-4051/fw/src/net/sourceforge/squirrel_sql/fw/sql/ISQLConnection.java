package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Date;

public interface ISQLConnection {

    public interface IPropertyNames
    {
        String AUTO_COMMIT = "autocommit";
        String CATALOG = "catalog";
    }    
    
    void close() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    
    SQLDriverPropertyCollection getConnectionProperties();

    boolean getAutoCommit() throws SQLException;

    void setAutoCommit(boolean value) throws SQLException;

    boolean getCommitOnClose();

    int getTransactionIsolation() throws SQLException;

    void setTransactionIsolation(int value) throws SQLException;

    void setCommitOnClose(boolean value);

    Statement createStatement() throws SQLException;

    PreparedStatement prepareStatement(String sql) throws SQLException;

    
    Date getTimeOpened();

    
    Date getTimeClosed();

    
    SQLDatabaseMetaData getSQLMetaData();

    Connection getConnection();

    String getCatalog() throws SQLException;

    void setCatalog(String catalogName) throws SQLException;

    SQLWarning getWarnings() throws SQLException;

    
    void addPropertyChangeListener(PropertyChangeListener listener);

    
    void removePropertyChangeListener(PropertyChangeListener listener);

    ISQLDriver getSQLDriver();

}