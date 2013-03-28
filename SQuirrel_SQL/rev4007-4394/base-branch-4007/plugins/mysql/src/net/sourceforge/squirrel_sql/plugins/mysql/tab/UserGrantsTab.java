package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class UserGrantsTab extends BaseSQLTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(UserGrantsTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("mysql.grants");
		
		String HINT = s_stringMgr.getString("mysql.hintGrants");
	}

	public UserGrantsTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected String getSQL()
	{
		final String db = getDatabaseObjectInfo().getQualifiedName();
        
        
		
        return "show grants for " + db;
	}
    
    private static String fixQuotes(String user) {
        String[] parts = user.split("\\@");
        String first = "";
        if (parts[0].length() > 1) {
            first = parts[0] + "'";
        } else {
            first = "'%'";
        }
        String last = "";
        if (parts[1].length() > 1) {
            last = "'" + parts[1];
        } else {
            last = "'%'";
        }
        return first + "@" + last;
        
    }
    
    public static void main(String[] args) {
        String newString = fixQuotes("'root@'");
        System.out.println("fixQuotes(@localhost)="+newString);
    }
}
