namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard
{
    using System;
    using System.Collections;
    using System.Web;
    using System.Web.Caching;
    using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
    using ThoughtWorks.CruiseControl.Core.Util;
    using System.Configuration;
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
            var buildLog = buildRetriever.GetBuild(buildSpecifier, sessionToken).Log;
            return transformer.Transform(buildLog, transformerFileNames, xsltArgs);
        }
    }
}
