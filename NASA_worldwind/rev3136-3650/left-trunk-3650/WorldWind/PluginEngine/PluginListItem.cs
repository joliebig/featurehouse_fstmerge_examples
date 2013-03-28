using System;
using System.Windows.Forms;
namespace WorldWind.PluginEngine
{
 public class PluginListItem : ListViewItem
 {
  PluginInfo pluginInfo;
  public PluginInfo PluginInfo
  {
   get
   {
    return pluginInfo;
   }
  }
  public new string Name
  {
   get
   {
    return pluginInfo.Name;
   }
  }
  public PluginListItem(PluginInfo pi)
  {
   this.pluginInfo = pi;
   this.Text = pi.Name;
   this.Checked = pi.IsLoadedAtStartup;
  }
 }
}
