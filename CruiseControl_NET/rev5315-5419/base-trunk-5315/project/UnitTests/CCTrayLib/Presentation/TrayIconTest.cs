using System;
using System.Drawing;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.CCTrayLib.Presentation;
namespace ThoughtWorks.CruiseControl.UnitTests.CCTrayLib.Presentation
{
 [TestFixture]
 public class TrayIconTest : IIconProvider
 {
  private Icon icon;
  [Test]
  public void CanSubscribeToAnIconProvider()
  {
   TrayIcon trayIcon = new TrayIcon();
   Assert.IsNull( trayIcon.Icon );
   icon = ResourceProjectStateIconProvider.GRAY.Icon;
   trayIcon.IconProvider = this;
   Assert.AreSame( icon, trayIcon.Icon );
  }
  [Test]
  public void UpdatesIconWhenTheIconProviderChangesItsIcon()
  {
   TrayIcon trayIcon = new TrayIcon();
   icon = ResourceProjectStateIconProvider.GRAY.Icon;
   trayIcon.IconProvider = this;
   Assert.AreSame( icon, trayIcon.Icon );
   icon = ResourceProjectStateIconProvider.RED.Icon;
   if (IconChanged != null)
    IconChanged( this, EventArgs.Empty );
   Assert.AreSame( icon, trayIcon.Icon );
  }
  public Icon Icon
  {
   get { return icon; }
  }
  public event EventHandler IconChanged;
 }
}
