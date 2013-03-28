
package net.sourceforge.squirrel_sql.plugins.derby.types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

public class DerbyClobDataTypeComponentFactory implements
        IDataTypeComponentFactory {

    
   public IDataTypeComponent constructDataTypeComponent() {
        return new DerbyClobDataTypeComponent();
    }

   
   public boolean providesTypeOverride(ISQLDatabaseMetaData md) {
      return DialectFactory.isDerby(md);
   }
    
   
   public DialectType getDialectType() {
      return DialectType.DERBY;
   }

}
