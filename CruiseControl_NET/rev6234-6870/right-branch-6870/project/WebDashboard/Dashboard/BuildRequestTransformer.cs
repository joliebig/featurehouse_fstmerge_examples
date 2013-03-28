namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard
{
    using System;
    using System.Collections;
    using System.Web;
    using System.Web.Caching;
    using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    using System.Text;
    using System.IO;
    public class BuildRequestTransformer
        : IBuildLogTransformer
 {
  private readonly IMultiTransformer transformer;
  private readonly IBuildRetriever buildRetriever;
  public BuildRequestTransformer(IBuildRetriever buildRetriever, IMultiTransformer transformer)
  {
   this.buildRetriever = buildRetriever;
   this.transformer = transformer;
  }
        public string Transform(IBuildSpecifier buildSpecifier, string[] transformerFileNames, Hashtable xsltArgs, string sessionToken, string[] taskTypes)
  {
            var log = this.RetrieveLogData(buildSpecifier, sessionToken);
            if ((taskTypes != null) && (taskTypes.Length > 0))
            {
                var buildLog = new BuildLog(log);
                var logBuilder = new StringBuilder(log);
                logBuilder.Remove(logBuilder.Length - 17, 16);
                foreach (var taskType in taskTypes)
                {
                    this.RetrieveLogData(buildSpecifier, sessionToken, buildLog, taskType, logBuilder);
                }
                logBuilder.Append("</cruisecontrol>");
                log = logBuilder.ToString();
            }
   return transformer.Transform(log, transformerFileNames, xsltArgs);
  }
        private string RetrieveLogData(IBuildSpecifier buildSpecifier, string sessionToken)
        {
            var cache = HttpRuntime.Cache;
            var logKey = buildSpecifier.ProjectSpecifier.ServerSpecifier.ServerName +
                buildSpecifier.ProjectSpecifier.ProjectName +
                buildSpecifier.BuildName +
                (sessionToken ?? string.Empty);
            var logData = cache[logKey] as SynchronisedData<string>;
            if (logData == null)
            {
                logData = new SynchronisedData<string>();
                cache.Add(
                    logKey,
                    logData,
                    null,
                    Cache.NoAbsoluteExpiration,
                    new TimeSpan(1, 0, 0),
                    CacheItemPriority.AboveNormal,
                    null);
                logData.LoadData(() =>
                {
                    var buildLog = buildRetriever.GetBuild(buildSpecifier, sessionToken).Log;
                    return buildLog;
                });
            }
            else
            {
                logData.WaitForLoad(10000);
            }
            if (logData.Data == null)
            {
                cache.Remove(logKey);
                throw new ApplicationException("Unable to retrieve log data");
            }
            return logData.Data;
        }
        private void RetrieveLogData(IBuildSpecifier buildSpecifier, string sessionToken, BuildLog buildLog, string taskType, StringBuilder builder)
        {
            var outputs = buildLog.FindOutputOfTaskType(taskType);
            foreach (var output in outputs)
            {
                var stream = new MemoryStream();
                var isXml = (output.DataType == "text/xml") || (output.DataType == "data/xml");
                this.buildRetriever.GetFile(buildSpecifier, sessionToken, output.FileName, stream);
                if (!isXml)
                {
                    builder.Append("<data type=\"" + taskType + "\"><![CDATA[");
                }
                stream.Seek(0, SeekOrigin.Begin);
                using (var reader = new StreamReader(stream))
                {
                    builder.Append(reader.ReadToEnd());
                }
                if (!isXml)
                {
                    builder.Append("]]></data>");
                }
            }
        }
 }
}
