using System;
using NMock;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Config;
namespace ThoughtWorks.CruiseControl.UnitTests.Core.Config
{
 [TestFixture]
 public class CachingConfigurationServiceTest
 {
  private DynamicMock slaveServiceMock;
  private CachingConfigurationService cachingConfigurationService;
  private DynamicMock configurationMock;
  private IConfiguration configuration;
  [SetUp]
  public void Setup()
  {
   slaveServiceMock = new DynamicMock(typeof(IConfigurationService));
   cachingConfigurationService = new CachingConfigurationService((IConfigurationService) slaveServiceMock.MockInstance);
   configurationMock = new DynamicMock(typeof(IConfiguration));
   configuration = (IConfiguration) configurationMock.MockInstance;
  }
  private void VerifyAll()
  {
   slaveServiceMock.Verify();
  }
  [Test]
  public void ShouldDelegateLoadRequests()
  {
   slaveServiceMock.ExpectAndReturn("Load", configuration);
   Assert.AreEqual(configuration, cachingConfigurationService.Load());
   VerifyAll();
  }
  [Test]
  public void ShouldCacheLoad()
  {
   slaveServiceMock.ExpectAndReturn("Load", configuration);
   cachingConfigurationService.Load();
   cachingConfigurationService.Load();
   VerifyAll();
  }
  [Test]
  public void ShouldDelegateSaveRequests()
  {
   slaveServiceMock.Expect("Save", configuration);
   cachingConfigurationService.Save(configuration);
   VerifyAll();
  }
  [Test]
  public void ShouldDelegateEventHanderRequests()
  {
   ConfigurationUpdateHandler handler = new ConfigurationUpdateHandler(HandlerTarget);
   slaveServiceMock.Expect("AddConfigurationUpdateHandler", handler);
   cachingConfigurationService.AddConfigurationUpdateHandler(handler);
   VerifyAll();
  }
  [Test]
  public void InvalidatesCacheIfSlaveServiceChanges()
  {
   SlaveServiceForTestingEvents slaveService = new SlaveServiceForTestingEvents();
   cachingConfigurationService = new CachingConfigurationService(slaveService);
   IConfiguration configuration2 = (IConfiguration) new DynamicMock(typeof(IConfiguration)).MockInstance;
   slaveService.configuration = configuration;
   Assert.AreEqual(configuration, cachingConfigurationService.Load());
   slaveService.handler();
   slaveService.configuration = configuration2;
   Assert.AreEqual(configuration2, cachingConfigurationService.Load());
   VerifyAll();
  }
  public void HandlerTarget()
  {
  }
  class SlaveServiceForTestingEvents : IConfigurationService
  {
   public IConfiguration configuration;
   public ConfigurationUpdateHandler handler;
   public IConfiguration Load()
   {
    return configuration;
   }
   public void Save(IConfiguration configuration)
   {
    throw new NotImplementedException();
   }
   public void AddConfigurationUpdateHandler(ConfigurationUpdateHandler handler)
   {
    this.handler = handler;
   }
      public void AddConfigurationSubfileLoadedHandler (
          ConfigurationSubfileLoadedHandler handler)
      {
      }
  }
 }
}
