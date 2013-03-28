namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard
{
    using System;
    using System.Collections;
    using System.Web;
    using System.Web.Caching;
    using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
    using ThoughtWorks.CruiseControl.Core.Util;
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
        public string Transform(IBuildSpecifier buildSpecifier, string[] transformerFileNames, Hashtable xsltArgs, string sessionToken)
  {
            var log = this.RetrieveLogData(buildSpecifier, sessionToken);
   return transformer.Transform(log, transformerFileNames, xsltArgs);
  }
        private string RetrieveLogData(IBuildSpecifier buildSpecifier, string sessionToken)
        {
            var cache = HttpRuntime.Cache;
            var logKey = buildSpecifier.ProjectSpecifier.ServerSpecifier.ServerName +
                buildSpecifier.ProjectSpecifier.ProjectName +
                buildSpecifier.BuildName +
                (sessionToken ?? string.Empty);
            var logData = cache[logKey] as SynchronisedData;
            if (logData == null)
            {
                logData = new SynchronisedData();
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
            return logData.Data as string;
        }
 }
}
