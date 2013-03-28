
package net.sourceforge.squirrel_sql.plugins.oracle.prefs;



import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.gui.AbstractPluginPreferencesUITest;
import net.sourceforge.squirrel_sql.client.plugin.gui.DummyPlugin;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.preferences.BaseQueryTokenizerPreferenceBean;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.Test;


@GUITest
public class OraclePluginPreferencesPanelUITest extends AbstractPluginPreferencesUITest
{
	@Test
	public void testSaveData() {
		JButtonFixture saveButton = fixture.button("saveButton");
		JCheckBoxFixture initSessionTimezoneCheckBox = fixture.checkBox("initSessionTimezoneCheckBox");
		
		initSessionTimezoneCheckBox.uncheck();
		
		saveButton.click();
		((OraclePluginPreferencesPanel)classUnderTest).loadData();
		initSessionTimezoneCheckBox.requireNotSelected();
		
		initSessionTimezoneCheckBox.check();
		saveButton.click();
		((OraclePluginPreferencesPanel)classUnderTest).loadData();
		initSessionTimezoneCheckBox.requireSelected();
	}
	
	@Test
	public void testInitSessionTZCheckBox() {
		JCheckBoxFixture initSessionTimezoneCheckBox = fixture.checkBox("initSessionTimezoneCheckBox");
		JTextComponentFixture sessionTimezoneTextField = fixture.textBox("sessionTimezoneTextField");

		initSessionTimezoneCheckBox.uncheck();
		initSessionTimezoneCheckBox.check();
		initSessionTimezoneCheckBox.uncheck();

		sessionTimezoneTextField.requireDisabled();
		
		initSessionTimezoneCheckBox.check();
		
		sessionTimezoneTextField.requireEnabled();		
		
	}
	
	
   public static void main(String[] args) throws Exception
   {
   	new OraclePluginPreferencesPanelUITest().constructTestFrame().setVisible(true);
   }

	@Override
   protected PluginQueryTokenizerPreferencesPanel getPrefsPanelToTest() throws PluginException
   {
   	prefsManager.initialize(new DummyPlugin(), new OraclePreferenceBean());
   	return new OraclePluginPreferencesPanel(prefsManager);
   }

	@Override
   protected BaseQueryTokenizerPreferenceBean getPreferenceBean()
   {
	   return new OraclePreferenceBean();
   }

	
}
