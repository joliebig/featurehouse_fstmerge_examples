package net.sourceforge.squirrel_sql.plugins.mysql.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.factories.Borders;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;



import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

public class AlterTableDialog extends JDialog
{
	



    private static final long serialVersionUID = 1L;

    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AlterTableDialog.class);

	
	public AlterTableDialog(ISession session, MysqlPlugin plugin,
								ITableInfo ti)
		throws SQLException
	{
		super(ctorHelper(session, plugin, ti), true);

		createGUI(session, plugin, ti);
	}

	private void createGUI(ISession session, MysqlPlugin plugin,
								ITableInfo ti)
		throws SQLException
	{
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(s_stringMgr.getString("AlterTableDialog.title",
										ti.getQualifiedName()));
		setContentPane(buildContentPane(session, plugin, ti));
	}
	@SuppressWarnings("unused")
	private JComponent buildContentPane(ISession session, MysqlPlugin plugin,
											ITableInfo ti)
		throws SQLException
	{
		final JPanel pnl = new JPanel(new BorderLayout());
		pnl.add(buildMainPanel(session, ti), BorderLayout.CENTER);
		pnl.add(buildToolBar(), BorderLayout.SOUTH);
		pnl.setBorder(Borders.TABBED_DIALOG_BORDER);

		return pnl;















}

	private JTabbedPane buildMainPanel(ISession session, ITableInfo ti)
		throws SQLException
	{
		final JTabbedPane tabPnl = UIFactory.getInstance().createTabbedPane();
		final JPanel pnl = new AlterColumnsPanelBuilder().buildPanel(session, ti);
		tabPnl.addTab(getString("AlterTableDialog.columns"), null, pnl,
						getString("AlterTableDialog.columnshint"));
		return tabPnl;
	}

	private JPanel buildToolBar()
	{
		final ButtonBarBuilder builder = new ButtonBarBuilder();
		builder.addGlue();
		
		builder.addGridded(new JButton(s_stringMgr.getString("mysql.alterDlgAlter")));
		builder.addRelatedGap();
		
		builder.addGridded(new JButton(s_stringMgr.getString("mysql.alterDlgClose")));

		return builder.getPanel();
	}

	private static String getString(String stringMgrKey)
	{
		return s_stringMgr.getString(stringMgrKey);
	}

	private static Frame ctorHelper(ISession session, MysqlPlugin plugin,
										ITableInfo ti)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("MysqlPlugin == null");
		}
		if (ti == null)
		{
			throw new IllegalArgumentException("ITableInfo == null");
		}
		return session.getApplication().getMainFrame();
	}
}
