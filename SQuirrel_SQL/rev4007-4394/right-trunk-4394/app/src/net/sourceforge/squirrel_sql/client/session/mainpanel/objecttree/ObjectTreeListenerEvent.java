package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.util.EventObject;

public class ObjectTreeListenerEvent extends EventObject
{
	
	private ObjectTree _tree;

	
	ObjectTreeListenerEvent(ObjectTree source)
	{
		super(checkParams(source));
		_tree = source;
	}

	
	public ObjectTree getObjectTree()
	{
		return _tree;
	}

	private static ObjectTree checkParams(ObjectTree source)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("ObjectTree == null");
		}
		return source;
	}
}