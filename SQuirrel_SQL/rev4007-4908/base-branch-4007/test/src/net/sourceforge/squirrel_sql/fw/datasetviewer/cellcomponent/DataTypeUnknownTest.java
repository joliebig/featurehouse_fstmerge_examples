package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeUnknownTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeUnknown(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
