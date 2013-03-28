package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.action.MakeEditableCommand;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;


public class DataSetViewerTextPanel extends BaseDataSetViewerDestination
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataSetViewerTextPanel.class);


	private final static int COLUMN_PADDING = 2;

	private MyJTextArea _outText = null;
	private int _rowCount;

	public DataSetViewerTextPanel()
	{
		super();
		_rowCount = 0;
	}

	public void init(IDataSetUpdateableModel updateableObject)
	{
		_outText = new MyJTextArea(updateableObject);
	}

	public void clear()
	{
		_outText.setText("");
		_rowCount = 0;
	}

	public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
	{
		super.setColumnDefinitions(colDefs);
		colDefs = getColumnDefinitions(); 
		if (getShowHeadings())
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < colDefs.length; ++i)
			{
				buf.append(format(colDefs[i].getLabel(), colDefs[i].getDisplayWidth(), ' '));
			}
			addLine(buf.toString());
			buf = new StringBuffer();
			for (int i = 0; i < colDefs.length; ++i)
			{
				buf.append(format("", colDefs[i].getDisplayWidth(), '-'));
			}
			addLine(buf.toString());
		}
	}

	protected void addRow(Object[] row)
	{
		_rowCount++;
		ColumnDisplayDefinition[] colDefs = getColumnDefinitions();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < row.length; ++i)
		{
			String cellValue = CellComponentFactory.renderObject(row[i], colDefs[i]);
			buf.append(format(cellValue, colDefs[i].getDisplayWidth(), ' '));
		}
		addLine(buf.toString());
	}

	public void moveToTop()
	{
		_outText.select(0, 0);
	}

	
	protected void allRowsAdded()
	{
	}

	
	public Component getComponent()
	{
		return _outText;
	}

	
	public int getRowCount()
	{
		return _rowCount;
	}

	protected void addLine(String line)
	{
		_outText.append(line);
		_outText.append("\n");
	}

	protected String format(String data, int displaySize, char fillChar)
	{
		data = data.replace('\n', ' ');
		data = data.replace('\r', ' ');
		StringBuffer output = new StringBuffer(data);
		if (displaySize > MAX_COLUMN_WIDTH)
		{
			displaySize = MAX_COLUMN_WIDTH;
		}

		if (output.length() > displaySize)
		{
			output.setLength(displaySize);
		}

		displaySize += COLUMN_PADDING;

		int extraPadding = displaySize - output.length();
		if (extraPadding > 0)
		{
			char[] padData = new char[extraPadding];
			Arrays.fill(padData, fillChar);
			output.append(padData);
		}

		return output.toString();
	}

	private final class MyJTextArea extends JTextArea
	{
		private TextPopupMenu _textPopupMenu;

		MyJTextArea(IDataSetUpdateableModel updateableObject)
		{
			super();
			boolean allowUpdate = false;
			if (updateableObject != null)
				allowUpdate = true;
			createUserInterface(allowUpdate, updateableObject);
		}

		protected void createUserInterface(boolean allowUpdate, 
			IDataSetUpdateableModel updateableObject)
		{
			setEditable(false);
			setLineWrap(false);
			setFont(new Font("Monospaced", Font.PLAIN, 12));

			_textPopupMenu = new MyJTextAreaPopupMenu(allowUpdate, updateableObject);
			_textPopupMenu.setTextComponent(this);

			addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						MyJTextArea.this.displayPopupMenu(evt);
					}
				}
				public void mouseReleased(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						MyJTextArea.this.displayPopupMenu(evt);
					}
				}
			});

		}

		void displayPopupMenu(MouseEvent evt)
		{
			_textPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	private static class MyJTextAreaPopupMenu extends TextPopupMenu
	{
		private MakeEditableAction _makeEditable = new MakeEditableAction();

		
		
		private IDataSetUpdateableModel _updateableModel = null;

		MyJTextAreaPopupMenu(boolean allowUpdate, 
					IDataSetUpdateableModel updateableObject)
		{
			super();
			
			_updateableModel = updateableObject;

			if (allowUpdate)
			{
				addSeparator();
				add(_makeEditable);
				addSeparator();
			}
		}

		private class MakeEditableAction extends BaseAction
		{
			MakeEditableAction()
			{
				
 				super(s_stringMgr.getString("dataSetViewerTablePanel.makeEditable"));
			}

			public void actionPerformed(ActionEvent evt)
			{
				if (_updateableModel != null)
				{
					new MakeEditableCommand(_updateableModel).execute();
				}
			}
		}
	}

}
