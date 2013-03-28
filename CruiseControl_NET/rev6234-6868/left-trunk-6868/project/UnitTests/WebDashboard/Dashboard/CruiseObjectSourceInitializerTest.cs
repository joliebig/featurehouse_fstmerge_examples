using System;
using System.Web;
using NUnit.Framework;
using Objection;
using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
using ThoughtWorks.CruiseControl.WebDashboard.Plugins.BuildReport;
namespace ThoughtWorks.CruiseControl.UnitTests.WebDashboard.Dashboard
{
 public class CruiseObjectSourceInitializerTest
 {
}
 public class StubBuildPlugin : IPlugin
 {
  public INamedAction[] NamedActions
  {
   get { return new INamedAction[] { new ImmutableNamedAction("MyPlugin", null) }; }
  }
  public string LinkDescription
  {
   get { throw new NotImplementedException(); }
  }
 }
}
