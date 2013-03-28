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
 }
 public interface IWidget
 {
  string Name{get;set;}
  System.Drawing.Point Location {get;set;}
  System.Drawing.Point AbsoluteLocation{get;}
  System.Drawing.Point ClientLocation{get;}
  System.Drawing.Size WidgetSize{get;set;}
  System.Drawing.Size ClientSize{get;}
  bool Enabled{get;set;}
  bool Visible{get;set;}
  bool CountHeight{get; set;}
  bool CountWidth{get; set;}
  IWidget ParentWidget{get;set;}
  IWidgetCollection ChildWidgets{get;set;}
  object Tag{get;set;}
  void Render(DrawArgs drawArgs);
  void Initialize(DrawArgs drawArgs);
 }
 public interface IWidgetCollection
 {
  void BringToFront(int index);
  void BringToFront(IWidget widget);
  void Add(IWidget widget);
  void Clear();
  void Insert(IWidget widget, int index);
  IWidget RemoveAt(int index);
  void Remove (IWidget widget);
  int Count{get;}
  IWidget this[int index] {get;set;}
 }
}
