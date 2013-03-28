
package net.sourceforge.squirrel_sql.client.preferences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.UpdateChannelComboBoxEntry.ChannelType;
import net.sourceforge.squirrel_sql.client.update.UpdateCheckFrequency;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.UpdateUtilImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class UpdatePreferencesPanel extends JPanel
{
	private static final Color CONNECTION_FAILURE_COLOR = Color.RED;

	private static final Color CONNECTION_SUCCESS_COLOR = new Color(67, 181, 118);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(UpdatePreferencesPanel.class);

	private static final UpdateUtil _updateUtil = new UpdateUtilImpl();

	static interface i18n
	{

		
		String AT_STARTUP_LABEL = s_stringMgr.getString("UpdatePreferencesPanel.atStartupLabel");

		
		String AUTO_BORDER_LABEL = s_stringMgr.getString("UpdatePreferencesPanel.autoBorderLabel");

		
		
		String AUTO_CHECK_FREQUENCY = s_stringMgr.getString("UpdatePreferencesPanel.autoCheckFrequency");

		
		String CHANNEL = s_stringMgr.getString("UpdatePreferencesPanel.channel");

		
		String UPDATE_SITE_BORDER_LABEL = s_stringMgr.getString("UpdatePreferencesPanel.updateSiteBorderLabel");

		
		String ENABLE_AUTO_UPDATE = s_stringMgr.getString("UpdatePreferencesPanel.enableAutoUpdate");

		
		String LOCAL_PATH = s_stringMgr.getString("UpdatePreferencesPanel.localPathLabel");

		
		String PATH = s_stringMgr.getString("UpdatePreferencesPanel.path");

		
		String PORT = s_stringMgr.getString("UpdatePreferencesPanel.port");

		
		String SERVER = s_stringMgr.getString("UpdatePreferencesPanel.server");

		
		String DAILY_LABEL = s_stringMgr.getString("UpdatePreferencesPanel.dailyLabel");

		
		String WEEKLY_LABEL = s_stringMgr.getString("UpdatePreferencesPanel.weeklyLabel");

		
		String SITE_TYPE_LABEL = s_stringMgr.getString("UpdatePreferencesPanel.siteTypeLabel");

		
		String TEST_LABEL = s_stringMgr.getString("UpdatePreferencesPanel.testLabel");

		
		String URL = s_stringMgr.getString("UpdatePreferencesPanel.urlLabel");

		
		String STATUS_LABEL_SUCCESS_MSG = s_stringMgr.getString("UpdatePreferencesPanel.statusLableSuccessMsg");

		
		String STATUS_LABEL_FAILURE_MSG = s_stringMgr.getString("UpdatePreferencesPanel.statusLableFailureMsg");

		
		
		String CONNECTION_FAILURE_DIALOG_MSG =
			s_stringMgr.getString("UpdatePreferencesPanel.connectionFailureDialogMsg");
	}

	private static final long serialVersionUID = 6411907298042579120L;

	private JLabel _serverLabel = null;

	private JLabel _portLabel = null;

	private JLabel _pathLabel = null;

	private JLabel _localPathLabel = null;

	private JLabel _channelLabel = null;

	private JLabel _urlLabel = null;

	private JTextField _updateServerName = new JTextField();

	private JTextField _updateServerPort = new JTextField();

	private JTextField _updateServerPath = new JTextField();

	private JTextField _localPath = new JTextField();

	private JTextField _updateUrl = new JTextField();

	private JLabel siteTypeLabel = null;

	private JRadioButton _remoteTypeButton = new JRadioButton("Remote");

	private JRadioButton _localTypeButton = new JRadioButton("Local");

	private ButtonGroup _updateSiteTypeGroup = new ButtonGroup();

	private UpdateChannelComboBoxEntry stableChannel =
		new UpdateChannelComboBoxEntry(ChannelType.STABLE, "stable");

	private UpdateChannelComboBoxEntry snapshotChannel =
		new UpdateChannelComboBoxEntry(ChannelType.SNAPSHOT, "snapshot");

	private JComboBox _updateServerChannel = new JComboBox(new Object[] { stableChannel, snapshotChannel });

	private JCheckBox _enableAutoUpdateChk = new JCheckBox(i18n.ENABLE_AUTO_UPDATE);

	private UpdateCheckFrequencyComboBoxEntry checkAtStartup =
		new UpdateCheckFrequencyComboBoxEntry(UpdateCheckFrequency.STARTUP, i18n.AT_STARTUP_LABEL);

	private UpdateCheckFrequencyComboBoxEntry checkDaily =
		new UpdateCheckFrequencyComboBoxEntry(UpdateCheckFrequency.DAILY, i18n.DAILY_LABEL);

	private UpdateCheckFrequencyComboBoxEntry checkWeekly =
		new UpdateCheckFrequencyComboBoxEntry(UpdateCheckFrequency.WEEKLY, i18n.WEEKLY_LABEL);

	private JComboBox _updateCheckFrequency =
		new JComboBox(new Object[] { checkAtStartup, checkDaily, checkWeekly });

	private JButton _testConnectionButton = new JButton(i18n.TEST_LABEL);

	private JLabel _testConnectionStatusLabel = new JLabel();

	private final Insets SEP_INSETS = new Insets(10, 14, 0, 14);

	private final Insets LABEL_INSETS = new Insets(2, 28, 6, 0);

	private final Insets FIELD_INSETS = new Insets(2, 8, 6, 28);

	private IApplication _app;

	public UpdatePreferencesPanel()
	{
		super(new GridBagLayout());
		createUserInterface();
	}

	void loadData(SquirrelPreferences prefs)
	{
		final IUpdateSettings updateSettings = prefs.getUpdateSettings();

		_updateServerName.setText(updateSettings.getUpdateServer());
		_updateServerPort.setText(updateSettings.getUpdateServerPort());
		_updateServerPath.setText(updateSettings.getUpdateServerPath());

		String channelStr = updateSettings.getUpdateServerChannel();
		_updateServerChannel.setSelectedItem(stableChannel);
		if (channelStr != null && channelStr.equals(ChannelType.SNAPSHOT.name()))
		{
			_updateServerChannel.setSelectedItem(snapshotChannel);
		}

		_enableAutoUpdateChk.setSelected(updateSettings.isEnableAutomaticUpdates());

		UpdateCheckFrequency updateCheckFrequency =
			UpdateCheckFrequency.getEnumForString(updateSettings.getUpdateCheckFrequency());

		if (updateCheckFrequency == UpdateCheckFrequency.DAILY)
		{
			_updateCheckFrequency.setSelectedItem(checkDaily);
		}
		if (updateCheckFrequency == UpdateCheckFrequency.STARTUP)
		{
			_updateCheckFrequency.setSelectedItem(checkAtStartup);
		}
		if (updateCheckFrequency == UpdateCheckFrequency.WEEKLY)
		{
			_updateCheckFrequency.setSelectedItem(checkWeekly);
		}

		if (updateSettings.isRemoteUpdateSite())
		{
			_remoteTypeButton.setSelected(true);
			enableRemoteSite();
		}
		else
		{
			_localTypeButton.setSelected(true);
			enableLocalPath();
		}
		_localPath.setText(updateSettings.getFileSystemUpdatePath());
		updateControlStatus();
		updateUrl();
	}

	void applyChanges(SquirrelPreferences prefs)
	{

		final IUpdateSettings updateSettings = new UpdateSettings();

		updateSettings.setUpdateServer(_updateServerName.getText());
		updateSettings.setUpdateServerPort(_updateServerPort.getText());
		updateSettings.setUpdateServerPath(_updateServerPath.getText());

		UpdateChannelComboBoxEntry channelEntry =
			(UpdateChannelComboBoxEntry) _updateServerChannel.getSelectedItem();

		String channelStr = ChannelType.STABLE.name();
		if (channelEntry.isSnapshot())
		{
			channelStr = ChannelType.SNAPSHOT.name();
		}
		updateSettings.setUpdateServerChannel(channelStr);

		updateSettings.setEnableAutomaticUpdates(_enableAutoUpdateChk.isSelected());

		UpdateCheckFrequencyComboBoxEntry freqEntry =
			(UpdateCheckFrequencyComboBoxEntry) _updateCheckFrequency.getSelectedItem();

		updateSettings.setUpdateCheckFrequency(freqEntry.getUpdateCheckFrequencyEnum().name());
		updateSettings.setRemoteUpdateSite(_remoteTypeButton.isSelected());
		updateSettings.setFileSystemUpdatePath(_localPath.getText());

		prefs.setUpdateSettings(updateSettings);

	}

	private void updateControlStatus()
	{
		final boolean enableAutoCheck = _enableAutoUpdateChk.isSelected();
		_updateCheckFrequency.setEnabled(enableAutoCheck);
	}

	private void createUserInterface()
	{
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		add(createUpdateSitePanel(), gbc);
		++gbc.gridy;
		add(createAutoUpdatePanel(), gbc);

		final ActionListener lis = new MyActionHandler();
		_enableAutoUpdateChk.addActionListener(lis);
	}

	private JPanel createUpdateSitePanel()
	{
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createTitledBorder(i18n.UPDATE_SITE_BORDER_LABEL));

		ItemListener urlUpdateItemListener = new UrlItemListener();
		DocumentListener urlDocumentListener = new UrlDocumentListener();
		final GridBagConstraints gbc = new GridBagConstraints();

		setSeparatorConstraints(gbc, 0);
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		siteTypeLabel = new JLabel(i18n.SITE_TYPE_LABEL, JLabel.RIGHT);
		pnl.add(siteTypeLabel, gbc);

		

		setSeparatorConstraints(gbc, 0);
		gbc.gridx = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(getSiteTypePanel(), gbc);

		setSeparatorConstraints(gbc, 1);
		pnl.add(getSep(), gbc);

		

		setLabelConstraints(gbc, 2);
		_serverLabel = new JLabel(i18n.SERVER, SwingConstants.RIGHT);
		pnl.add(_serverLabel, gbc);

		setFieldConstraints(gbc, 2);
		_updateServerName.getDocument().addDocumentListener(urlDocumentListener);
		pnl.add(_updateServerName, gbc);

		

		setLabelConstraints(gbc, 3);
		_portLabel = new JLabel(i18n.PORT, SwingConstants.RIGHT);
		pnl.add(_portLabel, gbc);

		setFieldConstraints(gbc, 3);
		_updateServerPort.getDocument().addDocumentListener(urlDocumentListener);
		pnl.add(_updateServerPort, gbc);

		

		setLabelConstraints(gbc, 4);
		_pathLabel = new JLabel(i18n.PATH, SwingConstants.RIGHT);
		pnl.add(_pathLabel, gbc);

		setFieldConstraints(gbc, 4);
		_updateServerPath.getDocument().addDocumentListener(urlDocumentListener);
		pnl.add(_updateServerPath, gbc);

		

		setLabelConstraints(gbc, 5);
		_channelLabel = new JLabel(i18n.CHANNEL, SwingConstants.RIGHT);
		pnl.add(_channelLabel, gbc);

		setFieldConstraints(gbc, 5);
		gbc.fill = GridBagConstraints.NONE;
		_updateServerChannel.addItemListener(urlUpdateItemListener);
		pnl.add(_updateServerChannel, gbc);

		

		setLabelConstraints(gbc, 6);
		_urlLabel = new JLabel(i18n.URL, SwingConstants.RIGHT);
		pnl.add(_urlLabel, gbc);

		setFieldConstraints(gbc, 6);
		updateUrl();
		pnl.add(_updateUrl, gbc);

		

		setFieldConstraints(gbc, 7);

		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(_testConnectionButton);
		buttonBox.add(Box.createHorizontalStrut(20));
		buttonBox.add(_testConnectionStatusLabel);
		_testConnectionButton.addActionListener(new TestConnectionButtonListener());
		pnl.add(buttonBox, gbc);

		

		setSeparatorConstraints(gbc, 8);
		pnl.add(getSep(), gbc);

		

		setLabelConstraints(gbc, 9);
		_localPathLabel = new JLabel(i18n.LOCAL_PATH, SwingConstants.RIGHT);
		pnl.add(_localPathLabel, gbc);

		setFieldConstraints(gbc, 9);
		pnl.add(_localPath, gbc);
		return pnl;
	}

	
	private void setLabelConstraints(GridBagConstraints gbc, int gridy)
	{
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.insets = LABEL_INSETS;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
	}

	
	private void setFieldConstraints(GridBagConstraints gbc, int gridy)
	{
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.insets = FIELD_INSETS;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

	}

	
	private void setSeparatorConstraints(GridBagConstraints gbc, int gridy)
	{
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.insets = SEP_INSETS;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
	}

	private void enableRemoteSite()
	{
		_localPath.setEnabled(false);
		_localPathLabel.setEnabled(false);
		_updateServerChannel.setEnabled(true);
		_updateServerName.setEnabled(true);
		_updateServerPath.setEnabled(true);
		_updateServerPort.setEnabled(true);
		_serverLabel.setEnabled(true);
		_portLabel.setEnabled(true);
		_pathLabel.setEnabled(true);
		_channelLabel.setEnabled(true);
		_testConnectionButton.setEnabled(true);
		_testConnectionStatusLabel.setEnabled(true);
		_updateUrl.setEnabled(true);
		_urlLabel.setEnabled(true);
	}

	private void enableLocalPath()
	{
		_localPath.setEnabled(true);
		_localPathLabel.setEnabled(true);
		_updateServerChannel.setEnabled(false);
		_updateServerName.setEnabled(false);
		_updateServerPath.setEnabled(false);
		_updateServerPort.setEnabled(false);
		_serverLabel.setEnabled(false);
		_portLabel.setEnabled(false);
		_pathLabel.setEnabled(false);
		_channelLabel.setEnabled(false);
		_testConnectionButton.setEnabled(false);
		_testConnectionStatusLabel.setEnabled(false);
		_updateUrl.setEnabled(false);
		_urlLabel.setEnabled(false);

	}

	private void updateUrl()
	{

		String portStr = _updateServerPort.getText();
		StringBuilder tmp = new StringBuilder("http://");
		tmp.append(_updateServerName.getText());
		if (!StringUtilities.isEmpty(portStr))
		{
			tmp.append(":");
			tmp.append(_updateServerPort.getText());
		}
		tmp.append("/");
		tmp.append(_updateServerPath.getText());
		tmp.append("/");
		tmp.append(_updateServerChannel.getSelectedItem().toString());
		tmp.append("/release.xml");
		_updateUrl.setText(tmp.toString());
		_updateUrl.setEditable(false);
		_updateUrl.revalidate();
	}

	private JPanel getSiteTypePanel()
	{
		_remoteTypeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				enableRemoteSite();
			}
		});
		_localTypeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				enableLocalPath();
			}
		});
		_updateSiteTypeGroup.add(_remoteTypeButton);
		_updateSiteTypeGroup.add(_localTypeButton);
		JPanel siteTypePanel = new JPanel();
		siteTypePanel.add(_remoteTypeButton);
		siteTypePanel.add(_localTypeButton);
		return siteTypePanel;
	}

	private JSeparator getSep()
	{
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		
		sep.setPreferredSize(new Dimension(100, 20));
		sep.setMinimumSize(new Dimension(100, 20));
		return sep;
	}

	private JPanel createAutoUpdatePanel()
	{
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createTitledBorder(i18n.AUTO_BORDER_LABEL));

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(_enableAutoUpdateChk, gbc);

		gbc.gridwidth = 1;

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.insets = new Insets(4, 20, 4, 10);
		pnl.add(new JLabel(i18n.AUTO_CHECK_FREQUENCY, JLabel.LEFT), gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(4, 0, 4, 0);
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(this._updateCheckFrequency, gbc);

		return pnl;
	}

	private final class MyActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			updateControlStatus();
		}
	}

	
	public void setApplication(IApplication app)
	{
		_app = app;
	}

	private class TestConnectionButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String serverName = _updateServerName.getText();

			StringBuilder path = new StringBuilder(_updateServerPath.getText());
			path.append("/");
			path.append(_updateServerChannel.getSelectedItem().toString());

			String portString = _updateServerPort.getText();

			try
			{
				int port = 80;
				if (!StringUtilities.isEmpty(portString))
				{
					port = Integer.parseInt(_updateServerPort.getText());
				}
				_updateUtil.downloadCurrentRelease(serverName, port, path.toString(),
					UpdateUtil.RELEASE_XML_FILENAME, _app.getSquirrelPreferences().getProxySettings());
				_testConnectionStatusLabel.setText(i18n.STATUS_LABEL_SUCCESS_MSG);
				_testConnectionStatusLabel.setForeground(CONNECTION_SUCCESS_COLOR);
			}
			catch (Exception e1)
			{
				_testConnectionStatusLabel.setText(i18n.STATUS_LABEL_FAILURE_MSG);
				_testConnectionStatusLabel.setForeground(CONNECTION_FAILURE_COLOR);
				_app.showErrorDialog(i18n.CONNECTION_FAILURE_DIALOG_MSG, e1);

			}

		}
	}

	
	private class UrlDocumentListener implements DocumentListener
	{
		public void changedUpdate(DocumentEvent e)
		{
			updateUrl();
		}

		public void insertUpdate(DocumentEvent e)
		{
			updateUrl();

		}

		public void removeUpdate(DocumentEvent e)
		{
			updateUrl();

		}
	}

	private class UrlItemListener implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			updateUrl();
		}
	}
}