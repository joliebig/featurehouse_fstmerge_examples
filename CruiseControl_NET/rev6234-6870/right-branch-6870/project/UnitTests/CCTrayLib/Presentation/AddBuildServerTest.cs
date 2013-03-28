using System;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.CCTrayLib.Configuration;
using ThoughtWorks.CruiseControl.CCTrayLib.Presentation;
namespace ThoughtWorks.CruiseControl.UnitTests.CCTrayLib.Presentation
{
 [TestFixture]
 public class AddBuildServerTest
 {
  [Test]
  [Explicit]
  public void ShowDialogForInteractiveTesting()
  {
   AddBuildServer addBuildServer = new AddBuildServer(null);
   BuildServer server = addBuildServer.ChooseNewBuildServer(null);
   Console.WriteLine(server);
  }
 }
}
