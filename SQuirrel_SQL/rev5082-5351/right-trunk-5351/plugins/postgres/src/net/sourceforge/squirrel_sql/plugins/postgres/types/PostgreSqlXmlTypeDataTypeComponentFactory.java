
package net.sourceforge.squirrel_sql.plugins.postgres.types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;


public class PostgreSqlXmlTypeDataTypeComponentFactory implements IDataTypeComponentFactory
{

	
	public IDataTypeComponent constructDataTypeComponent()
	{
		return new PostgreSqlXmlTypeDataTypeComponent();
	}

	
	public DialectType getDialectType()
	{
		return DialectType.POSTGRES;
	}

}
