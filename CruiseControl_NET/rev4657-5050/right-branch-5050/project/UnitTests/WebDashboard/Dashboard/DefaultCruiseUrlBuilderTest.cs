using NMock;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
namespace ThoughtWorks.CruiseControl.UnitTests.WebDashboard.Dashboard
{
 [TestFixture]
 public class DefaultCruiseUrlBuilderTest
 {
  private DynamicMock urlBuilderMock;
  private DefaultCruiseUrlBuilder cruiseUrlBuilder;
  private DefaultServerSpecifier serverSpecifier;
  private IProjectSpecifier projectSpecifier;
  private DefaultBuildSpecifier buildSpecifier;
  [SetUp]
  public void Setup()
  {
   urlBuilderMock = new DynamicMock(typeof (IUrlBuilder));
   serverSpecifier = new DefaultServerSpecifier("myserver");
   projectSpecifier = new DefaultProjectSpecifier(serverSpecifier, "myproject");
   buildSpecifier = new DefaultBuildSpecifier(projectSpecifier, "mybuild");
   cruiseUrlBuilder = new DefaultCruiseUrlBuilder((IUrlBuilder) urlBuilderMock.MockInstance);
  }
  private void VerifyAll()
  {
   urlBuilderMock.Verify();
  }
  [Test]
  public void ShouldBuildServerUrlAddingCorrectlyFormattedAction()
  {
   urlBuilderMock.ExpectAndReturn("BuildUrl", "myUrl", "myAction", "", "server/myserver");
   string url = cruiseUrlBuilder.BuildServerUrl("myAction", serverSpecifier);
   Assert.AreEqual("myUrl", url);
   VerifyAll();
  }
  [Test]
  public void ShouldBuildServerUrlAddingCorrectlyFormattedActionAndQueryString()
  {
   urlBuilderMock.ExpectAndReturn("BuildUrl", "myUrl", "myAction", "query1=arg1", "server/myserver");
   string url = cruiseUrlBuilder.BuildServerUrl("myAction", serverSpecifier, "query1=arg1");
   Assert.AreEqual("myUrl", url);
   VerifyAll();
  }
  [Test]
  public void ShouldBuildProjectUrlAddingCorrectlyFormattedAction()
  {
   urlBuilderMock.ExpectAndReturn("BuildUrl", "myUrl", "myAction", "", "server/myserver/project/myproject");
   string url = cruiseUrlBuilder.BuildProjectUrl("myAction", projectSpecifier);
   Assert.AreEqual("myUrl", url);
   VerifyAll();
  }
  [Test]
  public void ShouldUrlEncodeProject()
  {
   urlBuilderMock.ExpectAndReturn("BuildUrl", "myUrl", "myAction", "", "server/myserver/project/myproject%232");
   projectSpecifier = new DefaultProjectSpecifier(serverSpecifier, "myproject#2");
   string url = cruiseUrlBuilder.BuildProjectUrl("myAction", projectSpecifier);
   Assert.AreEqual("myUrl", url);
   VerifyAll();
  }
  [Test]
  public void ShouldUrlEncodeProjectWithSpaces()
  {
   urlBuilderMock.ExpectAndReturn("BuildUrl", "myUrl", "myAction", "", "server/myserver/project/myproject+2");
   projectSpecifier = new DefaultProjectSpecifier(serverSpecifier, "myproject 2");
   string url = cruiseUrlBuilder.BuildProjectUrl("myAction", projectSpecifier);
   Assert.AreEqual("myUrl", url);
   VerifyAll();
  }
  [Test]
  public void ShouldBuildBuildUrlAddingCorrectlyFormattedAction()
  {
   urlBuilderMock.ExpectAndReturn("BuildUrl", "myUrl", "myAction", "", "server/myserver/project/myproject/build/mybuild");
   string url = cruiseUrlBuilder.BuildBuildUrl("myAction", buildSpecifier);
   Assert.AreEqual("myUrl", url);
   VerifyAll();
  }
  [Test]
  public void ShouldDelegateExtensionToSubBuilder()
  {
   urlBuilderMock.ExpectAndReturn("Extension", "foo");
   cruiseUrlBuilder.Extension = "foo";
   VerifyAll();
  }
 }
}
