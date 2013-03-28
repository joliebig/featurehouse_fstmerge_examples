package net.sourceforge.squirrel_sql.plugins.sqlval;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

class SessionSettingsPanel extends JPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SessionSettingsPanel.class);


	
	private final static ILogger s_log =
		LoggerController.createLogger(SessionSettingsPanel.class);

	
	private final WebServicePreferences _prefs;

	
	private final WebServiceSessionProperties _sessionProps;

	
	
	private JCheckBox _anonDBMSChk = new JCheckBox(s_stringMgr.getString("sqlval.settingsAnon"));

	
	private OutputLabel _dbmsNameLbl = new OutputLabel(" ");

	
	private OutputLabel _dbmsVersionLbl = new OutputLabel(" ");

	
	private OutputLabel _techNameLbl = new OutputLabel(" ");

	
	private OutputLabel _techVersionLbl = new OutputLabel(" ");

	SessionSettingsPanel(WebServicePreferences prefs,
								WebServiceSessionProperties sessionProps)
	{
		super(new GridBagLayout());

		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		if (sessionProps == null)
		{
			throw new IllegalArgumentException("WebServiceSessionProperties == null");
		}

		_prefs = prefs;
		_sessionProps = sessionProps;
		createGUI();
		loadData();
	}

	void loadData()
	{
		_anonDBMSChk.setSelected(_sessionProps.getUseAnonymousDBMS());
		_dbmsNameLbl.setText(_sessionProps.getTargetDBMSName());
		_dbmsVersionLbl.setText(_sessionProps.getTargetDBMSVersion());
		_techNameLbl.setText(_sessionProps.getConnectionTechnology());
		_techVersionLbl.setText(_sessionProps.getConnectionTechnologyVersion());
	}

	
	void save()
	{
		_sessionProps.setUseAnonymousDBMS(_anonDBMSChk.isSelected());
	}

	
	private void createGUI()
	{
		setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(1, 4, 1, 4);
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(createDBMSPanel(), gbc);
	}

	
	private JPanel createDBMSPanel()
	{
		JPanel pnl = new JPanel();
		
		pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("sqlval.dbms")));

		pnl.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 4, 2, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		pnl.add(_anonDBMSChk, gbc);

		++gbc.gridy;
		
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.dbmsName"), JLabel.RIGHT), gbc);

		++gbc.gridy;
		
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.dbmsVersion"), JLabel.RIGHT), gbc);

		++gbc.gridy;
		
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.technology"), JLabel.RIGHT), gbc);

		++gbc.gridy;
		
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.technologyVersion"), JLabel.RIGHT), gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		pnl.add(_dbmsNameLbl, gbc);

		++gbc.gridy;
		pnl.add(_dbmsVersionLbl, gbc);

		++gbc.gridy;
		pnl.add(_techNameLbl, gbc);

		++gbc.gridy;
		pnl.add(_techVersionLbl, gbc);

		return pnl;
	}
}

