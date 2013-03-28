
package net.sourceforge.squirrel_sql.client.update.downloader;


public interface RetryStrategy
{
	
	boolean shouldTryAgain(int failureCount);

	
	long getTimeToWaitBeforeRetrying(int failureCount);
}
