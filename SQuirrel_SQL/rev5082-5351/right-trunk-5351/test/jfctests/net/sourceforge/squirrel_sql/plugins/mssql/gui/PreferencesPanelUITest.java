
package net.sourceforge.squirrel_sql.plugins.mssql.gui;

import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.gui.AbstractPluginPreferencesUITest;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.mssql.prefs.MSSQLPreferenceBean;

public class PreferencesPanelUITest extends AbstractPluginPreferencesUITest{


		
	public static void main(String[] args) throws Exception {
		new PreferencesPanelUITest().constructTestFrame().setVisible(true);
	}
	
	@Override
   protected IQueryTokenizerPreferenceBean getPreferenceBean()
   {
	   return new MSSQLPreferenceBean();
   }

	@Override
   protected PluginQueryTokenizerPreferencesPanel getPrefsPanelToTest() throws PluginException
   {
		return new PluginQueryTokenizerPreferencesPanel(prefsManager, 
         "MS SQL-Server",
         false);
   }

}
