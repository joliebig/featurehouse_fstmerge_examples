package net.sourceforge.squirrel_sql.client.gui.db;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.HashMap;
import java.util.Map;

class AliasWindowFactory implements AliasInternalFrame.IMaintenanceType
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(AliasWindowFactory.class);

	
	private final IApplication _app;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(AliasWindowFactory.class);
    
	
	private Map<IIdentifier, AliasInternalFrame> _modifySheets = 
        new HashMap<IIdentifier, AliasInternalFrame>();

	
	public AliasWindowFactory(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
	}

	
	public synchronized AliasInternalFrame getModifySheet(ISQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLALias == null");
		}

		AliasInternalFrame sheet = get(alias);
		if (sheet == null)
		{
			sheet = new AliasInternalFrame(_app, alias, MODIFY);
			_modifySheets.put(alias.getIdentifier(), sheet);
			_app.getMainFrame().addWidget(sheet);

			sheet.addWidgetListener(new WidgetAdapter()
			{
				public void widgetClosed(WidgetEvent evt)
				{
					synchronized (AliasWindowFactory.this)
					{
   						AliasInternalFrame frame = (AliasInternalFrame)evt.getWidget();
						_modifySheets.remove(frame.getSQLAlias().getIdentifier());
					}
				}
         });

         DialogWidget.centerWithinDesktop(sheet);
		}

		return sheet;
	}

	
	public AliasInternalFrame getCreateSheet()
	{
		final net.sourceforge.squirrel_sql.client.gui.db.DataCache cache = _app.getDataCache();
		final IIdentifierFactory factory = IdentifierFactory.getInstance();
		final ISQLAlias alias = cache.createAlias(factory.createIdentifier());
		final AliasInternalFrame sheet = new AliasInternalFrame(_app, alias, NEW);
		_app.getMainFrame().addWidget(sheet);
      DialogWidget.centerWithinDesktop(sheet);
		return sheet;
	}

	
	public AliasInternalFrame getCopySheet(SQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLALias == null");
		}

		final DataCache cache = _app.getDataCache();
		final IIdentifierFactory factory = IdentifierFactory.getInstance();
		SQLAlias newAlias = cache.createAlias(factory.createIdentifier());
		try
		{
			newAlias.assignFrom(alias, false);

         if(SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS == newAlias.getSchemaProperties().getGlobalState())
         {
            
            
            _app.getMessageHandler().showWarningMessage(s_stringMgr.getString("AliasWindowFactory.schemaPropsCopiedWarning"));
         }

         _app.getPluginManager().aliasCopied(alias, newAlias);

      }
		catch (ValidationException ex)
		{
            
			s_log.error(s_stringMgr.getString("AliasWindowFactory.error.copyAlias"), ex);
		}
		final AliasInternalFrame sheet = new AliasInternalFrame(_app, newAlias, COPY);
		_app.getMainFrame().addWidget(sheet);
		DialogWidget.centerWithinDesktop(sheet);
		return sheet;
	}

	private AliasInternalFrame get(ISQLAlias alias)
	{
		return _modifySheets.get(alias.getIdentifier());
	}
}
