
package net.sourceforge.squirrel_sql.plugins.h2.exp;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpanderFactory;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;


public class H2SequenceInodeExpanderFactory implements INodeExpanderFactory
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
