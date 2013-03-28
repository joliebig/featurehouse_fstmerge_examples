using System;
using System.Drawing;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Renderable;
namespace jhuapl.util
{
 public class JHU_Banner : RenderableObject
 {
  public enum ClassificationLevel
  {
   UNCLASS,
   CONFIDENTIAL,
   SECRET,
   TOPSECRET
  }
  public string[] ClassificationString =
  {
   "UNCLASSIFIED",
   "CONFIDENTIAL",
   "SECRET",
   "TOP SECRET"
  };
  public int[] ClassificationColor =
  {
   Color.PaleGreen.ToArgb(),
   Color.Turquoise.ToArgb(),
   Color.Red.ToArgb(),
   Color.Orange.ToArgb()
  };
  private const int distanceFromRight = 65;
  private const int distanceFromBottom = 5;
  public ClassificationLevel Classification;
  public JHU_Banner() : base("Banner", Vector3.Empty, Quaternion.Identity)
  {
   this.RenderPriority = RenderPriority.Icons;
   this.IsOn = true;
   Classification = ClassificationLevel.UNCLASS;
  }
  public override void Dispose()
  {
  }
  public override void Initialize(DrawArgs drawArgs)
  {
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return false;
  }
  public override void Render(DrawArgs drawArgs)
  {
   string text = ClassificationString[(int) Classification] + " - " + DateTime.Now.ToString();
   Rectangle bounds = drawArgs.defaultDrawingFont.MeasureString(null, text, DrawTextFormat.None, 0);
   drawArgs.defaultDrawingFont.DrawText(null, text,
    (drawArgs.screenWidth-bounds.Width)/2, drawArgs.screenHeight-bounds.Height-distanceFromBottom,
    ClassificationColor[(int) Classification] );
  }
  public override void Update(DrawArgs drawArgs)
  {
  }
 }
}
