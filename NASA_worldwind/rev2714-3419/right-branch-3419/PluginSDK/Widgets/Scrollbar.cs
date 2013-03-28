using System;
using System.Collections;
using System.Text;
namespace WorldWind.NewWidgets
{
    public class Scrollbar : IWidget
    {
        System.Drawing.Point m_Location = new System.Drawing.Point(0,0);
  System.Drawing.Size m_Size = new System.Drawing.Size(0,20);
  bool m_Visible = true;
  bool m_Enabled = true;
  IWidget m_ParentWidget = null;
  object m_Tag = null;
  System.Drawing.Color m_ForeColor = System.Drawing.Color.White;
  string m_Name = "";
        public bool Outline = true;
        public double Value = 0;
        public double Minimum = 0;
        public double Maximum = 1;
        protected bool m_countHeight = true;
        protected bool m_countWidth = true;
        public Scrollbar()
  {
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
        Microsoft.DirectX.Vector2[] m_outlinePoints = new Microsoft.DirectX.Vector2[5];
  public void Render(DrawArgs drawArgs)
        {
            if(m_Visible)
   {
                float percent = 0;
                if (Value < Minimum)
                    percent = 0;
                else if (Value > Maximum)
                    percent = 1.0f;
                else
                {
                    percent = (float)((Value - Minimum) / (Maximum - Minimum));
                }
                if (Outline)
                {
                    m_outlinePoints[0].X = AbsoluteLocation.X;
                    m_outlinePoints[0].Y = AbsoluteLocation.Y;
                    m_outlinePoints[1].X = AbsoluteLocation.X + ClientSize.Width;
                    m_outlinePoints[1].Y = AbsoluteLocation.Y;
                    m_outlinePoints[2].X = AbsoluteLocation.X + ClientSize.Width;
                    m_outlinePoints[2].Y = AbsoluteLocation.Y + ClientSize.Height;
                    m_outlinePoints[3].X = AbsoluteLocation.X;
                    m_outlinePoints[3].Y = AbsoluteLocation.Y + ClientSize.Height;
                    m_outlinePoints[4].X = AbsoluteLocation.X;
                    m_outlinePoints[4].Y = AbsoluteLocation.Y;
                    WidgetUtilities.DrawLine(m_outlinePoints, m_ForeColor.ToArgb(), drawArgs.device);
                }
                WidgetUtilities.DrawBox(
                    AbsoluteLocation.X,
                    AbsoluteLocation.Y,
                    (int)(percent * ClientSize.Width),
                    ClientSize.Height,
                    0.5f,
                    m_ForeColor.ToArgb(),
                    drawArgs.device);
   }
  }
    }
}
