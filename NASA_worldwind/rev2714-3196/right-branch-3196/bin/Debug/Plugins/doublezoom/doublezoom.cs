using System;
using System.IO;
using System.Diagnostics;
using System.Drawing;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System.Windows.Forms;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.Net;
namespace FiveofOh.Plugins
{
 public class ZoomDialog : System.Windows.Forms.Form
 {
  public enum zoomFields
  {
   ZoomFact,
   Shift,
   Ctrl,
   Alt,
   IsLeftButton,
   IsDoubleClick
  }
  private System.Windows.Forms.Button btnCancel;
  private System.Windows.Forms.Button btnOK;
  private System.Windows.Forms.GroupBox grpZoomIn;
  private System.Windows.Forms.Label lblZoomIn;
  private System.Windows.Forms.CheckBox chkShiftIn;
  private System.Windows.Forms.CheckBox chkCtrlIn;
  private System.Windows.Forms.CheckBox chkAltIn;
  private System.Windows.Forms.RadioButton optLeftIn;
  private System.Windows.Forms.RadioButton optRightIn;
  private System.Windows.Forms.RadioButton optSingleIn;
  private System.Windows.Forms.RadioButton optDoubleIn;
  private System.Windows.Forms.Label lblZoomFactIn;
  private System.Windows.Forms.TextBox txtZoomFactIn;
  private System.Windows.Forms.GroupBox grpButtonIn;
  private System.Windows.Forms.GroupBox grpClickIn;
  private System.Windows.Forms.GroupBox grpZoomOut;
  private System.Windows.Forms.Label lblZoomOut;
  private System.Windows.Forms.CheckBox chkShiftOut;
  private System.Windows.Forms.CheckBox chkCtrlOut;
  private System.Windows.Forms.CheckBox chkAltOut;
  private System.Windows.Forms.RadioButton optLeftOut;
  private System.Windows.Forms.RadioButton optRightOut;
  private System.Windows.Forms.RadioButton optSingleOut;
  private System.Windows.Forms.RadioButton optDoubleOut;
  private System.Windows.Forms.Label lblZoomFactOut;
  private System.Windows.Forms.TextBox txtZoomFactOut;
  private System.Windows.Forms.GroupBox grpButtonOut;
  private System.Windows.Forms.GroupBox grpClickOut;
  private DoubleClickZoomerLayer m_Layer;
  private string m_FilePath;
  public ZoomDialog(DoubleClickZoomerLayer layer, string zoomFilePath)
  {
   m_Layer = layer;
   m_FilePath = zoomFilePath;
   InitializeComponent();
   UpdateUI( zoomFilePath );
  }
  private void InitializeComponent()
  {
   this.btnCancel = new System.Windows.Forms.Button();
   this.btnOK = new System.Windows.Forms.Button();
   this.grpZoomIn = new System.Windows.Forms.GroupBox();
   this.lblZoomIn = new System.Windows.Forms.Label();
   this.chkShiftIn = new System.Windows.Forms.CheckBox();
   this.chkCtrlIn = new System.Windows.Forms.CheckBox();
   this.chkAltIn = new System.Windows.Forms.CheckBox();
   this.optLeftIn = new System.Windows.Forms.RadioButton();
   this.optRightIn = new System.Windows.Forms.RadioButton();
   this.optSingleIn = new System.Windows.Forms.RadioButton();
   this.optDoubleIn = new System.Windows.Forms.RadioButton();
   this.lblZoomFactIn = new System.Windows.Forms.Label();
   this.txtZoomFactIn = new System.Windows.Forms.TextBox();
   this.grpButtonIn = new System.Windows.Forms.GroupBox();
   this.grpClickIn = new System.Windows.Forms.GroupBox();
   this.grpZoomOut = new System.Windows.Forms.GroupBox();
   this.lblZoomOut = new System.Windows.Forms.Label();
   this.chkShiftOut = new System.Windows.Forms.CheckBox();
   this.chkCtrlOut = new System.Windows.Forms.CheckBox();
   this.chkAltOut = new System.Windows.Forms.CheckBox();
   this.optLeftOut = new System.Windows.Forms.RadioButton();
   this.optRightOut = new System.Windows.Forms.RadioButton();
   this.optSingleOut = new System.Windows.Forms.RadioButton();
   this.optDoubleOut = new System.Windows.Forms.RadioButton();
   this.lblZoomFactOut = new System.Windows.Forms.Label();
   this.txtZoomFactOut = new System.Windows.Forms.TextBox();
   this.grpButtonOut = new System.Windows.Forms.GroupBox();
   this.grpClickOut = new System.Windows.Forms.GroupBox();
   this.grpZoomIn.SuspendLayout();
   this.grpZoomOut.SuspendLayout();
   this.SuspendLayout();
   this.chkShiftIn.Checked = true;
   this.chkShiftIn.CheckState = System.Windows.Forms.CheckState.Checked;
   this.chkShiftIn.Location = new System.Drawing.Point(8, 16);
   this.chkShiftIn.Name = "chkShift";
   this.chkShiftIn.Size = new System.Drawing.Size(72, 24);
   this.chkShiftIn.TabIndex = 14;
   this.chkShiftIn.Text = "Shift";
   this.chkCtrlIn.Checked = true;
   this.chkCtrlIn.CheckState = System.Windows.Forms.CheckState.Checked;
   this.chkCtrlIn.Location = new System.Drawing.Point(8, 40);
   this.chkCtrlIn.Name = "chkCtrl";
   this.chkCtrlIn.Size = new System.Drawing.Size(72, 24);
   this.chkCtrlIn.TabIndex = 14;
   this.chkCtrlIn.Text = "Control";
   this.chkAltIn.Checked = true;
   this.chkAltIn.CheckState = System.Windows.Forms.CheckState.Checked;
   this.chkAltIn.Location = new System.Drawing.Point(8, 64);
   this.chkAltIn.Name = "chkAlt";
   this.chkAltIn.Size = new System.Drawing.Size(72, 24);
   this.chkAltIn.TabIndex = 14;
   this.chkAltIn.Text = "Alt";
   this.lblZoomFactIn.Location = new System.Drawing.Point(8, 96);
   this.lblZoomFactIn.Name = "lblZoomFactIn";
   this.lblZoomFactIn.Size = new System.Drawing.Size(72, 16);
   this.lblZoomFactIn.TabIndex = 9;
   this.lblZoomFactIn.Text = "Zoom Factor:";
   this.txtZoomFactIn.Location = new System.Drawing.Point(8, 112);
   this.txtZoomFactIn.Name = "txtZoomFactIn";
   this.txtZoomFactIn.Size = new System.Drawing.Size(72, 24);
   this.txtZoomFactIn.TabIndex = 16;
   this.txtZoomFactIn.Text = "";
   this.optLeftIn.Checked = true;
   this.optLeftIn.Location = new System.Drawing.Point(8, 16);
   this.optLeftIn.Name = "optLeftIn";
   this.optLeftIn.Size = new System.Drawing.Size(64, 16);
   this.optLeftIn.TabIndex = 1;
   this.optLeftIn.TabStop = true;
   this.optLeftIn.Text = "Left";
   this.optRightIn.Checked = true;
   this.optRightIn.Location = new System.Drawing.Point(8, optLeftIn.Top + optLeftIn.Height);
   this.optRightIn.Name = "optRightIn";
   this.optRightIn.Size = new System.Drawing.Size(64, 16);
   this.optRightIn.TabIndex = 1;
   this.optRightIn.TabStop = true;
   this.optRightIn.Text = "Right";
   this.grpButtonIn.Controls.Add(this.optLeftIn);
   this.grpButtonIn.Controls.Add(this.optRightIn);
   this.grpButtonIn.Location = new System.Drawing.Point(88, 16);
   this.grpButtonIn.Name = "grpButtonIn";
   this.grpButtonIn.Size = new System.Drawing.Size(88, 56);
   this.grpButtonIn.TabIndex = 26;
   this.grpButtonIn.TabStop = false;
   this.grpButtonIn.Text = "Button:";
   this.grpButtonIn.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
   this.grpButtonIn.Visible = true;
   this.optSingleIn.Checked = true;
   this.optSingleIn.Location = new System.Drawing.Point(8, 16);
   this.optSingleIn.Name = "optSingleIn";
   this.optSingleIn.Size = new System.Drawing.Size(64, 16);
   this.optSingleIn.TabIndex = 1;
   this.optSingleIn.TabStop = true;
   this.optSingleIn.Text = "Single";
   this.optDoubleIn.Checked = true;
   this.optDoubleIn.Location = new System.Drawing.Point(8, optSingleIn.Top + optSingleIn.Height);
   this.optDoubleIn.Name = "optDoubleIn";
   this.optDoubleIn.Size = new System.Drawing.Size(64, 16);
   this.optDoubleIn.TabIndex = 1;
   this.optDoubleIn.TabStop = true;
   this.optDoubleIn.Text = "Double";
   this.grpClickIn.Controls.Add(this.optSingleIn);
   this.grpClickIn.Controls.Add(this.optDoubleIn);
   this.grpClickIn.Location = new System.Drawing.Point(88, 72);
   this.grpClickIn.Name = "grpClickIn";
   this.grpClickIn.Size = new System.Drawing.Size(88, 56);
   this.grpClickIn.TabIndex = 26;
   this.grpClickIn.TabStop = false;
   this.grpClickIn.Text = "Click:";
   this.grpClickIn.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
   this.grpClickIn.Visible = true;
   this.grpZoomIn.Controls.Add(this.chkShiftIn);
   this.grpZoomIn.Controls.Add(this.chkCtrlIn);
   this.grpZoomIn.Controls.Add(this.chkAltIn);
   this.grpZoomIn.Controls.Add(this.lblZoomFactIn);
   this.grpZoomIn.Controls.Add(this.txtZoomFactIn);
   this.grpZoomIn.Controls.Add(this.grpButtonIn);
   this.grpZoomIn.Controls.Add(this.grpClickIn);
   this.grpZoomIn.Location = new System.Drawing.Point(8, 8);
   this.grpZoomIn.Name = "grpZoomIn";
   this.grpZoomIn.Size = new System.Drawing.Size(184, 144);
   this.grpZoomIn.TabIndex = 26;
   this.grpZoomIn.TabStop = false;
   this.grpZoomIn.Text = "Zoom In:";
   this.grpZoomIn.Visible = true;
   this.chkShiftOut.Checked = true;
   this.chkShiftOut.CheckState = System.Windows.Forms.CheckState.Checked;
   this.chkShiftOut.Location = this.chkShiftIn.Location;
   this.chkShiftOut.Name = "chkShiftOut";
   this.chkShiftOut.Size = this.chkShiftIn.Size;
   this.chkShiftOut.TabIndex = 14;
   this.chkShiftOut.Text = "Shift";
   this.chkCtrlOut.Checked = true;
   this.chkCtrlOut.CheckState = System.Windows.Forms.CheckState.Checked;
   this.chkCtrlOut.Location = this.chkCtrlIn.Location;
   this.chkCtrlOut.Name = "chkCtrlOut";
   this.chkCtrlOut.Size = this.chkCtrlIn.Size;
   this.chkCtrlOut.TabIndex = 14;
   this.chkCtrlOut.Text = "Control";
   this.chkAltOut.Checked = true;
   this.chkAltOut.CheckState = System.Windows.Forms.CheckState.Checked;
   this.chkAltOut.Location = this.chkAltIn.Location;
   this.chkAltOut.Name = "chkAltOut";
   this.chkAltOut.Size = this.chkAltIn.Size;
   this.chkAltOut.TabIndex = 14;
   this.chkAltOut.Text = "Alt";
   this.lblZoomFactOut.Location = this.lblZoomFactIn.Location;
   this.lblZoomFactOut.Name = "lblZoomFactOut";
   this.lblZoomFactOut.Size = this.lblZoomFactIn.Size;
   this.lblZoomFactOut.TabIndex = 9;
   this.lblZoomFactOut.Text = "Zoom Factor:";
   this.txtZoomFactOut.Location = this.txtZoomFactIn.Location;
   this.txtZoomFactOut.Name = "txtZoomFactOut";
   this.txtZoomFactOut.Size = this.txtZoomFactIn.Size;
   this.txtZoomFactOut.TabIndex = 16;
   this.txtZoomFactOut.Text = "";
   this.optLeftOut.Checked = true;
   this.optLeftOut.Location = this.optLeftIn.Location;
   this.optLeftOut.Name = "optLeftOut";
   this.optLeftOut.Size = this.optLeftIn.Size;
   this.optLeftOut.TabIndex = 1;
   this.optLeftOut.TabStop = true;
   this.optLeftOut.Text = this.optLeftIn.Text;
   this.optRightOut.Checked = true;
   this.optRightOut.Location = this.optRightIn.Location;
   this.optRightOut.Name = "optRightOut";
   this.optRightOut.Size = this.optRightIn.Size;
   this.optRightOut.TabIndex = 1;
   this.optRightOut.TabStop = true;
   this.optRightOut.Text = this.optRightIn.Text;
   this.grpButtonOut.Controls.Add(this.optLeftOut);
   this.grpButtonOut.Controls.Add(this.optRightOut);
   this.grpButtonOut.Location = this.grpButtonIn.Location;
   this.grpButtonOut.Name = "grpButtonOut";
   this.grpButtonOut.Size = this.grpButtonIn.Size;
   this.grpButtonOut.TabIndex = 26;
   this.grpButtonOut.TabStop = false;
   this.grpButtonOut.Text = this.grpButtonIn.Text;
   this.grpButtonOut.FlatStyle = this.grpButtonIn.FlatStyle;
   this.grpButtonOut.Visible = true;
   this.optSingleOut.Checked = true;
   this.optSingleOut.Location = this.optSingleIn.Location;
   this.optSingleOut.Name = "optSingleOut";
   this.optSingleOut.Size = this.optSingleIn.Size;
   this.optSingleOut.TabIndex = 1;
   this.optSingleOut.TabStop = true;
   this.optSingleOut.Text = this.optSingleIn.Text;
   this.optDoubleOut.Checked = true;
   this.optDoubleOut.Location = this.optDoubleIn.Location;
   this.optDoubleOut.Name = "optDoubleOut";
   this.optDoubleOut.Size = this.optDoubleIn.Size;
   this.optDoubleOut.TabIndex = 1;
   this.optDoubleOut.TabStop = true;
   this.optDoubleOut.Text = this.optDoubleIn.Text;
   this.grpClickOut.Controls.Add(this.optSingleOut);
   this.grpClickOut.Controls.Add(this.optDoubleOut);
   this.grpClickOut.Location = this.grpClickIn.Location;
   this.grpClickOut.Name = "grpClickOut";
   this.grpClickOut.Size = this.grpClickIn.Size;
   this.grpClickOut.TabIndex = 26;
   this.grpClickOut.TabStop = false;
   this.grpClickOut.Text = this.grpClickIn.Text;
   this.grpClickOut.FlatStyle = this.grpClickIn.FlatStyle;
   this.grpClickOut.Visible = true;
   this.grpZoomOut.Controls.Add(this.chkShiftOut);
   this.grpZoomOut.Controls.Add(this.chkCtrlOut);
   this.grpZoomOut.Controls.Add(this.chkAltOut);
   this.grpZoomOut.Controls.Add(this.lblZoomFactOut);
   this.grpZoomOut.Controls.Add(this.txtZoomFactOut);
   this.grpZoomOut.Controls.Add(this.grpButtonOut);
   this.grpZoomOut.Controls.Add(this.grpClickOut);
   this.grpZoomOut.Location = new System.Drawing.Point(this.grpZoomIn.Left+this.grpZoomIn.Width+8, this.grpZoomIn.Top);
   this.grpZoomOut.Name = "grpZoomOut";
   this.grpZoomOut.Size = this.grpZoomIn.Size;
   this.grpZoomOut.TabIndex = 26;
   this.grpZoomOut.TabStop = false;
   this.grpZoomOut.Text = "Zoom Out:";
   this.grpZoomOut.Visible = true;
   this.btnOK.Location = new System.Drawing.Point(grpZoomIn.Left + (grpZoomIn.Width - 72)/2, grpZoomIn.Top + grpZoomIn.Height + 8);
   this.btnOK.Name = "btnOK";
   this.btnOK.TabIndex = 1;
   this.btnOK.Text = "OK";
   this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
   this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
   this.btnCancel.Location = new System.Drawing.Point(grpZoomOut.Left + (grpZoomOut.Width - 72)/2, grpZoomOut.Top + grpZoomOut.Height + 8);
   this.btnCancel.Name = "btnCancel";
   this.btnCancel.TabIndex = 0;
   this.btnCancel.Text = "Cancel";
   this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
   this.AcceptButton = this.btnOK;
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.CancelButton = this.btnCancel;
   this.ClientSize = new System.Drawing.Size(grpZoomOut.Left + grpZoomOut.Width + 8, btnCancel.Top + btnCancel.Height + 8);
   this.ControlBox = false;
   this.Controls.Add(this.btnOK);
   this.Controls.Add(this.btnCancel);
   this.Controls.Add(this.grpZoomIn);
   this.Controls.Add(this.grpZoomOut);
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
   this.MaximizeBox = false;
   this.MinimizeBox = false;
   this.Name = "ZoomDialog";
   this.ShowInTaskbar = true;
   this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
   this.Text = "Zoom Options";
   this.Load += new System.EventHandler(this.ZoomDialog_Load);
   this.grpZoomIn.ResumeLayout(false);
   this.grpZoomOut.ResumeLayout(false);
   this.ResumeLayout(false);
  }
  private void UpdateUI(string zoomFilePath)
  {
   try
   {
    using(TextReader tr = File.OpenText(zoomFilePath))
    {
     string line = tr.ReadLine();
     string[] fields = line.Split(';');
     txtZoomFactIn.Text = double.Parse(fields[(int)zoomFields.ZoomFact]).ToString();
     chkShiftIn.Checked = bool.Parse(fields[(int)zoomFields.Shift]);
     chkCtrlIn.Checked = bool.Parse(fields[(int)zoomFields.Ctrl]);
     chkAltIn.Checked = bool.Parse(fields[(int)zoomFields.Alt]);
     optLeftIn.Checked = bool.Parse(fields[(int)zoomFields.IsLeftButton]);
     optRightIn.Checked = !optLeftIn.Checked;
     optDoubleIn.Checked = bool.Parse(fields[(int)zoomFields.IsDoubleClick]);
     optSingleIn.Checked = !optDoubleIn.Checked;
     line = tr.ReadLine();
     fields = line.Split(';');
     txtZoomFactOut.Text = double.Parse(fields[(int)zoomFields.ZoomFact]).ToString();
     chkShiftOut.Checked = bool.Parse(fields[(int)zoomFields.Shift]);
     chkCtrlOut.Checked = bool.Parse(fields[(int)zoomFields.Ctrl]);
     chkAltOut.Checked = bool.Parse(fields[(int)zoomFields.Alt]);
     optLeftOut.Checked = bool.Parse(fields[(int)zoomFields.IsLeftButton]);
     optRightOut.Checked = !optLeftOut.Checked;
     optDoubleOut.Checked = bool.Parse(fields[(int)zoomFields.IsDoubleClick]);
     optSingleOut.Checked = !optDoubleOut.Checked;
     tr.Close();
    }
   }
   catch(Exception caught)
   {
    Utility.Log.Write( caught );
    string msg = "Failed to parse " + zoomFilePath + "\n\n" + caught.Message;
    throw new ApplicationException( msg );
   }
  }
  private void btnOK_Click(object sender, System.EventArgs e)
  {
   try
   {
    if ( double.Parse(txtZoomFactIn.Text) < 1 ) { txtZoomFactIn.Text = (1/double.Parse(txtZoomFactIn.Text)).ToString(); }
    if ( double.Parse(txtZoomFactOut.Text) < 1 ) { txtZoomFactOut.Text = (1/double.Parse(txtZoomFactOut.Text)).ToString(); }
   }
   catch (Exception caught)
   {
     return;
   }
   try
   {
    StreamWriter objSW = new StreamWriter(m_FilePath);
    string keyLine1 = string.Format("{0};{1};{2};{3};{4};{5}", double.Parse(txtZoomFactIn.Text), chkShiftIn.Checked, chkCtrlIn.Checked, chkAltIn.Checked, optLeftIn.Checked, optDoubleIn.Checked);
    objSW.WriteLine ( keyLine1.ToLower() );
    string keyLine2 = string.Format("{0};{1};{2};{3};{4};{5}", double.Parse(txtZoomFactOut.Text), chkShiftOut.Checked, chkCtrlOut.Checked, chkAltOut.Checked, optLeftOut.Checked, optDoubleOut.Checked);
    objSW.WriteLine ( keyLine2.ToLower() );
    objSW.Close();
   }
   catch(Exception caught)
   {
    Utility.Log.Write( caught );
    string msg = "Failed to write to " + m_FilePath + "\n\n" + caught.Message;
    throw new ApplicationException( msg );
   }
   m_Layer.LoadSettings();
   this.Close();
  }
  private void btnCancel_Click(object sender, System.EventArgs e)
  {
   this.Close();
  }
  private void ZoomDialog_Load(object sender, System.EventArgs e)
  {
  }
 }
 public class DoubleClickZoomer : WorldWind.PluginEngine.Plugin
 {
  MenuItem menuItem;
  DoubleClickZoomerLayer layer;
  string zoomFilePath;
  string zoomFile = "DoubleZoom.ini";
  ZoomDialog m_Dialog;
  public enum zoomFields
  {
   ZoomFact,
   Shift,
   Ctrl,
   Alt,
   IsLeftButton,
   IsDoubleClick
  }
  public override void Load()
  {
   zoomFilePath = Path.Combine(PluginDirectory, zoomFile);
   menuItem = new MenuItem("Zoom Extender\tZ");
   menuItem.Click += new EventHandler(menuItemClicked);
   ParentApplication.ToolsMenu.MenuItems.Add( menuItem );
   layer = new DoubleClickZoomerLayer(
    ParentApplication.WorldWindow.CurrentWorld,
    ParentApplication.WorldWindow.DrawArgs,
    ParentApplication.WorldWindow,
    menuItem,
    zoomFilePath );
   ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(layer);
   if (!File.Exists(zoomFilePath))
   {
    string zoomLineIn = string.Format("{0};{1};{2};{3};{4};{5}", 2, false, false, false, true, true);
    string zoomLineOut = string.Format("{0};{1};{2};{3};{4};{5}", 2, false, false, false, false, true);
    StreamWriter sr = new StreamWriter(zoomFilePath);
    sr.WriteLine ( zoomLineIn.ToLower() );
    sr.WriteLine ( zoomLineOut.ToLower() );
    sr.Close();
   }
   layer.LoadSettings();
   ParentApplication.WorldWindow.DoubleClick +=new EventHandler(layer.DoubleClick);
   ParentApplication.WorldWindow.MouseDown +=new MouseEventHandler(layer.MouseDown);
   ParentApplication.WorldWindow.KeyUp += new KeyEventHandler(layer.KeyUp);
   ParentApplication.WorldWindow.Click +=new EventHandler(layer.Click);
  }
  public override void Unload()
  {
   if(menuItem!=null)
   {
    ParentApplication.ToolsMenu.MenuItems.Remove( menuItem );
    menuItem.Dispose();
    menuItem = null;
   }
   bool ShiftIn = false; bool CtrlIn = false; bool AltIn = false;
   StreamWriter objSW = new StreamWriter(zoomFilePath);
   if ( ( layer.ShiftKeysIn & Keys.Shift ) == Keys.Shift ) { ShiftIn = true; }
   if ( ( layer.ShiftKeysIn & Keys.Control ) == Keys.Control ) { CtrlIn = true; }
   if ( ( layer.ShiftKeysIn & Keys.Alt ) == Keys.Alt ) { AltIn = true; }
   string keyLineIn = string.Format("{0};{1};{2};{3};{4};{5}", layer.ZoomFactIn, ShiftIn, CtrlIn, AltIn, layer.IsLeftButtonIn, layer.IsDoubleClickIn);
   objSW.WriteLine ( keyLineIn.ToLower() );
   bool ShiftOut = false; bool CtrlOut = false; bool AltOut = false;
   if ( ( layer.ShiftKeysOut & Keys.Shift ) == Keys.Shift ) { ShiftOut = true; }
   if ( ( layer.ShiftKeysOut & Keys.Control ) == Keys.Control ) { CtrlOut = true; }
   if ( ( layer.ShiftKeysOut & Keys.Alt ) == Keys.Alt ) { AltOut = true; }
   string keyLineOut = string.Format("{0};{1};{2};{3};{4};{5}", layer.ZoomFactOut, ShiftOut, CtrlOut, AltOut, layer.IsLeftButtonOut, layer.IsDoubleClickOut);
   objSW.WriteLine ( keyLineOut.ToLower() );
   objSW.Close();
   ParentApplication.WorldWindow.DoubleClick -= new EventHandler(layer.DoubleClick);
   ParentApplication.WorldWindow.MouseDown -= new MouseEventHandler(layer.MouseDown);
   ParentApplication.WorldWindow.KeyUp -= new KeyEventHandler(layer.KeyUp);
   ParentApplication.WorldWindow.Click -= new EventHandler(layer.Click);
   ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Remove(layer);
  }
  void menuItemClicked(object sender, EventArgs e)
  {
   layer.IsOn = !layer.IsOn;
   menuItem.Checked = layer.IsOn;
  }
 }
 public class DoubleClickZoomerLayer : WorldWind.Renderable.RenderableObject
 {
  public Keys ShiftKeysIn;
  public double ZoomFactIn;
  public bool IsLeftButtonIn;
  public bool IsDoubleClickIn;
  public Keys ShiftKeysOut;
  public double ZoomFactOut;
  public bool IsLeftButtonOut;
  public bool IsDoubleClickOut;
  public enum zoomFields
  {
   ZoomFact,
   Shift,
   Ctrl,
   Alt,
   IsLeftButton,
   IsDoubleClick
  }
  DrawArgs m_drawArgs;
  World m_world;
  WorldWindow m_window;
  bool isPointGotoEnabled;
  Point mouseDownPoint;
  Angle qLatitude;
  Angle qLongitude;
  bool LeftClicked;
  MenuItem m_menuItem;
  ZoomDialog zDialog;
  string m_FilePath;
  public DoubleClickZoomerLayer(World world, DrawArgs drawArgs, WorldWindow worldWindow, MenuItem menuItem, string ZoomFilePath) : base("Zoom Extender")
  {
   m_world = world;
   m_drawArgs = drawArgs;
   m_window = worldWindow;
   m_menuItem = menuItem;
   m_FilePath = ZoomFilePath;
   RenderPriority = RenderPriority.Placenames;
   IsOn = true;
  }
  public void KeyUp(object sender, KeyEventArgs e)
  {
   if (e.KeyData==Keys.Z)
   {
    IsOn = !IsOn;
    e.Handled = true;
   }
  }
  public void DoubleClick( object sender, EventArgs e )
  {
   double Coeff;
   if(!isOn)
    return;
   mouseDownPoint = DrawArgs.LastMousePosition;
   m_drawArgs.WorldCamera.PickingRayIntersection(
    (int)mouseDownPoint.X,
    (int)mouseDownPoint.Y,
    out qLatitude,
    out qLongitude);
   if ( this.IsDoubleClickIn && ( ( Control.ModifierKeys & this.ShiftKeysIn ) == this.ShiftKeysIn ) && ( LeftClicked == this.IsLeftButtonIn ) ) {
    Coeff = 1/ZoomFactIn;
   }
   else if ( this.IsDoubleClickOut && ( ( Control.ModifierKeys & this.ShiftKeysOut ) == this.ShiftKeysOut ) && ( LeftClicked == this.IsLeftButtonOut ) ) {
    Coeff = ZoomFactOut;
   }
   else {
    return;
   }
   m_window.GotoLatLonAltitude( qLatitude.Degrees, qLongitude.Degrees, Coeff*m_drawArgs.WorldCamera.Altitude );
  }
  public void Click( object sender, EventArgs e )
  {
   double Coeff;
   if(!isOn)
    return;
   mouseDownPoint = DrawArgs.LastMousePosition;
   m_drawArgs.WorldCamera.PickingRayIntersection(
    (int)mouseDownPoint.X,
    (int)mouseDownPoint.Y,
    out qLatitude,
    out qLongitude);
   if ( !this.IsDoubleClickIn && ( Control.ModifierKeys == this.ShiftKeysIn ) && ( LeftClicked == this.IsLeftButtonIn ) ) {
    Coeff = 1/ZoomFactIn;
   }
   else if ( !this.IsDoubleClickOut && ( Control.ModifierKeys == this.ShiftKeysOut ) && ( LeftClicked == this.IsLeftButtonOut ) ) {
    Coeff = ZoomFactOut;
   }
   else {
    return;
   }
   m_window.GotoLatLonAltitude( qLatitude.Degrees, qLongitude.Degrees, Coeff*m_drawArgs.WorldCamera.Altitude );
  }
  public void MouseDown( object sender, MouseEventArgs e )
  {
   LeftClicked = false;
   if(!isOn)
    return;
   if (e.Button == MouseButtons.Left) {
    LeftClicked = true;
   }
        }
  public void SetShiftKeys( int r, bool Shift, bool Ctrl, bool Alt )
  {
   if ( r == 0 ) {
    this.ShiftKeysIn = Keys.None;
    if( Shift ) { this.ShiftKeysIn = this.ShiftKeysIn | Keys.Shift; }
    if( Ctrl ) { this.ShiftKeysIn = this.ShiftKeysIn | Keys.Control; }
    if( Alt ) { this.ShiftKeysIn = this.ShiftKeysIn | Keys.Alt; }
   }
   else if ( r == 1 ) {
    this.ShiftKeysOut = Keys.None;
    if( Shift ) { this.ShiftKeysOut = this.ShiftKeysOut | Keys.Shift; }
    if( Ctrl ) { this.ShiftKeysOut = this.ShiftKeysOut | Keys.Control; }
    if( Alt ) { this.ShiftKeysOut = this.ShiftKeysOut | Keys.Alt; }
   }
  }
  public void LoadSettings()
  {
   try
   {
    using(TextReader tr = File.OpenText(m_FilePath))
    {
     string line = tr.ReadLine();
     string[] fields = line.Split(';');
     ZoomFactIn = double.Parse(fields[(int)zoomFields.ZoomFact]);
     bool ShiftIn = bool.Parse(fields[(int)zoomFields.Shift]);
     bool CtrlIn = bool.Parse(fields[(int)zoomFields.Ctrl]);
     bool AltIn = bool.Parse(fields[(int)zoomFields.Alt]);
     SetShiftKeys( 0, ShiftIn, CtrlIn, AltIn );
     IsLeftButtonIn = bool.Parse(fields[(int)zoomFields.IsLeftButton]);
     IsDoubleClickIn = bool.Parse(fields[(int)zoomFields.IsDoubleClick]);
     line = tr.ReadLine();
     fields = line.Split(';');
     ZoomFactOut = double.Parse(fields[(int)zoomFields.ZoomFact]);
     bool ShiftOut = bool.Parse(fields[(int)zoomFields.Shift]);
     bool AltOut = bool.Parse(fields[(int)zoomFields.Alt]);
     bool CtrlOut = bool.Parse(fields[(int)zoomFields.Ctrl]);
     SetShiftKeys( 1, ShiftOut, CtrlOut, AltOut );
     IsLeftButtonOut = bool.Parse(fields[(int)zoomFields.IsLeftButton]);
     IsDoubleClickOut = bool.Parse(fields[(int)zoomFields.IsDoubleClick]);
     tr.Close();
    }
   }
   catch(Exception caught)
   {
    Utility.Log.Write( caught );
    string msg = "Failed to parse " + m_FilePath + "\n\n" + caught.Message;
    throw new ApplicationException( msg );
   }
  }
  public override bool IsOn
  {
   get
   {
    return base.IsOn;
   }
   set
   {
    if(value==isOn)
     return;
    base.IsOn = value;
    if(isOn)
    {
     isPointGotoEnabled = World.Settings.CameraIsPointGoto;
     World.Settings.CameraIsPointGoto = false;
    }
    else
    {
     World.Settings.CameraIsPointGoto = isPointGotoEnabled;
    }
    m_menuItem.Checked = base.IsOn;
   }
  }
  public override void Render(DrawArgs drawArgs)
  {
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   isInitialized = true;
  }
  public override void Update(DrawArgs drawArgs)
  {
   if(!isInitialized)
    Initialize(drawArgs);
  }
  public override void Dispose()
  {
   isInitialized = false;
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return false;
  }
   public override void BuildContextMenu( ContextMenu menu )
   {
     menu.MenuItems.Add("Properties", new System.EventHandler(OnPropertiesClick));
   }
   public void OnPropertiesClick(object sender, EventArgs e)
   {
   if( zDialog != null && ! zDialog.IsDisposed)
    return;
   zDialog = new ZoomDialog( this, this.m_FilePath );
   zDialog.Show();
   }
 }
}
