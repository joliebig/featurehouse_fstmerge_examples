package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ModifiedDefaultListCellRenderer extends DefaultListCellRenderer
{
	public Component getListCellRendererComponent(JList list,
												Object value,
												int index,
												boolean isSelected,
												boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
  		setOpaque(isSelected);
  		return this;
	}
}
