package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeIntegerTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeInteger(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
