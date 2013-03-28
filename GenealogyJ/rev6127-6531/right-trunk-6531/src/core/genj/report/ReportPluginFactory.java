
package genj.report;

import genj.app.PluginFactory;
import genj.app.Workbench;


public class ReportPluginFactory implements PluginFactory {
  
  private static ReportPlugin instance;
  
  
  public Object createPlugin(Workbench workbench) {
    if (instance==null)
      instance = new ReportPlugin(workbench);
    return instance;
  }
  
  public static ReportPlugin getInstance() {
    return instance;
  }

}
