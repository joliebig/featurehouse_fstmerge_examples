
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;


public interface INodeExpanderFactory
{
	
	INodeExpander createExpander(DatabaseObjectType type);
	
	
	String getParentLabelForType(DatabaseObjectType type);
}
