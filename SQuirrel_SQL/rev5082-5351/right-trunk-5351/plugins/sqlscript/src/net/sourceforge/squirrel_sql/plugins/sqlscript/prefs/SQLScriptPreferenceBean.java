package net.sourceforge.squirrel_sql.plugins.sqlscript.prefs;

import java.io.Serializable;


public class SQLScriptPreferenceBean implements Cloneable, 
                                             Serializable {
	static final String UNSUPPORTED = "Unsupported";

    
	private String _clientName;

	
	private String _clientVersion;

    
    private boolean qualifyTableNames = true;
    private boolean useDoubleQuotes = true;



    public static final int NO_ACTION = 0;
    
    public static final int CASCADE_DELETE = 1;
    
    public static final int SET_DEFAULT = 2;
    
    public static final int SET_NULL = 3;
    
    private int deleteAction = NO_ACTION;
    
    private int updateAction = NO_ACTION;
    
    
    private boolean deleteRefAction = false;
    
    
    private boolean updateRefAction = false;

	public SQLScriptPreferenceBean() {
		super();
	}

	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex.getMessage()); 
		}
	}

	
	public String getClientName() {
		return _clientName;
	}

	
	public void setClientName(String value) {
		_clientName = value;
	}

	
	public String getClientVersion() {
		return _clientVersion;
	}

	
	public void setClientVersion(String value) {
		_clientVersion = value;
	}

    
    public void setQualifyTableNames(boolean qualifyTableNames) {
        this.qualifyTableNames = qualifyTableNames;
    }

    
    public boolean isQualifyTableNames() {
        return qualifyTableNames;
    }

    public void setDeleteRefAction(boolean deleteRefAction) {
        this.deleteRefAction = deleteRefAction;
    }

    public boolean isDeleteRefAction() {
        return deleteRefAction;
    }

    public void setDeleteAction(int action) {
        this.deleteAction = action;
    }

    public int getDeleteAction() {
        return deleteAction;
    }

    public void setUpdateAction(int updateAction) {
        this.updateAction = updateAction;
    }

    public int getUpdateAction() {
        return updateAction;
    }

    public void setUpdateRefAction(boolean updateRefAction) {
        this.updateRefAction = updateRefAction;
    }

    public boolean isUpdateRefAction() {
        return updateRefAction;
    }

    public String getRefActionByType(int type) {
        switch (type) {
            case NO_ACTION:
                return "NO ACTION";
            case CASCADE_DELETE:
                return "CASCADE";
            case SET_DEFAULT:
                return "SET DEFAULT";
            case SET_NULL:
                return "SET NULL";
            default:
                return "NO ACTION";
        }
    }

   public boolean isUseDoubleQuotes()
   {
      return useDoubleQuotes;
   }

   public void setUseDoubleQuotes(boolean b)
   {
      useDoubleQuotes = b;
   }


}

