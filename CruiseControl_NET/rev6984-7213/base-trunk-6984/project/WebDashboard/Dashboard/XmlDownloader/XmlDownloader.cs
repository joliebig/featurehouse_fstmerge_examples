using System;
using System.Web;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.WebDashboard.Config;
using ThoughtWorks.CruiseControl.WebDashboard.IO;
using ThoughtWorks.CruiseControl.WebDashboard.MVC;
namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard.XmlDownloader
{
 public class XmlDownloader : IHttpHandler
 {
  public void ProcessRequest(HttpContext context)
  {
   ObjectGiver objectGiver = CreateObjectGiver(context);
   ICruiseRequest cruiseRequest = (ICruiseRequest) objectGiver.GiveObjectByType(typeof(ICruiseRequest));
   if (cruiseRequest.ServerName == "" || cruiseRequest.ProjectName == "" || cruiseRequest.BuildName == "")
   {
    throw new Exception("All of Server, Project and Build Names must be specified on request in order to retrieve a build log");
   }
   string log = ((IBuildRetriever) objectGiver.GiveObjectByType(typeof(IBuildRetriever))).GetBuild(cruiseRequest.BuildSpecifier).Log;
   context.Response.ContentType = "Text/XML";
   context.Response.Write(log);
   context.Response.Flush();
  }
  public bool IsReusable
  {
   get { return true; }
  }
  private ObjectGiver CreateObjectGiver(HttpContext context)
  {
   ObjectGiverAndRegistrar giverAndRegistrar = new ObjectGiverAndRegistrar();
   HttpRequest request = context.Request;
   giverAndRegistrar.AddTypedObject(typeof(HttpRequest), request);
   giverAndRegistrar.AddTypedObject(typeof(HttpContext), context);
   giverAndRegistrar.AddTypedObject(typeof(ObjectGiver), giverAndRegistrar);
   giverAndRegistrar.AddTypedObject(typeof(IRequest), new AggregatedRequest(new NameValueCollectionRequest(request.Form), new NameValueCollectionRequest(request.QueryString)));
   giverAndRegistrar.SetImplementationType(typeof(IPathMapper), typeof(HttpPathMapper));
   giverAndRegistrar.SetDependencyImplementationForType(typeof(PathMappingMultiTransformer), typeof(IMultiTransformer), typeof (HtmlAwareMultiTransformer));
   IConfigurationGetter configurationGetter = (IConfigurationGetter) giverAndRegistrar.GiveObjectByType(typeof(IConfigurationGetter));
   if (configurationGetter == null)
   {
    throw new CruiseControlException("Unable to instantiate configuration getter");
   }
   return giverAndRegistrar;
  }
 }
}
