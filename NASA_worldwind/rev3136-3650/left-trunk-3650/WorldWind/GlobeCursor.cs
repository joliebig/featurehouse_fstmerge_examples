using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.PluginEngine;
namespace Withak.Plugins
{
 public class GlobeCursorPlugin : WorldWind.PluginEngine.Plugin
 {
  KMLPlugin.RIcon ic;
  KMLPlugin.RIcons ics;
  Bitmap cursBmp = new Bitmap("Plugins\\cursoricon.png");
  public override void Load()
  {
   ics = new KMLPlugin.RIcons("Globe cursor");
   ic = new KMLPlugin.RIcon("", 0f, 0f, "", 0f);
   ic.Image = cursBmp;
   ic.Width = 16;
   ic.Height = 16;
   ics.Add(ic);
   ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(ics);
   ParentApplication.WorldWindow.MouseMove += new MouseEventHandler(MouseMove);
   base.Load();
  }
  public override void Unload()
  {
   ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Remove(ics);
   ParentApplication.WorldWindow.MouseMove -= new MouseEventHandler(MouseMove);
   base.Unload();
  }
  public void MouseMove(object sender, MouseEventArgs e)
  {
   Angle lat,lon = Angle.NaN;
   ParentApplication.WorldWindow.DrawArgs.WorldCamera.PickingRayIntersection(
    e.X,e.Y,out lat, out lon);
   ic.SetPosition((float)lat.Degrees, (float)lon.Degrees, 0f);
  }
 }
}
