
package net.sourceforge.squirrel_sql.plugins.postgres.types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;


public class PostgreSqlOtherTypeDataTypeComponentFactory implements IDataTypeComponentFactory
{

	private String typeName=null;
	
	
	public PostgreSqlOtherTypeDataTypeComponentFactory(String typeName) {
		this.typeName = typeName;
	}	
	
	
	public IDataTypeComponent constructDataTypeComponent()
	{
		return new PostgreSqlOtherTypeDataTypeComponent(typeName);
	}

	
	public DialectType getDialectType()
	{
		return DialectType.POSTGRES;
	}

}
