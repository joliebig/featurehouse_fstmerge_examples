using System;
using System.Globalization;
using System.Diagnostics;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.IO;
using System.Text;
using System.Windows.Forms;
using Utility;
namespace WorldWind.DataSource
{
 public class QueueMonitor : System.Windows.Forms.Form
 {
  private System.Windows.Forms.ListView listView;
  private System.Windows.Forms.ColumnHeader columnHeaderStartTime;
  private System.Windows.Forms.ColumnHeader columnHeaderState;
  private System.Windows.Forms.ColumnHeader columnHeaderBasePriority;
  private System.Windows.Forms.ColumnHeader columnHeaderDescription;
  private System.Windows.Forms.ContextMenu contextMenu;
  private System.Windows.Forms.MenuItem menuItemSpacer1;
  private System.Windows.Forms.MenuItem menuItemCopy;
  private System.Windows.Forms.MenuItem menuItemOpenUrl;
  private System.Windows.Forms.MenuItem menuItemViewDir;
  private System.Windows.Forms.MenuItem menuItemClear;
  private System.Windows.Forms.ColumnHeader columnHeaderPriority;
  private System.Windows.Forms.MenuItem menuItemHeaders;
        private System.Windows.Forms.MenuItem menuItemSpacer2;
  private System.Windows.Forms.MainMenu mainMenu1;
  private System.Windows.Forms.MenuItem menuItemEdit;
  private System.Windows.Forms.MenuItem menuItem6;
  private System.Windows.Forms.MenuItem menuItem7;
  private System.Windows.Forms.MenuItem menuItem9;
        private System.Windows.Forms.MenuItem menuItemRun;
        private IContainer components;
  private System.Windows.Forms.MenuItem menuItemEditCopy;
  private System.Windows.Forms.MenuItem menuItemEditDelete;
  private System.Windows.Forms.MenuItem menuItemEditClear;
  private bool isRunning = true;
  private System.Windows.Forms.MenuItem menuItemSpacer3;
  private const int maxItems = 50;
  private System.Windows.Forms.MenuItem menuItem4;
  private System.Windows.Forms.MenuItem menuItemWriteLog;
  private bool logToFile;
  private System.Windows.Forms.ColumnHeader columnHeaderProgress;
  private System.Windows.Forms.MenuItem menuItemTools;
  private System.Windows.Forms.MenuItem menuItemFile;
  private System.Windows.Forms.MenuItem menuItemFileClose;
  private System.Windows.Forms.MenuItem menuItemFileRetry;
  private System.Windows.Forms.MenuItem menuItemSeparator3;
  private System.Windows.Forms.MenuItem menuItemToolsDetails;
  private System.Windows.Forms.MenuItem menuItemToolsViewUrl;
  private System.Windows.Forms.MenuItem menuItemToolsViewDirectory;
  private const string logCategory = "HTTP";
        private System.Windows.Forms.MenuItem menuItemEditSelectAll;
        private Timer updateTimer;
  private ArrayList retryDownloads = new ArrayList();
  public QueueMonitor()
  {
   InitializeComponent();
   InitializeComponentText();
            listView.VirtualListSize = 0;
            updateTimer.Interval = 50;
            updateTimer.Start();
            updateTimer.Tick += new EventHandler(updateTimer_Tick);
  }
        delegate void UpdateTimerTickDelegate(object sender, EventArgs e);
        void updateTimer_Tick(object sender, EventArgs e)
        {
            if (InvokeRequired)
            {
                Delegate tickDelegate = new UpdateTimerTickDelegate(updateTimer_Tick);
                this.Invoke(tickDelegate, new object[] { sender, e });
            }
            else
            {
                listView.VirtualListSize = DataStore.ActiveRequestCount + DataStore.PendingRequestCount;
                this.updateTimer.Start();
            }
        }
  private void InitializeComponentText()
  {
   string tabChar = "\t";
   this.columnHeaderStartTime.Text = "Start time";
   this.columnHeaderState.Text = "State";
   this.columnHeaderProgress.Text = "Progress";
   this.columnHeaderDescription.Text = "Description";
   this.columnHeaderBasePriority.Text = "Base Pri";
   this.columnHeaderPriority.Text = "Priority";
   this.menuItemHeaders.Text = "View request &details";
   this.menuItemOpenUrl.Text = "Open &Url in web browser";
   this.menuItemViewDir.Text = "&View destination directory";
   this.menuItemCopy.Text = "&Copy information";
   this.menuItemClear.Text = "&Clear list";
   this.menuItemFile.Text = "&File";
   this.menuItemFileRetry.Text = "&Retry download";
   this.menuItemFileClose.Text = "&Close";
   this.menuItemEdit.Text = "&Edit";
   this.menuItemEditCopy.Text = "&Copy";
   this.menuItemEditDelete.Text = "&Delete";
   this.menuItemEditSelectAll.Text = "Select &All";
   this.menuItemEditClear.Text = "&Clear";
   this.menuItem9.Text = "&Monitor";
   this.menuItemRun.Text = "&Run";
   this.menuItemWriteLog.Text = "&Write Log";
   this.menuItemTools.Text = "&Tools";
   this.menuItemToolsDetails.Text = string.Format("View request &details{0}{1}", tabChar, "Enter");
   this.menuItemToolsViewUrl.Text = "Open &Url in web browser";
   this.menuItemToolsViewDirectory.Text = "&View destination directory";
   this.Text = "Download progress monitor";
  }
  protected override void OnKeyUp(System.Windows.Forms.KeyEventArgs e)
  {
   if(e==null)
    return;
   switch(e.KeyCode)
   {
    case Keys.Space:
     menuItemHeaders_Click(this,e);
     break;
    case Keys.Escape:
     Close();
     e.Handled = true;
     break;
    case Keys.H:
    case Keys.F4:
     if(e.Modifiers==Keys.Control)
     {
      Close();
      e.Handled = true;
     }
     break;
   }
   base.OnKeyUp(e);
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
            this.listView = new System.Windows.Forms.ListView();
            this.columnHeaderStartTime = new System.Windows.Forms.ColumnHeader();
            this.columnHeaderState = new System.Windows.Forms.ColumnHeader();
            this.columnHeaderProgress = new System.Windows.Forms.ColumnHeader();
            this.columnHeaderDescription = new System.Windows.Forms.ColumnHeader();
            this.columnHeaderBasePriority = new System.Windows.Forms.ColumnHeader();
            this.columnHeaderPriority = new System.Windows.Forms.ColumnHeader();
            this.contextMenu = new System.Windows.Forms.ContextMenu();
            this.menuItemHeaders = new System.Windows.Forms.MenuItem();
            this.menuItemSpacer1 = new System.Windows.Forms.MenuItem();
            this.menuItemOpenUrl = new System.Windows.Forms.MenuItem();
            this.menuItemViewDir = new System.Windows.Forms.MenuItem();
            this.menuItemSpacer2 = new System.Windows.Forms.MenuItem();
            this.menuItemCopy = new System.Windows.Forms.MenuItem();
            this.menuItemClear = new System.Windows.Forms.MenuItem();
            this.mainMenu1 = new System.Windows.Forms.MainMenu(this.components);
            this.menuItemFile = new System.Windows.Forms.MenuItem();
            this.menuItemFileRetry = new System.Windows.Forms.MenuItem();
            this.menuItemSeparator3 = new System.Windows.Forms.MenuItem();
            this.menuItemFileClose = new System.Windows.Forms.MenuItem();
            this.menuItemEdit = new System.Windows.Forms.MenuItem();
            this.menuItemEditCopy = new System.Windows.Forms.MenuItem();
            this.menuItemEditDelete = new System.Windows.Forms.MenuItem();
            this.menuItem6 = new System.Windows.Forms.MenuItem();
            this.menuItemEditSelectAll = new System.Windows.Forms.MenuItem();
            this.menuItem7 = new System.Windows.Forms.MenuItem();
            this.menuItemEditClear = new System.Windows.Forms.MenuItem();
            this.menuItem9 = new System.Windows.Forms.MenuItem();
            this.menuItemRun = new System.Windows.Forms.MenuItem();
            this.menuItem4 = new System.Windows.Forms.MenuItem();
            this.menuItemWriteLog = new System.Windows.Forms.MenuItem();
            this.menuItemTools = new System.Windows.Forms.MenuItem();
            this.menuItemToolsDetails = new System.Windows.Forms.MenuItem();
            this.menuItemSpacer3 = new System.Windows.Forms.MenuItem();
            this.menuItemToolsViewUrl = new System.Windows.Forms.MenuItem();
            this.menuItemToolsViewDirectory = new System.Windows.Forms.MenuItem();
            this.updateTimer = new System.Windows.Forms.Timer(this.components);
            this.SuspendLayout();
            this.listView.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.columnHeaderStartTime,
            this.columnHeaderState,
            this.columnHeaderProgress,
            this.columnHeaderDescription,
            this.columnHeaderBasePriority,
            this.columnHeaderPriority});
            this.listView.ContextMenu = this.contextMenu;
            this.listView.Dock = System.Windows.Forms.DockStyle.Fill;
            this.listView.FullRowSelect = true;
            this.listView.GridLines = true;
            this.listView.Location = new System.Drawing.Point(0, 0);
            this.listView.Name = "listView";
            this.listView.Size = new System.Drawing.Size(768, 179);
            this.listView.TabIndex = 0;
            this.listView.UseCompatibleStateImageBehavior = false;
            this.listView.View = System.Windows.Forms.View.Details;
            this.listView.VirtualMode = true;
            this.listView.DoubleClick += new System.EventHandler(this.menuItemHeaders_Click);
            this.listView.ColumnClick += new System.Windows.Forms.ColumnClickEventHandler(this.listView_ColumnClick);
            this.listView.RetrieveVirtualItem += new System.Windows.Forms.RetrieveVirtualItemEventHandler(this.listView_RetrieveVirtualItem);
            this.columnHeaderStartTime.Text = "Start time";
            this.columnHeaderState.Text = "State";
            this.columnHeaderState.Width = 65;
            this.columnHeaderProgress.Text = "Progress";
            this.columnHeaderProgress.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            this.columnHeaderProgress.Width = 65;
            this.columnHeaderDescription.Text = "Description";
            this.columnHeaderDescription.Width = 300;
            this.columnHeaderBasePriority.Text = "Base Pri";
            this.columnHeaderPriority.Text = "Priority";
            this.contextMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItemHeaders,
            this.menuItemSpacer1,
            this.menuItemOpenUrl,
            this.menuItemViewDir,
            this.menuItemSpacer2,
            this.menuItemCopy,
            this.menuItemClear});
            this.contextMenu.Popup += new System.EventHandler(this.contextMenu_Popup);
            this.menuItemHeaders.DefaultItem = true;
            this.menuItemHeaders.Index = 0;
            this.menuItemHeaders.Text = "View request &details";
            this.menuItemHeaders.Click += new System.EventHandler(this.menuItemHeaders_Click);
            this.menuItemSpacer1.Index = 1;
            this.menuItemSpacer1.Text = "-";
            this.menuItemOpenUrl.Index = 2;
            this.menuItemOpenUrl.Text = "Open &Url in web browser";
            this.menuItemOpenUrl.Click += new System.EventHandler(this.menuItemOpenUrl_Click);
            this.menuItemViewDir.Index = 3;
            this.menuItemViewDir.Text = "&View destination directory";
            this.menuItemViewDir.Click += new System.EventHandler(this.menuItemViewDir_Click);
            this.menuItemSpacer2.Index = 4;
            this.menuItemSpacer2.Text = "-";
            this.menuItemCopy.Index = 5;
            this.menuItemCopy.Text = "&Copy information";
            this.menuItemCopy.Click += new System.EventHandler(this.menuItemCopy_Click);
            this.menuItemClear.Index = 6;
            this.menuItemClear.Text = "&Clear list";
            this.menuItemClear.Click += new System.EventHandler(this.menuItemClear_Click);
            this.mainMenu1.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItemFile,
            this.menuItemEdit,
            this.menuItem9,
            this.menuItemTools});
            this.menuItemFile.Index = 0;
            this.menuItemFile.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItemFileRetry,
            this.menuItemSeparator3,
            this.menuItemFileClose});
            this.menuItemFile.Text = "&File";
            this.menuItemFile.Popup += new System.EventHandler(this.menuItemFile_Popup);
            this.menuItemFileRetry.Index = 0;
            this.menuItemFileRetry.Shortcut = System.Windows.Forms.Shortcut.CtrlR;
            this.menuItemFileRetry.Text = "&Retry download";
            this.menuItemSeparator3.Index = 1;
            this.menuItemSeparator3.Text = "-";
            this.menuItemFileClose.Index = 2;
            this.menuItemFileClose.Shortcut = System.Windows.Forms.Shortcut.CtrlF4;
            this.menuItemFileClose.Text = "&Close";
            this.menuItemFileClose.Click += new System.EventHandler(this.menuItemFileClose_Click);
            this.menuItemEdit.Index = 1;
            this.menuItemEdit.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItemEditCopy,
            this.menuItemEditDelete,
            this.menuItem6,
            this.menuItemEditSelectAll,
            this.menuItem7,
            this.menuItemEditClear});
            this.menuItemEdit.Text = "&Edit";
            this.menuItemEdit.Popup += new System.EventHandler(this.menuItemEdit_Popup);
            this.menuItemEditCopy.Index = 0;
            this.menuItemEditCopy.Shortcut = System.Windows.Forms.Shortcut.CtrlC;
            this.menuItemEditCopy.Text = "&Copy";
            this.menuItemEditCopy.Click += new System.EventHandler(this.menuItemCopy_Click);
            this.menuItemEditDelete.Index = 1;
            this.menuItemEditDelete.Shortcut = System.Windows.Forms.Shortcut.Del;
            this.menuItemEditDelete.Text = "&Delete";
            this.menuItemEditDelete.Click += new System.EventHandler(this.menuItemEditDelete_Click);
            this.menuItem6.Index = 2;
            this.menuItem6.Text = "-";
            this.menuItemEditSelectAll.Index = 3;
            this.menuItemEditSelectAll.Shortcut = System.Windows.Forms.Shortcut.CtrlA;
            this.menuItemEditSelectAll.Text = "Select &All";
            this.menuItemEditSelectAll.Click += new System.EventHandler(this.menuItemSelectAll_Click);
            this.menuItem7.Index = 4;
            this.menuItem7.Text = "-";
            this.menuItemEditClear.Index = 5;
            this.menuItemEditClear.Shortcut = System.Windows.Forms.Shortcut.AltBksp;
            this.menuItemEditClear.Text = "&Clear";
            this.menuItemEditClear.Click += new System.EventHandler(this.menuItemClear_Click);
            this.menuItem9.Index = 2;
            this.menuItem9.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItemRun,
            this.menuItem4,
            this.menuItemWriteLog});
            this.menuItem9.Text = "&Monitor";
            this.menuItemRun.Checked = true;
            this.menuItemRun.Index = 0;
            this.menuItemRun.Shortcut = System.Windows.Forms.Shortcut.CtrlR;
            this.menuItemRun.Text = "&Run";
            this.menuItemRun.Click += new System.EventHandler(this.menuItemRun_Click);
            this.menuItem4.Index = 1;
            this.menuItem4.Text = "-";
            this.menuItemWriteLog.Index = 2;
            this.menuItemWriteLog.Text = "&Write Log";
            this.menuItemWriteLog.Click += new System.EventHandler(this.menuItemWriteLog_Click);
            this.menuItemTools.Index = 3;
            this.menuItemTools.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItemToolsDetails,
            this.menuItemSpacer3,
            this.menuItemToolsViewUrl,
            this.menuItemToolsViewDirectory});
            this.menuItemTools.Text = "&Tools";
            this.menuItemTools.Popup += new System.EventHandler(this.menuItemTools_Popup);
            this.menuItemToolsDetails.Index = 0;
            this.menuItemToolsDetails.Text = "View request &details\tEnter";
            this.menuItemSpacer3.Index = 1;
            this.menuItemSpacer3.Text = "-";
            this.menuItemToolsViewUrl.Index = 2;
            this.menuItemToolsViewUrl.Text = "Open &Url in web browser";
            this.menuItemToolsViewUrl.Click += new System.EventHandler(this.menuItemOpenUrl_Click);
            this.menuItemToolsViewDirectory.Index = 3;
            this.menuItemToolsViewDirectory.Text = "&View destination directory";
            this.menuItemToolsViewDirectory.Click += new System.EventHandler(this.menuItemViewDir_Click);
            this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
            this.ClientSize = new System.Drawing.Size(768, 179);
            this.Controls.Add(this.listView);
            this.KeyPreview = true;
            this.Menu = this.mainMenu1;
            this.Name = "QueueMonitor";
            this.Text = "Download Queue Monitor";
            this.ResumeLayout(false);
  }
  private void listView_ColumnClick(object sender, System.Windows.Forms.ColumnClickEventArgs e)
  {
  }
  private void menuItemClear_Click(object sender, System.EventArgs e)
  {
   listView.Items.Clear();
  }
  private void menuItemViewDir_Click(object sender, System.EventArgs e)
  {
  }
  private void menuItemOpenUrl_Click(object sender, System.EventArgs e)
  {
  }
  internal void menuItemCopy_Click(object sender, System.EventArgs e)
  {
  }
  private void menuItemHeaders_Click(object sender, System.EventArgs e)
  {
  }
  private void menuItemSelectAll_Click(object sender, System.EventArgs e)
  {
  }
  private void menuItemRun_Click(object sender, System.EventArgs e)
  {
   isRunning = !isRunning;
   menuItemRun.Checked = isRunning;
  }
  private void menuItemEditDelete_Click(object sender, System.EventArgs e)
  {
   while(listView.SelectedItems.Count>0)
    listView.Items.Remove(listView.SelectedItems[0]);
  }
  private void menuItemWriteLog_Click(object sender, System.EventArgs e)
  {
   logToFile = !logToFile;
   menuItemWriteLog.Checked = logToFile;
  }
  private void contextMenu_Popup(object sender, System.EventArgs e)
  {
   bool hasSelection = listView.SelectedItems.Count>0;
   bool hasItems = listView.Items.Count > 0;
   this.menuItemHeaders.Enabled = listView.SelectedItems.Count==1;
   this.menuItemOpenUrl.Enabled = hasSelection;
   this.menuItemViewDir.Enabled = hasSelection;
   this.menuItemCopy.Enabled = hasSelection;
   this.menuItemClear.Enabled = hasItems;
  }
  private void menuItemFile_Popup(object sender, System.EventArgs e)
  {
  }
  private void menuItemEdit_Popup(object sender, System.EventArgs e)
  {
   bool hasSelection = listView.SelectedItems.Count>0;
   bool hasItems = listView.Items.Count > 0;
   this.menuItemEditCopy.Enabled = hasSelection;
   this.menuItemEditDelete.Enabled = hasSelection;
   this.menuItemEditClear.Enabled = hasItems;
   this.menuItemEditSelectAll.Enabled = hasItems;
  }
  private void menuItemTools_Popup(object sender, System.EventArgs e)
  {
   bool hasSelection = listView.SelectedItems.Count>0;
   this.menuItemToolsDetails.Enabled = hasSelection;
   this.menuItemToolsViewDirectory.Enabled = hasSelection;
   this.menuItemToolsViewUrl.Enabled = hasSelection;
  }
  private void menuItemFileClose_Click(object sender, System.EventArgs e)
  {
   Close();
  }
        private void listView_RetrieveVirtualItem(object sender, RetrieveVirtualItemEventArgs e)
        {
            int itemCount = DataStore.ActiveRequestCount + DataStore.PendingRequestCount;
            DataRequest dr = null;
            try
            {
                if (e.ItemIndex < DataStore.ActiveRequestCount)
                    dr = DataStore.ActiveRequests[e.ItemIndex] as DataRequest;
                else
                    dr = DataStore.PendingRequests[e.ItemIndex - DataStore.ActiveRequestCount] as DataRequest;
            }
            catch (ArgumentOutOfRangeException)
            {
                e.Item = new ListViewItem("");
                e.Item.SubItems.Add(new ListViewItem.ListViewSubItem(e.Item, ""));
                e.Item.SubItems.Add(new ListViewItem.ListViewSubItem(e.Item, ""));
                e.Item.SubItems.Add(new ListViewItem.ListViewSubItem(e.Item, ""));
                e.Item.SubItems.Add(new ListViewItem.ListViewSubItem(e.Item, ""));
                e.Item.SubItems.Add(new ListViewItem.ListViewSubItem(e.Item, ""));
                return;
            }
            e.Item = new ListViewItem( dr.NextTry.ToShortTimeString() );
            e.Item.SubItems.Add(new ListViewItem.ListViewSubItem(e.Item, dr.State.ToString()));
            e.Item.SubItems.Add(new ListViewItem.ListViewSubItem(e.Item, dr.Progress.ToString("0.00")));
            e.Item.SubItems.Add(new ListViewItem.ListViewSubItem(e.Item, dr.RequestDescriptor.Description));
            e.Item.SubItems.Add(new ListViewItem.ListViewSubItem(e.Item, dr.RequestDescriptor.BasePriority.ToString()));
            e.Item.SubItems.Add(new ListViewItem.ListViewSubItem(e.Item, dr.Priority.ToString("0.00")));
        }
 }
}
