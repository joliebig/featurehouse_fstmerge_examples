package net.sourceforge.squirrel_sql.client.session.properties;


import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionDialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ContentsTab;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.SortedSet;
import java.util.TreeSet;


public class EditWhereColsSheet extends SessionDialogWidget
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(EditWhereColsSheet.class);


	
	private interface i18n
	{
		
		
		String TITLE = s_stringMgr.getString("editWhereColsSheet.editWhereColumns");
	}

	
	private static final ILogger s_log =
		LoggerController.createLogger(EditWhereColsSheet.class);

	
	private IDatabaseObjectInfo _objectInfo;

	
	private JLabel _titleLbl = new JLabel();

	
	private EditWhereColsPanel _editWhereColsPanel = null;

	
	public EditWhereColsSheet(ISession session, IDatabaseObjectInfo objectInfo)
	{
		super(i18n.TITLE, true, session);
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("Null IDatabaseObjectInfo passed");
		}

		_objectInfo = objectInfo;
		createGUI();
	}

	
	public synchronized void setVisible(boolean show)
	{
		if (show)
		{
			if (!isVisible())
			{
				final boolean isDebug = s_log.isDebugEnabled();
				long start = 0;

				if (isDebug)
				{
					start = System.currentTimeMillis();
				}

				if (isDebug)
				{
					s_log.debug("Panel " + _editWhereColsPanel.getTitle()
						+ " initialized in "
						+ (System.currentTimeMillis() - start) + "ms");
				}

				pack();
				
				Dimension d = getSize();
				d.width += 5;
				d.height += 5;
				setSize(d);
				
				DialogWidget.centerWithinDesktop(this);
			}
			moveToFront();
		}
		super.setVisible(show);
	}











	
	private void performClose()
	{
		dispose();
	}

	public IDatabaseObjectInfo getDatabaseObjectInfo()
	{
		return _objectInfo;
	}

	
	private void performReset()
	{
		_editWhereColsPanel.reset();
	}

	
	private void performOk()
	{
		
		
		if (_editWhereColsPanel.ok())
			dispose();
	}

	
	private void createGUI()
	{
		SortedSet<String> columnNames = new TreeSet<String>();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		
		makeToolWindow(true);

		final ISession session = getSession();

		try
		{
			final ISQLConnection conn = session.getSQLConnection();
				TableColumnInfo[] infos = conn.getSQLMetaData().getColumnInfo((ITableInfo)_objectInfo);
				for (int i = 0; i < infos.length; i++) {
					 TableColumnInfo info = infos[i];
					 columnNames.add(info.getColumnName());
				}
		}
		catch (SQLException ex)
		{
			session.getApplication().showErrorDialog(
				
				s_stringMgr.getString("editWhereColsSheet.unableToEdit", ex));
		}
		String unambiguousname = 
            ContentsTab.getUnambiguousTableName(session, 
                                                _objectInfo.getQualifiedName());
		_editWhereColsPanel =
			new EditWhereColsPanel(session,
                                   (ITableInfo)_objectInfo,
                                   columnNames,  
                                   unambiguousname);

		final JScrollPane sp = new JScrollPane(_editWhereColsPanel);
		sp.setBorder(BorderFactory.createEmptyBorder());

		_titleLbl.setText(getTitle() + ": " + _objectInfo.getSimpleName());


		final JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 1;

		gbc.gridx = 0;
		gbc.gridy = 0;

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		contentPane.add(_titleLbl, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = GridBagConstraints.REMAINDER;

		
		gbc.gridx = 0;
		++gbc.gridy;
		contentPane.add(new JLabel(" "), gbc);

		gbc.gridx = 0;
		++gbc.gridy;
		contentPane.add(
			
			new JLabel(s_stringMgr.getString("editWhereColsSheet.limitSizeOfWhereClause")), gbc);
		gbc.gridx = 0;
		++gbc.gridy;
		contentPane.add(
			
			new JLabel(s_stringMgr.getString("editWhereColsSheet.shouldIncludePKs")), gbc);

		
		gbc.gridx = 0;
		++gbc.gridy;
		contentPane.add(new JLabel(" "), gbc);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		++gbc.gridy;
		gbc.weighty = 1;
		contentPane.add(_editWhereColsPanel, gbc);

		++gbc.gridy;
		gbc.gridwidth = 2;
		gbc.weighty = 0;
		contentPane.add(createButtonsPanel(), gbc);
	}

	
	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		
		JButton okBtn = new JButton(s_stringMgr.getString("editWherColsSheet.ok"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		
		JButton resetBtn = new JButton(s_stringMgr.getString("editWherColsSheet.reset"));
		resetBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performReset();
			}
		});
		
		JButton closeBtn = new JButton(s_stringMgr.getString("editWherColsSheet.close"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		pnl.add(okBtn);
		pnl.add(resetBtn);
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, resetBtn, closeBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}
}
