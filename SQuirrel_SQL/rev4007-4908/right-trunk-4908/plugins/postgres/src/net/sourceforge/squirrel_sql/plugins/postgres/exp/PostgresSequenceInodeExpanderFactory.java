
package net.sourceforge.squirrel_sql.plugins.postgres.exp;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpanderFactory;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

public class PostgresSequenceInodeExpanderFactory implements INodeExpanderFactory 
{

	
	public INodeExpander createExpander(DatabaseObjectType type)
	{

		return new SequenceParentExpander();
	}

	
	public String getParentLabelForType(DatabaseObjectType type)
	{
		return "SEQUENCE";
	}

}
