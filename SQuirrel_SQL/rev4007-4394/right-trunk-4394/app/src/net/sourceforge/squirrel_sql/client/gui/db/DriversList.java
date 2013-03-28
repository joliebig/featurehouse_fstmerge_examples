package net.sourceforge.squirrel_sql.client.gui.db;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

public class DriversList extends BaseList implements IDriversList
{
   private static final String PREF_KEY_SELECTED_DRIVER_INDEX = "Squirrel.selDriverIndex";


   
	private IApplication _app;

	
	private DriversListModel _model;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DriversList.class);    


   
	public DriversList(IApplication app) throws IllegalArgumentException
	{
      super(new DriversListModel(app), app);
		_app = app;
		_model = (DriversListModel) getList().getModel();

      getList().setLayout(new BorderLayout());

		SquirrelResources res = _app.getResources();
		getList().setCellRenderer(new DriverListCellRenderer(res.getIcon("list.driver.found"),res.getIcon("list.driver.notfound")));

		propertiesChanged(null);


		_app.getSquirrelPreferences().addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				final String propName = evt != null ? evt.getPropertyName() : null;
				propertiesChanged(propName);
			}
		});
	}

	
	public void addNotify()
	{
		getList().addNotify();
		
		
		ToolTipManager.sharedInstance().registerComponent(getList());
	}

	
	public void removeNotify()
	{
		getList().removeNotify();
		
		ToolTipManager.sharedInstance().unregisterComponent(getList());
	}

	
	public DriversListModel getTypedModel()
	{
		return _model;
	}

	
	public ISQLDriver getSelectedDriver()
	{
		return (ISQLDriver)getList().getSelectedValue();
	}

	
	public String getToolTipText(MouseEvent evt)
	{
		String tip = null;
		final int idx = getList().locationToIndex(evt.getPoint());
		if (idx != -1)
		{
			tip = ((ISQLDriver)getList().getModel().getElementAt(idx)).getName();
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

   public String getSelIndexPrefKey()
   {
      return PREF_KEY_SELECTED_DRIVER_INDEX;
   }
}

