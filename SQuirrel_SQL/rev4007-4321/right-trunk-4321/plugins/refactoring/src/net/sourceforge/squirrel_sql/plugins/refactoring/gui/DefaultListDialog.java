package net.sourceforge.squirrel_sql.plugins.refactoring.gui;



import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class DefaultListDialog extends JDialog
{

	private static final long serialVersionUID = 2908275309430303054L;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DefaultListDialog.class);

	private final IDatabaseObjectInfo[] _objectInfo;

	private String _selectItem = "";

	
	public static final int DIALOG_TYPE_INDEX = 3;

	
	public static final int DIALOG_TYPE_UNIQUE_CONSTRAINTS = 4;

	
	public static final int DIALOG_TYPE_FOREIGN_KEY = 5;

	private interface i18n
	{
		String TABLE_NAME_LABEL = s_stringMgr.getString("DefaultListDialog.tableNameLabel");

		String CANCEL_BUTTON_LABEL = s_stringMgr.getString("AbstractRefactoringDialog.cancelButtonLabel");

		String FOREIGN_KEY_LABEL = s_stringMgr.getString("DefaultListDialog.foreignKeyNameLabel");

		String INDEX_LABEL = s_stringMgr.getString("DefaultListDialog.indexNameLabel");

		String UNIQUE_CONSTRAINT_LABEL = s_stringMgr.getString("DefaultListDialog.uniqueConstraintLabel");

		String OK_BUTTON_LABEL = s_stringMgr.getString("DefaultListDialog.selectButtonLabel");
	}

	private JButton _executeButton = null;

	private JList _columnList;

	public DefaultListDialog(IDatabaseObjectInfo[] objectInfo, String tableName, int dialogType)
	{
		this._objectInfo = objectInfo;

		setTypeByID(dialogType);
		init(tableName);
	}

	
	private void setTypeByID(int dialogType)
	{
		String object = "";
		switch (dialogType)
		{

		case DIALOG_TYPE_INDEX:
			object = i18n.INDEX_LABEL;
			break;

		case DIALOG_TYPE_FOREIGN_KEY:
			object = i18n.FOREIGN_KEY_LABEL;
			break;

		case DIALOG_TYPE_UNIQUE_CONSTRAINTS:
			object = i18n.UNIQUE_CONSTRAINT_LABEL;
			break;
		default:
		}
		_selectItem = object;
		setTitle(s_stringMgr.getString("DefaultDropDialog.title", object));
	}

	public String getSelectedIndex()
	{
		return _columnList.getSelectedValue().toString();
	}

	public ArrayList<IDatabaseObjectInfo> getSelectedItems()
	{
		ArrayList<IDatabaseObjectInfo> idbo = new ArrayList<IDatabaseObjectInfo>();
		String[] simpleNames = getSimpleNames(_objectInfo);
		for (int index : _columnList.getSelectedIndices())
		{
			for (IDatabaseObjectInfo info : _objectInfo)
			{
				if (info.getSimpleName().equals(simpleNames[index]))
				{
					idbo.add(info);
					break;
				}
			}
		}
		return idbo;
	}

	public void addColumnSelectionListener(ActionListener columnListSelectionActionListener)
	{
		_executeButton.addActionListener(columnListSelectionActionListener);
	}

	
	private void init(String tableName)
	{
		super.setModal(true);

		setSize(425, 250);
		EmptyBorder border = new EmptyBorder(new Insets(5, 5, 5, 5));
		Dimension mediumField = new Dimension(126, 20);

		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		pane.setBorder(new EmptyBorder(10, 0, 0, 30));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = -1;

		
		JLabel tableNameLabel = getBorderedLabel(i18n.TABLE_NAME_LABEL, border);
		pane.add(tableNameLabel, getLabelConstraints(c));

		JTextField tableNameTextField = new JTextField(tableName);
		tableNameTextField.setPreferredSize(mediumField);
		tableNameTextField.setEditable(false);
		pane.add(tableNameTextField, getFieldConstraints(c));

		
		JLabel columnListLabel = getBorderedLabel(_selectItem, border);
		columnListLabel.setVerticalAlignment(JLabel.NORTH);
		pane.add(columnListLabel, getLabelConstraints(c));

		_columnList = new JList(getSimpleNames(_objectInfo));
		_columnList.addListSelectionListener(new ColumnListSelectionListener());
		_columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JScrollPane sp = new JScrollPane(_columnList);
		c = getFieldConstraints(c);
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		pane.add(sp, c);

		Container contentPane = super.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(pane, BorderLayout.CENTER);

		contentPane.add(getButtonPanel(), BorderLayout.SOUTH);
	}

	private JPanel getButtonPanel()
	{
		JPanel result = new JPanel();
		_executeButton = new JButton(i18n.OK_BUTTON_LABEL);

		JButton cancelButton = new JButton(i18n.CANCEL_BUTTON_LABEL);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		result.add(_executeButton);
		result.add(cancelButton);
		return result;
	}

	private String[] getSimpleNames(IDatabaseObjectInfo[] dbInfo)
	{
		ArrayList<String> simpleNames = new ArrayList<String>();
		for (IDatabaseObjectInfo info : dbInfo)
		{
			if (!simpleNames.contains(info.getSimpleName()))
				simpleNames.add(info.getSimpleName());
		}
		return simpleNames.toArray(new String[] {});
	}

	private GridBagConstraints getLabelConstraints(GridBagConstraints c)
	{
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		return c;
	}

	private GridBagConstraints getFieldConstraints(GridBagConstraints c)
	{
		c.gridx++;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		return c;
	}

	private JLabel getBorderedLabel(String text, Border border)
	{
		JLabel result = new JLabel(text);
		result.setBorder(border);
		result.setPreferredSize(new Dimension(115, 20));
		result.setHorizontalAlignment(SwingConstants.RIGHT);
		return result;
	}

	private class ColumnListSelectionListener implements ListSelectionListener
	{

		
		public void valueChanged(ListSelectionEvent e)
		{
			int[] selected = _columnList.getSelectedIndices();

			if (selected == null || selected.length == 0)
			{
				activate(_executeButton, false);
				return;
			}

			
			activate(_executeButton, true);
		}

	}

	private void activate(JButton button, boolean enable)
	{
		if (button != null)
		{
			button.setEnabled(enable);
		}
	}
}
