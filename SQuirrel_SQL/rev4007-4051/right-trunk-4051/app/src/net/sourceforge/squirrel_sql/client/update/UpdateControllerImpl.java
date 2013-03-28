
package net.sourceforge.squirrel_sql.client.update;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.XmlBeanUtilities;


public class UpdateControllerImpl implements UpdateController {

    public static final String DEFAULT_REPO_HOST = 
        "squirrel-sql.sourceforge.net";
    
    public static final String DEFAULT_REPO_PATH = 
        "releases";

    
    public static final String RELEASE_XML_FILENAME = "release.xml";
    
    
    private IApplication _app = null;
    
    private UpdateUtil _util = new UpdateUtil(); 
    
    public UpdateControllerImpl(IApplication app) {
        _app = app;
    }
    
    public boolean isUpdateToDate() {
        boolean result = true;
        
        
        String releaseFilename = _util.getLocalReleaseFile();
        
        
        ChannelXmlBean installedBean = 
            _util.getLocalReleaseInfo(releaseFilename);
        
        
        String channelName = installedBean.getName();
        
        StringBuilder releasePath = new StringBuilder(DEFAULT_REPO_PATH);
        releasePath.append("/");
        releasePath.append(channelName);
        
        
        ChannelXmlBean currentReleaseBean = 
            _util.downloadCurrentRelease(DEFAULT_REPO_HOST, 
                                         releasePath.toString(), 
                                         RELEASE_XML_FILENAME);
        
        
        
        return currentReleaseBean.equals(installedBean);
    }
    
    
    public Set<String> getInstalledPlugins() {
        Set<String> result = new HashSet<String>();
        PluginManager pmgr = _app.getPluginManager();
        PluginInfo[] infos = pmgr.getPluginInformation();
        for (PluginInfo info : infos) {
            result.add(info.getInternalName());
        }
        return result;
    }
    
    
    public boolean pullDownUpdateFiles() {
        return true;
    }
    
}
