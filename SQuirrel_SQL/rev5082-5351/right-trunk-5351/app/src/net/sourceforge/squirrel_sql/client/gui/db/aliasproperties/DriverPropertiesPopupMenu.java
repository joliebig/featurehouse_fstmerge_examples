
package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class DriverPropertiesPopupMenu extends JPopupMenu
{
	private static final long serialVersionUID = -8109748449852223185L;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DriverPropertiesPopupMenu.class);

	private static interface i18n
	{
		
		String addPropertyLabel = s_stringMgr.getString("DriverPropertiesPopupMenu.addPropertyLabel");
		
		
		String removePropertyLabel = s_stringMgr.getString("DriverPropertiesPopupMenu.removePropertyLabel");
	}

	private final DriverPropertiesTable driverPropertiesTable;

	public DriverPropertiesPopupMenu(DriverPropertiesTable table)
	{
		this.driverPropertiesTable = table;
		JMenuItem addPropertyMenuItem = new JMenuItem(i18n.addPropertyLabel);
		addPropertyMenuItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				AddDriverPropertyDialog dialog = new AddDriverPropertyDialog(driverPropertiesTable);
				GUIUtils.centerWithinParent(dialog);
				dialog.setVisible(true);
			}

		});
		JMenuItem removePropertyMenuItem = new JMenuItem(i18n.removePropertyLabel);
		removePropertyMenuItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				int selectedRowIndex = driverPropertiesTable.getSelectedRow();
				String propertyName = (String) driverPropertiesTable.getValueAt(selectedRowIndex, 0);
				driverPropertiesTable.removeProperty(propertyName);
			}

		});
		add(addPropertyMenuItem);
		add(removePropertyMenuItem);
	}

	
	public void show(Component invoker, int x, int y)
	{
		super.show(invoker, x, y);
	}
}
