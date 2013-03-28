package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;




public class DataTypeBinaryTest extends AbstractDataType {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeBinary(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}
    
    public void testAreEqual() {
        String val1Str = "value1";
        String val2Str = "value2";
        Byte[] val1ByteArr = StringUtilities.getByteArray(val1Str.getBytes());
        Byte[] val2ByteArr = StringUtilities.getByteArray(val2Str.getBytes());
        iut.areEqual(null, null);
        iut.areEqual(val1Str, val2Str);
        iut.areEqual(val1ByteArr, val2ByteArr);
        iut.areEqual(null, val2Str);
        iut.areEqual(val1Str, null);
        iut.areEqual(val1ByteArr, null);
        iut.areEqual(val1Str, val2ByteArr);
        iut.areEqual(null, val2ByteArr);
        iut.areEqual(val1ByteArr, val2Str);
    }

}
