
package net.sourceforge.squirrel_sql.client.update.downloader;

public class DefaultRetryStrategyImpl implements RetryStrategy
{

	
	@Override
	public long getTimeToWaitBeforeRetrying(int failureCount)
	{
		return (failureCount + 1) * 3000;
	}

	
	@Override
	public boolean shouldTryAgain(int failureCount)
	{
		return failureCount <= 3;
	}

}
