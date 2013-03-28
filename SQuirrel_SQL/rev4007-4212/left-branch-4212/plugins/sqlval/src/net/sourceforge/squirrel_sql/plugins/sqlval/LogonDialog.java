package net.sourceforge.squirrel_sql.plugins.sqlval;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlval.cmd.ConnectCommand;
import net.sourceforge.squirrel_sql.plugins.sqlval.cmd.DisconnectCommand;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class LogonDialog extends JDialog
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(LogonDialog.class);


	
	private final static ILogger s_log =
		LoggerController.createLogger(LogonDialog.class);

	
	private final ISession _session;

	private final WebServicePreferences _prefs;

	private final WebServiceSessionProperties _sessionProps;

	
	private AppPreferencesPanel _appPrefsPnl;

	
	private SessionSettingsPanel _confirmPnl;

	public LogonDialog(ISession session, WebServicePreferences prefs,
				WebServiceSessionProperties sessionProps)
	{
		
		super(session.getApplication().getMainFrame(), s_stringMgr.getString("sqlval.logon"), true);

		if (session == null)
		{
			throw new IllegalArgumentException("ISession = null");
		}
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences = null");
		}
		if (sessionProps == null)
		{
			throw new IllegalArgumentException("WebServiceSessionProperties = null");
		}

		_session = session;
		_prefs = prefs;
		_sessionProps = sessionProps;

		createGUI();

		
		try
		{
			new DisconnectCommand(_session, _prefs, _sessionProps).execute();
		}
		catch (BaseException ex)
		{
			s_log.error(ex);
		}
	}

	
	private void performClose()
	{
		dispose();
	}

	
	private void performOk()
	{
		_appPrefsPnl.save();
		_confirmPnl.save();

		
		ConnectCommand cmd = new ConnectCommand(_session, _prefs, _sessionProps);
		try
		{
			cmd.execute();
			dispose();
		}
		catch (Throwable th)
		{
			final String msg = "Error occured when talking to the web service";
			s_log.error(msg, th);
			_session.getApplication().showErrorDialog(msg, th);
		}
	}

	
	private void createGUI()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder());
		setContentPane(contentPane);

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(1, 4, 1, 4);
		gbc.weightx = 1;

		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		_appPrefsPnl = new AppPreferencesPanel(_prefs);
		contentPane.add(_appPrefsPnl, gbc);

		gbc.weighty = 0;
		++gbc.gridy;
		_confirmPnl = new SessionSettingsPanel(_prefs, _sessionProps);
		contentPane.add(_confirmPnl, gbc);

		++gbc.gridy;
		contentPane.add(createButtonsPanel(), gbc);

		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(true);
	}

	
	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		
		JButton okBtn = new JButton(s_stringMgr.getString("sqlval.logonOk"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		
		JButton closeBtn = new JButton(s_stringMgr.getString("sqlval.logonClose"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn });

		pnl.add(okBtn);
		pnl.add(closeBtn);

		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}
}

