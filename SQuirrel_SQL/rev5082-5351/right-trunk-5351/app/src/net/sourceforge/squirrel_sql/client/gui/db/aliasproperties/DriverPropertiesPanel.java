package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DriverPropertiesPanel extends JPanel 
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DriverPropertiesPanel.class);

   
   JCheckBox chkUseDriverProperties = new JCheckBox(s_stringMgr.getString("DriverPropertiesPanel.useDriverProperties"));

   private interface i18n
   {
      String INSTRUCTIONS = s_stringMgr.getString("DriverPropertiesPanel.instructions");
   }

	
	DriverPropertiesTable tbl;

	
	private final MultipleLineLabel _descriptionLbl = new MultipleLineLabel();

	public DriverPropertiesPanel(SQLDriverPropertyCollection props)
	{
		super(new GridBagLayout());
		if (props == null)
		{
			throw new IllegalArgumentException("SQLDriverPropertyCollection == null");
		}

		createUserInterface(props);
	}

	
	public SQLDriverPropertyCollection getSQLDriverProperties()
	{
      TableCellEditor cellEditor = tbl.getCellEditor();
      if(null != cellEditor)
      {
         cellEditor.stopCellEditing();
      }
		return tbl.getTypedModel().getSQLDriverProperties();
	}

	private void createUserInterface(SQLDriverPropertyCollection props)
	{
		tbl = new DriverPropertiesTable(props);

		final GridBagConstraints gbc = new GridBagConstraints();

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 0;
      gbc.weighty = 0;
      add(chkUseDriverProperties, gbc);



      gbc.fill = GridBagConstraints.BOTH;
      gbc.weighty = 1.0;
      gbc.weightx = 1.0;
      ++gbc.gridy;
		JScrollPane sp = new JScrollPane(tbl);
		add(sp, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0.0;
		++gbc.gridy;
		add(createInfoPanel(), gbc);

		tbl.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				updateDescription(tbl.getSelectedRow());
			}
		});

		if (tbl.getRowCount() > 0)
		{
			tbl.setRowSelectionInterval(0, 0);
		}
	}

	private void updateDescription(int idx)
	{
		if (idx != -1)
		{
			String desc = (String)tbl.getValueAt(idx, DriverPropertiesTableModel.IColumnIndexes.IDX_DESCRIPTION);
			_descriptionLbl.setText(desc);
		}
		else
		{
			_descriptionLbl.setText(" ");
		}
	}

	private Box createInfoPanel()
	{
		final Box pnl = Box.createVerticalBox();
		pnl.add(new JSeparator());
		pnl.add(_descriptionLbl);
		pnl.add(new JSeparator());
		pnl.add(new MultipleLineLabel(i18n.INSTRUCTIONS));

		return pnl;
	}
}

