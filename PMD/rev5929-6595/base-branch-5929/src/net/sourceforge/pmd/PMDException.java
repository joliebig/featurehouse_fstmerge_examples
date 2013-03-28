
package net.sourceforge.pmd;


public class PMDException extends Exception {
    private static final long serialVersionUID = 6938647389367956874L;

    private int severity;

    public PMDException(String message) {
        super(message);
    }

    public PMDException(String message, Exception reason) {
        super(message, reason);
    }

    
    @Deprecated
    public Exception getReason() {
        return (Exception) getCause();
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {
        return severity;
    }
}
