
package net.sourceforge.squirrel_sql.fw.sql;

public class SQLExecutionException extends Throwable {
    
    private static final long serialVersionUID = -3594099581993936333L;
    
    
    private String postError = null;
    
    public SQLExecutionException(Throwable cause, String postError) {
        super(cause);
        this.postError = postError;
    }

    
    public void setPostError(String postError) {
        this.postError = postError;
    }

    
    public String getPostError() {
        return postError;
    }
    
    
}
