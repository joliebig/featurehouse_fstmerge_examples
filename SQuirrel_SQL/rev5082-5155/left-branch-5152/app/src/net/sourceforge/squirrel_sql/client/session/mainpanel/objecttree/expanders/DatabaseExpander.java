package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaFilterMatcher;
import net.sourceforge.squirrel_sql.client.session.schemainfo.CatalogFilterMatcher;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DatabaseExpander implements INodeExpander
{
	
	private static ILogger s_log =
		LoggerController.createLogger(DatabaseExpander.class);

	
	private String[] _tableTypes = new String[] {};

	
	public DatabaseExpander(ISession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
        GetTableTypes task = new GetTableTypes(session);
		if (SwingUtilities.isEventDispatchThread()) {
		    session.getApplication().getThreadPool().addTask(task);
        } else {
            task.run();
        }
    }

    private class GetTableTypes implements Runnable {
        
        ISession _session = null;
        
        public GetTableTypes(ISession session) {
            _session = session;
        }
        
        public void run() {
            try
            {
                _tableTypes = _session.getSQLConnection().getSQLMetaData().getTableTypes();
            }
            catch (SQLException ex)
            {
                s_log.debug("DBMS doesn't support 'getTableTypes()", ex);
            }
        }
        
    }
    
	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = conn.getSQLMetaData();

		boolean supportsCatalogs = false;
		try
		{
			supportsCatalogs = md.supportsCatalogs();
		}
		catch (SQLException ex)
		{
			s_log.debug("DBMS doesn't support 'supportsCatalogs()", ex);
		}

		boolean supportsSchemas = false;
		try
		{
			supportsSchemas = md.supportsSchemas();
		}
		catch (SQLException ex)
		{
			s_log.debug("DBMS doesn't support 'supportsSchemas()", ex);
		}

		List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();

		if (parentDbinfo.getDatabaseObjectType() == DatabaseObjectType.SESSION)
		{
			
			
			List<ObjectTreeNode> addedChildren = new ArrayList<ObjectTreeNode>();
			if (supportsCatalogs)
			{
				addedChildren = createCatalogNodes(session, md);
				childNodes.addAll(addedChildren);
			}

			if (addedChildren.size() == 0 && supportsSchemas)
			{
				addedChildren = createSchemaNodes(session, md, null);
				childNodes.addAll(addedChildren);
			}

			if (addedChildren.size() == 0)
			{
				childNodes.addAll(createObjectTypeNodes(session, null, null));
			}
		}
		else if (parentDbinfo.getDatabaseObjectType() == DatabaseObjectType.CATALOG)
		{
			
			
			final String catalogName = parentDbinfo.getSimpleName();
			List<ObjectTreeNode> addedChildren = new ArrayList<ObjectTreeNode>();
			if (supportsSchemas)
			{
				addedChildren = createSchemaNodes(session, md, catalogName);
				childNodes.addAll(addedChildren);
			}
			
			if (addedChildren.size() == 0)
			{
				childNodes.addAll(createObjectTypeNodes(session, catalogName, null));
			}
		}
		else if (parentDbinfo.getDatabaseObjectType() == DatabaseObjectType.SCHEMA)
		{
			final String catalogName = parentDbinfo.getCatalogName();
			final String schemaName = parentDbinfo.getSimpleName();
			childNodes.addAll(createObjectTypeNodes(session, catalogName, schemaName));
		}

		return childNodes;
	}

	private List<ObjectTreeNode> createCatalogNodes(ISession session, SQLDatabaseMetaData md)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		if (session.getProperties().getLoadSchemasCatalogs())
		{
			final String[] catalogs = md.getCatalogs();

         CatalogFilterMatcher filterMatcher = new CatalogFilterMatcher(session.getProperties());

			for (int i = 0; i < catalogs.length; ++i)
			{
            IDatabaseObjectInfo dbo = new DatabaseObjectInfo(null, null,
                                 catalogs[i],
                                 DatabaseObjectType.CATALOG,
                                 md);
            if (filterMatcher.matches(dbo.getSimpleName()))
            {
					childNodes.add(new ObjectTreeNode(session, dbo));
				}
			}
		}
		return childNodes;
	}

	protected List<ObjectTreeNode> createSchemaNodes(ISession session, 
                                     SQLDatabaseMetaData md,
								     String catalogName)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		if (session.getProperties().getLoadSchemasCatalogs())
		{
         session.getSchemaInfo().waitTillSchemasAndCatalogsLoaded();
         final String[] schemas = session.getSchemaInfo().getSchemas();

         SchemaFilterMatcher filterMatcher = new SchemaFilterMatcher(session.getProperties());

			for (int i = 0; i < schemas.length; ++i)
			{
            IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName, null,
                                    schemas[i],
                                    DatabaseObjectType.SCHEMA, md);
            if(filterMatcher.matches(dbo.getSimpleName()))
            {
               childNodes.add(new ObjectTreeNode(session, dbo));
				}
			}
		}
		return childNodes;
	}

	private List<ObjectTreeNode> createObjectTypeNodes(ISession session, String catalogName,
											String schemaName)
	{
		final List<ObjectTreeNode> list = new ArrayList<ObjectTreeNode>();

		if (session.getProperties().getLoadSchemasCatalogs())
		{
			final ISQLConnection conn = session.getSQLConnection();
			final SQLDatabaseMetaData md = conn.getSQLMetaData();

			
			if (_tableTypes.length > 0)
			{
				for (int i = 0; i < _tableTypes.length; ++i)
				{
					IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName,
													schemaName, _tableTypes[i],
													DatabaseObjectType.TABLE_TYPE_DBO, md);
					ObjectTreeNode child = new ObjectTreeNode(session, dbo);
					list.add(child);
				}
			}
			else
			{
				s_log.debug("List of table types is empty so trying null table type to load all tables");
				IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName,
												schemaName, null,
												DatabaseObjectType.TABLE_TYPE_DBO, md);
				ObjectTreeNode child = new ObjectTreeNode(session, dbo);
				child.setUserObject("TABLE");
				list.add(child);
			}

			
			boolean supportsStoredProcs = false;
			try
			{
				supportsStoredProcs = md.supportsStoredProcedures();
			}
			catch (SQLException ex)
			{
				s_log.debug("DBMS doesn't support 'supportsStoredProcedures()'", ex);
			}
			if (supportsStoredProcs)
			{
				IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName,
													schemaName, "PROCEDURE",
													DatabaseObjectType.PROC_TYPE_DBO, md);
				ObjectTreeNode child = new ObjectTreeNode(session, dbo);
				list.add(child);
			}

			
			{
				IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName,
											schemaName, "UDT",
											DatabaseObjectType.UDT_TYPE_DBO, md);
				ObjectTreeNode child = new ObjectTreeNode(session, dbo);
				list.add(child);
			}
		}

		return list;
	}

}
