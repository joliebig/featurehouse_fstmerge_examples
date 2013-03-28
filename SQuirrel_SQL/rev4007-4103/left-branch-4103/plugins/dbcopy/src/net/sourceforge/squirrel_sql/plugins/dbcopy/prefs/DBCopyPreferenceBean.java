package net.sourceforge.squirrel_sql.plugins.dbcopy.prefs;

import java.io.Serializable;


public class DBCopyPreferenceBean implements Cloneable, 
                                             Serializable {
	static final String UNSUPPORTED = "Unsupported";

    
	private String _clientName;

	
	private String _clientVersion;

    
    private boolean useFileCaching = true;
    
    
    private int fileCacheBufferSize = 8192;
    
    
    private boolean useTruncate = true;
        
    
    private boolean autoCommitEnabled = true;
    
    
    private int commitCount = 100;
    
    
    private boolean writeScript = false;
    
    
    private boolean copyData = true;
    
    
    private boolean copyIndexDefs = true;
    
    
    private boolean copyForeignKeys = true;
    
    
    private boolean copyPrimaryKeys = true;
    
    
    private boolean pruneDuplicateIndexDefs = true;
    
    
    private boolean commitAfterTableDefs = true;
    
     
    private boolean promptForDialect = false;
    
    
    private boolean checkKeywords = true;
    
    
    private boolean testColumnNames = true;
        
    
    private int selectFetchSize = 1000;
    
    
    private boolean delayBetweenObjects = false;
    
    
    private long tableDelayMillis = 0;
    
    
    private long recordDelayMillis = 0;
    
	public DBCopyPreferenceBean() {
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

    
    public void setUseFileCaching(boolean useFileCaching) {
        this.useFileCaching = useFileCaching;
    }

    
    public boolean isUseFileCaching() {
        return useFileCaching;
    }

    
    public void setFileCacheBufferSize(int fileCacheBufferSize) {
        this.fileCacheBufferSize = fileCacheBufferSize;
    }

    
    public int getFileCacheBufferSize() {
        return fileCacheBufferSize;
    }

    
    public void setUseTruncate(boolean useTruncate) {
        this.useTruncate = useTruncate;
    }

    
    public boolean isUseTruncate() {
        return useTruncate;
    }

    
    public void setAutoCommitEnabled(boolean autoCommitEnabled) {
        this.autoCommitEnabled = autoCommitEnabled;
    }

    
    public boolean isAutoCommitEnabled() {
        return autoCommitEnabled;
    }

    
    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }

    
    public int getCommitCount() {
        return commitCount;
    }

    
    public void setWriteScript(boolean writeScript) {
        this.writeScript = writeScript;
    }

    
    public boolean isWriteScript() {
        return writeScript;
    }

    
    public void setCopyData(boolean copyData) {
        this.copyData = copyData;
    }

    
    public boolean isCopyData() {
        return copyData;
    }

    
    public void setCopyIndexDefs(boolean copyIndexDefs) {
        this.copyIndexDefs = copyIndexDefs;
    }

    
    public boolean isCopyIndexDefs() {
        return copyIndexDefs;
    }

    
    public void setCopyForeignKeys(boolean copyForeignKeys) {
        this.copyForeignKeys = copyForeignKeys;
    }

    
    public boolean isCopyForeignKeys() {
        return copyForeignKeys;
    }

    
    public void setPruneDuplicateIndexDefs(boolean pruneDuplicateIndexDefs) {
        this.pruneDuplicateIndexDefs = pruneDuplicateIndexDefs;
    }

    
    public boolean isPruneDuplicateIndexDefs() {
        return pruneDuplicateIndexDefs;
    }

    
    public void setCommitAfterTableDefs(boolean commitAfterTableDefs) {
        this.commitAfterTableDefs = commitAfterTableDefs;
    }

    
    public boolean isCommitAfterTableDefs() {
        return commitAfterTableDefs;
    }

    
    public void setPromptForDialect(boolean promptForDialect) {
        this.promptForDialect = promptForDialect;
    }

    
    public boolean isPromptForDialect() {
        return promptForDialect;
    }

    
    public void setCheckKeywords(boolean checkKeywords) {
        this.checkKeywords = checkKeywords;
    }

    
    public boolean isCheckKeywords() {
        return checkKeywords;
    }

    
    public void setTestColumnNames(boolean testColumnNames) {
        this.testColumnNames = testColumnNames;
    }

    
    public boolean isTestColumnNames() {
        return testColumnNames;
    }

    
    public void setCopyPrimaryKeys(boolean copyPrimaryKeys) {
        this.copyPrimaryKeys = copyPrimaryKeys;
    }

    
    public boolean isCopyPrimaryKeys() {
        return copyPrimaryKeys;
    }

    
    public void setSelectFetchSize(int selectFetchSize) {
        this.selectFetchSize = selectFetchSize;
    }

    
    public int getSelectFetchSize() {
        return selectFetchSize;
    }

    
    public void setTableDelayMillis(long tableDelayMillis) {
        this.tableDelayMillis = tableDelayMillis;
    }

    
    public long getTableDelayMillis() {
        return tableDelayMillis;
    }

    
    public void setRecordDelayMillis(long recordDelayMillis) {
        this.recordDelayMillis = recordDelayMillis;
    }

    
    public long getRecordDelayMillis() {
        return recordDelayMillis;
    }

    
    public void setDelayBetweenObjects(boolean delayBetweenObjects) {
        this.delayBetweenObjects = delayBetweenObjects;
    }

    
    public boolean isDelayBetweenObjects() {
        return delayBetweenObjects;
    }
 
}

