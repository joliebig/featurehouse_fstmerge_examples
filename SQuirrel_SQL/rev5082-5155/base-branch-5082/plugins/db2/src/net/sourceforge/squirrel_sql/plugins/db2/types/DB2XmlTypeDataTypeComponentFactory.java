
package net.sourceforge.squirrel_sql.plugins.db2.types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;


public class DB2XmlTypeDataTypeComponentFactory implements
		IDataTypeComponentFactory {

	
	public IDataTypeComponent constructDataTypeComponent() {
		return new DB2XmlTypeDataTypeComponent();
	}

	
	public DialectType getDialectType() {
		return DialectType.DB2;
	}

}
