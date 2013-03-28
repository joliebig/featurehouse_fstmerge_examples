
package test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;


public class DBMetaDataConcurrencyTester {

    private static String jdbcUrl = "jdbc:oracle:thin:@cumberland:1521:csuite";
    private static String user = "BELAIR40";
    private static String pass = "password";
    private static String tableName = "CS_ACL";
    
    private static Connection con = null;
    private static DatabaseMetaData md = null;
    
    private static int iterations = 100;
    private static int sleepTime = 10;
    private static int threads = 1;
    private static boolean getProcedures = false;
    private static boolean getProductName = false;
    private static boolean getProductVersion = true;
    private static boolean getJDBCVersion = false;
    private static boolean getTables = false;
    private static boolean getColumns = true;
    
    private static boolean printStackTraceOnError = false;
    
    private static void init() throws Exception {
        Class.forName("oracle.jdbc.OracleDriver");
        con = DriverManager.getConnection(jdbcUrl, user, pass);
        md = con.getMetaData();        
    }
    
    
    public static void main(String[] args) throws Exception {
        init();
        Thread[] getProceduresThreads = new Thread[threads];
        Thread[] getProductNameThreads = new Thread[threads];
        Thread[] getProductVersionThreads = new Thread[threads];
        Thread[] getJDBCVersionThreads = new Thread[threads];
        Thread[] getTablesThreads = new Thread[threads];
        Thread[] getColumnsThreads = new Thread[threads];
        
        
        for (int i = 0; i < threads; i++) {
            getProceduresThreads[i] = create(new GetProceduresRunnable(), i);
            getProductNameThreads[i] = create(new GetProductNameRunnable(), i);
            getProductVersionThreads[i] = create(new GetProductVersionRunnable(), i);
            getJDBCVersionThreads[i] = create(new GetJDBCVersionRunnable(), i);
            getTablesThreads[i] = create(new GetTablesRunnable(), i);
            getColumnsThreads[i] = create(new GetColumnsRunnable(), i);
        }
        
        
        for (int i = 0; i < threads; i++) {
            getProceduresThreads[i].start();
            getProductNameThreads[i].start();
            getProductVersionThreads[i].start();
            getJDBCVersionThreads[i].start();
            getTablesThreads[i].start();
            getColumnsThreads[i].start();
        }

        
        for (int i = 0; i < threads; i++) {
            getProceduresThreads[i].join();
            getProductNameThreads[i].join();
            getProductVersionThreads[i].join();
            getJDBCVersionThreads[i].join();
            getTablesThreads[i].join();
            getColumnsThreads[i].join();
        }
    }
    
    private static Thread create(Runnable runnable, int index) {
        Thread result = new Thread(runnable);
        result.setName(runnable.getClass().getName()+index);
        return result;
    }
    
    private static void handleException(Exception e) {
        System.err.println(e.getMessage());
        if (printStackTraceOnError) {
            e.printStackTrace();
        }
    }
    
    private static class GetProceduresRunnable implements Runnable {
                
        @SuppressWarnings("unused")
        public void run() {
            int count = 0;
            while (getProcedures && count++ < iterations) {
                ResultSet rs = null;
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    rs = md.getProcedures(null, user, null);
                    while (rs.next()) {
                        String catalog = rs.getString(1);
                        String schema = rs.getString(2);
                        String name = rs.getString(3);
                        String remarks = rs.getString(7);
                        String type = rs.getString(8);
                    }
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } finally {
                    SQLUtilities.closeResultSet(rs); 
                }
            }
        }
    }
    
    private static class GetProductNameRunnable implements Runnable {
                
        public void run() {
            int count = 0;
            while (getProductName && count++ < iterations) {
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    md.getDatabaseProductName();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } 
            }
        }
    }

    private static class GetProductVersionRunnable implements Runnable {
        
        public void run() {
            int count = 0;
            while (getProductVersion && count++ < iterations) {
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    md.getDatabaseProductVersion();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } 
            }
        }
    }    
    
    private static class GetJDBCVersionRunnable implements Runnable {
        
        public void run() {
            int count = 0;
            while (getJDBCVersion && count++ < iterations) {
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    md.getJDBCMajorVersion();
                    md.getJDBCMinorVersion();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } 
            }
        }
    }

    
    
    private static class GetTablesRunnable implements Runnable {
        @SuppressWarnings("unused")
        public void run() {
            int count = 0;
            while (getTables && count++ < iterations) {
                ResultSet rs = null;
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    rs = md.getTables(null, user, null, null);
                    ArrayList<String> list = new ArrayList<String>();
                    while (rs.next()) {
                        String catalog = rs.getString(1);
                        String schema = rs.getString(2);
                        String name = rs.getString(3);
                        String type = rs.getString(4);
                        String remarks = rs.getString(5);
                        
                        
                        
                        
                        
                        
                        list.add(name);
                    }
                    
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } finally {
                    SQLUtilities.closeResultSet(rs); 
                }
            }
        }
    }
    
    private static class GetColumnsRunnable implements Runnable {
        
        @SuppressWarnings("unused")
        public void run() {
            int count = 0;
            while (getColumns && count++ < iterations) {
                ResultSet rs = null;
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    rs = md.getColumns(null, user, tableName, null);
                    while (rs.next()) {
                        String catalog = rs.getString(1);
                        String schema = rs.getString(2);
                        String tableName = rs.getString(3);
                        String columnName = rs.getString(4);
                        int dataType = rs.getInt(5);
                        String typeName = rs.getString(6);
                        int columnSize = rs.getInt(7);
                        int decimalDigits = rs.getInt(9);
                        int numPrecRadiz = rs.getInt(10);
                        int nullable = rs.getInt(11);
                        String remarks = rs.getString(12);
                        String columnDef = rs.getString(13);
                        int sqlDataType = rs.getInt(14);
                        int sqlDateTimeSub = rs.getInt(15);
                        int charOctetLength = rs.getInt(16);
                        int ordPosition = rs.getInt(17);
                        String isNullable = rs.getString(18);
                        
                        
                        
                        
                        
                    }
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } finally {
                    SQLUtilities.closeResultSet(rs); 
                }
            }
        }
    }
    
}
