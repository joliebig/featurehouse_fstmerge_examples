
package net.sourceforge.squirrel_sql.client.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PreferenceUtil;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;


public class PluginQueryTokenizerPreferencesManager {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(PluginQueryTokenizerPreferencesManager.class);
    
    
    private static final String USER_PREFS_FILE_NAME = "prefs.xml";    
    
    
    private File _userSettingsFolder;
    
    
    private IQueryTokenizerPreferenceBean _prefs = null;
    
    
    private IPlugin plugin = null;
    
    
    private boolean _initialized = false;
    
    
    public PluginQueryTokenizerPreferencesManager() {
        
    }
    
    
    public void initialize(IPlugin thePlugin, 
                           IQueryTokenizerPreferenceBean defaultPrefsBean) 
        throws PluginException 
    {
        if (thePlugin == null) {
            throw new IllegalArgumentException(
                    "IPlugin arguement cannot be null");
        }
        if (defaultPrefsBean == null) {
            throw new IllegalArgumentException(
                    "IQueryTokenizerPreferenceBean arguement cannot be null");
        }
        plugin = thePlugin;
        
        
        try {
            _userSettingsFolder = plugin.getPluginUserSettingsFolder();
        } catch (IOException ex) {
            throw new PluginException(ex);
        }        
        _prefs = defaultPrefsBean;
        loadPrefs();
        _initialized = true;
    }
    
    
    public IQueryTokenizerPreferenceBean getPreferences() {
        if (!_initialized) {
            throw new IllegalStateException("initialize() must be called first");
        }
        return _prefs;
    }
    
    
    public void unload() {
        savePrefs();
    }
    
    
    public void savePrefs() {
        if (!_initialized) {
            throw new IllegalStateException("initialize() must be called first");
        }        
        try {
            XMLBeanWriter wtr = new XMLBeanWriter(_prefs);
            wtr.save(new File(_userSettingsFolder, USER_PREFS_FILE_NAME));
        } catch (Exception ex) {
            s_log.error("Error occured writing to preferences file: "
                    + USER_PREFS_FILE_NAME, ex);
        }
    }

    
    private void loadPrefs() {
        try {
            XMLBeanReader doc = new XMLBeanReader();
            
            File prefFile = PreferenceUtil.getPreferenceFileToReadFrom(plugin);
            
            doc.load(prefFile, _prefs.getClass().getClassLoader());
                        
            Iterator<Object> it = doc.iterator();
            
            if (it.hasNext()) {
                _prefs = (IQueryTokenizerPreferenceBean)it.next();
            }
        } catch (FileNotFoundException ignore) {
            s_log.info(USER_PREFS_FILE_NAME + " not found - will be created");
        } catch (Exception ex) {
            s_log.error("Error occured reading from preferences file: "
                    + USER_PREFS_FILE_NAME, ex);
        }

        _prefs.setClientName(Version.getApplicationName() + "/" + plugin.getDescriptiveName());
        _prefs.setClientVersion(Version.getShortVersion() + "/" + plugin.getVersion());
    }    
    
}
