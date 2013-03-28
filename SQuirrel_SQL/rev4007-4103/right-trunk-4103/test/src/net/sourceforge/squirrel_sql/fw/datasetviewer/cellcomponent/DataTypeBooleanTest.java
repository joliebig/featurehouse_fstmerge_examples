package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeBooleanTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeBoolean(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
