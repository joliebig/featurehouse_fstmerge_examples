
package net.sourceforge.squirrel_sql.client.update.async;

import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;


public interface UpdateCheckRunnableCallback
{
	
	void updateCheckComplete(boolean isUpdateToDate, ChannelXmlBean installedChannelXmlBean,
		ChannelXmlBean currentChannelXmlBean);

	
	void updateCheckFailed(Exception e);
}