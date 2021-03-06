
package net.sourceforge.squirrel_sql.fw.dialects;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class DialectFactory {

    
    public static final int SOURCE_TYPE = 0;
    
    
    public static final int DEST_TYPE = 1;
    
    
    private final static ILogger s_log = 
        LoggerController.createLogger(DialectFactory.class);  
    
    private static final AxionDialect axionDialect = new AxionDialect();
    
    private static final DB2Dialect db2Dialect = new DB2Dialect();
    
    
    
    
    
    
    
    private static final DaffodilDialect daffodilDialect = new DaffodilDialect();
    
    private static final DerbyDialect derbyDialect = new DerbyDialect();
    
    private static final FirebirdDialect firebirdDialect = new FirebirdDialect();
    
    private static final FrontBaseDialect frontbaseDialect = new FrontBaseDialect();
    
    private static final HADBDialect hadbDialect = new HADBDialect();
    
    private static final H2Dialect h2Dialect = new H2Dialect();
    
    private static final HSQLDialect hsqlDialect = new HSQLDialect();
    
    private static final InformixDialect informixDialect = new InformixDialect();
    
    private static final InterbaseDialect interbaseDialect = new InterbaseDialect();
    
    private static final IngresDialect ingresDialect = new IngresDialect();
    
    private static final MAXDBDialect maxDbDialect = new MAXDBDialect();
    
    private static final McKoiDialect mckoiDialect = new McKoiDialect();
    
    private static final MySQLDialect mysqlDialect = new MySQLDialect();
    
    private static final MySQL5Dialect mysql5Dialect = new MySQL5Dialect();    
    
    private static final Oracle9iDialect oracle9iDialect = new Oracle9iDialect();
    
    private static final PointbaseDialect pointbaseDialect = 
                                                         new PointbaseDialect();
    
    private static final PostgreSQLDialect postgreSQLDialect = 
                                                        new PostgreSQLDialect();

    private static final ProgressDialect progressDialect = new ProgressDialect();
    
    private static final SybaseDialect sybaseDialect = new SybaseDialect();
    
    private static final SQLServerDialect sqlserverDialect = new SQLServerDialect();
    
    private static final TimesTenDialect timestenDialect = new TimesTenDialect();
    
    private static HashMap<String, HibernateDialect> dbNameDialectMap = 
        new HashMap<String, HibernateDialect>();
    
    public static boolean isPromptForDialect = false; 
    
    
    private static final StringManager s_stringMgr =
                  StringManagerFactory.getStringManager(DialectFactory.class);
    
    
    static {
        dbNameDialectMap.put(axionDialect.getDisplayName(), axionDialect);
        dbNameDialectMap.put(db2Dialect.getDisplayName(), db2Dialect);
        
        
        dbNameDialectMap.put(daffodilDialect.getDisplayName(), daffodilDialect);
        dbNameDialectMap.put(derbyDialect.getDisplayName(), derbyDialect);
        dbNameDialectMap.put(firebirdDialect.getDisplayName(), firebirdDialect);
        dbNameDialectMap.put(frontbaseDialect.getDisplayName(), frontbaseDialect);
        dbNameDialectMap.put(hadbDialect.getDisplayName(), hadbDialect);
        dbNameDialectMap.put(hsqlDialect.getDisplayName(), hsqlDialect);
        dbNameDialectMap.put(h2Dialect.getDisplayName(), h2Dialect);
        dbNameDialectMap.put(informixDialect.getDisplayName(), informixDialect);
        dbNameDialectMap.put(ingresDialect.getDisplayName(), ingresDialect);
        dbNameDialectMap.put(interbaseDialect.getDisplayName(), interbaseDialect);
        dbNameDialectMap.put(maxDbDialect.getDisplayName(), maxDbDialect);
        dbNameDialectMap.put(mckoiDialect.getDisplayName(), mckoiDialect);
        dbNameDialectMap.put(sqlserverDialect.getDisplayName(), sqlserverDialect);
        dbNameDialectMap.put(mysqlDialect.getDisplayName(), mysqlDialect);
        dbNameDialectMap.put(oracle9iDialect.getDisplayName(), oracle9iDialect);
        dbNameDialectMap.put(pointbaseDialect.getDisplayName(), pointbaseDialect);
        dbNameDialectMap.put(postgreSQLDialect.getDisplayName(), postgreSQLDialect);
        dbNameDialectMap.put(progressDialect.getDisplayName(), progressDialect);
        dbNameDialectMap.put(sybaseDialect.getDisplayName(), sybaseDialect);
        dbNameDialectMap.put(timestenDialect.getDisplayName(), timestenDialect);
    }
    
    public static boolean isAxion(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, axionDialect);
    }
    
    public static boolean isDaffodil(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, daffodilDialect);
    }
    
    public static boolean isDB2(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, db2Dialect);
    }

    public static boolean isDerby(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, derbyDialect);
    }    
    
    public static boolean isFirebird(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, firebirdDialect);
    }
    
    public static boolean isFrontBase(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, frontbaseDialect);
    }

    public static boolean isHADB(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, hadbDialect);
    }    
    
    public static boolean isH2(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, h2Dialect);
    }
    
    public static boolean isHSQL(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, hsqlDialect);
    }    

    public static boolean isInformix(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, informixDialect);
    }    
    
    public static boolean isIngres(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, ingresDialect);
    }

    public static boolean isInterbase(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, interbaseDialect);
    }
    
    public static boolean isMaxDB(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, maxDbDialect);
    }
    
    public static boolean isMcKoi(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, mckoiDialect);        
    }

    public static boolean isMSSQLServer(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, sqlserverDialect);
    }            
    
    public static boolean isMySQL(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, mysqlDialect);
    }        

    public static boolean isMySQL5(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, mysql5Dialect);
    }        
    
    public static boolean isOracle(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, oracle9iDialect);        
    }
    
    public static boolean isPointbase(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, pointbaseDialect);        
    }

    public static boolean isPostgreSQL(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, postgreSQLDialect);        
    }    
    
    public static boolean isProgress(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, progressDialect);        
    }
    
    public static boolean isSyBase(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, sybaseDialect);        
    }
    
    public static boolean isTimesTen(ISQLDatabaseMetaData md) {
        return dialectSupportsProduct(md, timestenDialect);            	
    }
    
    
    public static DialectType getDialectType(ISQLDatabaseMetaData md) {
       HibernateDialect dialect = null;
       try {
          dialect = getDialect(md);
       } catch (UnknownDialectException e) {
          try {
             s_log.error("getDialectType: Unknown dialect for product="
                  + md.getDatabaseProductName() + " version="
                  + md.getDatabaseProductVersion()+" - "+e.getMessage(), e);
          } catch (SQLException ex) {
             s_log.error("getDialectType: unable to get database "
                  + "product/version: " + ex.getMessage(), ex);
          }
       }
       if (dialect == null) {
          return DialectType.GENERIC;
       }
       return dialect.getDialectType();
    }
    
    
    private static boolean dialectSupportsProduct(ISQLDatabaseMetaData data, 
    											  HibernateDialect dialect) 
    {
        boolean result = false;
        if (data != null && dialect != null) {
        	try {
        		String productName = data.getDatabaseProductName();
        		String productVersion = data.getDatabaseProductVersion();
        		result = dialect.supportsProduct(productName, productVersion);
        	} catch (Exception e) {
        		s_log.error(
        		    "Encountered unexpected exception while attempting to " +
        		    "determine database product name/version: "+e.getMessage());
        		if (s_log.isDebugEnabled()) {
        			StringWriter s = new StringWriter();
        			PrintWriter p = new PrintWriter(s);
        			e.printStackTrace(p);
        			s_log.debug(s.getBuffer().toString());
        		}
        	}
        }
        return result;
    }
            
    public static HibernateDialect getDialect(String dbName) {
        return dbNameDialectMap.get(dbName);
    }
    
    
    public static HibernateDialect getDialect(ISQLDatabaseMetaData md) 
        throws UnknownDialectException
    {
        if (isAxion(md)) {
            return axionDialect;
        }
        if (isDaffodil(md)) {
            return daffodilDialect;
        }
        if (isDB2(md)) {
            return db2Dialect;
        }
        if (isDerby(md)) {
            return derbyDialect;
        }
        if (isFirebird(md)) {
            return firebirdDialect;
        }
        if (isFrontBase(md)) {
            return frontbaseDialect;
        }
        if (isHADB(md)) {
            return hadbDialect;
        }
        if (isH2(md)) {
            return h2Dialect;
        }
        if (isHSQL(md)) {
            return hsqlDialect;
        }
        if (isInformix(md)) {
            return informixDialect;
        }
        if (isIngres(md)) {
            return ingresDialect;
        }
        if (isInterbase(md)) {
            return ingresDialect;
        }        
        if (isMaxDB(md)) {
            return maxDbDialect;
        }
        if (isMcKoi(md)) {
            return mckoiDialect;
        }
        if (isMySQL(md)) {
            return mysqlDialect;
        }
        if (isMSSQLServer(md)) {
            return sqlserverDialect;
        }
        if (isOracle(md)) {
            return oracle9iDialect;
        }
        if (isPointbase(md)) {
            return pointbaseDialect;
        }
        if (isPostgreSQL(md)) {
            return postgreSQLDialect;
        }
        if (isProgress(md)) {
            return progressDialect;
        }
        if (isSyBase(md)) {
            return sybaseDialect;
        }
        if (isTimesTen(md)) {
            return timestenDialect;
        }        
        throw new UnknownDialectException();
    }
    
    public static HibernateDialect getDialect(int sessionType,
                                              JFrame parent, 
                                              ISQLDatabaseMetaData md) 
        throws UserCancelledOperationException 
    {
        HibernateDialect result = null;
        
        
        if (isPromptForDialect) {
            result = showDialectDialog(parent, sessionType);
        } else {
            try {
                result = getDialect(md);
            } catch (UnknownDialectException e) {
                
                result = showDialectDialog(parent, sessionType);    
            }       
        }
        return result;
    }

    
    private static HibernateDialect showDialectDialog(JFrame parent,
                                                      int sessionType) 
        throws UserCancelledOperationException 
    {
        Object[] dbNames = getDbNames();
        String chooserTitle = s_stringMgr.getString("dialectChooseTitle");
        String typeStr = null;
        if (sessionType == SOURCE_TYPE) {
            typeStr = s_stringMgr.getString("sourceSessionTypeName");
        }
        if (sessionType == DEST_TYPE) {
            typeStr = s_stringMgr.getString("destSessionTypeName");
        }
        String message = 
            s_stringMgr.getString("dialectDetectFailedMessage", typeStr);
        if (isPromptForDialect) {
            message = s_stringMgr.getString("autoDetectDisabledMessage", typeStr);
        } 
        String dbName = 
            (String)JOptionPane.showInputDialog(parent,
                                                message,
                                                chooserTitle,
                                                JOptionPane.INFORMATION_MESSAGE, 
                                                null, 
                                                dbNames, 
                                                dbNames[0]);
        if (dbName == null || "".equals(dbName)) {
            throw new UserCancelledOperationException();
        }
        return dbNameDialectMap.get(dbName);
    }
    
    
    public static Object[] getDbNames() {
        Set<String> keyset = dbNameDialectMap.keySet();
        Object[] keys = keyset.toArray();
        Arrays.sort(keys);
        return keys;
    }
    
    
    public static Object[] getSupportedDialects() {
        Collection<HibernateDialect> c = dbNameDialectMap.values();
        return c.toArray();
    }
        
}
