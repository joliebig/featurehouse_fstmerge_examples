namespace RssBandit.WinGui.Controls
{
 using System;
 using System.ComponentModel;
 using System.Drawing;
 using System.Windows.Forms;
 public enum VisualStyles
 {
  Mozilla = 0,
  XP,
  Win9x,
  DoubleDots,
  Lines
 }
 public enum SplitterState
 {
  Collapsed = 0,
  Expanding,
  Expanded,
  Collapsing
 }
 [ToolboxBitmap(typeof(CollapsibleSplitter))]
 [DesignerAttribute(typeof(CollapsibleSplitterDesigner))]
 public class CollapsibleSplitter : System.Windows.Forms.Splitter
 {
  private bool hot;
  private System.Drawing.Color hotColor = CalculateColor(SystemColors.Highlight, SystemColors.Window, 70);
  private System.Windows.Forms.Control controlToHide;
  private System.Drawing.Rectangle rr;
  private System.Windows.Forms.Form parentForm;
  private bool expandParentForm;
  private VisualStyles visualStyle;
  private System.Windows.Forms.Border3DStyle borderStyle = System.Windows.Forms.Border3DStyle.Flat;
  private System.Windows.Forms.Timer animationTimer;
  private int controlWidth;
  private int controlHeight;
  private int parentFormWidth;
  private int parentFormHeight;
  private SplitterState currentState;
  private int animationDelay = 20;
  private int animationStep = 20;
  private bool useAnimations;
  [Bindable(true), Category("Collapsing Options"), DefaultValue("False"),
  Description("The initial state of the Splitter. Set to True if the control to hide is not visible by default")]
  public bool IsCollapsed
  {
   get
   {
    if(this.controlToHide!= null)
     return !this.controlToHide.Visible;
    else
     return true;
   }
  }
  [Bindable(true), Category("Collapsing Options"), DefaultValue(""),
  Description("The System.Windows.Forms.Control that the splitter will collapse")]
  public System.Windows.Forms.Control ControlToHide
  {
   get{ return this.controlToHide; }
   set{ this.controlToHide = value; }
  }
  [Bindable(true), Category("Collapsing Options"), DefaultValue("True"),
  Description("Determines if the collapse and expanding actions will be animated")]
  public bool UseAnimations
  {
   get { return this.useAnimations; }
   set { this.useAnimations = value; }
  }
  [Bindable(true), Category("Collapsing Options"), DefaultValue("20"),
  Description("The delay in millisenconds between animation steps")]
  public int AnimationDelay
  {
   get{ return this.animationDelay; }
   set{ this.animationDelay = value; }
  }
  [Bindable(true), Category("Collapsing Options"), DefaultValue("20"),
  Description("The amount of pixels moved in each animation step")]
  public int AnimationStep
  {
   get{ return this.animationStep; }
   set{ this.animationStep = value; }
  }
  [Bindable(true), Category("Collapsing Options"), DefaultValue("False"),
  Description("When true the entire parent form will be expanded and collapsed, otherwise just the contol to expand will be changed")]
  public bool ExpandParentForm
  {
   get{ return this.expandParentForm; }
   set{ this.expandParentForm = value; }
  }
  [Bindable(true), Category("Collapsing Options"), DefaultValue("VisualStyles.XP"),
  Description("The visual style that will be painted on the control")]
  public VisualStyles VisualStyle
  {
   get{ return this.visualStyle; }
   set
   {
    this.visualStyle = value;
    this.Invalidate();
   }
  }
  [Bindable(true), Category("Collapsing Options"), DefaultValue("System.Windows.Forms.Border3DStyle.Flat"),
  Description("An optional border style to paint on the control. Set to Flat for no border")]
  public System.Windows.Forms.Border3DStyle BorderStyle3D
  {
   get{ return this.borderStyle; }
   set
   {
    this.borderStyle = value;
    this.Invalidate();
   }
  }
  public void ToggleState()
  {
   this.ToggleSplitter();
  }
  public CollapsibleSplitter()
  {
   this.Click += new System.EventHandler(OnClick);
   this.Resize += new System.EventHandler(OnResize);
   this.MouseLeave += new System.EventHandler(OnMouseLeave);
   this.MouseMove += new MouseEventHandler(OnMouseMove);
   this.animationTimer = new System.Windows.Forms.Timer();
   this.animationTimer.Interval = animationDelay;
   this.animationTimer.Tick += new System.EventHandler(this.animationTimerTick);
  }
  protected override void OnHandleCreated(EventArgs e)
  {
   base.OnHandleCreated(e);
   this.parentForm = this.FindForm();
   if(this.controlToHide != null)
   {
    if(this.controlToHide.Visible)
    {
     this.currentState = SplitterState.Expanded;
    }
    else
    {
     this.currentState = SplitterState.Collapsed;
    }
   }
  }
  protected override void OnEnabledChanged(System.EventArgs e)
  {
   base.OnEnabledChanged(e);
   this.Invalidate();
  }
  protected override void OnMouseDown(MouseEventArgs e)
  {
   if(this.controlToHide!= null)
   {
    if(!this.hot && this.controlToHide.Visible)
    {
     base.OnMouseDown(e);
    }
   }
  }
  private void OnResize(object sender, System.EventArgs e)
  {
   this.Invalidate();
  }
  private void OnMouseMove(object sender, System.Windows.Forms.MouseEventArgs e)
  {
   if(e.X >= rr.X && e.X <= rr.X + rr.Width && e.Y >= rr.Y && e.Y <= rr.Y + rr.Height)
   {
    if(!this.hot)
    {
     this.hot = true;
     this.Cursor = Cursors.Hand;
     this.Invalidate();
    }
   }
   else
   {
    if(this.hot)
    {
     this.hot = false;
     this.Invalidate();;
    }
    this.Cursor = Cursors.Default;
    if(controlToHide!= null)
    {
     if(!controlToHide.Visible)
      this.Cursor = Cursors.Default;
     else
     {
      if(this.Dock == DockStyle.Left || this.Dock == DockStyle.Right)
      {
       this.Cursor = Cursors.VSplit;
      }
      else
      {
       this.Cursor = Cursors.HSplit;
      }
     }
    }
   }
  }
  private void OnMouseLeave(object sender, System.EventArgs e)
  {
   this.hot = false;
   this.Invalidate();;
  }
  private void OnClick(object sender, System.EventArgs e)
  {
   if(controlToHide!= null && hot &&
    currentState != SplitterState.Collapsing &&
    currentState != SplitterState.Expanding)
   {
    ToggleSplitter();
   }
  }
  private void ToggleSplitter()
  {
   if(currentState == SplitterState.Collapsing || currentState == SplitterState.Expanding)
    return;
   controlWidth = controlToHide.Width;
   controlHeight = controlToHide.Height;
   if(controlToHide.Visible)
   {
    if(useAnimations)
    {
     currentState = SplitterState.Collapsing;
     if(parentForm != null)
     {
      if(this.Dock == DockStyle.Left || this.Dock == DockStyle.Right)
      {
       parentFormWidth = parentForm.Width - controlWidth;
      }
      else
      {
       parentFormHeight = parentForm.Height - controlHeight;
      }
     }
     this.animationTimer.Enabled = true;
    }
    else
    {
     currentState = SplitterState.Collapsed;
     controlToHide.Visible = false;
     if(expandParentForm && parentForm != null)
     {
      if(this.Dock == DockStyle.Left || this.Dock == DockStyle.Right)
      {
       parentForm.Width -= controlToHide.Width;
      }
      else
      {
       parentForm.Height -= controlToHide.Height;
      }
     }
    }
   }
   else
   {
    if(useAnimations)
    {
     currentState = SplitterState.Expanding;
     if(this.Dock == DockStyle.Left || this.Dock == DockStyle.Right)
     {
      if(parentForm != null)
      {
       parentFormWidth = parentForm.Width + controlWidth;
      }
      controlToHide.Width = 0;
     }
     else
     {
      if(parentForm != null)
      {
       parentFormHeight = parentForm.Height + controlHeight;
      }
      controlToHide.Height = 0;
     }
     controlToHide.Visible = true;
     this.animationTimer.Enabled = true;
    }
    else
    {
     currentState = SplitterState.Expanded;
     controlToHide.Visible = true;
     if(expandParentForm && parentForm != null)
     {
      if(this.Dock == DockStyle.Left || this.Dock == DockStyle.Right)
      {
       parentForm.Width += controlToHide.Width;
      }
      else
      {
       parentForm.Height += controlToHide.Height;
      }
     }
    }
   }
  }
  private void animationTimerTick(object sender, System.EventArgs e)
  {
   switch(currentState)
   {
    case SplitterState.Collapsing:
     if(this.Dock == DockStyle.Left || this.Dock == DockStyle.Right)
     {
      if(controlToHide.Width > animationStep)
      {
       if(expandParentForm && parentForm.WindowState != FormWindowState.Maximized
        && parentForm != null)
       {
        parentForm.Width -= animationStep;
       }
       controlToHide.Width -= animationStep;
      }
      else
      {
       if(expandParentForm && parentForm.WindowState != FormWindowState.Maximized
        && parentForm != null)
       {
        parentForm.Width = parentFormWidth;
       }
       controlToHide.Visible = false;
       animationTimer.Enabled = false;
       controlToHide.Width = controlWidth;
       currentState = SplitterState.Collapsed;
       this.Invalidate();
      }
     }
     else
     {
      if(controlToHide.Height > animationStep)
      {
       if(expandParentForm && parentForm.WindowState != FormWindowState.Maximized
        && parentForm != null)
       {
        parentForm.Height -= animationStep;
       }
       controlToHide.Height -= animationStep;
      }
      else
      {
       if(expandParentForm && parentForm.WindowState != FormWindowState.Maximized
        && parentForm != null)
       {
        parentForm.Height = parentFormHeight;
       }
       controlToHide.Visible = false;
       animationTimer.Enabled = false;
       controlToHide.Height = controlHeight;
       currentState = SplitterState.Collapsed;
       this.Invalidate();
      }
     }
     break;
    case SplitterState.Expanding:
     if(this.Dock == DockStyle.Left || this.Dock == DockStyle.Right)
     {
      if(controlToHide.Width < (controlWidth - animationStep))
      {
       if(expandParentForm && parentForm.WindowState != FormWindowState.Maximized
        && parentForm != null)
       {
        parentForm.Width += animationStep;
       }
       controlToHide.Width += animationStep;
      }
      else
      {
       if(expandParentForm && parentForm.WindowState != FormWindowState.Maximized
        && parentForm != null)
       {
        parentForm.Width = parentFormWidth;
       }
       controlToHide.Width = controlWidth;
       controlToHide.Visible = true;
       animationTimer.Enabled = false;
       currentState = SplitterState.Expanded;
       this.Invalidate();
      }
     }
     else
     {
      if(controlToHide.Height < (controlHeight - animationStep))
      {
       if(expandParentForm && parentForm.WindowState != FormWindowState.Maximized
        && parentForm != null)
       {
        parentForm.Height += animationStep;
       }
       controlToHide.Height += animationStep;
      }
      else
      {
       if(expandParentForm && parentForm.WindowState != FormWindowState.Maximized
        && parentForm != null)
       {
        parentForm.Height = parentFormHeight;
       }
       controlToHide.Height = controlHeight;
       controlToHide.Visible = true;
       animationTimer.Enabled = false;
       currentState = SplitterState.Expanded;
       this.Invalidate();
      }
     }
     break;
   }
  }
  protected override void OnPaint(PaintEventArgs e)
  {
   Graphics g = e.Graphics;
   Rectangle r = this.ClientRectangle;
   g.FillRectangle(new SolidBrush(this.BackColor), r);
   if(this.Dock == DockStyle.Left || this.Dock == DockStyle.Right)
   {
    rr = new Rectangle(r.X, (int)r.Y + ((r.Height - 115)/2), 8, 115);
    this.Width = 8;
    if(hot)
    {
     g.FillRectangle(new SolidBrush(hotColor), new Rectangle(rr.X + 1, rr.Y, 6, 115));
    }
    else
    {
     g.FillRectangle(new SolidBrush(this.BackColor), new Rectangle(rr.X + 1, rr.Y, 6, 115));
    }
    g.DrawLine(new Pen(SystemColors.ControlDark, 1), rr.X + 1, rr.Y, rr.X + rr.Width - 2, rr.Y);
    g.DrawLine(new Pen(SystemColors.ControlDark, 1), rr.X + 1, rr.Y + rr.Height, rr.X + rr.Width - 2, rr.Y + rr.Height);
    if(this.Enabled)
    {
     g.FillPolygon(new SolidBrush(SystemColors.ControlDarkDark), ArrowPointArray(rr.X + 2, rr.Y + 3));
     g.FillPolygon(new SolidBrush(SystemColors.ControlDarkDark), ArrowPointArray(rr.X + 2, rr.Y + rr.Height - 9));
    }
    int x = rr.X + 3;
    int y = rr.Y + 14;
    switch(visualStyle)
    {
     case VisualStyles.Mozilla:
      for(int i=0; i < 30; i++)
      {
       g.DrawLine(new Pen(SystemColors.ControlLightLight), x, y + (i*3), x+1, y + 1 + (i*3));
       g.DrawLine(new Pen(SystemColors.ControlDarkDark), x+1, y + 1 + (i*3), x+2, y + 2 + (i*3));
       if(hot)
       {
        g.DrawLine(new Pen(hotColor), x+2, y + 1 + (i*3), x+2, y + 2 + (i*3));
       }
       else
       {
        g.DrawLine(new Pen(this.BackColor), x+2, y + 1 + (i*3), x+2, y + 2 + (i*3));
       }
      }
      break;
     case VisualStyles.DoubleDots:
      for(int i=0; i < 30; i++)
      {
       g.DrawRectangle(new Pen(SystemColors.ControlLightLight), x, y + 1 + (i*3), 1, 1 );
       g.DrawRectangle(new Pen(SystemColors.ControlDark), x - 1, y +(i*3), 1, 1 );
       i++;
       g.DrawRectangle(new Pen(SystemColors.ControlLightLight), x + 2, y + 1 + (i*3), 1, 1 );
       g.DrawRectangle(new Pen(SystemColors.ControlDark), x + 1, y + (i*3), 1, 1 );
      }
      break;
     case VisualStyles.Win9x:
      g.DrawLine(new Pen(SystemColors.ControlLightLight), x, y, x + 2, y);
      g.DrawLine(new Pen(SystemColors.ControlLightLight), x, y, x,y + 90);
      g.DrawLine(new Pen(SystemColors.ControlDark), x + 2, y, x + 2, y + 90);
      g.DrawLine(new Pen(SystemColors.ControlDark), x, y + 90, x + 2, y + 90);
      break;
     case VisualStyles.XP:
      for(int i=0; i < 18; i++)
      {
       g.DrawRectangle(new Pen(SystemColors.ControlLight), x, y + (i*5), 2, 2 );
       g.DrawRectangle(new Pen(SystemColors.ControlLightLight), x + 1, y + 1 + (i*5), 1, 1 );
       g.DrawRectangle(new Pen(SystemColors.ControlDarkDark), x, y +(i*5), 1, 1 );
       g.DrawLine(new Pen(SystemColors.ControlDark), x, y + (i*5), x, y + (i*5) + 1);
       g.DrawLine(new Pen(SystemColors.ControlDark), x, y + (i*5), x + 1, y + (i*5));
      }
      break;
     case VisualStyles.Lines:
      for(int i=0; i < 44; i++)
      {
       g.DrawLine(new Pen(SystemColors.ControlDark), x, y + (i*2), x + 2, y + (i*2));
      }
      break;
    }
    if(this.borderStyle != System.Windows.Forms.Border3DStyle.Flat)
    {
     ControlPaint.DrawBorder3D(e.Graphics, this.ClientRectangle, this.borderStyle, Border3DSide.Left);
     ControlPaint.DrawBorder3D(e.Graphics, this.ClientRectangle, this.borderStyle, Border3DSide.Right);
    }
   }
   else if (this.Dock == DockStyle.Top || this.Dock == DockStyle.Bottom)
   {
    rr = new Rectangle((int)r.X + ((r.Width - 115)/2), r.Y, 115, 8);
    this.Height = 8;
    if(hot)
    {
     g.FillRectangle(new SolidBrush(hotColor), new Rectangle(rr.X, rr.Y + 1, 115, 6));
    }
    else
    {
     g.FillRectangle(new SolidBrush(this.BackColor), new Rectangle(rr.X, rr.Y + 1, 115, 6));
    }
    g.DrawLine(new Pen(SystemColors.ControlDark, 1), rr.X, rr.Y + 1, rr.X, rr.Y + rr.Height - 2);
    g.DrawLine(new Pen(SystemColors.ControlDark, 1), rr.X + rr.Width, rr.Y + 1, rr.X + rr.Width, rr.Y + rr.Height - 2);
    if(this.Enabled)
    {
     g.FillPolygon(new SolidBrush(SystemColors.ControlDarkDark), ArrowPointArray(rr.X + 3, rr.Y + 2));
     g.FillPolygon(new SolidBrush(SystemColors.ControlDarkDark), ArrowPointArray(rr.X + rr.Width - 9, rr.Y + 2));
    }
    int x = rr.X + 14;
    int y = rr.Y + 3;
    switch(visualStyle)
    {
     case VisualStyles.Mozilla:
      for(int i=0; i < 30; i++)
      {
       g.DrawLine(new Pen(SystemColors.ControlLightLight), x + (i*3), y, x + 1 + (i*3), y + 1);
       g.DrawLine(new Pen(SystemColors.ControlDarkDark), x + 1 + (i*3), y + 1, x + 2 + (i*3), y + 2);
       if(hot)
       {
        g.DrawLine(new Pen(hotColor), x + 1 + (i*3), y + 2, x + 2 + (i*3), y + 2);
       }
       else
       {
        g.DrawLine(new Pen(this.BackColor), x + 1 + (i*3), y + 2, x + 2 + (i*3), y + 2);
       }
      }
      break;
     case VisualStyles.DoubleDots:
      for(int i=0; i < 30; i++)
      {
       g.DrawRectangle(new Pen(SystemColors.ControlLightLight), x + 1 + (i*3), y, 1, 1 );
       g.DrawRectangle(new Pen(SystemColors.ControlDark), x + (i*3), y - 1, 1, 1 );
       i++;
       g.DrawRectangle(new Pen(SystemColors.ControlLightLight), x + 1 + (i*3), y + 2, 1, 1 );
       g.DrawRectangle(new Pen(SystemColors.ControlDark), x + (i*3), y + 1, 1, 1 );
      }
      break;
     case VisualStyles.Win9x:
      g.DrawLine(new Pen(SystemColors.ControlLightLight), x, y, x, y + 2);
      g.DrawLine(new Pen(SystemColors.ControlLightLight), x, y, x + 88, y);
      g.DrawLine(new Pen(SystemColors.ControlDark), x, y + 2, x + 88, y + 2);
      g.DrawLine(new Pen(SystemColors.ControlDark), x + 88, y, x + 88, y + 2);
      break;
     case VisualStyles.XP:
      for(int i=0; i < 18; i++)
      {
       g.DrawRectangle(new Pen(SystemColors.ControlLight), x + (i*5), y, 2, 2 );
       g.DrawRectangle(new Pen(SystemColors.ControlLightLight), x + 1 + (i*5), y + 1, 1, 1 );
       g.DrawRectangle(new Pen(SystemColors.ControlDarkDark), x +(i*5), y, 1, 1 );
       g.DrawLine(new Pen(SystemColors.ControlDark), x + (i*5), y, x + (i*5) + 1, y);
       g.DrawLine(new Pen(SystemColors.ControlDark), x + (i*5), y, x + (i*5), y + 1);
      }
      break;
     case VisualStyles.Lines:
      for(int i=0; i < 44; i++)
      {
       g.DrawLine(new Pen(SystemColors.ControlDark), x + (i*2), y, x + (i*2), y + 2);
      }
      break;
    }
    if(this.borderStyle != System.Windows.Forms.Border3DStyle.Flat)
    {
     ControlPaint.DrawBorder3D(e.Graphics, this.ClientRectangle, this.borderStyle, Border3DSide.Top);
     ControlPaint.DrawBorder3D(e.Graphics, this.ClientRectangle, this.borderStyle, Border3DSide.Bottom);
    }
   }
   else
   {
    throw new Exception("The Collapsible Splitter control cannot have the Filled or None Dockstyle property");
   }
   g.Dispose();
  }
  private Point[] ArrowPointArray(int x, int y)
  {
   Point[] point = new Point[3];
   if(controlToHide!= null)
   {
    if (
     (this.Dock == DockStyle.Right && controlToHide.Visible)
     || (this.Dock == DockStyle.Left && !controlToHide.Visible)
     )
    {
     point[0] = new Point(x,y);
     point[1] = new Point(x + 3, y + 3);
     point[2] = new Point(x, y + 6);
    }
    else if (
     (this.Dock == DockStyle.Right && !controlToHide.Visible)
     || (this.Dock == DockStyle.Left && controlToHide.Visible)
     )
    {
     point[0] = new Point(x + 3 ,y);
     point[1] = new Point(x, y + 3);
     point[2] = new Point(x + 3, y + 6);
    }
    else if (
     (this.Dock == DockStyle.Top && controlToHide.Visible)
     || (this.Dock == DockStyle.Bottom && !controlToHide.Visible)
     )
    {
     point[0] = new Point(x + 3, y);
     point[1] = new Point(x + 6, y + 4);
     point[2] = new Point(x, y + 4);
    }
    else if (
     (this.Dock == DockStyle.Top && !controlToHide.Visible)
     || (this.Dock == DockStyle.Bottom && controlToHide.Visible)
     )
    {
     point[0] = new Point(x,y);
     point[1] = new Point(x + 6, y);
     point[2] = new Point(x + 3, y + 3);
    }
   }
   return point;
  }
  private static Color CalculateColor(Color front, Color back, int alpha)
  {
   Color frontColor = Color.FromArgb(255, front);
   Color backColor = Color.FromArgb(255, back);
   float frontRed = frontColor.R;
   float frontGreen = frontColor.G;
   float frontBlue = frontColor.B;
   float backRed = backColor.R;
   float backGreen = backColor.G;
   float backBlue = backColor.B;
   float fRed = frontRed*alpha/255 + backRed*((float)(255-alpha)/255);
   byte newRed = (byte)fRed;
   float fGreen = frontGreen*alpha/255 + backGreen*((float)(255-alpha)/255);
   byte newGreen = (byte)fGreen;
   float fBlue = frontBlue*alpha/255 + backBlue*((float)(255-alpha)/255);
   byte newBlue = (byte)fBlue;
   return Color.FromArgb(255, newRed, newGreen, newBlue);
  }
 }
 public class CollapsibleSplitterDesigner : System.Windows.Forms.Design.ControlDesigner
 {
  public CollapsibleSplitterDesigner()
  {
  }
  protected override void PreFilterProperties(System.Collections.IDictionary properties)
  {
   properties.Remove("IsCollapsed");
   properties.Remove("BorderStyle");
   properties.Remove("Size");
  }
 }
}
