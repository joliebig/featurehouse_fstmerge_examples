package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import net.sourceforge.squirrel_sql.fw.gui.MemoryComboBox;

public class SQLHistoryComboBox extends MemoryComboBox
{
	public SQLHistoryComboBox(boolean useSharedModel)
	{
		super();
		setModel(new SQLHistoryComboBoxModel(useSharedModel));
		setRenderer(new Renderer());

		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateToolTip();
			}
		});
	}

	public SQLHistoryComboBoxModel getTypedModel()
	{
		return (SQLHistoryComboBoxModel)getModel();
	}

	public void setUseSharedModel(boolean use)
	{
		getTypedModel().setUseSharedModel(use);
	}

	public boolean isUsingSharedDataModel()
	{
		return getTypedModel().isUsingSharedDataModel();
	}

	
	private void updateToolTip()
	{
		String tt = " ";
		SQLHistoryItem shi = (SQLHistoryItem)getSelectedItem();
		if (shi != null)
		{
			tt = formatToolTip(shi.getSQL());
		}
		Component[] comps = SQLHistoryComboBox.this.getComponents();
		for (int i = 0; i < comps.length; ++i)
		{
			if (comps[i] instanceof JComponent)
			{
				((JComponent)comps[i]).setToolTipText(tt);
			}
		}
	}

	private String formatToolTip(String tt)
	{
      final StringBuffer buf = new StringBuffer();
      buf.append("<HTML><PRE>");

      if (200 < tt.length())
      {
         buf.append(tt.substring(0, 200)).append(" ...");
      }
      else
      {
         buf.append(tt);
      }

      buf.append("</PRE></HTML>");
      return buf.toString();
   }

	public void dispose()
	{
		
		
		setModel(new DefaultComboBoxModel());
	}

	
	private final class Renderer extends BasicComboBoxRenderer
	{
		public Component getListCellRendererComponent(JList list,
																	 Object value, int index, boolean isSelected,
																	 boolean cellHasFocus)
		{
			if (isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
				if (index != -1)
				{
					final String tt = ((SQLHistoryItem)value).getSQL();
					final String text = formatToolTip(tt);
					list.setToolTipText(text);
				}
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setFont(list.getFont());
			setText((value == null) ? "" : value.toString());

			return this;
		}
	}
}
