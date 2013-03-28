using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Publishers;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.UnitTests.Core.Publishers
{
 [TestFixture]
 public class RSSPublisherTest : IntegrationFixture
 {
  IIntegrationResult result;
  RssPublisher publisher;
  [SetUp]
  public void Setup()
  {
   publisher = new RssPublisher();
  }
        private static Modification[] OneModification()
        {
            Modification[] mods = new Modification[1];
            mods[0] = new Modification();
            mods[0].Comment = "some comment";
            mods[0].FileName = "thecode.cs";
            mods[0].FolderName = "$/SomeMainFolder/TheFolder";
            mods[0].Type = "Modified";
            mods[0].UserName = "Coder";
            return mods;
        }
    }
}
