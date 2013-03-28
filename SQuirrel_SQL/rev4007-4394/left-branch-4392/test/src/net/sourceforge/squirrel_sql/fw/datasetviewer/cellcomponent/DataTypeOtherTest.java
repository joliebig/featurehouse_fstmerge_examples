package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeOtherTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeOther(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
