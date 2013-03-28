package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeStringTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeString(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
