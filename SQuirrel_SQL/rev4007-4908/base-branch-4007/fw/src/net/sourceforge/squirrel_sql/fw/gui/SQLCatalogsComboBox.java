package net.sourceforge.squirrel_sql.fw.gui;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

public class SQLCatalogsComboBox extends JComboBox
{
    private static final long serialVersionUID = 1L;

    
	public SQLCatalogsComboBox()
	{
		super();
	}

    
    public void setCatalogs(String[] catalogs, String selectedCatalog) {
        super.removeAllItems();
        if (catalogs != null)
        {
            final Map<String, String> map = new TreeMap<String, String>();
            for (int i = 0; i < catalogs.length; ++i)
            {
                map.put(catalogs[i], catalogs[i]);
            }
            for (Iterator<String> it = map.values().iterator(); it.hasNext();)
            {
                addItem(it.next());
            }
            if (selectedCatalog != null)
            {
                setSelectedCatalog(selectedCatalog);
            }
        }
        setMaximumSize(getPreferredSize());
        
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
		return (String) getSelectedItem();
	}

	public void setSelectedCatalog(String selectedCatalog)
	{
		if (selectedCatalog != null)
		{
			getModel().setSelectedItem(selectedCatalog);
		}
	}
}
