
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;


public interface IDataTypeComponentFactory {

   
   IDataTypeComponent constructDataTypeComponent();

   
   DialectType getDialectType();
}
