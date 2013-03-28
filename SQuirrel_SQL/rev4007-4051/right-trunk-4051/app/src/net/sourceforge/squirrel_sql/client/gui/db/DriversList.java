package net.sourceforge.squirrel_sql.client.gui.db;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

public class DriversList extends JList implements IDriversList
{
	
	private IApplication _app;

	
	private DriversListModel _model;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DriversList.class);    
    
	
	public DriversList(IApplication app) throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_model = new DriversListModel(_app);
		setModel(_model);
		setLayout(new BorderLayout());
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		SquirrelResources res = _app.getResources();
		setCellRenderer(new DriverListCellRenderer(res.getIcon("list.driver.found"),res.getIcon("list.driver.notfound")));

		propertiesChanged(null);

		final int selDriverIdx = app.getSquirrelPreferences().getDriversSelectedIndex();
		final int size = getModel().getSize();
		if (selDriverIdx > -1 && selDriverIdx < size)
		{
			setSelectedIndex(selDriverIdx);
		}
		else
		{
			setSelectedIndex(0);
		}

		_app.getSquirrelPreferences().addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				final String propName = evt != null ? evt.getPropertyName() : null;
				propertiesChanged(propName);
			}
		});

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
					public void run() {
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
							setSelectedIndex(modelSize - 1);
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

	
	public DriversListModel getTypedModel()
	{
		return _model;
	}

	
	public ISQLDriver getSelectedDriver()
	{
		return (ISQLDriver)getSelectedValue();
	}

	
	public String getToolTipText(MouseEvent evt)
	{
		String tip = null;
		final int idx = locationToIndex(evt.getPoint());
		if (idx != -1)
		{
			tip = ((ISQLDriver)getModel().getElementAt(idx)).getName();
		}
		else
		{
			tip = getToolTipText();
		}
		return tip;
	}

	
	public String getToolTipText()
	{
        
		return s_stringMgr.getString("DriversList.tooltiptext");
	}

	
	private void propertiesChanged(String propName)
	{
		if (propName == null
			|| propName.equals(SquirrelPreferences.IPropertyNames.SHOW_LOADED_DRIVERS_ONLY))
		{
			boolean show = _app.getSquirrelPreferences().getShowLoadedDriversOnly();
			_model.setShowLoadedDriversOnly(show);
		}
	}
}

