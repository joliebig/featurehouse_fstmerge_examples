package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;


import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public abstract class BaseSourceTab extends BaseObjectTab
{
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(BaseSourceTab.class);
	
	
	private final static ILogger s_log =
		LoggerController.createLogger(BaseSourceTab.class);

	
	private final String _hint;

	
    private String _title;

	
	private BaseSourcePanel _comp;

	
	private JScrollPane _scroller;

	public BaseSourceTab(String hint)
	{
		this(null, hint);
	}

	public BaseSourceTab(String title, String hint) {
  		super();
  		if (title != null) {
  			_title = title;
  		} else {
  		    
  			_title = s_stringMgr.getString("BaseSourceTab.title");
  		}
  		
  		_hint = hint != null ? hint : _title;
    }

	
	public String getTitle()
	{
		return _title;
	}

	
	public String getHint()
	{
		return _hint;
	}

	public void clear()
	{
	}

	public Component getComponent()
	{
        if (_scroller == null) {
    		if (_comp == null) {
    			_comp = new DefaultSourcePanel();
    		}
            _scroller = new JScrollPane(_comp);
            LineNumber lineNumber = new LineNumber( _comp );
            _scroller.setRowHeaderView( lineNumber );
            _scroller.getVerticalScrollBar().setUnitIncrement(10);
        }
		return _scroller;
	}
    
    
    public void setSourcePanel(BaseSourcePanel panel) {
        _comp = panel;
    }
    
	protected void refreshComponent()
	{
		ISession session = getSession();
		if (session == null)
		{
			throw new IllegalStateException("Null ISession");
		}
		try
		{
			PreparedStatement pstmt = createStatement();
			try
			{
				_comp.load(getSession(), pstmt);
			}
			finally
			{
				try
				{
					pstmt.close();
				}
				catch (SQLException ex)
				{
					s_log.error(ex);
				}
			}
		}
		catch (SQLException ex)
		{
			s_log.error(ex);
			session.showErrorMessage(ex);
		}
	}

    
	protected abstract PreparedStatement createStatement() throws SQLException;

	private final class DefaultSourcePanel extends BaseSourcePanel
	{
        private static final long serialVersionUID = 1L;

        private JTextArea _ta;

        DefaultSourcePanel()
		{
			super(new BorderLayout());
			createUserInterface();
		}

		public void load(ISession session, PreparedStatement stmt)
		{
			_ta.setText("");
			try
			{
				ResultSet rs = stmt.executeQuery();
				StringBuffer buf = new StringBuffer(4096);
				while (rs.next())
				{
					buf.append(rs.getString(1));
				}
				_ta.setText(buf.toString());
				_ta.setCaretPosition(0);
			}
			catch (SQLException ex)
			{
				session.showErrorMessage(ex);
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
