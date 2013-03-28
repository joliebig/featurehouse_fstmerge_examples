package net.sourceforge.squirrel_sql.plugins.dbinfo;

import java.io.Serializable;

public class DBInfo implements Serializable {
    private String _author;
    private String _title;

    private String _procSourceSql;

    public DBInfo() {
        super();
    }

    public String getAuthor() {
        return _author;
    }

    public void setAuthor(String value) {
        _author = value;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String value) {
        _title = value;
    }

    public String getProcSourceSql() {
        return _procSourceSql;
    }

    public void setProcSourceSql(String value) {
        _procSourceSql = value;
    }

    public static class DBInfoEntry implements Serializable {
        private String _title;
        private String _sql;
    }
}

