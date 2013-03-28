package net.sourceforge.pmd.util.viewer.model;


public class ViewerModelEvent {
    
    public static final int CODE_RECOMPILED = 1;
    
    public static final int NODE_SELECTED = 2;
    
    public static final int PATH_EXPRESSION_APPENDED = 3;
    
    public static final int PATH_EXPRESSION_EVALUATED = 4;
    private Object source;
    private int reason;
    private Object parameter;

    
    public ViewerModelEvent(Object source, int reason) {
        this(source, reason, null);
    }

    
    public ViewerModelEvent(Object source, int reason, Object parameter) {
        this.source = source;
        this.reason = reason;
        this.parameter = parameter;
    }

    public int getReason() {
        return reason;
    }

    public Object getSource() {
        return source;
    }

    public Object getParameter() {
        return parameter;
    }
}
