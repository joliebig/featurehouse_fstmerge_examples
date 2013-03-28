using System;
using System.Collections;
using System.Windows.Forms;
using WorldWind;
namespace WorldWind.NewWidgets
{
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
