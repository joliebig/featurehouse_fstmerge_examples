package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeClobTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeClob(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
