package net.sourceforge.squirrel_sql.client.plugin;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener;
import net.sourceforge.squirrel_sql.fw.util.MyURLClassLoader;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class PluginManager {
    
    private static final StringManager s_stringMgr = 
        StringManagerFactory.getStringManager(PluginManager.class);

    
    private static final ILogger s_log = 
        LoggerController.createLogger(PluginManager.class);

    
    private IApplication _app;

    
    private MyURLClassLoader _pluginsClassLoader;

    
    private final List<PluginInfo> _plugins = new ArrayList<PluginInfo>();

    
    private final Map<String, IPlugin> _loadedPlugins = 
        new HashMap<String, IPlugin>();

    
    private final List<SessionPluginInfo> _sessionPlugins = 
        new ArrayList<SessionPluginInfo>();

    
    private final Map<IIdentifier,List<SessionPluginInfo>> _activeSessions  = 
        new HashMap<IIdentifier,List<SessionPluginInfo>>();

    
    private final Map<String, PluginLoadInfo> _pluginLoadInfoColl = 
        new HashMap<String, PluginLoadInfo>();

    private HashMap<IIdentifier, List<PluginSessionCallback>> 
            _pluginSessionCallbacksBySessionID = 
                new HashMap<IIdentifier, List<PluginSessionCallback>>();

    
    private ClassLoaderListener classLoaderListener = null;

    
    public PluginManager(IApplication app) {
        super();
        if (app == null) {
            throw new IllegalArgumentException("IApplication == null");
        }

        _app = app;
    }

    
    public synchronized void sessionCreated(ISession session) {
        if (session == null) {
            throw new IllegalArgumentException("ISession == null");
        }

        for (Iterator<SessionPluginInfo> it = 
                _sessionPlugins.iterator(); it.hasNext();) {
            SessionPluginInfo spi = it.next();
            try {
                spi.getSessionPlugin().sessionCreated(session);
            } catch (Throwable th) {
                String msg = s_stringMgr.getString(
                        "PluginManager.error.sessioncreated", 
                        spi.getPlugin().getDescriptiveName());
                s_log.error(msg, th);
                _app.showErrorDialog(msg, th);
            }
        }
    }

    
    public synchronized void sessionStarted(final ISession session) {
        if (session == null) {
            throw new IllegalArgumentException("ISession == null");
        }
        final List<SessionPluginInfo> plugins = 
            new ArrayList<SessionPluginInfo>();
        _activeSessions.put(session.getIdentifier(), plugins);

        ArrayList<SessionPluginInfo> startInFG = 
            new ArrayList<SessionPluginInfo>();
        final ArrayList<SessionPluginInfo> startInBG = 
            new ArrayList<SessionPluginInfo>();
        for (Iterator<SessionPluginInfo> it = 
            _sessionPlugins.iterator(); it.hasNext();) {
            SessionPluginInfo spi = it.next();
            if (spi.getSessionPlugin().allowsSessionStartedInBackground()) {
                startInBG.add(spi);
            } else {
                startInFG.add(spi);
            }

        }
        session.setPluginsfinishedLoading(true);

        for (Iterator<SessionPluginInfo> it = 
                startInFG.iterator(); it.hasNext();) {
            SessionPluginInfo spi = it.next();
            sendSessionStarted(session, spi, plugins);
        }

        session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                for (Iterator<SessionPluginInfo> it = 
                        startInBG.iterator(); it.hasNext();) {
                    SessionPluginInfo spi = it.next();
                    sendSessionStarted(session, spi, plugins);
                }
                session.setPluginsfinishedLoading(true);
            }
        });
    }

    private void sendSessionStarted(ISession session, 
                                    SessionPluginInfo spi,
                                    List<SessionPluginInfo> plugins) 
    {
        try {
            PluginSessionCallback pluginSessionCallback = 
                spi.getSessionPlugin().sessionStarted(session);

            if (null != pluginSessionCallback) {
                List<PluginSessionCallback> list = 
                    _pluginSessionCallbacksBySessionID.get(session.getIdentifier());
                if (null == list) {
                    list = new ArrayList<PluginSessionCallback>();
                    _pluginSessionCallbacksBySessionID.put(
                                                        session.getIdentifier(), 
                                                        list);
                }
                list.add(pluginSessionCallback);

                plugins.add(spi);
            }
        } catch (final Throwable th) {
            final String msg = s_stringMgr.getString(
                    "PluginManager.error.sessionstarted", spi.getPlugin()
                            .getDescriptiveName());
            s_log.error(msg, th);
            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    _app.showErrorDialog(msg, th);
                }
            });

        }
    }

    
    public synchronized void sessionEnding(ISession session) {
        if (session == null) {
            throw new IllegalArgumentException("ISession == null");
        }

        List<SessionPluginInfo> plugins = 
            _activeSessions.remove(session.getIdentifier());
        if (plugins != null) {
            for (Iterator<SessionPluginInfo> it =   
                    plugins.iterator(); it.hasNext();) {
                SessionPluginInfo spi = it.next();
                try {
                    spi.getSessionPlugin().sessionEnding(session);
                } catch (Throwable th) {
                    String msg = s_stringMgr.getString(
                                          "PluginManager.error.sessionended", 
                                          spi.getPlugin().getDescriptiveName());
                    s_log.error(msg, th);
                    _app.showErrorDialog(msg, th);
                }
            }

            _pluginSessionCallbacksBySessionID.remove(session.getIdentifier());
        }
    }

    public synchronized void unloadPlugin(String pluginInternalName) {
        for (Iterator<IPlugin> it = _loadedPlugins.values().iterator(); it.hasNext();) {
            IPlugin plugin = it.next();
            if (plugin.getInternalName().equals(pluginInternalName)) {
                plugin.unload();
                it.remove();
            }
        }
        for (Iterator<SessionPluginInfo> it = _sessionPlugins.iterator(); it.hasNext();) {
           SessionPluginInfo plugin = it.next();
           if (plugin.getInternalName().equals(pluginInternalName)) {
               it.remove();
           }
        }
    }
    
    
    public synchronized void unloadPlugins() {
        for (Iterator<IPlugin> it = _loadedPlugins.values().iterator(); it.hasNext();) {
            IPlugin plugin = it.next();
            try {
                plugin.unload();
            } catch (Throwable th) {
                String msg = s_stringMgr.getString(
                        "PluginManager.error.unloading", plugin
                                .getInternalName());
                s_log.error(msg, th);
                _app.showErrorDialog(msg, th);
            }
        }
    }

    public synchronized PluginInfo[] getPluginInformation() {
        return _plugins.toArray(new PluginInfo[_plugins.size()]);
    }

    public synchronized SessionPluginInfo[] getPluginInformation(
            ISession session) {
        if (session == null) {
            throw new IllegalArgumentException("Null ISession passed");
        }
        List<SessionPluginInfo> list = 
            _activeSessions.get(session.getIdentifier());
        if (list != null) {
            return list.toArray(new SessionPluginInfo[list.size()]);
        }
        return new SessionPluginInfo[0];
    }

    public synchronized IPluginDatabaseObjectType[] getDatabaseObjectTypes(
            ISession session) {
        List<IPluginDatabaseObjectType> objTypesList = 
                new ArrayList<IPluginDatabaseObjectType>();
        List<SessionPluginInfo> plugins = 
            _activeSessions.get(session.getIdentifier());
        if (plugins != null) {
            for (Iterator<SessionPluginInfo> it = 
                    plugins.iterator(); it.hasNext();) {
                SessionPluginInfo spi = it.next();
                IPluginDatabaseObjectType[] objTypes = spi.getSessionPlugin()
                        .getObjectTypes(session);
                if (objTypes != null) {
                    for (int i = 0; i < objTypes.length; ++i) {
                        objTypesList.add(objTypes[i]);
                    }
                }
            }
        }

        return objTypesList.toArray(
                            new IPluginDatabaseObjectType[objTypesList.size()]);
    }

    
    public URL[] getPluginURLs() {
        return _pluginsClassLoader.getURLs();
    }

    public PluginStatus[] getPluginStatuses() {
        return _app.getSquirrelPreferences().getPluginStatuses();
    }

    public synchronized void setPluginStatuses(PluginStatus[] values) {
        _app.getSquirrelPreferences().setPluginStatuses(values);
    }

    
    public Iterator<SessionPluginInfo> getSessionPluginIterator() {
        return _sessionPlugins.iterator();
    }

    
    public void loadPlugins() {
        List<URL> pluginUrls = new ArrayList<URL>();
        File dir = new ApplicationFiles().getPluginsDirectory();
        boolean isMac = System.getProperty("os.name").toLowerCase().startsWith(
                "mac");
        if (dir.isDirectory()) {
            final Map<String, PluginStatus> pluginStatuses = 
                new HashMap<String, PluginStatus>();
            {
                final PluginStatus[] ar = getPluginStatuses();
                for (int i = 0; i < ar.length; ++i) {
                    pluginStatuses.put(ar[i].getInternalName(), ar[i]);
                }
            }
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isFile()) {
                    checkPlugin(files[i], pluginStatuses, pluginUrls, isMac);
                }
            }
        }

        URL[] urls = pluginUrls.toArray(new URL[pluginUrls.size()]);
        if (s_log.isDebugEnabled()) {
            for (int i = 0; i < urls.length; ++i) {
                s_log.debug("Plugin class loader URL[" + i + "] = " + urls[i]);
            }
        }

        loadPluginInfoCache();

        _pluginsClassLoader = new MyURLClassLoader(urls);
        _pluginsClassLoader.addClassLoaderListener(classLoaderListener);
        Class<?>[] classes = _pluginsClassLoader.getAssignableClasses(
                IPlugin.class, s_log);
        for (int i = 0; i < classes.length; ++i) {
            try {
                loadPlugin(classes[i]);
            } catch (Throwable th) {
                String msg = s_stringMgr.getString(
                                        "PluginManager.error.loadpluginclass", 
                                        classes[i].getName());
                th.printStackTrace();
                s_log.error(msg, th);
                _app.showErrorDialog(msg, th);
            }
        }
        Collections.sort(_plugins, new Comparator<PluginInfo>() {
            public int compare(PluginInfo arg0, PluginInfo arg1) {
                if (arg0 == null || arg1 == null) {
                    throw new NullPointerException(
                                            "arg1 and arg2 must not be null");
                }
                return arg0.getInternalName().compareTo(arg1.getInternalName());
            }
            
        });
    }

    private void checkPlugin(File pluginFile, 
                             Map<String, PluginStatus> pluginStatuses,
                             List<URL> pluginUrls, 
                             boolean isMac) 
    {
        final String fileName = pluginFile.getAbsolutePath();
        if (!fileName.toLowerCase().endsWith("src.jar")
                && (fileName.toLowerCase().endsWith(".zip") 
                        || fileName.toLowerCase().endsWith(".jar"))) {
            try {
                if (fileName.toLowerCase().endsWith("jedit.jar")) {
                    String msg = 
                        s_stringMgr.getString("PluginManager.error.jedit");
                    _app.showErrorDialog(msg);
                    return;
                }

                final String fullFilePath = pluginFile.getAbsolutePath();
                final String internalName = 
                    Utilities.removeFileNameSuffix(pluginFile.getName());
                final PluginStatus ps = pluginStatuses.get(internalName);
                if (!isMac && internalName.startsWith("macosx")) {
                    s_log.info(
                      "Detected MacOS X plugin on non-Mac platform - skipping");
                    return;
                }
                if (ps != null && !ps.isLoadAtStartup()) {
                    
                    
                    
                    PluginInfo pi = new PluginInfo();
                    pi.setPlugin(new MyPlaceHolderPlugin(internalName));
                    _plugins.add(pi);                    

                } else {
                    pluginUrls.add(pluginFile.toURL());

                    
                    final String pluginDirName = 
                        Utilities.removeFileNameSuffix(fullFilePath);
                    final File libDir = new File(pluginDirName, "lib");
                    addPluginLibraries(libDir, pluginUrls);

                }
            } catch (IOException ex) {
                String msg = s_stringMgr.getString(
                        "PluginManager.error.loadplugin", fileName);
                s_log.error(msg, ex);
                _app.showErrorDialog(msg, ex);
            }
        }
    }

    private void loadPluginInfoCache() {

    }

    private void addPluginLibraries(File libDir, List<URL> pluginUrls) {
        if (libDir.exists() && libDir.isDirectory()) {
            File[] libDirFiles = libDir.listFiles();
            for (int j = 0; j < libDirFiles.length; ++j) {
                if (libDirFiles[j].isFile()) {
                    final String fn = libDirFiles[j].getAbsolutePath();
                    if (fn.toLowerCase().endsWith(".zip")
                            || fn.toLowerCase().endsWith(".jar")) {
                        try {
                            pluginUrls.add(libDirFiles[j].toURL());
                        } catch (IOException ex) {
                            String msg = s_stringMgr.getString(
                                    "PluginManager.error.loadlib", fn);
                            s_log.error(msg, ex);
                            _app.showErrorDialog(msg, ex);
                        }
                    }
                }
            }
        }

    }

    
    public void initializePlugins() {
        _app.getWindowManager().addSessionSheetListener(
                new InternalFrameAdapter() {
                    public void internalFrameOpened(InternalFrameEvent e) {
                        onInternalFrameOpened(e);
                    }
                });

        for (Iterator<IPlugin> it = 
                _loadedPlugins.values().iterator(); it.hasNext();) {
            IPlugin plugin = it.next();
            try {
                final PluginLoadInfo pli = getPluginLoadInfo(plugin);
                pli.startInitializing();
                plugin.initialize();
                pli.endInitializing();
            } catch (Throwable th) {
                String msg = s_stringMgr.getString(
                        "PluginManager.error.initplugin", plugin
                                .getInternalName());
                s_log.error(msg, th);
                _app.showErrorDialog(msg, th);
            }
        }
    }

    
    public void setClassLoaderListener(ClassLoaderListener listener) {
        classLoaderListener = listener;
    }

    private void onInternalFrameOpened(InternalFrameEvent e) {
        JInternalFrame frame = e.getInternalFrame();

        if (frame instanceof BaseSessionInternalFrame) {
            ISession session = ((BaseSessionInternalFrame) frame).getSession();

            List<PluginSessionCallback> list = 
                _pluginSessionCallbacksBySessionID.get(session.getIdentifier());

            if (null != list) {
                for (int i = 0; i < list.size(); i++) {
                    PluginSessionCallback psc = list.get(i);

                    if (frame instanceof SQLInternalFrame) {
                        psc.sqlInternalFrameOpened((SQLInternalFrame) frame,
                                session);
                    } else if (frame instanceof ObjectTreeInternalFrame) {
                        psc.objectTreeInternalFrameOpened(
                                (ObjectTreeInternalFrame) frame, session);
                    }
                }
            }
        }
    }

    
    public Iterator<PluginLoadInfo> getPluginLoadInfoIterator() {
        return _pluginLoadInfoColl.values().iterator();
    }

    private void loadPlugin(Class<?> pluginClass) {
        PluginInfo pi = new PluginInfo(pluginClass.getName());
        try {
            final PluginLoadInfo pli = new PluginLoadInfo();
            final IPlugin plugin = (IPlugin) pluginClass.newInstance();
            pli.pluginCreated(plugin);
            _pluginLoadInfoColl.put(plugin.getInternalName(), pli);
            pi.setPlugin(plugin);
            _plugins.add(pi);
            if (validatePlugin(plugin)) {
                pli.startLoading();
                plugin.load(_app);
                pi.setLoaded(true);
                _loadedPlugins.put(plugin.getInternalName(), plugin);
                if (ISessionPlugin.class.isAssignableFrom(pluginClass)) {
                    _sessionPlugins.add(new SessionPluginInfo(pi));
                }
            }
            pli.endLoading();
        } catch (Throwable th) {
            String msg = s_stringMgr.getString(
                    "PluginManager.error.loadpluginclass", pluginClass
                            .getName());
            th.printStackTrace();
            s_log.error(msg, th);
            _app.showErrorDialog(msg, th);
        }
    }

    private boolean validatePlugin(IPlugin plugin) {
        String pluginInternalName = plugin.getInternalName();
        if (pluginInternalName == null
                || pluginInternalName.trim().length() == 0) {
            s_log.error("Plugin " + plugin.getClass().getName()
                    + "doesn't return a valid getInternalName()");
            return false;
        }

        if (_loadedPlugins.get(pluginInternalName) != null) {
            s_log.error("A Plugin with the internal name " + pluginInternalName
                    + " has already been loaded");
            return false;
        }

        return true;
    }

    private PluginLoadInfo getPluginLoadInfo(IPlugin plugin) {
        return _pluginLoadInfoColl.get(plugin.getInternalName());
    }

    
    public Object bindExternalPluginService(String internalNameOfPlugin,
                                            Class<?> toBindTo) 
    {
        IPlugin plugin = _loadedPlugins.get(internalNameOfPlugin);

        if (null == plugin) {
            return null;
        }

        final Object obj = plugin.getExternalService();

        if (null == obj) {
            throw new RuntimeException("The plugin " + internalNameOfPlugin
                    + " doesn't provide any external service.");
        }

        InvocationHandler ih = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                Method m = obj.getClass().getMethod(method.getName(),
                        method.getParameterTypes());
                return m.invoke(obj, args);
            }
        };

        return Proxy.newProxyInstance(_pluginsClassLoader,
                new Class[] { toBindTo }, ih);
    }

    public IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(
            SQLAlias alias) {
        ArrayList<IAliasPropertiesPanelController> ret = 
            new ArrayList<IAliasPropertiesPanelController>();
        for (Iterator<IPlugin> i = _loadedPlugins.values().iterator(); i.hasNext();) {
            IPlugin plugin = i.next();

            IAliasPropertiesPanelController[] ctrls = plugin
                    .getAliasPropertiesPanelControllers(alias);
            if (null != ctrls) {
                ret.addAll(Arrays.asList(ctrls));
            }
        }

        return ret.toArray(new IAliasPropertiesPanelController[ret.size()]);
    }

    public void aliasCopied(SQLAlias source, SQLAlias target) {
        for (Iterator<IPlugin> i = _loadedPlugins.values().iterator(); i.hasNext();) {
            IPlugin plugin = i.next();
            plugin.aliasCopied(source, target);
        }
    }

    public void aliasRemoved(SQLAlias alias) {
        for (Iterator<IPlugin> i = _loadedPlugins.values().iterator(); i.hasNext();) {
            IPlugin plugin = i.next();
            plugin.aliasRemoved(alias);
        }
    }

    
    private static class MyPlaceHolderPlugin extends DefaultPlugin {
        private String _internalName = null;

        public MyPlaceHolderPlugin(String internalName) {
            _internalName = internalName;
        }

        
        public String getAuthor() {
            return "";
        }

        
        public String getDescriptiveName() {
            return "";
        }

        
        public String getInternalName() {
            return _internalName;
        }

        
        public String getVersion() {
            return "";
        }

    }
}
