using System; 
using System.IO; 
using System.Windows.Forms; 
using NUnit.Framework; namespace  RssBandit.UnitTests {
	
 [TestFixture] 
 public class  ShortcutManagerTests  : BaseTestFixture {
		
  [Test] 
  public  void TestInformational()
  {
   Console.WriteLine((int)Keys.KeyCode);
   Console.WriteLine((int)Keys.Alt);
   Console.WriteLine((int)Keys.Menu);
  }
 
  [Test] 
  public  void TestLoadValidSettings()
  {
   ShortcutHandler manager = new ShortcutHandler();
   using(FileStream stream = File.OpenRead(UNPACK_DESTINATION + @"\Settings\ValidShortcutSettings.xml"))
   {
    manager.Load(stream);
   }
   Assert.AreEqual(System.Windows.Forms.Shortcut.F1, manager.GetShortcut("cmdTestOne"));
   Assert.AreEqual(System.Windows.Forms.Shortcut.Ctrl4, manager.GetShortcut("cmdTestTwo"));
   Assert.IsTrue(manager.IsShortcutDisplayed("cmdTestTwo"));
   Assert.AreEqual(System.Windows.Forms.Shortcut.None, manager.GetShortcut("cmdNotInSettings"));
  }
 
  [Test] 
  public  void TestLoadValidSettingsWithNoWhitespace()
  {
   ShortcutHandler manager = new ShortcutHandler();
   using(FileStream stream = File.OpenRead(UNPACK_DESTINATION + @"\Settings\DefaultSettingsWithNoWhitespace.xml"))
   {
    manager.Load(stream);
   }
   Assert.AreEqual(Shortcut.None, manager.GetShortcut("cmdNotInSettings"));
   Assert.AreEqual(Shortcut.CtrlE, manager.GetShortcut("cmdExportFeeds"));
   Assert.IsTrue(manager.IsCommandInvoked("BrowserCreateNewTab", Keys.N | Keys.Control));
  }
 
  [Test] 
  public  void TestDefaultSettings()
  {
   ShortcutHandler manager = new ShortcutHandler();
   using(FileStream stream = File.OpenRead(UNPACK_DESTINATION + @"\Settings\DefaultSettings.xml"))
   {
    manager.Load(stream);
   }
   Assert.AreEqual(System.Windows.Forms.Shortcut.None, manager.GetShortcut("cmdNotInSettings"));
   Assert.IsTrue(manager.IsCommandInvoked("BrowserCreateNewTab", Keys.N | Keys.Control));
  }
 
  [SetUp] 
  public  void SetUp()
  {
   base.UnpackResourceDirectory("Settings");
  }
 
  [TearDown] 
  public  void TearDown()
  {
   DeleteDirectory(UNPACK_DESTINATION);
  }

	}

}
