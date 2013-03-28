

package edu.rice.cs.drjava.plugins.eclipse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;


import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class EclipsePlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "edu.rice.cs.drjava";
 
  
  private static EclipsePlugin _plugin;
  
  private ResourceBundle _resourceBundle;
  
  private BundleContext _context;
  
  
  public EclipsePlugin() {
    super();
    try {
      _resourceBundle = ResourceBundle.getBundle("edu.rice.cs.drjava.plugins.eclipse.EclipsePluginResources");
    }
    catch (MissingResourceException x) {
      _resourceBundle = null;
    }
  }
  
  
  public void start(BundleContext context) throws Exception {
   super.start(context);
   _context = context;
   _plugin = this;
  }
  
  public void log(String msg) {
   log(msg, null);
  }

  public void log(String msg, Exception e) {
   getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, e));
  }
  
  public void stop(BundleContext context) throws Exception {
   _plugin = null;
   _context = null;
   super.stop(context);
  }

  
  public static EclipsePlugin getDefault() {
    return _plugin;
  }
  
  
  public static IWorkspace getWorkspace() {
    return ResourcesPlugin.getWorkspace();
  }
  
  
  public static ImageDescriptor getImageDescriptor(String path) {
   return imageDescriptorFromPlugin(PLUGIN_ID, path);
  }
  
  
  public static String getResourceString(String key) {
    ResourceBundle bundle = getDefault().getResourceBundle();
    try {
      return bundle.getString(key);
    }
    catch (MissingResourceException e) {
      return key;
    }
  }
  
  
  public ResourceBundle getResourceBundle() {
    return _resourceBundle;
  }

  
  public String getPluginClasspath() {
    Bundle bundle = _context.getBundle();
    URL installURL = bundle.getEntry("/");
    try { return FileLocator.toFileURL(installURL).getPath(); }
    catch (IOException e) { return ""; }
  }
  
  
  protected void initializeDefaultPreferences(IPreferenceStore store) {
    
    
    store.setDefault(DrJavaConstants.INTERACTIONS_RESET_PROMPT, true);
    store.setDefault(DrJavaConstants.ALLOW_PRIVATE_ACCESS, false);
    store.setDefault(DrJavaConstants.INTERACTIONS_EXIT_PROMPT, true);
    store.setDefault(DrJavaConstants.HISTORY_MAX_SIZE, 500);
    store.setDefault(DrJavaConstants.JVM_ARGS, "");
  }
}
