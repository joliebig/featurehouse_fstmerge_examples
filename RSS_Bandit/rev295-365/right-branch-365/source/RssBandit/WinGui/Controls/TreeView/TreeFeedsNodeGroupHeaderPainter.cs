using System.Drawing;
using System.Drawing.Drawing2D;
using Infragistics.Win;
namespace RssBandit.WinGui
{
 public class TreeFeedsNodeGroupHeaderPainter
 {
  public static void PaintOutlook2003Header(Graphics graphics, Rectangle r)
  {
   Bitmap bmp = new Bitmap(r.Width, r.Height);
   using (Graphics g = Graphics.FromImage(bmp))
   {
    Rectangle bounds = new Rectangle(0, 0, r.Width, r.Height);
    if ((bounds.Width > 0) && (bounds.Height > 0)) {
     using (LinearGradientBrush brush = new LinearGradientBrush(
          new Point(bounds.X, bounds.Y - 1), new Point(bounds.X, bounds.Bottom),
          Office2003Colors.OutlookNavPaneGroupHeaderGradientLight,
          Office2003Colors.OutlookNavPaneGroupHeaderGradientDark))
     {
      g.FillRectangle(brush, bounds);
     }
     using (Pen pen = new Pen(Office2003Colors.MainMenuBarGradientDark)) {
      g.DrawLine(pen, bounds.X, bounds.Y, bounds.Right - 1, bounds.Y);
     }
     using (Pen pen = new Pen(Office2003Colors.OutlookNavPaneBorder)) {
      g.DrawLine(pen, bounds.X, bounds.Bottom - 1, bounds.Right - 1, bounds.Bottom - 1);
     }
     graphics.DrawImage(bmp, r.Left, r.Top);
    }
   }
  }
 }
}
