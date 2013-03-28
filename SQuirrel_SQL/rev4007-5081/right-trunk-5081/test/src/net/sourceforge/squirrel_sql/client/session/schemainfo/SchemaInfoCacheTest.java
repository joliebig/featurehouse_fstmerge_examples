
package net.sourceforge.squirrel_sql.client.session.schemainfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.AbstractSerializableTest;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.test.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchemaInfoCacheTest extends AbstractSerializableTest {

   SchemaInfoCache schemaInfoCacheUnderTest = new SchemaInfoCache();

   ISQLDatabaseMetaData mockMetaData = null;

   Exception exceptionEncountered = null;

   
   ITableInfo[] tableInfos = new ITableInfo[10];

   String testCatalog = "cat";

   String testSchema = "schema";

   String testTableType = "TABLE";

   @Before
   public void setUp() throws Exception {
   	super.serializableToTest = new SchemaInfoCache();
      mockMetaData = TestUtil.getEasyMockH2SQLMetaData();
      for (int i = 0; i < tableInfos.length; i++) {
         String tableName = "table" + i;
         tableInfos[i] = new TableInfo(testCatalog, testSchema, tableName,
               testTableType, null, mockMetaData);
      }
      schemaInfoCacheUnderTest.writeToTableCache(tableInfos);
   }

   @After
   public void tearDown() throws Exception {
   	super.serializableToTest = null;
      mockMetaData = null;
   }

   
   @Test
   public void testGetITableInfosForReadOnly_order() {
      List<ITableInfo> tis = schemaInfoCacheUnderTest
            .getITableInfosForReadOnly();
      int idx = 0;
      for (ITableInfo iTableInfo : tis) {
         String expectedTableName = tableInfos[idx++].getSimpleName();
         String actualTableName = iTableInfo.getSimpleName();
         assertEquals(expectedTableName, actualTableName);
      }

      
      ITableInfo ti = tableInfos[5];
      
      
      schemaInfoCacheUnderTest.clearTables(testCatalog, testSchema, ti.getSimpleName(),
            new String[] { testTableType });

       
      schemaInfoCacheUnderTest.writeToTableCache(ti);
      
      checkSortOrder();
   }

   private void checkSortOrder() {
      List<ITableInfo> tis = schemaInfoCacheUnderTest
            .getITableInfosForReadOnly();

      ITableInfo last = null;
      for (ITableInfo iTableInfo : tis) {
         if (last == null) {
            last = iTableInfo;
         } else {
            String prev = last.getSimpleName();
            String curr = iTableInfo.getSimpleName();
            System.out.println("prev: "+prev+" curr:"+curr);
            if (prev.compareTo(curr) > 0) {
               fail("Table named "+prev+" appeared before "+curr+" in the sorted list");
            }
            last = iTableInfo;
         }
      }
   }
   
   
   
   
   public final void testGetITableInfosForReadOnly() {

      @SuppressWarnings("unchecked")
      Map map = schemaInfoCacheUnderTest.getTableNamesForReadOnly();
      @SuppressWarnings("unchecked")
      IteratorThread thread = new IteratorThread(map.values().iterator());

      Thread t = new Thread(thread);
      t.start();
      sleep(500);
      schemaInfoCacheUnderTest.clearTables(null, null, null, null);
      sleep(500);
      if (exceptionEncountered != null) {
         exceptionEncountered.printStackTrace();
         fail("Unexpected exception: " + exceptionEncountered.toString());

      }
   }

   private class IteratorThread implements Runnable {

      private Iterator<ITableInfo> iterator = null;

      public IteratorThread(Iterator<ITableInfo> i) {
         iterator = i;
      }

      
      public void run() {
         try {
            while (iterator.hasNext()) {
               iterator.next();
               sleep(500);
            }
         } catch (Exception e) {
            exceptionEncountered = e;
         }
      }

   }

   private void sleep(long millis) {
      try {
         Thread.sleep(millis);
      } catch (Exception e) {
      }
   }
}
