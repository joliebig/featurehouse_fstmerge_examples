package net.sourceforge.squirrel_sql.fw.gui;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SQLCatalogsComboBox extends JComboBox
{
    private static final long serialVersionUID = 1L;

 	
 	private static final StringManager s_stringMgr =
 		StringManagerFactory.getStringManager(SQLCatalogsComboBox.class);
 	
 	private interface i18n {
 		
 		String NONE_LABEL = s_stringMgr.getString("SQLCatalogsComboBox.noneLabel");
 	}
 	
    
	public SQLCatalogsComboBox()
	{
		super();
	}

   
   public void setCatalogs(String[] catalogs, String selectedCatalog)
	{
		super.removeAllItems();
		if (catalogs != null)
		{
			final Map<String, String> map = new TreeMap<String, String>();
			for (String catalog : catalogs)
			{
				if (!isEmptyCatalog(catalog))
				{
					map.put(catalog, catalog);
				}
			}
			if (isEmptyCatalog(selectedCatalog))
			{
				addItem(new NoCatalogPlaceHolder());
			}
			for (String catalog : map.values())
			{
				addItem(catalog);
			}
			if (!isEmptyCatalog(selectedCatalog))
			{
				setSelectedCatalog(selectedCatalog);
			}
		}
		setMaximumSize(getPreferredSize());
	}
    
    private boolean isEmptyCatalog(String catalog) {
   	 return catalog == null || "".equals(catalog);
    }
    
	
	public void setConnection(ISQLConnection conn) throws SQLException
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}
		final SQLDatabaseMetaData md = conn.getSQLMetaData();
		if (md.supportsCatalogs())
		{
			final String[] catalogs = md.getCatalogs();
			if (catalogs != null)
			{
				setCatalogs(catalogs, conn.getCatalog());
			}
		}
	}

	public String getSelectedCatalog()
	{
		return getSelectedItem().toString();
	}
		
	public void setSelectedCatalog(String selectedCatalog)
	{
		if (selectedCatalog != null)
		{
			getModel().setSelectedItem(selectedCatalog);
		}
	}
	
	
	@Override
	public void setSelectedItem(Object o) {
		super.setSelectedItem(o);
		
		
		
		if (super.getItemAt(0) instanceof NoCatalogPlaceHolder) {
			super.removeItemAt(0);
			validate();
		}		
	}	
	
	
	private class NoCatalogPlaceHolder {
		public String toString() { 
			return i18n.NONE_LABEL; 
		}
	}
}
