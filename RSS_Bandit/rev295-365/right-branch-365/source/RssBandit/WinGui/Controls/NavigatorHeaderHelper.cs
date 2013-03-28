using System;
using System.Drawing;
using System.Windows.Forms;
using Infragistics.Win;
using Infragistics.Win.UltraWinExplorerBar;
using RssBandit.WinGui.Controls;
namespace RssBandit.WinGui.Controls
{
 public class NavigatorHeaderHelper
 {
  public event EventHandler ImageClick;
  private UltraExplorerBar navigator;
  private Cursor _saveCursorImageArea;
  public NavigatorHeaderHelper(UltraExplorerBar navigator, Image image)
  {
   this.navigator = navigator;
   this.navigator.DrawFilter = new NavigatorHeaderDrawFilter(image);
   this.navigator.MouseMove += new MouseEventHandler(this.OnMouseMove);
   this.navigator.Click += new EventHandler(this.OnClick);
  }
  protected void OnImageClick() {
   if (ImageClick != null)
    ImageClick(this, EventArgs.Empty);
  }
  private void OnMouseMove(object sender, MouseEventArgs args)
  {
   if (IsMouseInsideImageArea()) {
    if (_saveCursorImageArea == null) {
     _saveCursorImageArea = this.navigator.Cursor;
     this.navigator.Cursor = Cursors.Hand;
    }
   } else {
    if (_saveCursorImageArea != null) {
     this.navigator.Cursor = _saveCursorImageArea;
     _saveCursorImageArea = null;
    }
   }
  }
  private void OnClick(object sender, EventArgs args)
  {
   if (IsMouseInsideImageArea()) {
    OnImageClick();
   }
  }
  private bool IsMouseInsideImageArea() {
   Point mousePosition = this.navigator.PointToClient(Control.MousePosition);
   UIElement e = this.navigator.UIElement.ElementFromPoint(mousePosition);
   if (e is EditorWithTextDisplayTextUIElement) {
    int offset = e.Rect.Y + (e.Rect.Height - 16) / 2;
    Point p = new Point(e.Rect.X + e.Rect.Width - offset - 16, offset);
    Rectangle r = new Rectangle(p, new Size(16, 16));
    if (r.Contains(mousePosition)) {
     return true;
    }
   }
   return false;
  }
 }
}
