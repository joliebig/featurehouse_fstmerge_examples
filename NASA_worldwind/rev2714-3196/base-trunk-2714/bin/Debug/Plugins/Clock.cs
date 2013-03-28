using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System;
using System.Drawing;
using WorldWind;
using WorldWind;
using WorldWind.Renderable;
namespace Mashiharu.PluginSample
{
 public class ClockPlugin : WorldWind.PluginEngine.Plugin
 {
  Clock clock;
  public override void Load()
  {
   clock = new Clock(Application);
   Application.WorldWindow.CurrentWorld.RenderableObjects.Add(clock);
  }
  public override void Unload()
  {
   Application.WorldWindow.CurrentWorld.RenderableObjects.Remove(clock.Name);
  }
 }
 public class Clock : RenderableObject
 {
  int color = Color.Yellow.ToArgb();
  const int distanceFromCorner = 5;
  MainApplication ww;
  public Clock(MainApplication app) : base("Clock", Vector3.Empty, Quaternion.Identity)
  {
   this.ww = app;
   this.RenderPriority = RenderPriority.Icons;
   this.IsOn = true;
  }
  public void Load()
  {
   ww.WorldWindow.CurrentWorld.RenderableObjects.Add(this);
  }
  public override void Render(DrawArgs drawArgs)
  {
   string text = DateTime.Now.ToString();
   Rectangle bounds = drawArgs.defaultDrawingFont.MeasureString(null, text, DrawTextFormat.None, 0);
   drawArgs.defaultDrawingFont.DrawText(null, text,
    drawArgs.screenWidth-bounds.Width-distanceFromCorner, drawArgs.screenHeight-bounds.Height-distanceFromCorner,
    color );
  }
  public override void Initialize(DrawArgs drawArgs)
  {
  }
  public override void Update(DrawArgs drawArgs)
  {
  }
  public override void Dispose()
  {
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return false;
  }
 }
}
