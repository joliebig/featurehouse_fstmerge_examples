package net.sourceforge.squirrel_sql.plugins.sqlval;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

class AppPreferencesPanel extends JPanel
{
   private static final long serialVersionUID = 1L;

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AppPreferencesPanel.class);

	
	private static final String INFO = s_stringMgr.getString("sqlval.info");

	
	private final WebServicePreferences _prefs;

	

	
	private JCheckBox _anonLogonChk = new JCheckBox(s_stringMgr.getString("sqlval.anonymous"));

	

	
	private JCheckBox _anonClientChk = new JCheckBox(s_stringMgr.getString("sqlval.anonymous2"));

	
	private JTextField _userNameText = new JTextField();

	
	private JPasswordField _passwordText = new JPasswordField();

	
	private OutputLabel _clientNameLbl = new OutputLabel(" ");

	
	private OutputLabel _clientVersionLbl = new OutputLabel(" ");

	AppPreferencesPanel(WebServicePreferences prefs)
	{
		super(new GridBagLayout());

		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}

		_prefs = prefs;
		createGUI();
		loadData();
	}

	void loadData()
	{
		_anonLogonChk.setSelected(_prefs.getUseAnonymousLogon());
		_userNameText.setText(_prefs.getUserName());
		_passwordText.setText(_prefs.retrievePassword());
		_anonClientChk.setSelected(_prefs.getUseAnonymousClient());
		_clientNameLbl.setText(_prefs.getClientName());
		_clientVersionLbl.setText(_prefs.getClientVersion());

		setControlState();
	}

	
	void save()
	{
		_prefs.setUseAnonymousLogon(_anonLogonChk.isSelected());
		_prefs.setUserName(_userNameText.getText());
		_prefs.setPassword(new String(_passwordText.getPassword()));
		_prefs.setUseAnonymousClient(_anonClientChk.isSelected());
		_prefs.setClientName(_clientNameLbl.getText());
		_prefs.setClientVersion(_clientVersionLbl.getText());
	}

	
	private void setControlState()
	{
		setAnonymousUserControlState(_prefs.getUseAnonymousLogon());
	}

	
	private void setAnonymousUserControlState(boolean state)
	{
		_userNameText.setEnabled(!state);
		_passwordText.setEnabled(!state);
	}

	
	private void createGUI()
	{
		setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(1, 4, 1, 4);
		gbc.weightx = 1;

		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(createInfoPanel(), gbc);

		gbc.weighty = 0;
		++gbc.gridy;
		add(createLogonPanel(), gbc);

		++gbc.gridy;
		add(createClientPanel(), gbc);
	}

	
	private JPanel createInfoPanel()
	{
		final JPanel pnl = new JPanel();
		
		pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("sqlval.infoBorder")));
		pnl.setLayout(new BorderLayout());
		final MultipleLineLabel lbl = new MultipleLineLabel(INFO);
		lbl.setCaretPosition(0);
		lbl.setRows(3);
		lbl.setColumns(30);
		final JScrollPane sp = new JScrollPane(lbl);
		sp.setBorder(BorderFactory.createEmptyBorder());
		pnl.add(sp, BorderLayout.CENTER);
		return pnl;
	}

	
	private JPanel createLogonPanel()
	{
		_userNameText.setColumns(15);
		_passwordText.setColumns(15);

		JPanel pnl = new JPanel();
		
		pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("sqlval.loOnAs")));

		pnl.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 4, 2, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		pnl.add(_anonLogonChk, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		++gbc.gridx;
		
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.user"), JLabel.RIGHT), gbc);

		++gbc.gridy;
		
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.pwdPref"), JLabel.RIGHT), gbc);

		gbc.fill = GridBagConstraints.NONE;
		++gbc.gridx;
		gbc.gridy = 0;
		gbc.weightx = 0;
		pnl.add(_userNameText, gbc);

		++gbc.gridy;
		pnl.add(_passwordText, gbc);

		_anonLogonChk.addActionListener(new AnonymousCheckBoxListener());

		return pnl;
	}

	
	private JPanel createClientPanel()
	{
		JPanel pnl = new JPanel();
		
		pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("sqlval.clientBorder")));

		pnl.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 4, 2, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		pnl.add(_anonClientChk, gbc);

		gbc.gridwidth = 1;
		++gbc.gridy;
		
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.clientLogon"), JLabel.RIGHT), gbc);

		++gbc.gridy;
		
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.version"), JLabel.RIGHT), gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		pnl.add(_clientNameLbl, gbc);

		++gbc.gridy;
		pnl.add(_clientVersionLbl, gbc);

		return pnl;
	}

	
	private final class AnonymousCheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			setAnonymousUserControlState(_anonLogonChk.isSelected());
		}
	}
}

