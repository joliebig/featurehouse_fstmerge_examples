package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeShortTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeShort(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
