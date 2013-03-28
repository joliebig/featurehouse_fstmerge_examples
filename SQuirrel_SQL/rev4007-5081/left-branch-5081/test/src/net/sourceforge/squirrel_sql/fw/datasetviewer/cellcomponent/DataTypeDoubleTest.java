package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeDoubleTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeDouble(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
