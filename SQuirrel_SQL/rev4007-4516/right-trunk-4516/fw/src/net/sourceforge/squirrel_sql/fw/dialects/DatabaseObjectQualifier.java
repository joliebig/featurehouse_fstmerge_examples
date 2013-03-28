package net.sourceforge.squirrel_sql.fw.dialects;


public class DatabaseObjectQualifier {
    private String catalog;
    private String schema;


    public DatabaseObjectQualifier() {
    }


    public DatabaseObjectQualifier(String catalog, String schema) {
        this.catalog = catalog;
        this.schema = schema;
    }


    public String getCatalog() {
        return catalog;
    }


    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }


    public String getSchema() {
        return schema;
    }


    public void setSchema(String schema) {
        this.schema = schema;
    }
}
