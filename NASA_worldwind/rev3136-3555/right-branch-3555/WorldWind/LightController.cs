using System.Windows.Forms;
using System.Drawing;
using System;
using WorldWind;
namespace Murris.Plugins
{
       public class LightController : WorldWind.PluginEngine.Plugin
       {
               static string Name = "Sunlight options";
               MenuItem m_MenuItem;
               LightDialog m_Form;
               public override void Load()
               {
                       m_MenuItem = new MenuItem("Sunlight control...");
                       m_MenuItem.Click += new EventHandler(menuItemClicked);
                       ParentApplication.ToolsMenu.MenuItems.Add(m_MenuItem);
               }
               public override void Unload()
               {
                       ParentApplication.PluginsMenu.MenuItems.Remove(m_MenuItem);
                       if (m_Form != null)
                       {
                               if(!m_Form.IsDisposed)
                               {
                                       m_Form.Dispose();
                               }
                               m_Form = null;
                       }
               }
               private void menuItemClicked(object sender, System.EventArgs e)
               {
                       if (m_Form == null)
                       {
                               m_Form = new LightDialog(Name);
                       }
                       else
                       {
                               if(m_Form.IsDisposed) m_Form = new LightDialog(Name);
                       }
                       m_Form.Visible = true;
               }
               public class LightDialog : System.Windows.Forms.Form
               {
                       private System.Windows.Forms.Label lblSunShading;
                       private System.Windows.Forms.CheckBox chkSunShading;
                       private System.Windows.Forms.Label lblSunFixed;
                       private System.Windows.Forms.CheckBox chkSunFixed;
                       private System.Windows.Forms.Label lblSunHeading;
                       private System.Windows.Forms.TrackBar tbSunHeading;
                       private System.Windows.Forms.Label lblSunElevation;
                       private System.Windows.Forms.TrackBar tbSunElevation;
                       private System.Windows.Forms.Button btnOK;
                       private System.Windows.Forms.Button btnCancel;
                       private bool suspendEvents = true;
                       private bool EnableSunShadingBack;
                       private bool SunSynchedWithTimeBack;
                       private double SunHeadingBack;
                       private double SunElevationBack;
                       public LightDialog(string Name)
                       {
                               InitializeComponent();
                               this.Text = Name;
                               EnableSunShadingBack = World.Settings.EnableSunShading;
                               SunSynchedWithTimeBack = World.Settings.SunSynchedWithTime;
                               SunHeadingBack = World.Settings.SunHeading;
                               SunElevationBack = World.Settings.SunElevation;
                               suspendEvents = true;
                               chkSunShading.Checked = World.Settings.EnableSunShading;
                               chkSunFixed.Checked = !World.Settings.SunSynchedWithTime;
                               tbSunHeading.Value = (int)MathEngine.RadiansToDegrees(World.Settings.SunHeading);
                               tbSunElevation.Value = (int)MathEngine.RadiansToDegrees(World.Settings.SunElevation);
                               suspendEvents = false;
                       }
                       private void InitializeComponent()
                       {
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.lblSunShading = new System.Windows.Forms.Label();
         this.chkSunShading = new System.Windows.Forms.CheckBox();
         this.lblSunFixed = new System.Windows.Forms.Label();
         this.chkSunFixed = new System.Windows.Forms.CheckBox();
         this.lblSunHeading = new System.Windows.Forms.Label();
         this.tbSunHeading = new System.Windows.Forms.TrackBar();
         this.lblSunElevation = new System.Windows.Forms.Label();
         this.tbSunElevation = new System.Windows.Forms.TrackBar();
         ((System.ComponentModel.ISupportInitialize)(this.tbSunHeading)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.tbSunElevation)).BeginInit();
         this.SuspendLayout();
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(211, 180);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 0;
         this.btnCancel.Text = "Cancel";
         this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
         this.btnOK.Location = new System.Drawing.Point(124, 180);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 1;
         this.btnOK.Text = "OK";
         this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
         this.lblSunShading.AutoSize = true;
         this.lblSunShading.Location = new System.Drawing.Point(16, 18);
         this.lblSunShading.Name = "lblSunShading";
         this.lblSunShading.Size = new System.Drawing.Size(32, 13);
         this.lblSunShading.TabIndex = 2;
         this.lblSunShading.Text = "Sun :";
         this.chkSunShading.Location = new System.Drawing.Point(98, 15);
         this.chkSunShading.Name = "chkSunShading";
         this.chkSunShading.Size = new System.Drawing.Size(104, 24);
         this.chkSunShading.TabIndex = 3;
         this.chkSunShading.Text = "Enable sunlight";
         this.chkSunShading.CheckedChanged += new System.EventHandler(this.FormChanged);
         this.lblSunFixed.AutoSize = true;
         this.lblSunFixed.Location = new System.Drawing.Point(16, 49);
         this.lblSunFixed.Name = "lblSunFixed";
         this.lblSunFixed.Size = new System.Drawing.Size(64, 13);
         this.lblSunFixed.TabIndex = 4;
         this.lblSunFixed.Text = "Orientation :";
         this.chkSunFixed.Location = new System.Drawing.Point(98, 46);
         this.chkSunFixed.Name = "chkSunFixed";
         this.chkSunFixed.Size = new System.Drawing.Size(104, 24);
         this.chkSunFixed.TabIndex = 5;
         this.chkSunFixed.Text = "Fixed";
         this.chkSunFixed.CheckedChanged += new System.EventHandler(this.FormChanged);
         this.lblSunHeading.AutoSize = true;
         this.lblSunHeading.Location = new System.Drawing.Point(16, 80);
         this.lblSunHeading.Name = "lblSunHeading";
         this.lblSunHeading.Size = new System.Drawing.Size(53, 13);
         this.lblSunHeading.TabIndex = 6;
         this.lblSunHeading.Text = "Heading :";
         this.tbSunHeading.LargeChange = 15;
         this.tbSunHeading.Location = new System.Drawing.Point(92, 77);
         this.tbSunHeading.Maximum = 180;
         this.tbSunHeading.Minimum = -180;
         this.tbSunHeading.Name = "tbSunHeading";
         this.tbSunHeading.Size = new System.Drawing.Size(160, 37);
         this.tbSunHeading.TabIndex = 7;
         this.tbSunHeading.TickFrequency = 30;
         this.tbSunHeading.ValueChanged += new System.EventHandler(this.FormChanged);
         this.lblSunElevation.AutoSize = true;
         this.lblSunElevation.Location = new System.Drawing.Point(16, 125);
         this.lblSunElevation.Name = "lblSunElevation";
         this.lblSunElevation.Size = new System.Drawing.Size(57, 13);
         this.lblSunElevation.TabIndex = 8;
         this.lblSunElevation.Text = "Elevation :";
         this.tbSunElevation.Location = new System.Drawing.Point(92, 122);
         this.tbSunElevation.Maximum = 90;
         this.tbSunElevation.Minimum = -10;
         this.tbSunElevation.Name = "tbSunElevation";
         this.tbSunElevation.Size = new System.Drawing.Size(160, 37);
         this.tbSunElevation.TabIndex = 9;
         this.tbSunElevation.TickFrequency = 10;
         this.tbSunElevation.ValueChanged += new System.EventHandler(this.FormChanged);
         this.AcceptButton = this.btnOK;
         this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(306, 220);
         this.ControlBox = false;
         this.Controls.Add(this.lblSunShading);
         this.Controls.Add(this.chkSunShading);
         this.Controls.Add(this.lblSunFixed);
         this.Controls.Add(this.chkSunFixed);
         this.Controls.Add(this.lblSunHeading);
         this.Controls.Add(this.tbSunHeading);
         this.Controls.Add(this.lblSunElevation);
         this.Controls.Add(this.tbSunElevation);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.btnCancel);
         this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
         this.MaximizeBox = false;
         this.MinimizeBox = false;
         this.Name = "LightDialog";
         this.ShowInTaskbar = false;
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
         this.TopMost = true;
         ((System.ComponentModel.ISupportInitialize)(this.tbSunHeading)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.tbSunElevation)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();
                       }
                       protected override void OnPaint(PaintEventArgs e)
                       {
                               base.OnPaint(e);
                               DrawLightGraphics();
                       }
                       private void FormChanged(object sender, System.EventArgs e)
                       {
                               if(!suspendEvents)
                               {
                                       World.Settings.EnableSunShading = chkSunShading.Checked;
                                       World.Settings.SunSynchedWithTime = !chkSunFixed.Checked;
                                       World.Settings.SunHeading = MathEngine.DegreesToRadians(tbSunHeading.Value);
                                       World.Settings.SunElevation = MathEngine.DegreesToRadians(tbSunElevation.Value);
                                       if(chkSunShading.Checked)
                                       {
                                               chkSunFixed.Enabled = true;
                                       }
                                       else
                                       {
                                               chkSunFixed.Enabled = false;
                                       }
                                       if(chkSunFixed.Checked && chkSunFixed.Enabled)
                                       {
                                               tbSunHeading.Enabled = true;
                                               tbSunElevation.Enabled = true;
                                       }
                                       else
                                       {
                                               tbSunHeading.Enabled = false;
                                               tbSunElevation.Enabled = false;
                                       }
                                       DrawLightGraphics();
                               }
                       }
                       private void DrawLightGraphics()
                       {
                               Graphics g = this.CreateGraphics();
                               int w = 32;
                               int h = 32;
                               int x, y;
                               Pen pen1 = new Pen(Color.Black, 1);
                               Pen pen2 = new Pen(Color.Red, 1);
                               g.ResetTransform();
                               g.TranslateTransform(256f, 77f);
                               g.FillRectangle(new SolidBrush(this.BackColor), 0, 0, w+1, h+1);
                               if(tbSunHeading.Enabled) {
                                       g.DrawEllipse(pen1, 0, 0, w, h);
                                       g.DrawLine(pen1, 0, h/2, w, h/2);
                                       g.DrawLine(pen1, w/2, 0, w/2, h);
                                       x = (int)(w/2 * (float)Math.Sin(World.Settings.SunHeading));
                                       y = (int)(h/2 * (float)Math.Cos(World.Settings.SunHeading));
                                       g.DrawLine(pen2, w/2, h/2, w/2 + x, h/2 - y);
                               }
                               g.ResetTransform();
                               g.TranslateTransform(256f, 122f);
                               g.FillRectangle(new SolidBrush(this.BackColor), 0, 0, w+1, h+10);
                               if(tbSunElevation.Enabled) {
                                       g.DrawArc(pen1, -w, 0, w+w, h+h, -tbSunElevation.Minimum, -tbSunElevation.Maximum + tbSunElevation.Minimum);
                                       g.DrawLine(pen1, 0, h, w, h);
                                       g.DrawLine(pen1, 0, 0, 0, h);
                                       x = (int)(w * (float)Math.Cos(World.Settings.SunElevation));
                                       y = (int)(h * (float)Math.Sin(World.Settings.SunElevation));
                                       g.DrawLine(pen2, 0, h, x, h - y);
                               }
                       }
                       private void btnOK_Click(object sender, System.EventArgs e)
                       {
                               this.Close();
                       }
                       private void btnCancel_Click(object sender, System.EventArgs e)
                       {
                               World.Settings.EnableSunShading = EnableSunShadingBack;
                               World.Settings.SunSynchedWithTime = SunSynchedWithTimeBack;
                               World.Settings.SunHeading = SunHeadingBack;
                               World.Settings.SunElevation = SunElevationBack;
                               this.Close();
                       }
               }
       }
}
