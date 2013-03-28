package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ScriptListDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ScriptListDialog.class);


	JTable tblScriptList;
	JButton btnExecute;
	JButton btnAdd;
	JButton btnEdit;
	JButton btnRemove;
	JButton btnGenerateTemplate;

	JList lstExtraClasspath;
	JButton btnCpAdd;
	JButton btnCpRemove;


	ScriptListDialog(Frame owner, String title, String applicableScriptsText)
	{
      super(owner, title, false);

		JTabbedPane tab = new JTabbedPane();
		
		tab.addTab(s_stringMgr.getString("userscript.dlgTabScripts"), createScriptPanel(applicableScriptsText));
		tab.setSelectedIndex(0);
		
		tab.addTab(s_stringMgr.getString("userscript.dlgTabecp"), createClasspathPanel());

		getContentPane().add(tab);
		setSize(500, 400);
	}

	private JPanel createClasspathPanel()
	{
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());

		lstExtraClasspath = new JList();
		ret.add(new JScrollPane(lstExtraClasspath), BorderLayout.CENTER);

		JPanel pnlSouth = new JPanel();
		pnlSouth.setLayout(new GridLayout(1,2));

		
		btnCpAdd = new JButton(s_stringMgr.getString("userscript.dlgTabScriptsAdd"));
		
		btnCpRemove = new JButton(s_stringMgr.getString("userscript.dlgTabScriptsRemove"));

		pnlSouth.add(btnCpAdd);
		pnlSouth.add(btnCpRemove);

		ret.add(pnlSouth, BorderLayout.SOUTH);

		return ret;
	}
	
    @SuppressWarnings("unused")
	private JPanel createScriptPanel(String applicableScriptsText)
	{
		JPanel scriptPnl = new JPanel();
		scriptPnl.setLayout(new BorderLayout());

		ScriptListTableModel tm = new ScriptListTableModel();
		tblScriptList = new JTable();
		tblScriptList.setModel(tm);

		
		
		
		tblScriptList.setColumnModel(new DefaultTableColumnModel());
		
		


		TableColumn[] tcs = tm.getTableColumns();
		for (int i = 0; i < tcs.length; i++)
		{
			tblScriptList.addColumn(tcs[i]);
		}

		tblScriptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		scriptPnl.add(new JScrollPane(tblScriptList), BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(2, 1));

		JPanel southPanelLower = new JPanel();
		southPanelLower.setLayout(new GridLayout(1, 3));

		
      btnExecute = new JButton(s_stringMgr.getString("userscript.dlgTabScriptsExecute"));
		
		btnAdd = new JButton(s_stringMgr.getString("userscript.dlgTabScriptsAdd2"));
		
		btnEdit = new JButton(s_stringMgr.getString("userscript.dlgTabScriptsEdit"));
		
		btnRemove = new JButton(s_stringMgr.getString("userscript.dlgTabScriptsRemove2"));
		
		btnGenerateTemplate = new JButton(s_stringMgr.getString("userscript.dlgTabScriptsGenTempl"));
		southPanelLower.add(btnExecute);
		southPanelLower.add(btnAdd);
		southPanelLower.add(btnEdit);
		southPanelLower.add(btnRemove);
		southPanel.add(southPanelLower);
		southPanel.add(btnGenerateTemplate);

		scriptPnl.add(southPanel, BorderLayout.SOUTH);

		return scriptPnl;
	}
}
