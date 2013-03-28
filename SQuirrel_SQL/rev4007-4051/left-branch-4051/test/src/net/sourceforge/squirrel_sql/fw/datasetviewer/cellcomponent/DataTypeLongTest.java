package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeLongTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeLong(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
