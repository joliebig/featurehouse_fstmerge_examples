package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class SQLInternalFrame extends SessionTabWidget
								implements ISQLInternalFrame
{
	
	private final IApplication _app;

	private SQLPanel _sqlPanel;
	
	private SQLToolBar _toolBar;

	private StatusBar _statusBar = new StatusBar();

	public SQLInternalFrame(ISession session)
	{
		super(session.getTitle(), true, true, true, true, session);
		_app = session.getApplication();
		setVisible(false);
		createGUI(session);
	}

	public SQLPanel getSQLPanel()
	{
		return _sqlPanel;
	}

	public ISQLPanelAPI getSQLPanelAPI()
	{
		return _sqlPanel.getSQLPanelAPI();
	}

	private void createGUI(ISession session)
	{
		setVisible(false);
		final IApplication app = session.getApplication();
		Icon icon = app.getResources().getIcon(getClass(), "frameIcon"); 
		if (icon != null)
		{
			setFrameIcon(icon);
		}

		
		
		
		
		
		addWidgetListener(new WidgetAdapter()
		{
			public void widgetActivated(WidgetEvent evt)
			{
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  _sqlPanel.getSQLEntryPanel().getTextComponent().requestFocus();
               }
            });
			}

         public void widgetClosing(WidgetEvent e)
         {
            _sqlPanel.sessionWindowClosing();
         }
		});

		_sqlPanel = new SQLPanel(getSession(), false);


		
      _sqlPanel.setVisible(true);

		_toolBar = new SQLToolBar(getSession(), _sqlPanel.getSQLPanelAPI());
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(_toolBar, BorderLayout.NORTH);
		contentPanel.add(_sqlPanel, BorderLayout.CENTER);

		Font fn = app.getFontInfoStore().getStatusBarFontInfo().createFont();
		_statusBar.setFont(fn);
		contentPanel.add(_statusBar, BorderLayout.SOUTH);

		RowColumnLabel lblRowCol = new RowColumnLabel(_sqlPanel.getSQLEntryPanel());
		_statusBar.addJComponent(lblRowCol);

		setContentPane(contentPanel);
		validate();
	}


   public void requestFocus()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _sqlPanel.getSQLEntryPanel().requestFocus();
         }
      });

   }

   public void addSeparatorToToolbar()
   {
      if (null != _toolBar)
      {
         _toolBar.addSeparator();
      }
   }

   public void addToToolbar(Action action)
   {
      if (null != _toolBar)
      {
         _toolBar.add(action);
      }
   }

   public void addToToolsPopUp(String selectionString, Action action)
   {
      getSQLPanelAPI().addToToolsPopUp(selectionString, action);
   }

   public boolean hasSQLPanelAPI()
   {
      return true;
   }


   
	private class SQLToolBar extends ToolBar
	{
		SQLToolBar(ISession session, ISQLPanelAPI panel)
		{
			super();
			createGUI(session, panel);
		}

		private void createGUI(ISession session, ISQLPanelAPI panel)
		{
			ActionCollection actions = session.getApplication().getActionCollection();
			setUseRolloverButtons(true);
			setFloatable(false);
			add(actions.get(ExecuteSqlAction.class));
			addSeparator();
			add(actions.get(FileNewAction.class));
			add(actions.get(FileOpenAction.class));
			add(actions.get(FileAppendAction.class));
			add(actions.get(FileSaveAction.class));
			add(actions.get(FileSaveAsAction.class));
            add(actions.get(FilePrintAction.class));
			add(actions.get(FileCloseAction.class));
			addSeparator();
			add(actions.get(PreviousSqlAction.class));
			add(actions.get(NextSqlAction.class));
			add(actions.get(SelectSqlAction.class));
			addSeparator();
			add(actions.get(SQLFilterAction.class));
			actions.get(SQLFilterAction.class).setEnabled(true);
		}
	}


   @Override
   public void dispose()
   {
      super.dispose();    
   }
}