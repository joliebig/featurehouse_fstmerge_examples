using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using WorldWind;
using WorldWind.Net;
using WorldWind.Renderable;
namespace WorldWind
{
 public class DynamicClouds : WorldWind.PluginEngine.Plugin
 {
  const string remoteImageUrl = "http://worldwind26.arc.nasa.gov/GlobalCloudsArchive/PastDay";
  const string archiveImageDirectoryPath = "Plugins\\DynamicClouds\\Images";
  DynamicCloudLayer m_dynamicCloudLayer = null;
  public override void Load()
  {
   m_dynamicCloudLayer = new DynamicCloudLayer(
    "Dynamic Clouds",
    ParentApplication.WorldWindow.CurrentWorld,
    remoteImageUrl,
    Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\" + archiveImageDirectoryPath);
   ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(m_dynamicCloudLayer);
  }
  public override void Unload()
  {
  }
 }
}
