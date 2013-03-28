package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;





public class DataTypeBigDecimalTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeBigDecimal(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}

}
