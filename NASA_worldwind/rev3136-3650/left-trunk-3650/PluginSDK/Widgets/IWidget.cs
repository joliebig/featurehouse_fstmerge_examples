using System;
using System.Collections;
using System.Windows.Forms;
using WorldWind;
namespace WorldWind.NewWidgets
{
 public interface IWidget
 {
  string Name{get;set;}
  System.Drawing.Point Location {get;set;}
  System.Drawing.Point AbsoluteLocation{get;}
  System.Drawing.Point ClientLocation{get;}
  System.Drawing.Size WidgetSize{get;set;}
        System.Drawing.Size ClientSize { get;set;}
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
}
