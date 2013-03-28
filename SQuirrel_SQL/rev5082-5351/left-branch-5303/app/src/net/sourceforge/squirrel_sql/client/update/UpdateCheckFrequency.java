
package net.sourceforge.squirrel_sql.client.update;


public enum UpdateCheckFrequency
{
	DAILY, 
	WEEKLY, 
	STARTUP; 

	
	public long DAY_DURATION = 1000 * 60 * 60 * 24;

	
	public long WEEK_DURATION = 7 * DAY_DURATION;

	
	public boolean isTimeForUpdateCheck(long delta)
	{
		if (this == DAILY && delta > DAY_DURATION) { return true; }
		if (this == WEEKLY && delta > WEEK_DURATION) { return true; }
		return false;
	}

	
	public static UpdateCheckFrequency getEnumForString(String value)
	{
		if ("daily".equalsIgnoreCase(value)) { return UpdateCheckFrequency.DAILY; }
		if ("startup".equalsIgnoreCase(value)) { return UpdateCheckFrequency.STARTUP; }
		if ("weekly".equalsIgnoreCase(value)) { return UpdateCheckFrequency.WEEKLY; }
		throw new IllegalArgumentException("Uknown update check frequency: " + value);

	}
}
