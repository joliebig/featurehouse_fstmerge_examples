
package net.sourceforge.squirrel_sql.fw.util;

import net.sourceforge.squirrel_sql.client.session.ISession;


public class MockMessageHandler implements IMessageHandler {

	private boolean showMessages = false;
	
	private boolean showWarningMessages = false;
	
	private boolean showErrorMessages = false;
	
    public void showMessage(Throwable th, ExceptionFormatter formatter) {
    	if (showMessages) {
    		System.out.println(
    			"MockMessageHandler.showMessage(Throwable): th.getMessage="+
    			th.getMessage());
    	}
    }

    public void showMessage(String msg) {
    	if (showMessages) {
    		System.out.println(
    			"MockMessageHandler.showMessage(Throwable): msg="+msg);
    	}
    }

    public void showErrorMessage(Throwable th, ExceptionFormatter formatter) {
    	if (showErrorMessages) {
    		System.out.println(
    			"MockMessageHandler.showErrorMessage(Throwable): th.getMessage="+
    			th.getMessage());
    	}
    }

    public void showErrorMessage(String msg) {
    	if (showErrorMessages) {
    		System.out.println(
    			"MockMessageHandler.showErrorMessage(String): msg="+msg);
    	}
    }

    
    public void showWarningMessage(String msg) {
    	if (showWarningMessages) {
    		System.out.println(
    			"MockMessageHandler.showWarningMessage(String): msg="+msg);
    	}
    }

	
	public void setShowMessages(boolean showMessages) {
		this.showMessages = showMessages;
	}

	
	public boolean isShowMessages() {
		return showMessages;
	}

	
	public void setShowWarningMessages(boolean showWarningMessages) {
		this.showWarningMessages = showWarningMessages;
	}

	
	public boolean isShowWarningMessages() {
		return showWarningMessages;
	}

	
	public void setShowErrorMessages(boolean showErrorMessages) {
		this.showErrorMessages = showErrorMessages;
	}

	
	public boolean isShowErrorMessages() {
		return showErrorMessages;
	}

	
	public void setExceptionFormatter(ExceptionFormatter formatter, ISession session) {
	    
    }

    
    public ExceptionFormatter getExceptionFormatter() {
        throw new UnsupportedOperationException();
    }

    
}
