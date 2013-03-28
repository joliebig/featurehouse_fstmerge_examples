using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using FireIRC.Extenciblility;
using System.IO;
using OVT.Melissa.PluginSupport;
using FireIRC;
using System.Resources;
using OVT.FireIRC.Resources.Resources;
using WeifenLuo.WinFormsUI.Docking;
namespace OVT.FireIRC.Resources
{
    public partial class MainForm : Form
    {
        private int childFormNumber = 0;
        private Timer t = new Timer();
        private TreeView treeView1 = new TreeView();
        private DockContent treeview = new DockContent();
        private DockContent highlightLog = new DockContent();
        private RichTextBox richTextBox1 = new RichTextBox();
        public MainForm()
        {
            InitializeComponent();
            CreateTreeBrowser();
            CreateHighlightLog();
            UpdateResources();
        }
        public void UpdateResources()
        {
            ResourceManagement.ConvertToManagedResource(this);
            ResourceManagement.ConvertToManagedResource(menuStrip);
        }
        public void CreateTreeBrowser()
        {
            treeview.HideOnClose = true;
            treeView1.Dock = DockStyle.Fill;
            treeview.Controls.Add(treeView1);
            treeview.TabText = "Channel Explorer";
            treeview.Show(Panel);
            treeview.DockState = DockState.DockLeft;
        }
        public void CreateHighlightLog()
        {
            highlightLog.HideOnClose = true;
            richTextBox1.Dock = DockStyle.Fill;
            highlightLog.Controls.Add(richTextBox1);
            highlightLog.TabText = "Highlight Log";
            highlightLog.Show(Panel);
            highlightLog.DockState = DockState.DockBottomAutoHide;
        }
        public ToolStripMenuItem CommandMenu
        {
            get { return commandsToolStripMenuItem; }
        }
        public ImageList ToolbarImages
        {
            get { return FireIRCCore.Themes[FireIRCCore.Settings.PrimaryTheme].ToolbarImages; }
        }
        public ImageList TreeViewImages
        {
            get { return FireIRCCore.Themes[FireIRCCore.Settings.PrimaryTheme].TreeViewImages; }
        }
        public TreeNodeCollection ServerBrowser
        {
            get { return treeView1.Nodes; }
        }
        public void ExpandAll()
        {
            treeView1.ExpandAll();
        }
        private void ShowNewForm(object sender, EventArgs e)
        {
            Form childForm = new Form();
            childForm.MdiParent = this;
            childForm.Text = "Window " + childFormNumber++;
            childForm.Show();
        }
        private void ExitToolsStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void CloseAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            foreach (Form childForm in MdiChildren)
            {
                childForm.Close();
            }
        }
        public DockPanel Panel
        {
            get { return dockPanel1; }
        }
        private void MainForm_Load(object sender, EventArgs e)
        {
            treeView1.ExpandAll();
            UpdateImages();
            UpdateWindowFont();
            treeView1.ImageList = TreeViewImages;
            Application.ThreadException += new System.Threading.ThreadExceptionEventHandler(Application_ThreadException);
            Splash s = new Splash();
            s.ShowDialog();
            notifyIcon1.Visible = true;
        }
        void Application_ThreadException(object sender, System.Threading.ThreadExceptionEventArgs e)
        {
            ExceptionHandled ee = new ExceptionHandled(e.Exception);
            ee.ShowDialog();
        }
        private void optionsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Options o = new Options();
            o.ShowDialog();
        }
        private void scriptEditorToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Management m = new Management();
            m.ShowDialog();
        }
        private void toolStripButton3_Click(object sender, EventArgs e)
        {
            if (FireIRCCore.GetActiveChannelWindow() == null)
            {
                Favorates f = new Favorates("null");
                f.ShowDialog();
            }
            else
            {
                Favorates f = new Favorates(FireIRCCore.ActiveServer);
                f.ShowDialog();
            }
        }
        private void toolStripButton8_Click(object sender, EventArgs e)
        {
            Options o = new Options("Colors");
            o.ShowDialog();
        }
        private void toolStripButton7_Click(object sender, EventArgs e)
        {
            t.Show();
        }
        private void toolStripButton6_Click(object sender, EventArgs e)
        {
            AddressBook a = new AddressBook();
            a.ShowDialog();
        }
        private void connectToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ConnectForm c = new ConnectForm();
            c.ShowDialog();
        }
        private void aboutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            AboutFireIRC a = new AboutFireIRC();
            a.ShowDialog();
        }
        private void MainForm_FormClosing(object sender, FormClosingEventArgs e)
        {
        }
        public void UpdateImages()
        {
            treeView1.ImageList = FireIRCCore.Themes[FireIRCCore.Settings.PrimaryTheme].TreeViewImages;
        }
        public void UpdateWindowFont()
        {
            treeView1.BackColor = FireIRCCore.Settings.Background;
            treeView1.ForeColor = FireIRCCore.Settings.Forground;
            treeView1.Font = FireIRCCore.Settings.MainFont;
        }
        private void treeView1_AfterSelect(object sender, TreeViewEventArgs e)
        {
            if (e.Node.SelectedImageKey == "irc_server")
            {
                FireIRCCore.GetChannel(e.Node.Text, "server").BringToFront();
            }
            else
            {
                try { FireIRCCore.GetChannel(e.Node.Parent.Parent.Text, e.Node.Text).BringToFront(); }
                catch (NullReferenceException) { }
            }
        }
        public void Tip(string title, string message)
        {
            notifyIcon1.ShowBalloonTip(60, "FireIRC: " + title, message, ToolTipIcon.Info);
        }
        public void AppendToLog(LogType type, string text)
        {
            if (type == LogType.HighlightLog) { richTextBox1.AppendText(text + "\n"); }
        }
        public enum LogType
        {
            HighlightLog,
        }
        private void toolStripButton13_Click(object sender, EventArgs e)
        {
            AboutFireIRC a = new AboutFireIRC();
            a.ShowDialog();
        }
        private void printToolStripMenuItem_Click(object sender, EventArgs e)
        {
        }
        private void channelBrowserToolStripMenuItem_Click(object sender, EventArgs e)
        {
            treeview.Show(Panel);
        }
        private void highlightLogToolStripMenuItem_Click(object sender, EventArgs e)
        {
            highlightLog.Show(Panel);
        }
    }
}
