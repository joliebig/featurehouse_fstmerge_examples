using NMock;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.UnitTests.Core.Util;
namespace ThoughtWorks.CruiseControl.UnitTests.Core.Config
{
 [TestFixture]
 public class FileWatcherConfigurationServiceTest
 {
  private FileWatcherConfigurationService fileService;
  private MockFileWatcher fileWatcher;
  [SetUp]
  public void Setup()
  {
   fileWatcher = new MockFileWatcher();
   IMock mockService = new DynamicMock(typeof (IConfigurationService));
   fileService = new FileWatcherConfigurationService((IConfigurationService) mockService.MockInstance, fileWatcher);
  }
  [Test]
  public void CallsUpdateHandlersWhenFileWatcherChanges()
  {
   fileService.AddConfigurationUpdateHandler(new ConfigurationUpdateHandler(OnUpdate));
   updateCalled = false;
   fileWatcher.RaiseEvent();
   Assert.IsTrue(updateCalled);
  }
  private bool updateCalled = false;
  public void OnUpdate()
  {
   updateCalled = true;
  }
 }
}
