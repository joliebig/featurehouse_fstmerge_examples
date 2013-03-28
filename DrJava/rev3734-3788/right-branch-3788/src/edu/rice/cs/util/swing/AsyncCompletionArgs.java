

package edu.rice.cs.util.swing;


public class AsyncCompletionArgs<R> {

	private R _result;

	private Exception _caughtException;

	private boolean _cancelRequested;

	public AsyncCompletionArgs(R result, boolean cancelRequested) {
		this(result, null, cancelRequested);
	}

	public AsyncCompletionArgs(R result, Exception caughtException, boolean wasCanceled) {
		_result = result;
		_caughtException = caughtException;
		_cancelRequested = wasCanceled;
	}

	
	public R getResult() {
		return _result;
	}

	
	public Exception getCaughtException() {
		return _caughtException;
	}

	
	public void throwCaughtException() throws Exception {
		if (_caughtException != null) {
			throw _caughtException;
		}
	}

	
	public boolean cancelRequested() {
		return _cancelRequested;
	}
}
