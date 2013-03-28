
package genj.report;

import genj.app.PluginFactory;
import genj.app.Workbench;


public class ReportPluginFactory implements PluginFactory {
  
  
  public Object createPlugin(Workbench workbench) {
    return new ReportPlugin(workbench);
  }

}
