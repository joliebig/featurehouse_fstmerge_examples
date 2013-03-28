
package net.sourceforge.squirrel_sql.plugins.dbcopy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.DBCopyPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;


public class ScriptWriter {

    private final static ILogger s_log = 
        LoggerController.createLogger(ScriptWriter.class);
    
    
    private static PrintWriter out = null;
    
    private static String scriptsDirName = null;
    
    private static DBCopyPreferenceBean prefs = 
                                            PreferencesManager.getPreferences();
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ScriptWriter.class);        
    
    
    public static void write(String stmt) {
        if (!prefs.isWriteScript() || out == null) {
            return;
        }
        out.println(stmt);
    }
    
    public static void write(String pstmt, String[] bindVars) {
        if (!prefs.isWriteScript()) {
            return;
        }        
        write(getBoundStatement(pstmt, bindVars));
    }
    
    public static String getBoundStatement(String pstmt, String[] bindVars) {
        String[] parts = pstmt.split("\\?");
        StringBuffer result = new StringBuffer(parts[0]);
        for (int i = 0; i < bindVars.length; i++) {
            String nextVal = bindVars[i];

            try {
                if (nextVal != null) {
                    Double.parseDouble(nextVal);
                } else {
                    nextVal = "null";
                }
            } catch (NumberFormatException e) {
                nextVal = "'" + nextVal + "'";
            }
            
            
            result.append(nextVal);
            result.append(parts[i+1]);
        }
        return result.toString();
    }
    
    public static void open(ISession source, ISession dest) {
        if (!prefs.isWriteScript()) {
            return;
        }        
        try {
            setupScriptsDir();
            if (scriptsDirName == null) {
                return;
            }
            String filename = constructFilename(source, dest);
            File f = new File(filename);
            if (f.exists()) {
                f.delete();
            }
            out = new PrintWriter(new FileOutputStream(filename));
        } catch(Exception e) {
            s_log.error("", e);
        }
    }
    
    public static void close() {
        if (!prefs.isWriteScript() || out == null) {
            return;
        }
        out.close();
    }
    
    private static String constructFilename(ISession source, ISession dest) {
        StringBuffer result = new StringBuffer(scriptsDirName);
        result.append(File.separator);
        result.append(source.getAlias().getUserName());
        result.append("_to_");
        result.append(dest.getAlias().getUserName());
        result.append(".sql");
        return result.toString();
    }
    
    private static void setupScriptsDir() {
        String userHomeDir = System.getProperty("user.home");
        if (userHomeDir != null) {            
            StringBuffer scriptsDir = new StringBuffer();
            ApplicationFiles appFiles = new ApplicationFiles();
            
            scriptsDir.append(appFiles.getPluginsUserSettingsDirectory());
            scriptsDir.append(File.separator);
            
            scriptsDir.append("dbcopy");
            if (mkdir(scriptsDir.toString())) {
                scriptsDir.append(File.separator);
                
                scriptsDir.append("scripts");
                if (mkdir(scriptsDir.toString())) {
                    scriptsDirName = scriptsDir.toString();
                }
            }
        } else {
            
            
            
            String msg = 
                s_stringMgr.getString("ScriptWriter.error.nouserhome");
            s_log.error(msg);
            scriptsDirName = null;
        }
    }
    
    private static boolean mkdir(String directory) {
        boolean result = true;
        File f = new File(directory);
        if (!f.exists()) {
            if (!f.mkdir()) {
                result = false;
            }
        } else {
            if (!f.isDirectory()) {
                result = false;
            }
        }
        return result;
    }
}
