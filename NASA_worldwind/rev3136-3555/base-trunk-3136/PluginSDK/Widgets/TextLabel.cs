using System;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
namespace WorldWind.NewWidgets
{
    public enum Alignment
    {
        Left,
        Center,
        Right
    }
 public class TextLabel : IWidget
 {
  string m_Text = "";
  System.Drawing.Point m_Location = new System.Drawing.Point(0,0);
  System.Drawing.Size m_Size = new System.Drawing.Size(0,20);
  bool m_Visible = true;
  bool m_Enabled = true;
  IWidget m_ParentWidget = null;
  object m_Tag = null;
  System.Drawing.Color m_ForeColor = System.Drawing.Color.White;
  string m_Name = "";
        System.Drawing.Font m_localFont = null;
        Font m_drawingFont = null;
        public Alignment Alignment = Alignment.Left;
        public bool WordBreak = false;
        protected bool m_countHeight = true;
        protected bool m_countWidth = true;
  public TextLabel()
  {
  }
        public System.Drawing.Font Font
        {
            get { return m_localFont; }
            set
            {
                m_localFont = value;
                if (m_drawingFont != null)
                {
                    m_drawingFont.Dispose();
                    m_drawingFont = new Font(DrawArgs.Device, m_localFont);
                }
            }
        }
  public string Name
  {
   get
   {
    return m_Name;
   }
   set
   {
    m_Name = value;
   }
  }
  public System.Drawing.Color ForeColor
  {
   get
   {
    return m_ForeColor;
   }
   set
   {
    m_ForeColor = value;
   }
  }
  public string Text
  {
   get
   {
    return m_Text;
   }
   set
   {
    m_Text = value;
   }
  }
  public IWidget ParentWidget
  {
   get
   {
    return m_ParentWidget;
   }
   set
   {
    m_ParentWidget = value;
   }
  }
  public bool Visible
  {
   get
   {
    return m_Visible;
   }
   set
   {
    m_Visible = value;
   }
  }
  public object Tag
  {
   get
   {
    return m_Tag;
   }
   set
   {
    m_Tag = value;
   }
  }
  public IWidgetCollection ChildWidgets
  {
   get
   {
    return null;
   }
   set
   {
   }
  }
  public System.Drawing.Size ClientSize
  {
   get
   {
    return m_Size;
   }
   set
   {
    m_Size = value;
   }
  }
  public bool Enabled
  {
   get
   {
    return m_Enabled;
   }
   set
   {
    m_Enabled = value;
   }
  }
  public System.Drawing.Point ClientLocation
  {
   get
   {
    return m_Location;
   }
   set
   {
    m_Location = value;
   }
  }
  public System.Drawing.Point AbsoluteLocation
  {
   get
   {
    if(m_ParentWidget != null)
    {
     return new System.Drawing.Point(
      m_Location.X + m_ParentWidget.AbsoluteLocation.X,
      m_Location.Y + m_ParentWidget.AbsoluteLocation.Y);
    }
    else
    {
     return m_Location;
    }
   }
  }
        public System.Drawing.Point Location
        {
            get { return m_Location; }
            set { m_Location = value; }
        }
        public System.Drawing.Size WidgetSize
        {
            get { return m_Size; }
            set { m_Size = value; }
        }
        public bool CountHeight
        {
            get { return m_countHeight; }
            set { m_countHeight = value; }
        }
        public bool CountWidth
        {
            get { return m_countWidth; }
            set { m_countWidth = value; }
        }
        public void Initialize(DrawArgs drawArgs)
        {
        }
  public void Render(DrawArgs drawArgs)
  {
   if(m_Visible)
   {
                if (m_localFont != null && m_drawingFont == null)
                {
                    m_drawingFont = new Font(drawArgs.device, m_localFont);
                }
                DrawTextFormat drawTextFormat = (WordBreak ? DrawTextFormat.WordBreak : DrawTextFormat.SingleLine);
                switch(this.Alignment)
                {
                    case Alignment.Left:
                        drawTextFormat |= DrawTextFormat.Left;
                        break;
                    case Alignment.Center:
                        drawTextFormat |= DrawTextFormat.Center;
                        break;
                    case Alignment.Right:
                        drawTextFormat |= DrawTextFormat.Right;
                        break;
                }
                if (m_drawingFont == null)
                {
                    drawArgs.defaultDrawingFont.DrawText(
                        null,
                        m_Text,
                        new System.Drawing.Rectangle(AbsoluteLocation.X, AbsoluteLocation.Y, m_Size.Width, m_Size.Height),
                        drawTextFormat,
                        m_ForeColor);
                }
                else
                {
                    m_drawingFont.DrawText(
                        null,
                        m_Text,
                        new System.Drawing.Rectangle(AbsoluteLocation.X, AbsoluteLocation.Y, m_Size.Width, m_Size.Height),
                        drawTextFormat,
                        m_ForeColor);
                }
   }
  }
 }
}
