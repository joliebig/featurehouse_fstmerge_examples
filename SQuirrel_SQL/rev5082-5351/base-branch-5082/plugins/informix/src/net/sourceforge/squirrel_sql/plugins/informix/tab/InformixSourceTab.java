package net.sourceforge.squirrel_sql.plugins.informix.tab;


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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public abstract class InformixSourceTab extends BaseSourceTab
{

	@SuppressWarnings("unused")
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(InformixSourceTab.class);

	public static final int VIEW_TYPE = 0;

	public static final int STORED_PROC_TYPE = 1;

	public static final int TRIGGER_TYPE = 2;

	protected int sourceType = VIEW_TYPE;

	
	private final static ILogger s_log = LoggerController.createLogger(InformixSourceTab.class);

	private static CommentSpec[] commentSpecs = new CommentSpec[]
		{ new CommentSpec("/*", "*/"), new CommentSpec("--", "\n") };

	private static CodeReformator formatter = new CodeReformator(";", commentSpecs);

	public InformixSourceTab(String hint) {
		super(hint);
		super.setSourcePanel(new InformixSourcePanel());
	}

	private final class InformixSourcePanel extends BaseSourcePanel
	{
		private static final long serialVersionUID = 1L;

		private JTextArea _ta;

		InformixSourcePanel() {
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
				StringBuffer buf = new StringBuffer(4096);
				int lastProcId = -1;
				while (rs.next())
				{
					if (sourceType == STORED_PROC_TYPE)
					{
						int tmpProcId = rs.getInt(1);
						String tmpProcData = rs.getString(2);
						if (lastProcId != tmpProcId)
						{
							
							if (lastProcId != -1)
							{
								
								
								
								buf.append("\n\n");
							}
							lastProcId = tmpProcId;
						}
						buf.append(tmpProcData);
					}
					if (sourceType == TRIGGER_TYPE)
					{
						String data = rs.getString(1);
						buf.append(data);
					}
					if (sourceType == VIEW_TYPE)
					{
						String line = rs.getString(1);
						buf.append(line);
					}
				}
				String trimmedSource = buf.toString().trim();
				if (sourceType == VIEW_TYPE)
				{
					if (s_log.isDebugEnabled())
					{
						s_log.debug("View source before formatting: " + trimmedSource);
					}
					_ta.setText(formatter.reformat(trimmedSource));
				} else if (sourceType == TRIGGER_TYPE)
				{
					if (s_log.isDebugEnabled())
					{
						s_log.debug("Trigger source before formatting: " + trimmedSource);
					}
					_ta.setText(formatter.reformat(trimmedSource));
				} else
				{
					
					
					
					_ta.setText(trimmedSource);
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
