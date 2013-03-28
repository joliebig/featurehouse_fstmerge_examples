package net.sourceforge.squirrel_sql.plugins.sessionscript;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;

public class ViewSessionScriptsPanel extends JPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ViewSessionScriptsPanel.class);


	private SessionScriptPlugin _plugin;
	private IApplication _app;
	private SQLALiasesCombo _aliasesCmb = new SQLALiasesCombo();
	private JTextArea _sqlEntry = new JTextArea();
	private JButton _saveBtn;

	ViewSessionScriptsPanel(SessionScriptPlugin plugin, IApplication app)
	{
		super();
		if (plugin == null)
		{
			throw new IllegalArgumentException("SessionScriptPlugin == null");
		}
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_plugin = plugin;
		_app = app;

		createUserInterface();
		refreshScript();
	}

	private void refreshScript()
	{
		boolean setText = false;
		ISQLAlias alias = _aliasesCmb.getSelectedSQLAlias();
		if (alias != null)
		{
			AliasScript script = _plugin.getScriptsCache().get(alias);
			String sql = script.getSQL();
			if (sql != null && sql.length() > 0)
			{
				_sqlEntry.setText(sql);
				setText = true;
			}
		}
		if (!setText)
		{
			_sqlEntry.setText("");
		}
		_saveBtn.setEnabled(false);
	}

	private void createUserInterface()
	{
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		_aliasesCmb.load(_app);
		_aliasesCmb.addActionListener(new AliasesComboListener(this));

		_sqlEntry.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		_sqlEntry.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent evt)
			{
				_saveBtn.setEnabled(true);
			}
			public void insertUpdate(DocumentEvent evt)
			{
				_saveBtn.setEnabled(true);
			}
			public void removeUpdate(DocumentEvent evt)
			{
				_saveBtn.setEnabled(true);
			}
		});

		
		_saveBtn = new JButton(s_stringMgr.getString("sessionscript.Save"));
		_saveBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				synchronized (_sqlEntry)
				{
					ISQLAlias alias = _aliasesCmb.getSelectedSQLAlias();
					if (alias != null)
					{
						AliasScript script = _plugin.getScriptsCache().get(alias);
						script.setSQL(_sqlEntry.getText());
						_saveBtn.setEnabled(false);
					}
				}
			}
		});

		setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(_aliasesCmb, gbc);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		++gbc.gridy;
		add(new JScrollPane(_sqlEntry), gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		gbc.weighty = 0;
		++gbc.gridx;
		add(_saveBtn, gbc);
	}

	
	private static final class AliasesComboListener implements ActionListener
	{
		private ViewSessionScriptsPanel _pnl;

		AliasesComboListener(ViewSessionScriptsPanel pnl)
		{
			super();
			_pnl = pnl;
		}

		public void actionPerformed(ActionEvent evt)
		{
			_pnl.refreshScript();
		}

	}
}
