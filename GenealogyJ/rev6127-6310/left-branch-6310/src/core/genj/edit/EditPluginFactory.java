
package genj.edit;

import genj.app.PluginFactory;
import genj.app.Workbench;


public class EditPluginFactory implements PluginFactory {
  
  public Object createPlugin(Workbench workbench) {
    return new EditPlugin(workbench);
  }

}
