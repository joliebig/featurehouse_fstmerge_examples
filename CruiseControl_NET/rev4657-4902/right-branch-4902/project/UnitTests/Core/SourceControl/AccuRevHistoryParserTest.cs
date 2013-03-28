using System.IO;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Sourcecontrol;
namespace ThoughtWorks.CruiseControl.UnitTests.Core.Sourcecontrol
{
 [TestFixture]
 public class AccuRevHistoryParserTest
 {
  AccuRevHistoryParser parser;
  [SetUp]
  protected void Setup()
  {
   parser = new AccuRevHistoryParser();
  }
  [Test]
  public void CanParse()
  {
            AccuRevMother histData = AccuRevMother.GetInstance();
      TextReader historyReader = histData.historyOutputReader;
            Modification[] mods = parser.Parse(historyReader, histData.oldestHistoryModification,
                                               histData.newestHistoryModification);
   Assert.IsNotNull(mods, "mods should not be null");
      Assert.AreEqual(histData.historyOutputModifications.Length, mods.Length);
      for (int i = 0; i < histData.historyOutputModifications.Length; i++)
      {
          Assert.AreEqual(histData.historyOutputModifications[i].ChangeNumber, mods[i].ChangeNumber);
          Assert.AreEqual(histData.historyOutputModifications[i].Comment, mods[i].Comment);
          Assert.AreEqual(histData.historyOutputModifications[i].FileName, mods[i].FileName);
          Assert.AreEqual(histData.historyOutputModifications[i].FolderName, mods[i].FolderName);
          Assert.AreEqual(histData.historyOutputModifications[i].ModifiedTime, mods[i].ModifiedTime);
          Assert.AreEqual(histData.historyOutputModifications[i].Type, mods[i].Type);
          Assert.AreEqual(histData.historyOutputModifications[i].UserName, mods[i].UserName);
      }
  }
 }
}
