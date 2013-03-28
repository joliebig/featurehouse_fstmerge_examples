package net.sourceforge.squirrel_sql.fw.util;



interface ITaskThreadPoolCallback {
	void incrementFreeThreadCount();
	void decrementFreeThreadCount();

	Runnable nextTask();

	void showMessage(Throwable th);
}

