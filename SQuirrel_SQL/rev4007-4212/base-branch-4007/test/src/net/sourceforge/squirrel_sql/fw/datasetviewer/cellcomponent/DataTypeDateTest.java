package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;




public class DataTypeDateTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeDate(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}
    
    
    
    public void testGetReadDateAsTimestamp() {
        
        assertFalse("Expected default value to be false for read date as timestamp", 
                    DataTypeDate.getReadDateAsTimestamp());
    }

}
