package net.sourceforge.squirrel_sql.plugins.oracle.prefs;


import java.io.Serializable;
import java.util.TimeZone;

import net.sourceforge.squirrel_sql.fw.preferences.BaseQueryTokenizerPreferenceBean;


public class OraclePreferenceBean extends BaseQueryTokenizerPreferenceBean implements Cloneable, Serializable {

    static final long serialVersionUID = 5818886723165356478L;

    static final String UNSUPPORTED = "Unsupported";

    private boolean excludeRecycleBinTables = true;
    
    private boolean showErrorOffset = true;
    
    private boolean initSessionTimezone = true;
    
    private String sessionTimezone = TimeZone.getDefault().getID();
    
    public OraclePreferenceBean() {
        super();
        statementSeparator = ";";
        procedureSeparator = "/";
        lineComment = "--";
        removeMultiLineComments = false;
        installCustomQueryTokenizer = true;
    }

    
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError(ex.getMessage()); 
        }
    }

    
    public void setExcludeRecycleBinTables(boolean excludeRecycleBinTables) {
        this.excludeRecycleBinTables = excludeRecycleBinTables;
    }

    
    public boolean isExcludeRecycleBinTables() {
        return excludeRecycleBinTables;
    }

   
   public boolean isShowErrorOffset() {
      return showErrorOffset;
   }

   
   public void setShowErrorOffset(boolean showErrorOffset) {
      this.showErrorOffset = showErrorOffset;
   }

	
	public void setSessionTimezone(String sessionTimezone)
	{
		this.sessionTimezone = sessionTimezone;
	}

	
	public String getSessionTimezone()
	{
		return sessionTimezone;
	}

	
	public void setInitSessionTimezone(boolean initSessionTimezone)
	{
		this.initSessionTimezone = initSessionTimezone;
	}

	
	public boolean getInitSessionTimezone()
	{
		return initSessionTimezone;
	}

}
