package net.sourceforge.squirrel_sql.client.gui.db;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.sourceforge.squirrel_sql.fw.gui.ModifiedDefaultListCellRenderer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;



public class AliasesList extends JList implements IAliasesList
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AliasesList.class);

	
	private final IApplication _app;

	
	private final AliasesListModel _model;

	public AliasesList(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_model = new AliasesListModel(_app);
		setModel(_model);
		setLayout(new BorderLayout());
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		setCellRenderer(new ModifiedDefaultListCellRenderer());

		final int selAliasIdx = app.getSquirrelPreferences().getAliasesSelectedIndex();
		final int size = getModel().getSize();
		if (selAliasIdx > -1 && selAliasIdx < size)
		{
			setSelectedIndex(selAliasIdx);
		}
		else
		{
			setSelectedIndex(0);
		}

		_model.addListDataListener(new ListDataListener()
		{
			public void contentsChanged(ListDataEvent evt)
			{
				
			}
			public void intervalAdded(ListDataEvent evt)
			{
				final int idx = evt.getIndex0();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						clearSelection();
						setSelectedIndex(idx);
					}
				});
			}
			public void intervalRemoved(ListDataEvent evt)
			{
				final int idx = evt.getIndex0();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						clearSelection();
						int modelSize = getModel().getSize();
						if (idx < modelSize)
						{
							setSelectedIndex(idx);
						}
						else if (modelSize > 0)
						{
							setSelectedIndex(size - 1);
						}
					}
				});
			}
		});
	}

	
	public void addNotify()
	{
		super.addNotify();
		
		
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	
	public void removeNotify()
	{
		super.removeNotify();
		
		ToolTipManager.sharedInstance().unregisterComponent(this);
	}

	
	public AliasesListModel getTypedModel()
	{
		return _model;
	}

	
	public SQLAlias getSelectedAlias()
	{
		return (SQLAlias)getSelectedValue();
	}

	
	public String getToolTipText(MouseEvent evt)
	{
		String tip = null;
		final int idx = locationToIndex(evt.getPoint());
		if (idx != -1)
		{
			tip = ((ISQLAlias)getModel().getElementAt(idx)).getName();
		}
		else
		{
			tip = getToolTipText();
		}
		return tip;
	}

	
	public String getToolTipText()
	{
		return s_stringMgr.getString("AliasesList.tooltip");
	}
}
