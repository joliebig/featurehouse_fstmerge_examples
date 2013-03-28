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
namespace OVT.FireIRC.Resources
{
    public partial class MainForm : Form
    {
        private int childFormNumber = 0;
        private Timer t = new Timer();
        public MainForm()
        {
            InitializeComponent();
            UpdateResources();
        }
        public void UpdateResources()
        {
            ResourceManagement.ConvertToManagedResource(this);
            ResourceManagement.ConvertToManagedResource(menuStrip);
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
        private void CascadeToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.Cascade);
        }
        private void TileVerticalToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.TileVertical);
        }
        private void TileHorizontalToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.TileHorizontal);
        }
        private void ArrangeIconsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.ArrangeIcons);
        }
        private void CloseAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            foreach (Form childForm in MdiChildren)
            {
                childForm.Close();
            }
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
        private void toolStripButton11_Click(object sender, EventArgs e)
        {
            Exception ed = new Exception("Testing Exception Handler");
            ed.Data.Add("FireIRC Version", GetType().Assembly.GetName().Version.ToString());
            throw ed;
        }
        public void UpdateImages()
        {
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
        private void label1_Click(object sender, EventArgs e)
        {
        }
        private void label1_DoubleClick(object sender, EventArgs e)
        {
            if (splitter2.Visible == true)
            {
                splitter2.Visible = false;
                tabControl1.Visible = false;
            }
            else if (splitter2.Visible == false)
            {
                splitter2.Visible = true;
                tabControl1.Visible = true;
            }
        }
        public void AppendToLog(LogType type, string text)
        {
            if (type == LogType.HighlightLog) { richTextBox1.AppendText(text + "\n"); }
        }
        public enum LogType
        {
            HighlightLog,
        }
        private void label2_Click(object sender, EventArgs e)
        {
        }
        private void label2_DoubleClick(object sender, EventArgs e)
        {
            if (splitter1.Visible == true)
            {
                splitter1.Visible = false;
                panel1.Visible = false;
                label2.Text = ">";
            }
            else
            {
                splitter1.Visible = true;
                panel1.Visible = true;
                label2.Text = "<";
            }
        }
        private void toolStripButton13_Click(object sender, EventArgs e)
        {
            AboutFireIRC a = new AboutFireIRC();
            a.ShowDialog();
        }
        private void tabControl1_SelectedIndexChanged(object sender, EventArgs e)
        {
            if(tabControl1.SelectedTab == tabPage3)
            {
                comboBox2.Items.Clear();
                foreach(string i in Directory.GetFiles(Path.Combine(PropertyService.ConfigDirectory,"Logs")))
                {
                    comboBox2.Items.Add(i);
                }
            }
        }
        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
        }
        private void richTextBox2_TextChanged(object sender, EventArgs e)
        {
        }
        private void comboBox2_SelectedIndexChanged(object sender, EventArgs e)
        {
            richTextBox3.Lines = File.ReadAllLines(comboBox2.Text);
        }
        private void systemConsoleToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChannelWindow w = new ChannelWindow("Console", "Console");
            w.IsConsole = true;
            w.Show();
            w.Write("http://test.com");
        }
        private void printToolStripMenuItem_Click(object sender, EventArgs e)
        {
        }
        private void toolStripButton11_Click_1(object sender, EventArgs e)
        {
            ChannelWindow w = new ChannelWindow("null", "console");
            w.IsConsole = true;
            w.Show();
        }
    }
}
