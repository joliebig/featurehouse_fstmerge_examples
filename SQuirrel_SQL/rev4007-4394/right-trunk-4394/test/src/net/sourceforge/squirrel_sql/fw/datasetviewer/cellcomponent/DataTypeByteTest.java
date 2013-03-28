package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeByteTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeByte(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
