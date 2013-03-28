package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class ObjectTreeNode extends DefaultMutableTreeNode
{
	


    private static final long serialVersionUID = 1L;

    
	private final IApplication _app;

	
	private final IIdentifier _sessionId;

	
	private final IDatabaseObjectInfo _dboInfo;

	
	private boolean _allowsChildren = true;

	
	private final List<INodeExpander> _expanders = 
        new ArrayList<INodeExpander>();

	
	public ObjectTreeNode(ISession session, IDatabaseObjectInfo dboInfo)
	{
		super(getNodeTitle(dboInfo));
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (dboInfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}

		_app = session.getApplication();
		_sessionId = session.getIdentifier();
		_dboInfo = dboInfo;
	}

   public void add(MutableTreeNode newChild)
   {
      super.add(newChild);
      newChild.setParent(this);
   }

	
	public ISession getSession()
	{
		return _app.getSessionManager().getSession(_sessionId);
	}

	
	public IDatabaseObjectInfo getDatabaseObjectInfo()
	{
		return _dboInfo;
	}

	
	public DatabaseObjectType getDatabaseObjectType()
	{
		return _dboInfo.getDatabaseObjectType();
	}

	
	public boolean getAllowsChildren()
	{
		return _allowsChildren;
	}

	public boolean isLeaf()
	{
		return !_allowsChildren;
	}

	
	public INodeExpander[] getExpanders()
	{
		return _expanders.toArray(new INodeExpander[_expanders.size()]);
	}

	
	public void addExpander(INodeExpander value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("INodeExpander == null");
		}

		_expanders.add(value);
	}

	
	public void setAllowsChildren(boolean value)
	{
		super.setAllowsChildren(value);
		_allowsChildren = value;
	}

	private static String getNodeTitle(IDatabaseObjectInfo dbinfo)
	{
		if (dbinfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}
		return dbinfo.toString();
	}
}
