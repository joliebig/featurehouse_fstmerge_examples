package net.sourceforge.squirrel_sql.client.gui.session;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.RefreshSchemaInfoAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.fw.gui.SQLCatalogsComboBox;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class ObjectTreeInternalFrame extends SessionTabWidget
										implements IObjectTreeInternalFrame
{
	
	private final IApplication _app;

	private ObjectTreePanel _objTreePanel;

	
	private ObjectTreeToolBar _toolBar;

	private boolean _hasBeenVisible = false;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ObjectTreeInternalFrame.class);            
    
	public ObjectTreeInternalFrame(ISession session)
	{
		super(session.getTitle(), true, true, true, true, session);
		_app = session.getApplication();
		setVisible(false);
		createGUI(session);
	}

	public void addNotify()
	{
		super.addNotify();
		if (!_hasBeenVisible)
		{
			_hasBeenVisible = true;
			
			
			_objTreePanel.refreshTree();
		}
	}

	public ObjectTreePanel getObjectTreePanel()
	{
		return _objTreePanel;
	}

	public IObjectTreeAPI getObjectTreeAPI()
	{
		return _objTreePanel;
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
				Window window = SwingUtilities.windowForComponent(ObjectTreeInternalFrame.this.getObjectTreePanel());
				Component focusOwner = (window != null) ? window.getFocusOwner() : null;
				if (focusOwner != null)
				{
					FocusEvent lost = new FocusEvent(focusOwner, FocusEvent.FOCUS_LOST);
					FocusEvent gained = new FocusEvent(focusOwner, FocusEvent.FOCUS_GAINED);
					window.dispatchEvent(lost);
					window.dispatchEvent(gained);
					window.dispatchEvent(lost);
					focusOwner.requestFocus();
				}
			}
		});

		_objTreePanel = new ObjectTreePanel(getSession());
		_objTreePanel.addTreeSelectionListener(new ObjectTreeSelectionListener());
		_toolBar = new ObjectTreeToolBar(getSession(), _objTreePanel);
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(_toolBar, BorderLayout.NORTH);
		contentPanel.add(_objTreePanel, BorderLayout.CENTER);
		setContentPane(contentPanel);
		validate();
	}

   public boolean hasSQLPanelAPI()
   {
      return false; 
   }

   
	private class ObjectTreeToolBar extends ToolBar
	{
		
		private final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectTreeToolBar.class);
		private ILogger s_log = LoggerController.createLogger(ObjectTreeToolBar.class);
      private CatalogsPanel _catalogsPanel;

      ObjectTreeToolBar(ISession session, ObjectTreePanel panel)
      {
         super();
         createGUI(session, panel);
      }

		private void createGUI(ISession session, ObjectTreePanel panel)
		{
         _catalogsPanel = new CatalogsPanel(session, this);
         _catalogsPanel.addActionListener(new CatalogsComboListener());
         add(_catalogsPanel);

         ActionCollection actions = session.getApplication()
					.getActionCollection();
			setUseRolloverButtons(true);
			setFloatable(false);
			add(actions.get(RefreshSchemaInfoAction.class));
		}
	}

	private final class CatalogsComboListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object src = evt.getSource();
			if (src instanceof SQLCatalogsComboBox)
			{
				SQLCatalogsComboBox cmb = (SQLCatalogsComboBox)src;
				String catalog = cmb.getSelectedCatalog();
				if (catalog != null)
				{
					try
					{
						getSession().getSQLConnection().setCatalog(catalog);
					}
					catch (SQLException ex)
					{
						getSession().showErrorMessage(ex);
					}
				}
			}
		}
	}

   
   private final class ObjectTreeSelectionListener
         implements
            TreeSelectionListener
   {
      public void valueChanged(TreeSelectionEvent evt)
      {
         final TreePath selPath = evt.getNewLeadSelectionPath();
         if (selPath != null)
         {
            StringBuffer buf = new StringBuffer();
            Object[] fullPath = selPath.getPath();
            for (int i = 0; i < fullPath.length; ++i)
            {
               if (fullPath[i] instanceof ObjectTreeNode)
               {
                  ObjectTreeNode node = (ObjectTreeNode)fullPath[i];
                  buf.append('/').append(node.toString());
               }
            }
            
         }
      }
   }
}
