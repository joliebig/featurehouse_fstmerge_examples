

package org.jfree.data.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;


public class JDBCCategoryDataset extends DefaultCategoryDataset {

    
    private transient Connection connection;

    
    private boolean transpose = true;


    
    public JDBCCategoryDataset(String url,
                               String driverName,
                               String user,
                               String passwd)
        throws ClassNotFoundException, SQLException {

        Class.forName(driverName);
        this.connection = DriverManager.getConnection(url, user, passwd);
    }

    
    public JDBCCategoryDataset(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("A connection must be supplied.");
        }
        this.connection = connection;
    }

    
    public JDBCCategoryDataset(Connection connection, String query) 
        throws SQLException {
        this(connection);
        executeQuery(query);
    }

    
    public boolean getTranspose() {
        return this.transpose;
    }

    
    public void setTranspose(boolean transpose) {
        this.transpose = transpose;
    }

    
    public void executeQuery(String query) throws SQLException {
        executeQuery(this.connection, query);
    }

    
    public void executeQuery(Connection con, String query) throws SQLException {

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = con.createStatement();
            resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();

            if (columnCount < 2) {
                throw new SQLException(
                    "JDBCCategoryDataset.executeQuery() : insufficient columns "
                    + "returned from the database.");
            }

            
            int i = getRowCount();
            while (--i >= 0) {
                removeRow(i);
            }

            while (resultSet.next()) {
                
                Comparable rowKey = resultSet.getString(1);
                for (int column = 2; column <= columnCount; column++) {

                    Comparable columnKey = metaData.getColumnName(column);
                    int columnType = metaData.getColumnType(column);

                    switch (columnType) {
                        case Types.TINYINT:
                        case Types.SMALLINT:
                        case Types.INTEGER:
                        case Types.BIGINT:
                        case Types.FLOAT:
                        case Types.DOUBLE:
                        case Types.DECIMAL:
                        case Types.NUMERIC:
                        case Types.REAL: {
                            Number value = (Number) resultSet.getObject(column);
                            if (this.transpose) {
                                setValue(value, columnKey, rowKey);
                            }
                            else {
                                setValue(value, rowKey, columnKey);
                            }
                            break;
                        }
                        case Types.DATE:
                        case Types.TIME:
                        case Types.TIMESTAMP: {
                            Date date = (Date) resultSet.getObject(column);
                            Number value = new Long(date.getTime());
                            if (this.transpose) {
                                setValue(value, columnKey, rowKey);
                            }
                            else {
                                setValue(value, rowKey, columnKey);
                            }
                            break;
                        }
                        case Types.CHAR:
                        case Types.VARCHAR:
                        case Types.LONGVARCHAR: {
                            String string 
                                = (String) resultSet.getObject(column);
                            try {
                                Number value = Double.valueOf(string);
                                if (this.transpose) {
                                    setValue(value, columnKey, rowKey);
                                }
                                else {
                                    setValue(value, rowKey, columnKey);
                                }
                            }
                            catch (NumberFormatException e) {
                                
                            }
                            break;
                        }
                        default:
                            
                            break;
                    }
                }
            }

            fireDatasetChanged();
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (Exception e) {
                    
                }
            }
        }
    }

}
