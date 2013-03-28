
package net.sourceforge.squirrel_sql.client.session;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class ExtendedColumnInfoTest {

    TableColumnInfo tcinfo1 = EasyMock.createMock(TableColumnInfo.class);
    TableColumnInfo tcinfo2 = EasyMock.createMock(TableColumnInfo.class);

    @Before
    public void setUp() throws Exception {
        expect(tcinfo1.getCatalogName()).andReturn("testCatalog1").anyTimes();
        expect(tcinfo1.getSchemaName()).andReturn("testSchema1").anyTimes();
        expect(tcinfo1.getTableName()).andReturn("testTable1").anyTimes();
        expect(tcinfo1.getColumnName()).andReturn("testColumn1").anyTimes();
        expect(tcinfo1.getTypeName()).andReturn("integer").anyTimes();
        expect(tcinfo1.getColumnSize()).andReturn(10).anyTimes();
        expect(tcinfo1.getDecimalDigits()).andReturn(0).anyTimes();
        expect(tcinfo1.isNullable()).andReturn("YES").anyTimes();

        expect(tcinfo2.getCatalogName()).andReturn("testCatalog2").anyTimes();
        expect(tcinfo2.getSchemaName()).andReturn("testSchema2").anyTimes();
        expect(tcinfo2.getTableName()).andReturn("testTable2").anyTimes();
        expect(tcinfo2.getColumnName()).andReturn("testColumn2").anyTimes();
        expect(tcinfo2.getTypeName()).andReturn("integer").anyTimes();
        expect(tcinfo2.getColumnSize()).andReturn(10).anyTimes();
        expect(tcinfo2.getDecimalDigits()).andReturn(0).anyTimes();
        expect(tcinfo2.isNullable()).andReturn("YES").anyTimes();
    
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testEqualsAndHashcode() {
        
        replay(tcinfo1);
        replay(tcinfo2);
        
        ExtendedColumnInfo info1 = new ExtendedColumnInfo(tcinfo1, "table1");
        ExtendedColumnInfo info2 = new ExtendedColumnInfo(tcinfo1, "table1");
        ExtendedColumnInfo info3 = new ExtendedColumnInfo(tcinfo2, "table2");
        ExtendedColumnInfo info4 = new ExtendedColumnInfo(tcinfo1, "table1") {
            private static final long serialVersionUID = 1L;            
        };
        
        new EqualsTester(info1, info2, info3, info4);
    }

}
