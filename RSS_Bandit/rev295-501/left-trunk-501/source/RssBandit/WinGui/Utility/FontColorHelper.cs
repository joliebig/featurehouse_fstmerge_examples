using System.Diagnostics;
using System.Drawing;
using System.Globalization;
using System.Windows.Forms;
using Infragistics.Win;
using NewsComponents.Utils;
namespace RssBandit.WinGui.Utility
{
 public enum FontStates: int {
  Read = 0,
  Unread = 1,
  Flag = 2,
  Referrer = 3,
  Error = 4,
  NewComments = 5,
  UnreadCounter = 6,
 }
 internal class FontColorHelper {
  class ColorStyleContainer
  {
   public Color Color;
   public FontStyle FontStyle;
   public ColorStyleContainer(Color clr, FontStyle style) {
    this.Color = clr;
    this.FontStyle = style;
   }
   public ColorStyleContainer Clone() {
    return new ColorStyleContainer(this.Color, this.FontStyle);
   }
  }
  static MicrosoftColorTable p_currentColorTable;
  static Font p_defaultFont = new Font(Control.DefaultFont, Control.DefaultFont.Style);
  static ColorStyleContainer[] p_defaultForeColorStyles =
   new ColorStyleContainer[7] {
     new ColorStyleContainer(SystemColors.ControlText, FontStyle.Regular),
     new ColorStyleContainer(SystemColors.ControlText, FontStyle.Italic),
     new ColorStyleContainer(SystemColors.ControlText, FontStyle.Bold),
     new ColorStyleContainer(Color.Blue, FontStyle.Regular),
     new ColorStyleContainer(Color.Red, FontStyle.Regular),
     new ColorStyleContainer(Color.Green, FontStyle.Italic),
     new ColorStyleContainer(SystemColors.ControlText, FontStyle.Bold),
  };
  static ColorStyleContainer[] p_foreColorStyles;
  static FontColorHelper() {
   p_currentColorTable = Office2003ColorTable.Colors;
   p_foreColorStyles = new ColorStyleContainer[p_defaultForeColorStyles.Length];
   for (int i=0; i<p_foreColorStyles.Length; i++)
    p_foreColorStyles[i] = p_defaultForeColorStyles[i].Clone();
   p_foreColorStyles[(int) FontStates.UnreadCounter].FontStyle =
    RssBanditApplication.ReadAppSettingsEntry("UnreadCounterFontStyle", DefaultUnreadCounterStyle);
   p_foreColorStyles[(int) FontStates.UnreadCounter].Color =
    RssBanditApplication.ReadAppSettingsEntry("UnreadCounterFontColor", DefaultUnreadCounterColor);
  }
  public static MicrosoftColorTable UiColorScheme {
   get { return p_currentColorTable; }
  }
  public static Font CopyToFont(FontData data) {
   Font res = new Font(data.Name, data.SizeInPoints, GraphicsUnit.Point);
   if(data.Bold==DefaultableBoolean.True)
    res = new Font(res, res.Style | FontStyle.Bold);
   if(data.Italic==DefaultableBoolean.True)
    res = new Font(res, res.Style | FontStyle.Italic);
   if(data.Strikeout==DefaultableBoolean.True)
    res = new Font(res, res.Style | FontStyle.Strikeout);
   if(data.Underline==DefaultableBoolean.True)
    res = new Font(res, res.Style | FontStyle.Underline);
   return res;
  }
  public static void CopyFromFont(FontData data, Font font) {
   data.Bold = ConvertToDefaultableBoolean(font.Bold);
   data.Italic = ConvertToDefaultableBoolean(font.Italic);
   data.Name = font.Name;
   data.SizeInPoints = font.SizeInPoints;
   data.Strikeout = ConvertToDefaultableBoolean(font.Strikeout);
   data.Underline = ConvertToDefaultableBoolean(font.Underline);
  }
  public static Font DefaultFont { get { return p_defaultFont; } set { p_defaultFont = value; } }
  public static Font NormalFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, NormalStyle); } }
  public static Font HighlightFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, HighlightStyle); } }
  public static Font UnreadFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, UnreadStyle); } }
  public static Font ReferenceFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, ReferenceStyle); } }
  public static Font FailureFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, FailureStyle); } }
  public static Font NewCommentsFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, NewCommentsStyle); } }
  public static Font UnreadCounterFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, UnreadCounterStyle); } }
  public static FontStyle NormalStyle { [DebuggerStepThrough] get { return p_foreColorStyles[0].FontStyle; } set { p_foreColorStyles[0].FontStyle = value;} }
  public static FontStyle HighlightStyle { [DebuggerStepThrough] get { return p_foreColorStyles[1].FontStyle; } set { p_foreColorStyles[1].FontStyle = value; }}
  public static FontStyle UnreadStyle { [DebuggerStepThrough] get { return p_foreColorStyles[2].FontStyle; } set { p_foreColorStyles[2].FontStyle = value; }}
  public static FontStyle ReferenceStyle { [DebuggerStepThrough] get { return p_foreColorStyles[3].FontStyle; } set { p_foreColorStyles[3].FontStyle = value;} }
  public static FontStyle FailureStyle { [DebuggerStepThrough] get { return p_foreColorStyles[4].FontStyle; } set { p_foreColorStyles[4].FontStyle = value;} }
  public static FontStyle NewCommentsStyle { [DebuggerStepThrough] get { return p_foreColorStyles[5].FontStyle; } set { p_foreColorStyles[5].FontStyle = value;} }
  public static FontStyle UnreadCounterStyle { [DebuggerStepThrough] get { return p_foreColorStyles[6].FontStyle; } set { p_foreColorStyles[6].FontStyle = value;} }
  public static Color NormalColor { [DebuggerStepThrough] get { return p_foreColorStyles[0].Color; } set { p_foreColorStyles[0].Color = value;} }
  public static Color HighlightColor { [DebuggerStepThrough] get { return p_foreColorStyles[1].Color; } set { p_foreColorStyles[1].Color = value; }}
  public static Color UnreadColor { [DebuggerStepThrough] get { return p_foreColorStyles[2].Color; } set { p_foreColorStyles[2].Color = value; }}
  public static Color ReferenceColor { [DebuggerStepThrough] get { return p_foreColorStyles[3].Color; } set { p_foreColorStyles[3].Color = value;} }
  public static Color FailureColor { [DebuggerStepThrough] get { return p_foreColorStyles[4].Color; } set { p_foreColorStyles[4].Color = value;} }
  public static Color NewCommentsColor { [DebuggerStepThrough] get { return p_foreColorStyles[5].Color; } set { p_foreColorStyles[5].Color = value;} }
  public static Color UnreadCounterColor { [DebuggerStepThrough] get { return p_foreColorStyles[6].Color; } set { p_foreColorStyles[6].Color = value;} }
  public static Font DefaultNormalFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, DefaultNormalStyle); } }
  public static Font DefaultHighlightFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, DefaultHighlightStyle); } }
  public static Font DefaultUnreadFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, DefaultUnreadStyle); } }
  public static Font DefaultReferenceFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, DefaultReferenceStyle); } }
  public static Font DefaultFailureFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, DefaultFailureStyle); } }
  public static Font DefaultNewCommentsFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, DefaultNewCommentsStyle); } }
  public static Font DefaultUnreadCounterFont { [DebuggerStepThrough] get { return new Font(p_defaultFont, DefaultUnreadCounterStyle); } }
  public static FontStyle DefaultNormalStyle { [DebuggerStepThrough] get { return p_defaultForeColorStyles[0].FontStyle; } set { p_defaultForeColorStyles[0].FontStyle = value;} }
  public static FontStyle DefaultHighlightStyle { [DebuggerStepThrough] get { return p_defaultForeColorStyles[1].FontStyle; } set { p_defaultForeColorStyles[1].FontStyle = value; }}
  public static FontStyle DefaultUnreadStyle { [DebuggerStepThrough] get { return p_defaultForeColorStyles[2].FontStyle; } set { p_defaultForeColorStyles[2].FontStyle = value; }}
  public static FontStyle DefaultReferenceStyle { [DebuggerStepThrough] get { return p_defaultForeColorStyles[3].FontStyle; } set { p_defaultForeColorStyles[3].FontStyle = value;} }
  public static FontStyle DefaultFailureStyle { [DebuggerStepThrough] get { return p_defaultForeColorStyles[4].FontStyle; } set { p_defaultForeColorStyles[4].FontStyle = value;} }
  public static FontStyle DefaultNewCommentsStyle { [DebuggerStepThrough] get { return p_defaultForeColorStyles[5].FontStyle; } set { p_defaultForeColorStyles[5].FontStyle = value;} }
  public static FontStyle DefaultUnreadCounterStyle { [DebuggerStepThrough] get { return p_defaultForeColorStyles[6].FontStyle; } set { p_defaultForeColorStyles[6].FontStyle = value;} }
  public static Color DefaultNormalColor { [DebuggerStepThrough] get { return p_defaultForeColorStyles[0].Color; } set { p_defaultForeColorStyles[0].Color = value;} }
  public static Color DefaultHighlightColor { [DebuggerStepThrough] get { return p_defaultForeColorStyles[1].Color; } set { p_defaultForeColorStyles[1].Color = value; }}
  public static Color DefaultUnreadColor { [DebuggerStepThrough] get { return p_defaultForeColorStyles[2].Color; } set { p_defaultForeColorStyles[2].Color = value; }}
  public static Color DefaultReferenceColor { [DebuggerStepThrough] get { return p_defaultForeColorStyles[3].Color; } set { p_defaultForeColorStyles[3].Color = value;} }
  public static Color DefaultFailureColor { [DebuggerStepThrough] get { return p_defaultForeColorStyles[4].Color; } set { p_defaultForeColorStyles[4].Color = value;} }
  public static Color DefaultNewCommentsColor { [DebuggerStepThrough] get { return p_defaultForeColorStyles[5].Color; } set { p_defaultForeColorStyles[5].Color = value;} }
  public static Color DefaultUnreadCounterColor { [DebuggerStepThrough] get { return p_defaultForeColorStyles[6].Color; } set { p_defaultForeColorStyles[6].Color = value;} }
  public static Font MergeFontStyles(Font leadingFont, FontStyle style) {
   if (leadingFont.Style == style)
    return leadingFont;
   return new Font(leadingFont, leadingFont.Style | style);
  }
  public static bool StyleEqual(FontData data, FontStyle style) {
   if((style & FontStyle.Bold)==FontStyle.Bold) {
    if(data.Bold==DefaultableBoolean.False)
     return false;
   }
   if((style & FontStyle.Italic)==FontStyle.Italic) {
    if(data.Italic==DefaultableBoolean.False)
     return false;
   }
   if((style & FontStyle.Strikeout)==FontStyle.Strikeout) {
    if(data.Strikeout==DefaultableBoolean.False)
     return false;
   }
   if((style & FontStyle.Underline)==FontStyle.Underline) {
    if(data.Underline==DefaultableBoolean.False)
     return false;
   }
   if(data.Bold==DefaultableBoolean.True) {
    if((style & FontStyle.Bold)!=FontStyle.Bold)
     return false;
   }
   if(data.Italic==DefaultableBoolean.True) {
    if((style & FontStyle.Italic)!=FontStyle.Italic)
     return false;
   }
   if(data.Strikeout==DefaultableBoolean.True) {
    if((style & FontStyle.Strikeout)!=FontStyle.Strikeout)
     return false;
   }
   if(data.Underline==DefaultableBoolean.True) {
    if((style & FontStyle.Underline)!=FontStyle.Underline)
     return false;
   }
   return true;
  }
  private static DefaultableBoolean ConvertToDefaultableBoolean(bool value) {
   if(value)
    return DefaultableBoolean.True;
   else
    return DefaultableBoolean.False;
  }
  private static Font GetFontFromName(string name, Font defaultValue) {
   try {
    if (!string.IsNullOrEmpty(name)) {
     FontConverter oFontConv = new FontConverter();
     return oFontConv.ConvertFromString(null, CultureInfo.InvariantCulture, name) as Font;
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  private static string GetNameOfFont(Font font) {
   FontConverter oFontConv = new FontConverter();
   return oFontConv.ConvertToString(null,CultureInfo.InvariantCulture,font);
  }
 }
}
