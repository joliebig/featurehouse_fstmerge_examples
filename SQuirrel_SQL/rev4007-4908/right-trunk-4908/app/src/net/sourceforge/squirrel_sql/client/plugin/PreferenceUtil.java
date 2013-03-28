
package net.sourceforge.squirrel_sql.client.plugin;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class PreferenceUtil {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(PreferenceUtil.class);
                
    
    private static final String USER_PREFS_FILE_NAME = "prefs.xml";    
    
    
    
    public static File getPreferenceFileToReadFrom(IPlugin p) throws IOException {
        File userSettingsFolder = p.getPluginUserSettingsFolder();
        final File newUserPreferenceFile = 
            new File(userSettingsFolder, USER_PREFS_FILE_NAME);
        
        File result = newUserPreferenceFile;
        
        String migratePrefsProperty = 
            System.getProperty("migratePreferences", "false");
        if (migratePrefsProperty != null 
                && migratePrefsProperty.equalsIgnoreCase("true")) 
        {
            String oldSquirrelLocation = 
                System.getProperty("oldSQuirreLInstallDir");
            
            if (oldSquirrelLocation == null || oldSquirrelLocation.equals("")) {
                throw new IllegalStateException(
                    "migratePreferences was set to true, but " +
                    "oldSQuirreLInstallDir wasn't set.");
            }
            
            final File oldAppPreferenceFile = 
                new File(oldSquirrelLocation + File.separator + "plugins" + 
                         File.separator + p.getInternalName() +
                         File.separator + USER_PREFS_FILE_NAME);
            
            
            
            
            if (oldAppPreferenceFile.exists()) {
                
                if (oldAppPreferenceFile.lastModified() > 
                                        newUserPreferenceFile.lastModified()) 
                {
                    result = oldAppPreferenceFile;
                    s_log.info("-DmigratePreferences was specified; using "+
                               oldAppPreferenceFile.getAbsolutePath()+
                               " as the source for preferences - will save " +
                               "them to "+newUserPreferenceFile.getAbsolutePath());
                } else {
                    s_log.info("-DmigratePreferences was specified, but file "+
                            newUserPreferenceFile.getAbsolutePath()+ " is newer " +
                            "than "+oldAppPreferenceFile.getAbsolutePath() +
                            ": migration will be skipped");
                }
                
            } else {
                s_log.info("-DmigratePreferences was specified, but file " +
                           oldAppPreferenceFile.getAbsolutePath()+" does not " +
                           "exist! Please remove -DmigratePreferences from the " +
                           "launch script, or fix -DoldSquirrelLocation to " +
                           "point to a valid previous SQuirreL installation " +
                           "directory");
            }
        }
        return result; 
    }
    
}
