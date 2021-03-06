using System;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using RssBandit.WinGui.Utility;
namespace RssBandit.WinGui.Controls.CollapsiblePanels {
 public enum PanelBarType
 {
  StandardTaskBar = 1,
  FilledTaskBar = 2
 }
 public class CollapsiblePanelBar : Panel, System.ComponentModel.ISupportInitialize
 {
  private CollapsiblePanelCollection panels = new CollapsiblePanelCollection();
  private int miSpacing = 4;
  private int miBorder = 4;
  private bool mbInitializing = false;
  private PanelBarType meBarType;
  private bool mbPanelsResizable;
  public CollapsiblePanelBar() : base()
  {
   InitializeComponent();
   this.BackColor = Color.CornflowerBlue;
   meBarType = PanelBarType.StandardTaskBar;
  }
  private void InitializeComponent()
  {
  }
  public int Spacing
  {
   get
   {
    return this.miSpacing;
   }
   set
   {
    this.miSpacing = value;
    UpdatePositions();
   }
  }
  public int Border
  {
   get
   {
    return this.miBorder;
   }
   set
   {
    this.miBorder = value;
    UpdatePositions();
   }
  }
  public bool PanelsResizable
  {
   get
   {
    return this.mbPanelsResizable;
   }
   set
   {
    this.mbPanelsResizable = value;
   }
  }
  public PanelBarType BarType
  {
   get
   {
    return meBarType;
   }
   set
   {
    meBarType = value;
    UpdatePositions();
   }
  }
  public void BeginInit()
  {
   this.mbInitializing = true;
  }
  public void EndInit()
  {
   this.mbInitializing = false;
  }
  public void DistributePanelsEqualy()
  {
   int iHeight;
   int i;
   CollapsiblePanel oPanel;
   if (panels.Count >0)
   {
    iHeight = (this.ClientSize.Height -
     2 * miBorder -
     (panels.Count-1)*miSpacing) / panels.Count;
    for (i=0; i<panels.Count;i++)
    {
     oPanel = panels.Item(i);
     oPanel.ExpandedHeight = iHeight;
     if (oPanel.IsExpanded)
     {
      oPanel.Height = oPanel.ExpandedHeight;
     }
    }
    UpdatePositions();
   }
  }
  public void CollapseAll() {
   if (this.panels.Count > 0) {
    foreach (CollapsiblePanel panel in this.panels) {
     if (panel.PanelState == PanelState.Expanded)
      panel.PanelState = PanelState.Collapsed;
    }
    UpdatePositions();
   }
  }
  public void ExpandAll() {
   if (this.panels.Count > 0) {
    foreach (CollapsiblePanel panel in this.panels) {
     if (panel.PanelState == PanelState.Collapsed)
      panel.PanelState = PanelState.Expanded;
    }
    UpdatePositions();
   }
  }
  public void RefreshLayout()
  {
   UpdatePositions();
  }
  private void UpdatePositions()
  {
   CollapsiblePanel oCurPanel;
   int iStretchPanelIndex;
   int iHeight;
   iStretchPanelIndex = -1;
   iHeight = 2 * miBorder;
   if (meBarType == PanelBarType.FilledTaskBar)
   {
    for(int i = 0; i < panels.Count; i++)
    {
     oCurPanel = panels.Item(i);
     if (oCurPanel.IsExpanded)
     {
      oCurPanel.Height = oCurPanel.ExpandedHeight;
     }
     if ((iStretchPanelIndex == -1) &&
      (oCurPanel.PanelState == PanelState.Expanded) &&
      (! oCurPanel.FixedSize))
     {
      iStretchPanelIndex = i;
     }
     else
     {
      iHeight = iHeight + oCurPanel.Height + miSpacing;
     }
    }
    if (iStretchPanelIndex >= 0)
    {
     oCurPanel = panels.Item(iStretchPanelIndex);
     oCurPanel.Height = this.ClientSize.Height - iHeight;
    }
   }
   for(int i = panels.Count-1; i >=0; i--)
   {
    oCurPanel = this.panels.Item(i);
    if(i == this.panels.Count - 1)
    {
     oCurPanel.Top = this.miBorder;
    }
    else
    {
     oCurPanel.Top = this.panels.Item(i + 1).Bottom + this.miSpacing;
    }
    SetPanelEdges(oCurPanel);
    if ((i > iStretchPanelIndex) || (iStretchPanelIndex == -1))
    {
     oCurPanel.Anchor = (System.Windows.Forms.AnchorStyles)
      ((System.Windows.Forms.AnchorStyles.Top)
      | (System.Windows.Forms.AnchorStyles.Left)
      | (System.Windows.Forms.AnchorStyles.Right));
    }
    else if (iStretchPanelIndex == i)
    {
     oCurPanel.Anchor = (System.Windows.Forms.AnchorStyles)
      ((System.Windows.Forms.AnchorStyles.Top)
      | (System.Windows.Forms.AnchorStyles.Bottom)
      | (System.Windows.Forms.AnchorStyles.Left)
      | (System.Windows.Forms.AnchorStyles.Right));
    }
    else
    {
     oCurPanel.Anchor = (System.Windows.Forms.AnchorStyles)
      ((System.Windows.Forms.AnchorStyles.Bottom)
      | (System.Windows.Forms.AnchorStyles.Left)
      | (System.Windows.Forms.AnchorStyles.Right));
    }
   }
  }
  private void SetPanelEdges(CollapsiblePanel Panel)
  {
   Panel.Left = this.miBorder;
   Panel.Width = this.ClientSize.Width - (2 * this.miBorder);
  }
  protected override void OnControlAdded(ControlEventArgs e)
  {
   CollapsiblePanel oPanel;
   base.OnControlAdded(e);
   if(e.Control is CollapsiblePanel)
   {
    oPanel = e.Control as CollapsiblePanel;
    oPanel.Anchor = ((System.Windows.Forms.AnchorStyles)
     (System.Windows.Forms.AnchorStyles.Top
     | System.Windows.Forms.AnchorStyles.Left)
     | System.Windows.Forms.AnchorStyles.Right);
    this.Controls.Add(e.Control);
    if(true == mbInitializing)
    {
     this.panels.Add((CollapsiblePanel)e.Control);
     this.panels.Item(this.panels.Count - 1).PanelStateChanged +=
      new CollapsiblePanel.PanelStateChangedEventHandler(this.panel_StateChanged);
    }
    else
    {
     panels.Insert(0, (CollapsiblePanel)e.Control);
     panels.Item(0).PanelStateChanged +=
      new CollapsiblePanel.PanelStateChangedEventHandler(this.panel_StateChanged);
     UpdatePositions();
    }
   }
  }
  protected override void OnControlRemoved(ControlEventArgs e)
  {
   base.OnControlRemoved(e);
   if(e.Control is CollapsiblePanel)
   {
    int index = this.panels.IndexOf((CollapsiblePanel)e.Control);
    if(-1 != index)
    {
     this.panels.Remove(index);
     UpdatePositions();
    }
   }
  }
  private void panel_StateChanged(object sender, PanelEventArgs e)
  {
   int index;
   index = panels.IndexOf(e.CollapsiblePanel);
   if (index != -1)
   {
    UpdatePositions();
   }
  }
 }
}
