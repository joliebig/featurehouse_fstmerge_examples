
package net.sourceforge.squirrel_sql.plugins.oracle.prefs;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimeZone;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class OraclePluginPreferencesPanel extends PluginQueryTokenizerPreferencesPanel
{

	private static final long serialVersionUID = 1L;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(OraclePluginPreferencesPanel.class);

	static interface i18n
	{
		
		
		String HIDE_RECYCLE_BIN_CB_LABEL =
			s_stringMgr.getString("OraclePluginPreferencesPanel.hideRecycleBinCheckBoxLabel");

		
		
		String HIDE_RECYCLE_BIN_CB_TT =
			s_stringMgr.getString("OraclePluginPreferencesPanel.hideRecycleBinCheckBoxToolTip");

		
		String SESSION_TIMEZONE_LABEL = 
			s_stringMgr.getString("OraclePluginPreferencesPanel.sessionTimezoneLabel");
		
		
		
		String SESSION_TIMEZONE_TT = s_stringMgr.getString("OraclePluginPreferencesPanel.sessionTimezoneTT");
		
		
		
		String SHOW_ERROR_OFFSET_LABEL =
			s_stringMgr.getString("OraclePluginPreferencesPanel.showErrorOffsetLabel");

		
		
		String SHOW_ERROR_OFFSET_TT = s_stringMgr.getString("OraclePluginPreferencesPanel.showErrorOffsetTT");
				
	}

	
	private final static JCheckBox excludeRecycleBinTablesCheckBox =
		new JCheckBox(i18n.HIDE_RECYCLE_BIN_CB_LABEL);

	private final static JCheckBox showErrorOffsetCheckBox = new JCheckBox(i18n.SHOW_ERROR_OFFSET_LABEL);


	private final static JCheckBox initSessionTimezoneCheckBox = new JCheckBox("Initialize Session Timezone");

	private final static JTextField sessionTimezone = new JTextField();

	
	public OraclePluginPreferencesPanel(PluginQueryTokenizerPreferencesManager prefsMgr)
	{
		super(prefsMgr, "Oracle");
	}

	
	@Override
	protected JPanel createTopPanel()
	{
		JPanel result = super.createTopPanel();
		int lastY = super.lastY;
		addRecycleBinCheckBox(result, 0, lastY++);
		addShowErrorOffsetCheckBox(result, 0, lastY++);
		addSessionTimezoneCheckBox(result, 0, lastY++);
		addSessionTimezoneTextField(result, 0, lastY++);
		return result;
	}

	private void addRecycleBinCheckBox(JPanel result, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; 
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 5, 0, 0);
		excludeRecycleBinTablesCheckBox.setToolTipText(i18n.HIDE_RECYCLE_BIN_CB_TT);
		result.add(excludeRecycleBinTablesCheckBox, c);
	}

	private void addShowErrorOffsetCheckBox(JPanel result, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; 
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 5, 0, 0);
		showErrorOffsetCheckBox.setToolTipText(i18n.SHOW_ERROR_OFFSET_TT);
		result.add(showErrorOffsetCheckBox, c);
	}

	private void addSessionTimezoneCheckBox(JPanel result, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; 
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 5, 0, 0);
		initSessionTimezoneCheckBox.setToolTipText(i18n.SESSION_TIMEZONE_TT);
		result.add(initSessionTimezoneCheckBox, c);
		
		initSessionTimezoneCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				sessionTimezone.setEnabled(initSessionTimezoneCheckBox.isSelected());
			}
		});
	}

	private void addSessionTimezoneTextField(JPanel result, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, LEFT_INDENT_INSET_SIZE, 0, 0);
		sessionTimezone.setToolTipText(i18n.SESSION_TIMEZONE_TT);
		result.add(sessionTimezone, c);
	}
	
	
	
	@Override
	protected void loadData()
	{
		super.loadData();
		IQueryTokenizerPreferenceBean prefs = _prefsManager.getPreferences();
		OraclePreferenceBean oraclePrefs = (OraclePreferenceBean) prefs;
		excludeRecycleBinTablesCheckBox.setSelected(oraclePrefs.isExcludeRecycleBinTables());
		showErrorOffsetCheckBox.setSelected(oraclePrefs.isShowErrorOffset());
		if (oraclePrefs.getInitSessionTimezone()) {
			initSessionTimezoneCheckBox.setSelected(true);
			sessionTimezone.setEnabled(true);
		} else {
			initSessionTimezoneCheckBox.setSelected(false);
			sessionTimezone.setEnabled(false);
		}
		if (oraclePrefs.getSessionTimezone() != null && !"".equals(oraclePrefs.getSessionTimezone())) {
			sessionTimezone.setText(oraclePrefs.getSessionTimezone());
		} else {
			sessionTimezone.setText(TimeZone.getDefault().getID());
		}
	}

	
	@Override
	protected void save()
	{
		IQueryTokenizerPreferenceBean prefs = _prefsManager.getPreferences();
		OraclePreferenceBean oraclePrefs = (OraclePreferenceBean) prefs;
		oraclePrefs.setExcludeRecycleBinTables(excludeRecycleBinTablesCheckBox.isSelected());
		oraclePrefs.setShowErrorOffset(showErrorOffsetCheckBox.isSelected());
		oraclePrefs.setInitSessionTimezone(initSessionTimezoneCheckBox.isSelected());
		if (initSessionTimezoneCheckBox.isSelected()) {
			oraclePrefs.setSessionTimezone(sessionTimezone.getText());
		}
		super.save();
	}

}
