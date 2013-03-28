package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeFloatTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeFloat(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
