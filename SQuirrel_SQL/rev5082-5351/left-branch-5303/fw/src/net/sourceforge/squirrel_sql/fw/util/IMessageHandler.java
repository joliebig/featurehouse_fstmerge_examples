package net.sourceforge.squirrel_sql.fw.util;



public interface IMessageHandler {
    
    void showMessage(Throwable th, ExceptionFormatter formatter);

    
    void showMessage(String msg);

    
    void showErrorMessage(Throwable th, ExceptionFormatter formatter);

    
    void showErrorMessage(String msg);

    void showWarningMessage(String msg);    

}