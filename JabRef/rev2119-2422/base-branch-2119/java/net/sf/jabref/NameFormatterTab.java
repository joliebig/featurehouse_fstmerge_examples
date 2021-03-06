package net.sf.jabref;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import net.sf.jabref.export.layout.format.NameFormat;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class NameFormatterTab extends JPanel implements PrefsTab {

	public static final String NAME_FORMATTER_VALUE = "nameFormatterFormats";

	public static final String NAME_FORMATER_KEY = "nameFormatterNames";

	public static Map getNameFormatters(){
		
		Map result = new HashMap();

		String[] names = Globals.prefs.getStringArray(NAME_FORMATER_KEY);
		String[] formats = Globals.prefs.getStringArray(NAME_FORMATTER_VALUE);
		
		if (names == null){
			names = new String[]{};
		}
		if (formats == null){
			formats = new String[]{};
		}
		
		for (int i = 0; i < names.length; i++) {
			if (i < formats.length)
				result.put(names[i], formats[i]);
			else
				result.put(names[i], NameFormat.DEFAULT_FORMAT);
		}
		
		return result;
	}
	
	private boolean tableChanged = false;

	private JTable table;

	private int rowCount = -1;

	private Vector tableRows = new Vector(10);

	class TableRow {
		String name;

		String format;

		public TableRow() {
			this("");
		}

		public TableRow(String name) {
			this(name, NameFormat.DEFAULT_FORMAT);
		}

		public TableRow(String name, String format) {
			this.name = name;
			this.format = format;
		}
	}

	
	public NameFormatterTab(HelpDialog helpDialog) {
		setLayout(new BorderLayout());

		TableModel tm = new AbstractTableModel() {
			public int getRowCount() {
				return rowCount;
			}

			public int getColumnCount() {
				return 2;
			}

			public Object getValueAt(int row, int column) {
				if (row >= tableRows.size())
					return "";
				TableRow tr = (TableRow) tableRows.elementAt(row);
				if (tr == null)
					return "";
				switch (column) {
				case 0:
					return tr.name;
				case 1:
					return tr.format;
				}
				return null; 
			}

			public String getColumnName(int col) {
				return (col == 0 ? Globals.lang("Formatter Name") : Globals.lang("Format String"));
			}

			public Class getColumnClass(int column) {
				if (column == 0)
					return String.class;
				else
					return String.class;
			}

			public boolean isCellEditable(int row, int col) {
				return true;
			}

			public void setValueAt(Object value, int row, int col) {
				tableChanged = true;

				
				while (row >= tableRows.size())
					tableRows.add(new TableRow());

				TableRow rowContent = (TableRow) tableRows.elementAt(row);

				if (col == 0)
					rowContent.name = value.toString();
				else
					rowContent.format = value.toString();
			}
		};

		table = new JTable(tm);
		TableColumnModel cm = table.getColumnModel();
		cm.getColumn(0).setPreferredWidth(140);
		cm.getColumn(1).setPreferredWidth(400);

		FormLayout layout = new FormLayout("1dlu, 8dlu, left:pref, 4dlu, fill:pref", "");

		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		JPanel pan = new JPanel();

		JPanel tabPanel = new JPanel();
		tabPanel.setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		table.setPreferredScrollableViewportSize(new Dimension(250, 200));
		sp.setMinimumSize(new Dimension(250, 300));
		sp.setPreferredSize(new Dimension(600, 300));
		tabPanel.add(sp, BorderLayout.CENTER);

		JToolBar tlb = new JToolBar(SwingConstants.VERTICAL);
		tlb.setFloatable(false);
		tlb.setBorder(null);
		tlb.add(new AddRowAction());
		tlb.add(new DeleteRowAction());
		tlb.add(new HelpAction(helpDialog, GUIGlobals.nameFormatterHelp,
			"Help on Name Formatting", GUIGlobals.getIconUrl("helpSmall")));

		tabPanel.add(tlb, BorderLayout.EAST);

		builder.appendSeparator(Globals.lang("Special Name Formatters"));
		builder.nextLine();
		builder.append(pan);
		builder.append(tabPanel);
		builder.nextLine();

		pan = builder.getPanel();
		pan.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(pan, BorderLayout.CENTER);
	}

	public void setValues() {
		tableRows.clear();
		String[] names = Globals.prefs.getStringArray(NAME_FORMATER_KEY);
		String[] formats = Globals.prefs.getStringArray(NAME_FORMATTER_VALUE);
		
		if (names == null){
			names = new String[]{};
		}
		if (formats == null){
			formats = new String[]{};
		}
		
		for (int i = 0; i < names.length; i++) {
			if (i < formats.length)
				tableRows.add(new TableRow(names[i], formats[i]));
			else
				tableRows.add(new TableRow(names[i]));
		}
		rowCount = tableRows.size() + 5;
	}

	class DeleteRowAction extends AbstractAction {
		
		public DeleteRowAction() {
			super("Delete row", GUIGlobals.getImage("remove"));
			putValue(SHORT_DESCRIPTION, Globals.lang("Delete rows"));
		}

		public void actionPerformed(ActionEvent e) {
			tableChanged = true;
			
			int[] selectedRows = table.getSelectedRows();
		
			int numberDeleted = 0;
			
			for (int i = selectedRows.length - 1; i >= 0; i--) {
				if (selectedRows[i] < tableRows.size()) {
					tableRows.remove(selectedRows[i]);
					numberDeleted++;
				}
			}
			
			rowCount -= numberDeleted;
			
			if (selectedRows.length > 1)
				table.clearSelection();
			
			table.revalidate();
			table.repaint();
		}
	}

	class AddRowAction extends AbstractAction {
		public AddRowAction() {
			super("Add row", GUIGlobals.getImage("add"));
			putValue(SHORT_DESCRIPTION, Globals.lang("Insert rows"));
		}

		public void actionPerformed(ActionEvent e) {
			int[] rows = table.getSelectedRows();
			if (rows.length == 0) {
				
				rowCount++;
				table.revalidate();
				table.repaint();
				return;
			}
			for (int i = 0; i < rows.length; i++) {
				if (rows[i] + i - 1 < tableRows.size())
					tableRows.add(Math.max(0, rows[i] + i - 1), new TableRow());
			}
			rowCount += rows.length;
			if (rows.length > 1)
				table.clearSelection();
			table.revalidate();
			table.repaint();
			tableChanged = true;
		}
	}

	
	public void storeSettings() {

		if (table.isEditing()) {
			int col = table.getEditingColumn(), row = table.getEditingRow();
			table.getCellEditor(row, col).stopCellEditing();
		}

		
		
		if (tableChanged) {
			
			int i = 0;
			while (i < tableRows.size()) {
				if (((TableRow) tableRows.elementAt(i)).name.equals(""))
					tableRows.removeElementAt(i);
				else
					i++;
			}
			
			String[] names = new String[tableRows.size()], formats = new String[tableRows.size()];

			for (i = 0; i < tableRows.size(); i++) {
				TableRow tr = (TableRow) tableRows.elementAt(i);
				names[i] = tr.name;
				formats[i] = tr.format;
			}

			
			Globals.prefs.putStringArray(NAME_FORMATER_KEY, names);
			Globals.prefs.putStringArray(NAME_FORMATTER_VALUE, formats);
		}
	}

	public boolean readyToClose() {
		return true;
	}

	public String getTabName() {
        return Globals.lang("Name formatter");
	}
}
