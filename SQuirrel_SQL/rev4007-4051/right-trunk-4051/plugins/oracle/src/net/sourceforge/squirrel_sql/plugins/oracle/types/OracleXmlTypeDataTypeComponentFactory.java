
package net.sourceforge.squirrel_sql.plugins.oracle.types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory;

public class OracleXmlTypeDataTypeComponentFactory implements
        IDataTypeComponentFactory {

    public IDataTypeComponent constructDataTypeComponent() {
        return new OracleXmlTypeDataTypeComponent();
    }

}
