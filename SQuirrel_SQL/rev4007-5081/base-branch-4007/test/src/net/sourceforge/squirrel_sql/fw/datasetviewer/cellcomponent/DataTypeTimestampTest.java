package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeTimestampTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeTimestamp(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
