using System;
using System.Collections;
using System.Windows.Forms;
using WorldWind;
namespace WorldWind.NewWidgets
{
 public delegate void MouseClickAction(System.Windows.Forms.MouseEventArgs e);
 public interface IInteractive
 {
  MouseClickAction LeftClickAction{set; get;}
  MouseClickAction RightClickAction{set; get;}
  bool OnMouseDown(MouseEventArgs e);
  bool OnMouseUp(MouseEventArgs e);
  bool OnMouseMove(MouseEventArgs e);
  bool OnMouseWheel(MouseEventArgs e);
  bool OnMouseEnter(EventArgs e);
  bool OnMouseLeave(EventArgs e);
  bool OnKeyDown(KeyEventArgs e);
  bool OnKeyUp(KeyEventArgs e);
        bool OnKeyPress(KeyPressEventArgs e);
 }
}
