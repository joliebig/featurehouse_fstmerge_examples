package net.sourceforge.squirrel_sql.fw.sql;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import junit.framework.Assert;


public class SQLUtil {
    
    private static int genericSQLCount = 0;
    
    public static String getGenericSQLScript() {
        StringBuffer result = new StringBuffer();
        result.append(GenericSQL.CREATE_STUDENT);
        result.append("\n\n");
        result.append(GenericSQL.CREATE_COURSES);
        result.append("\n\n");
        result.append(GenericSQL.CREATE_PROFESSOR);
        result.append("\n\n");
        result.append(GenericSQL.CREATE_TAKE);
        result.append("\n\n");
        result.append(GenericSQL.CREATE_TEACH);
        result.append("\n\n");
        result.append(GenericSQL.STUDENTS_NOT_TAKING_CS112);
        result.append("\n\n");
        
        genericSQLCount = 6;
        return result.toString();
    }

    public static void checkQueryTokenizer(IQueryTokenizer qt, 
                                           int stmtCount) 
    {
        int count = 0;
        while (qt.hasQuery()) {
            count++;
            System.out.println(" query: "+qt.nextQuery());
        }
        Assert.assertEquals(stmtCount, count);                
    }    
    
    
    public static void setGenericSQLCount(int genericSQLCount) {
        SQLUtil.genericSQLCount = genericSQLCount;
    }

    
    public static int getGenericSQLCount() {
        return genericSQLCount;
    }
    
    
    public static String createSQLFile(List<String> sqls, 
                                       boolean deleteOnExit) 
        throws IOException 
    {
        File f = File.createTempFile("test", ".sql");
        if (deleteOnExit) {
            f.deleteOnExit();
        }
        PrintWriter out = new PrintWriter(new FileWriter(f));
        for (String sql : sqls) {
            out.println(sql);
            out.println();            
        }
        out.close();
        String tmpFilename = f.getAbsolutePath();
        System.out.println("tmpFilename="+tmpFilename);
                
        return tmpFilename;
    }
    
}
