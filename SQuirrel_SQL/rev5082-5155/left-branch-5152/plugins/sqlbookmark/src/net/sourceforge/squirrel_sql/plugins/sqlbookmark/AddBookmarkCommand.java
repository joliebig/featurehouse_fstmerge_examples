

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.awt.Frame;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class AddBookmarkCommand implements ICommand {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddBookmarkCommand.class);


	 private static ILogger logger =
	LoggerController.createLogger(AddBookmarkCommand.class);

    
    private final Frame frame;

    
    private final ISession session;

    
    private SQLBookmarkPlugin plugin;

    
    public AddBookmarkCommand(Frame frame, ISession session, SQLBookmarkPlugin plugin)
        throws IllegalArgumentException {
        super();
        if (session == null) {
            throw new IllegalArgumentException("Null ISession passed");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Null IPlugin passed");
        }
        this.frame = frame;
        this.session = session;
        this.plugin = plugin;
    }

    
    public void execute() {
        if (session == null) {
           return;
        }

       ISQLEntryPanel sqlEntryPanel;

       if(session.getActiveSessionWindow() instanceof SessionInternalFrame)
       {
          sqlEntryPanel = ((SessionInternalFrame)session.getActiveSessionWindow()).getSQLPanelAPI().getSQLEntryPanel();
       }
       else if(session.getActiveSessionWindow() instanceof SQLInternalFrame)
       {
          sqlEntryPanel = ((SQLInternalFrame)session.getActiveSessionWindow()).getSQLPanelAPI().getSQLEntryPanel();
       }
       else
       {
          return;
       }

       String sql = sqlEntryPanel.getSQLToBeExecuted();
       if(null == sql || 0 == sql.trim().length())
       {
			 
			 JOptionPane.showMessageDialog(frame, s_stringMgr.getString("sqlbookmark.noAdd"));
          return;
       }



           AddBookmarkDialog abd = new AddBookmarkDialog(frame, plugin);
           GUIUtils.centerWithinParent(abd);
           abd.setVisible(true);

           if(false == abd.isOK())
            return;


	    Bookmark bookmark = new Bookmark(abd.getBookmarkName(), abd.getDescription(), sql);

       if (!plugin.getBookmarkManager().add(bookmark))
       {
          plugin.addBookmarkItem(bookmark);
       }

       plugin.getBookmarkManager().save();
    }

}
