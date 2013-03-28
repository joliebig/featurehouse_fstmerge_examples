package net.sourceforge.squirrel_sql.plugins.postgres.tab;


import java.awt.BorderLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourcePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public abstract class PostgresSourceTab extends BaseSourceTab
{

	public static final int VIEW_TYPE = 0;

	public static final int STORED_PROC_TYPE = 1;

	public static final int TRIGGER_TYPE = 2;

	protected int sourceType = VIEW_TYPE;

	
	private final static ILogger s_log = LoggerController.createLogger(PostgresSourceTab.class);

	private static CommentSpec[] commentSpecs = new CommentSpec[]
		{ new CommentSpec("/*", "*/"), new CommentSpec("--", "\n") };

	private static CodeReformator formatter = new CodeReformator(";", commentSpecs);

	public PostgresSourceTab(String hint) {
		super(hint);
		super.setSourcePanel(new PostgresSourcePanel());
	}

	private final class PostgresSourcePanel extends BaseSourcePanel
	{
		private static final long serialVersionUID = 1L;

		private JTextArea _ta;

		PostgresSourcePanel() {
			super(new BorderLayout());
			createUserInterface();
		}

		public void load(ISession session, PreparedStatement stmt)
		{
			_ta.setText("");
			_ta.setWrapStyleWord(true);
			ResultSet rs = null;
			try
			{
				rs = stmt.executeQuery();
				StringBuilder buf = new StringBuilder(4096);
				while (rs.next())
				{
					String nextString = rs.getString(1);
					if (sourceType == STORED_PROC_TYPE)
					{
						buf.append(nextString);
					}
					if (sourceType == TRIGGER_TYPE)
					{
						buf.append(nextString.trim());
						buf.append(" ");
					}
					if (sourceType == VIEW_TYPE)
					{
						buf.append(nextString.trim());
						buf.append(" ");
					}
				}
				
				
				if (sourceType == VIEW_TYPE || sourceType == TRIGGER_TYPE)
				{
					if (s_log.isDebugEnabled())
					{
						s_log.debug("View source before formatting: " + buf.toString());
					}
					_ta.setText(formatter.reformat(buf.toString()));
				} else
				{
					_ta.setText(buf.toString());
				}
				_ta.setCaretPosition(0);
			} catch (SQLException ex)
			{
				session.showErrorMessage(ex);
			} finally
			{
				SQLUtilities.closeResultSet(rs);
			}

		}

		private void createUserInterface()
		{
			_ta = new JTextArea();
			_ta.setEditable(false);
			add(_ta, BorderLayout.CENTER);
		}
	}

}
