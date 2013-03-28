package net.sourceforge.squirrel_sql.plugins.derby.tokenizer;


import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.CREATE_COURSES;
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.CREATE_PROFESSOR;
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.CREATE_STUDENT;
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.CREATE_TAKE;
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.CREATE_TEACH;
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.STUDENTS_NOT_TAKING_CS112;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtil;

public class DerbyQueryTokenizerTest extends TestCase {

    static String nullSQL = null;       
    static String tmpFilename = null;
    static boolean removeMultilineComment = true;
    
    QueryTokenizer qt = null;
    static int sqlFileStmtCount = 0;
    
    
    
    public void setUp() throws Exception {
        createSQLFile();
    }
    
    public void tearDown() {
        
    }
    
    public void testHasQuery() {
        qt = new DerbyQueryTokenizer(";", "--", false);
        qt.setScriptToTokenize(CREATE_STUDENT);
        SQLUtil.checkQueryTokenizer(qt, 1);
        
        qt = new DerbyQueryTokenizer(";", "--", false);
        qt.setScriptToTokenize(CREATE_COURSES);
        SQLUtil.checkQueryTokenizer(qt, 1);        
    }

    public void testGenericSQL() {
        String script = SQLUtil.getGenericSQLScript();
        qt = new DerbyQueryTokenizer(";", "--", false);
        qt.setScriptToTokenize(script);
        SQLUtil.checkQueryTokenizer(qt, SQLUtil.getGenericSQLCount());
    }
        
    public void testHasQueryFromFile() {
        String fileSQL = "run '" + tmpFilename + "'\n";
        qt = new DerbyQueryTokenizer(";", "--", false);
        qt.setScriptToTokenize(fileSQL);
        SQLUtil.checkQueryTokenizer(qt, sqlFileStmtCount);
    }
    
    private static void createSQLFile() throws IOException {
        if (tmpFilename != null) {
            return;
        }
        String[] sqls = new String[] {
                CREATE_COURSES, 
                CREATE_PROFESSOR,
                CREATE_TAKE,
                CREATE_TEACH,
                STUDENTS_NOT_TAKING_CS112,                
        };
        
        tmpFilename = SQLUtil.createSQLFile( Arrays.asList(sqls), true);        
        sqlFileStmtCount = sqls.length;
    }
    
}
