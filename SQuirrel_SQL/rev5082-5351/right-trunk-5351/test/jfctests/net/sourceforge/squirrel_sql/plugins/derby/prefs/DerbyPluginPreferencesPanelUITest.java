
package net.sourceforge.squirrel_sql.plugins.derby.prefs;


import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.gui.AbstractPluginPreferencesUITest;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.preferences.BaseQueryTokenizerPreferenceBean;

import org.fest.swing.annotation.GUITest;

@GUITest
public class DerbyPluginPreferencesPanelUITest extends AbstractPluginPreferencesUITest
{

	
   public static void main(String[] args) throws Exception
   {
   	new DerbyPluginPreferencesPanelUITest().constructTestFrame().setVisible(true);
   }	
	
	@Override
   protected PluginQueryTokenizerPreferencesPanel getPrefsPanelToTest() throws PluginException
   {
   	return new DerbyPluginPreferencesPanel(prefsManager);
   }

	@Override
   protected BaseQueryTokenizerPreferenceBean getPreferenceBean()
   {
	   return new DerbyPreferenceBean();
   }


}
