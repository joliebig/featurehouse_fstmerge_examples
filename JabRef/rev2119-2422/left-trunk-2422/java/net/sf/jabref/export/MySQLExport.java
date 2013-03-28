package net.sf.jabref.export;



import net.sf.jabref.*;
import net.sf.jabref.groups.*;
import net.sf.jabref.sql.SQLutil;
import java.util.Set;


public class MySQLExport extends ExportFormat {

    public MySQLExport() {
        super(Globals.lang("MySQL database"), "mysql", null, null, ".sql");
    }

    
    public void performExport(final BibtexDatabase database,
        final MetaData metaData, final String file, final String encoding,
        Set<String> keySet) throws Exception {

        SQLutil.exportDatabase(database, metaData, keySet, file, SQLutil.DBTYPE.MYSQL);

    }


}