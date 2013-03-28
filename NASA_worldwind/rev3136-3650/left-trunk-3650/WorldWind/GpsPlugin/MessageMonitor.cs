using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
namespace GpsTrackerPlugin
{
 public class MessageMonitor : System.Windows.Forms.Form
 {
  private bool m_bPlay;
  private System.Windows.Forms.CheckBox checkBoxRenderer;
  private System.Windows.Forms.CheckBox checkBoxCOMRawData;
  private System.Windows.Forms.Button buttonPausePlay;
  private System.Windows.Forms.GroupBox groupBox1;
  private System.Windows.Forms.CheckBox checkBoxUnfilterAPRS;
  private System.Windows.Forms.CheckBox checkBoxTCPUDPRawData;
  private System.Windows.Forms.ColumnHeader columnHeader1;
  private System.Windows.Forms.ColumnHeader columnHeader2;
  private System.Windows.Forms.CheckBox checkBoxFileData;
  private System.Windows.Forms.ListView listViewData;
  private System.Windows.Forms.Timer timerBringToFront;
  private System.ComponentModel.IContainer components;
        public delegate void AddStringToMessagesList(string sType, string sMessage);
  public MessageMonitor()
  {
   InitializeComponent();
   m_bPlay = true;
   timerBringToFront.Enabled=true;
  }
  protected override void Dispose( bool disposing )
  {
   if( disposing )
   {
    if(components != null)
    {
     components.Dispose();
    }
   }
   base.Dispose( disposing );
  }
  private void InitializeComponent()
  {
            this.components = new System.ComponentModel.Container();
            System.Windows.Forms.ListViewItem listViewItem1 = new System.Windows.Forms.ListViewItem(new string[] {
            "",
            ""}, -1);
            this.checkBoxRenderer = new System.Windows.Forms.CheckBox();
            this.checkBoxCOMRawData = new System.Windows.Forms.CheckBox();
            this.buttonPausePlay = new System.Windows.Forms.Button();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.checkBoxUnfilterAPRS = new System.Windows.Forms.CheckBox();
            this.checkBoxTCPUDPRawData = new System.Windows.Forms.CheckBox();
            this.listViewData = new System.Windows.Forms.ListView();
            this.columnHeader1 = new System.Windows.Forms.ColumnHeader();
            this.columnHeader2 = new System.Windows.Forms.ColumnHeader();
            this.checkBoxFileData = new System.Windows.Forms.CheckBox();
            this.timerBringToFront = new System.Windows.Forms.Timer(this.components);
            this.SuspendLayout();
            this.checkBoxRenderer.Checked = true;
            this.checkBoxRenderer.CheckState = System.Windows.Forms.CheckState.Checked;
            this.checkBoxRenderer.Location = new System.Drawing.Point(8, 360);
            this.checkBoxRenderer.Name = "checkBoxRenderer";
            this.checkBoxRenderer.Size = new System.Drawing.Size(328, 16);
            this.checkBoxRenderer.TabIndex = 1;
            this.checkBoxRenderer.Text = "Show Messages to WorldWind GPSTracker Icons Renderer";
            this.checkBoxCOMRawData.Location = new System.Drawing.Point(8, 392);
            this.checkBoxCOMRawData.Name = "checkBoxCOMRawData";
            this.checkBoxCOMRawData.Size = new System.Drawing.Size(152, 16);
            this.checkBoxCOMRawData.TabIndex = 2;
            this.checkBoxCOMRawData.Text = "Show Raw COM Data";
            this.buttonPausePlay.Location = new System.Drawing.Point(584, 320);
            this.buttonPausePlay.Name = "buttonPausePlay";
            this.buttonPausePlay.Size = new System.Drawing.Size(56, 24);
            this.buttonPausePlay.TabIndex = 3;
            this.buttonPausePlay.Text = "Pause";
            this.buttonPausePlay.Click += new System.EventHandler(this.buttonPausePlay_Click);
            this.groupBox1.Location = new System.Drawing.Point(-24, 344);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(664, 8);
            this.groupBox1.TabIndex = 15;
            this.groupBox1.TabStop = false;
            this.checkBoxUnfilterAPRS.Location = new System.Drawing.Point(8, 376);
            this.checkBoxUnfilterAPRS.Name = "checkBoxUnfilterAPRS";
            this.checkBoxUnfilterAPRS.Size = new System.Drawing.Size(288, 16);
            this.checkBoxUnfilterAPRS.TabIndex = 16;
            this.checkBoxUnfilterAPRS.Text = "Show Unfiltered APRS Messages";
            this.checkBoxTCPUDPRawData.Location = new System.Drawing.Point(352, 360);
            this.checkBoxTCPUDPRawData.Name = "checkBoxTCPUDPRawData";
            this.checkBoxTCPUDPRawData.Size = new System.Drawing.Size(200, 16);
            this.checkBoxTCPUDPRawData.TabIndex = 17;
            this.checkBoxTCPUDPRawData.Text = "Show Raw TCP|UDP Data";
            this.listViewData.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.columnHeader1,
            this.columnHeader2});
            this.listViewData.FullRowSelect = true;
            this.listViewData.HeaderStyle = System.Windows.Forms.ColumnHeaderStyle.Nonclickable;
            this.listViewData.Items.AddRange(new System.Windows.Forms.ListViewItem[] {
            listViewItem1});
            this.listViewData.LabelWrap = false;
            this.listViewData.Location = new System.Drawing.Point(8, 8);
            this.listViewData.Name = "listViewData";
            this.listViewData.Size = new System.Drawing.Size(632, 304);
            this.listViewData.TabIndex = 18;
            this.listViewData.UseCompatibleStateImageBehavior = false;
            this.listViewData.View = System.Windows.Forms.View.Details;
            this.columnHeader1.Text = "Type";
            this.columnHeader1.Width = 102;
            this.columnHeader2.Text = "Data";
            this.columnHeader2.Width = 512;
            this.checkBoxFileData.Location = new System.Drawing.Point(352, 376);
            this.checkBoxFileData.Name = "checkBoxFileData";
            this.checkBoxFileData.Size = new System.Drawing.Size(200, 16);
            this.checkBoxFileData.TabIndex = 19;
            this.checkBoxFileData.Text = "Show Raw File Data";
            this.timerBringToFront.Interval = 2000;
            this.timerBringToFront.Tick += new System.EventHandler(this.timerBringToFront_Tick);
            this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
            this.ClientSize = new System.Drawing.Size(650, 415);
            this.Controls.Add(this.checkBoxFileData);
            this.Controls.Add(this.listViewData);
            this.Controls.Add(this.checkBoxTCPUDPRawData);
            this.Controls.Add(this.checkBoxUnfilterAPRS);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.buttonPausePlay);
            this.Controls.Add(this.checkBoxCOMRawData);
            this.Controls.Add(this.checkBoxRenderer);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.Name = "MessageMonitor";
            this.Text = "GPSTracker :: MessageMonitor";
            this.ResumeLayout(false);
  }
  private void buttonPausePlay_Click(object sender, System.EventArgs e)
  {
   if (m_bPlay)
   {
    m_bPlay=false;
    buttonPausePlay.Text="Play";
   }
   else
   {
    m_bPlay=true;
    buttonPausePlay.Text="Pause";
   }
  }
  public void AddMessage(char [] cMessage, int iLength)
  {
   try
   {
    if (checkBoxRenderer.Checked && m_bPlay)
    {
     char [] trimChars = { ' ', '\t', '\r', '\n' };
     String sMessage = new String(cMessage,0,iLength);
     AddToList("Renderer", sMessage);
    }
   }
   catch (Exception)
   {
   }
  }
  public void AddMessageUnfilteredAPRS(string sMessage)
  {
   try
   {
    if (checkBoxUnfilterAPRS.Checked && m_bPlay)
    {
     char [] trimChars = { ' ', '\t', '\r', '\n' };
     sMessage = sMessage.Trim(trimChars);
     AddToList("Unfiltered APRS", sMessage);
    }
   }
   catch (Exception)
   {
   }
  }
  public void AddMessageCOMRaw(string sMessage)
  {
   try
   {
    if (checkBoxCOMRawData.Checked && m_bPlay)
     AddToList("Raw COM", sMessage);
   }
   catch (Exception)
   {
   }
  }
  public void AddMessageTCPUDPRaw(string sMessage)
  {
   try
   {
    if (checkBoxTCPUDPRawData.Checked && m_bPlay)
     AddToList("Raw TCP/UDP", sMessage);
   }
   catch (Exception)
    {
   }
  }
  public void AddMessageFileRaw(string sMessage)
  {
   try
   {
    if (checkBoxFileData.Checked && m_bPlay)
     AddToList("Raw File", sMessage);
   }
   catch (Exception)
   {
    }
   }
        public void AddToMessagesList(string sType, string sMessage)
  {
   ListViewItem item = new ListViewItem(sType);
   item.SubItems.Add(sMessage);
   listViewData.Items.Add(item);
   item.EnsureVisible();
  }
  private void AddToList(string sType, string sMessage)
  {
            listViewData.BeginInvoke(new AddStringToMessagesList(AddToMessagesList), new object[] { sType, sMessage });
  }
  private void timerBringToFront_Tick(object sender, System.EventArgs e)
  {
   BringToFront();
   timerBringToFront.Enabled=false;
  }
 }
}
