using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public class ListenerFile
    {
        public static void WriteInfo(IIntegrationResult result, string message)
        {
            System.Text.StringBuilder ListenData = new StringBuilder();
            ListenData.AppendLine("<data>");
            ListenData.AppendLine(string.Format("<Item Time=\"{0}\" Data=\"{1}\" />",
                                    System.DateTime.Now.ToString("yyyy-MM-dd hh:mm:ss"),
                                    CleanUpMessageForXMLLogging(message)));
            ListenData.AppendLine("</data>");
        }
        public static void RemoveListenerFile(string listenerFileLocation)
        {
            const int MaxAmountOfRetries = 10;
            int RetryCounter = 0;
            while (System.IO.File.Exists(listenerFileLocation) && (RetryCounter <= MaxAmountOfRetries))
            {
                try
                {
                    System.IO.File.Delete(listenerFileLocation);
                }
                catch (Exception e)
                {
                    RetryCounter += 1;
                    System.Threading.Thread.Sleep(200);
                    if (RetryCounter > MaxAmountOfRetries)
                        throw new CruiseControlException(
                            string.Format("Failed to delete {0} after {1} attempts", listenerFileLocation, RetryCounter), e);
                }
            }
        }
        private static string CleanUpMessageForXMLLogging(string msg)
        {
            return msg.Replace("\"", string.Empty);
        }
    }
}
