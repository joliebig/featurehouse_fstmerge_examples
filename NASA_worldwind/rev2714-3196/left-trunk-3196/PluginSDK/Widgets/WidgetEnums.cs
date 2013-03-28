using System;
namespace WorldWind.NewWidgets
{
 public class WidgetEnums
 {
  [Flags]
  public enum AnchorStyles
  {
   None = 0x0000,
   Top = 0x0001,
   Bottom = 0x0002,
   Left = 0x0004,
   Right = 0x0008,
  }
  public WidgetEnums()
  {
  }
 }
}
