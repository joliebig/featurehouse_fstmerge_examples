
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.IFirebirdManagerSessionPreferencesBean;


public class PreferencesManager {
    
    private final static ILogger log = LoggerController.createLogger(PreferencesManager.class);
    
    public static final int PREFERENCES_BEAN_GLOBAL = 0;
    public static final int PREFERENCES_BEAN_BEACKUP_AND_RESTORE = 1;
    public static final int PREFERENCES_BEAN_CREATE_DATABASE = 2;
    public static final int PREFERENCES_BEAN_USERS = 3;
    public static final int PREFERENCES_BEAN_GRANT_AND_REVOKE = 4;
    
    
    private static final String PREFERENCES_FILE_NAME_GLOBAL = "prefsGlobal.xml";    
    private static final String PREFERENCES_FILE_NAME_SESSION_BACKUP_RESTORE = "prefsBckRes.xml";    
    private static final String PREFERENCES_FILE_NAME_SESSION_CREATE_DB = "prefsCreateDb.xml";    
    private static final String PREFERENCES_FILE_NAME_SESSION_USERS = "prefsUsers.xml";    
    private static final String PREFERENCES_FILE_NAME_SESSION_GRANT_REVOKE = "prefsGrantRevoke.xml";    
    
    
    private static File userSettingsFolder;
    
    
    private static FirebirdManagerPreferenceBean firebirdManagerPrefs = null;
    
    private static IPlugin firebirdManagerPlugin = null;
    
    
    public static void initialize(IPlugin plugin) throws PluginException {
        firebirdManagerPlugin = plugin;
        try {
            userSettingsFolder = firebirdManagerPlugin.getPluginUserSettingsFolder();
        } catch (IOException ex) {
            throw new PluginException(ex);
        }        
        firebirdManagerPrefs = (FirebirdManagerPreferenceBean)loadPreferences(PREFERENCES_BEAN_GLOBAL);
    }
    
    
    public static FirebirdManagerPreferenceBean getGlobalPreferences() {
        return firebirdManagerPrefs;
    }
    
    
    public static void unload() {
        savePreferences(firebirdManagerPrefs, PREFERENCES_BEAN_GLOBAL);
    }
    
    
    public static void savePreferences(IFirebirdManagerSessionPreferencesBean sessionPreferencesBean, int beanType) {
    	String filename = getSessionPreferencesFilename(beanType);
        try {
            XMLBeanWriter writer = new XMLBeanWriter(sessionPreferencesBean);
            writer.save(new File(userSettingsFolder, filename));
        } catch (Exception e) {
            log.error("Cannot write the firebird manager preferences to file: "
                    + filename, e);
        }
    }

    
    public static IFirebirdManagerSessionPreferencesBean loadPreferences(int beanType) {
    	IFirebirdManagerSessionPreferencesBean prefBean = null;
    	String filename = getSessionPreferencesFilename(beanType);
    	if (filename.length() == 0) {
    		return getEmptyPreferencesBean(beanType);
    	}
        try {
            XMLBeanReader reader = new XMLBeanReader();
            File file = new File(userSettingsFolder, filename);
            reader.load(file, getSessionPreferencesClassloader(beanType));
            
            Iterator<Object> it = reader.iterator();
            if (it.hasNext()) {
            	prefBean = (IFirebirdManagerSessionPreferencesBean)it.next();
            }
        } catch (FileNotFoundException eNotFound) {
            log.info(filename + " not found. It will be created!");
        } catch (Exception e) {
            log.error("Cannot read from the firebird manager preferences file: "
                    + filename, e);
        }
        if (prefBean == null) {
    		return getEmptyPreferencesBean(beanType);
        }
        
        return prefBean;
    }    
    private static String getSessionPreferencesFilename(int beanType) {
    	switch (beanType) {
		case PREFERENCES_BEAN_GLOBAL:
			return PREFERENCES_FILE_NAME_GLOBAL;
		case PREFERENCES_BEAN_BEACKUP_AND_RESTORE:
			return PREFERENCES_FILE_NAME_SESSION_BACKUP_RESTORE;
		case PREFERENCES_BEAN_CREATE_DATABASE:
			return PREFERENCES_FILE_NAME_SESSION_CREATE_DB;
		case PREFERENCES_BEAN_USERS:
			return PREFERENCES_FILE_NAME_SESSION_USERS;
		case PREFERENCES_BEAN_GRANT_AND_REVOKE:
			return PREFERENCES_FILE_NAME_SESSION_GRANT_REVOKE;
		default:
			return "";
		}
    }
    private static IFirebirdManagerSessionPreferencesBean getEmptyPreferencesBean(int beanType) {
    	switch (beanType) {
		case PREFERENCES_BEAN_GLOBAL:
			return new FirebirdManagerPreferenceBean();
		case PREFERENCES_BEAN_BEACKUP_AND_RESTORE:
			return new FirebirdManagerBackupAndRestorePreferenceBean();
		case PREFERENCES_BEAN_CREATE_DATABASE:
			return new FirebirdManagerCreateDatabasePreferenceBean();
		case PREFERENCES_BEAN_USERS:
			return new FirebirdManagerUsersPreferenceBean();


		default:
			return null;
		}
    }
    private static ClassLoader getSessionPreferencesClassloader(int beanType) {
        switch (beanType) {
		case PREFERENCES_BEAN_GLOBAL:
            return FirebirdManagerPreferenceBean.class.getClassLoader();
		case PREFERENCES_BEAN_BEACKUP_AND_RESTORE:
            return FirebirdManagerBackupAndRestorePreferenceBean.class.getClassLoader();
		case PREFERENCES_BEAN_CREATE_DATABASE:
            return FirebirdManagerCreateDatabasePreferenceBean.class.getClassLoader();
		case PREFERENCES_BEAN_USERS:
            return FirebirdManagerUsersPreferenceBean.class.getClassLoader();


		default:
			return null;
		}
        
    }
}
