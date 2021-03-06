package net.sourceforge.squirrel_sql.client.session.sqlfilter;


import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionDialogWidget;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ContentsTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.List;

public class SQLFilterSheet extends SessionDialogWidget
{
    private static final long serialVersionUID = 1L;

    
	private static final ILogger s_log =
		LoggerController.createLogger(SQLFilterSheet.class);

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLFilterSheet.class);
    
    private static final String TITLE = 
        s_stringMgr.getString("SQLFilterSheet.title");
    
	
	private final IObjectTreeAPI _objectTree;

	
	transient private final IDatabaseObjectInfo _objectInfo;

	
	private List<ISQLFilterPanel> _panels = new ArrayList<ISQLFilterPanel>();

	
	private int _tabSelected;

	
	private JLabel _titleLbl = new JLabel();

	
	private JButton _clearFilter = new JButton();

	
	transient private WhereClausePanel _whereClausePanel = null;

	
	transient private OrderByClausePanel _orderByClausePanel = null;

	
	public SQLFilterSheet(IObjectTreeAPI objectTree,
							IDatabaseObjectInfo objectInfo)
	{
		super(TITLE, true, objectTree.getSession());
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}
		_objectTree = objectTree;
		_objectInfo = objectInfo;

		createGUI();
	}

	
	public synchronized void setVisible(boolean show)
	{
		boolean reallyShow = true;

		if (show)
		{
			if (!isVisible())
			{
				ContentsTab tab =
					(ContentsTab)_objectTree.getTabbedPaneIfSelected(
											_objectInfo.getDatabaseObjectType(),
											ContentsTab.getContentsTabTitle());

            if (tab == null)
				{
					reallyShow = false;
                    
                    String msg = 
                        s_stringMgr.getString("SQLFilterSheet.contentsMsg");
					_objectTree.getSession().showMessage(msg);
				}
				else
				{
					final boolean isDebug = s_log.isDebugEnabled();
					long start = 0;
					for (Iterator<ISQLFilterPanel> it = _panels.iterator(); it.hasNext();)
					{
						ISQLFilterPanel pnl = it.next();
						if (isDebug)
						{
							start = System.currentTimeMillis();
						}
	
						pnl.initialize(tab.getSQLFilterClauses());
						if (isDebug)
						{
							s_log.debug("Panel " + pnl.getTitle()
									+ " initialized in "
									+ (System.currentTimeMillis() - start) + "ms");
						}
					}
					pack();
					
					Dimension d = getSize();
					d.width += 5;
					d.height += 5;
					setSize(d);
					
					DialogWidget.centerWithinDesktop(this);
					moveToFront();
				}
			}
		}

		if (!show || reallyShow)
		{
			super.setVisible(show);
		}
	}

	
	public void setTitle(String title)
	{
      if(null != _titleLbl)
      {
         
         
		   _titleLbl.setText(title + ": " + _objectInfo.getSimpleName());
      }
	}

	
	private void performClose()
	{
		dispose();
	}

	public IDatabaseObjectInfo getDatabaseObjectInfo()
	{
		return _objectInfo;
	}

	public IObjectTreeAPI getObjectTree()
	{
		return _objectTree;
	}

	
	private void performOk()
	{
		final boolean isDebug = s_log.isDebugEnabled();
		long start = 0;
		for (Iterator<ISQLFilterPanel> it = _panels.iterator(); it.hasNext();)
		{
			ISQLFilterPanel pnl = it.next();
			if (isDebug)
			{
				start = System.currentTimeMillis();
			}
			pnl.applyChanges();
			if (isDebug)
			{
				s_log.debug("Panel " + pnl.getTitle() + " applied changes in "
						+ (System.currentTimeMillis() - start) + "ms");
			}
		}
		try
		{
			ContentsTab cTab =
				(ContentsTab)_objectTree.getTabbedPaneIfSelected(
											_objectInfo.getDatabaseObjectType(),
											 ContentsTab.getContentsTabTitle());
         if (cTab != null)
			{
				cTab.refreshComponent();
			}
		}
		catch (DataSetException ex)
		{
			getSession().showErrorMessage(ex);
		}

		dispose();
	}

	
	private void createGUI()
	{
		SortedSet<String> columnNames = new TreeSet<String>();
		Map<String, Boolean> textColumns = new TreeMap<String, Boolean>();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(getTitle());

		
		makeToolWindow(true);

		try
		{
			ISQLConnection sqlConnection = getSession().getSQLConnection();
            SQLDatabaseMetaData md = sqlConnection.getSQLMetaData();
            TableColumnInfo[] infos = md.getColumnInfo((ITableInfo)_objectInfo);
            for (int i = 0; i < infos.length; i++) {
                String columnName = infos[i].getColumnName();
                int dataType = infos[i].getDataType();
                columnNames.add(columnName);
                if ((dataType == Types.CHAR)
                        || (dataType == Types.CLOB)
                        || (dataType == Types.LONGVARCHAR)
                        || (dataType == Types.VARCHAR))
                {
                    textColumns.put(columnName, Boolean.TRUE);
                }
                
            }
		}
		catch (SQLException ex)
		{
            
            String msg = 
                s_stringMgr.getString("SQLFilterSheet.error.columnList",
                                      ex);
			getSession().getApplication().showErrorDialog(msg);
		}

		_whereClausePanel =
		    new WhereClausePanel(columnNames, textColumns, _objectInfo.getQualifiedName());
		_orderByClausePanel =
			new OrderByClausePanel(columnNames, _objectInfo.getQualifiedName());
		_panels.add(_whereClausePanel);
		_panels.add(_orderByClausePanel);

		JTabbedPane tabPane = UIFactory.getInstance().createTabbedPane();
		for (Iterator<ISQLFilterPanel> it = _panels.iterator(); it.hasNext();)
		{
			ISQLFilterPanel pnl = it.next();
			String pnlTitle = pnl.getTitle();
			String hint = pnl.getHint();
			final JScrollPane sp = new JScrollPane(pnl.getPanelComponent());
			sp.setBorder(BorderFactory.createEmptyBorder());
			tabPane.addTab(pnlTitle, null, sp, hint);
		}

		tabPane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent event)
			{
				setButtonLabel(
					((JTabbedPane)event.getSource()).getSelectedIndex());
			}
		});

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
		setButtonLabel(0);
		_tabSelected = 0;
		_clearFilter.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clearFilter();
			}
		});
		contentPane.add(_clearFilter);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		++gbc.gridy;
		gbc.weighty = 1;
		contentPane.add(tabPane, gbc);

		++gbc.gridy;
		gbc.gridwidth = 2;
		gbc.weighty = 0;
		contentPane.add(createButtonsPanel(), gbc);
	}

	
	private void clearFilter()
	{
		if (_tabSelected == 0)
		{
			_whereClausePanel.clearFilter();
		}
		else
		{
			_orderByClausePanel.clearFilter();
		}
	}

	
	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();
        
		String okLabel = s_stringMgr.getString("SQLFilterSheet.okButtonLabel");
		JButton okBtn = new JButton(okLabel);
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
        
        String closeLabel = 
            s_stringMgr.getString("SQLFilterSheet.closeButtonLabel");
		JButton closeBtn = new JButton(closeLabel);
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		pnl.add(okBtn);
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	
	private void setButtonLabel(int tabSelected)
	{
        String title = null;
        if (tabSelected == 0) {
            title = _whereClausePanel.getTitle();
        } else {
            title = _orderByClausePanel.getTitle();
        }
        
        String label = 
            s_stringMgr.getString("SQLFilterSheet.clearButtonLabel", title);
        _clearFilter.setText(label);
        _tabSelected = tabSelected;
	}
}
