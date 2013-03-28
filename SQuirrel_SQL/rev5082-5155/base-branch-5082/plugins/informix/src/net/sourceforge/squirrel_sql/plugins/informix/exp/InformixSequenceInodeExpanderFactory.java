
package net.sourceforge.squirrel_sql.plugins.informix.exp;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpanderFactory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.SequenceParentExpander;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

public class InformixSequenceInodeExpanderFactory implements INodeExpanderFactory 
{

	
	public INodeExpander createExpander(DatabaseObjectType type)
	{
		SequenceParentExpander result = new SequenceParentExpander();
		result.setExtractor(new InformixSequenceExtractorImpl());
		return result;
	}

	
	public String getParentLabelForType(DatabaseObjectType type)
	{
		return "SEQUENCE";
	}

}
