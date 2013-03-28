using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Net;
using System.IO;
using System.Threading;
using OVT.Melissa.SharpDevelop;
namespace FireIRC
{
    public partial class FireIRCUpdator : Form
    {
        bool downloadingFile = false;
        public FireIRCUpdator()
        {
            InitializeComponent();
        }
        private void checkBox4_CheckedChanged(object sender, EventArgs e)
        {
        }
        private void FireIRCUpdator_Load(object sender, EventArgs e)
        {
        }
        private void timer1_Tick(object sender, EventArgs e)
        {
            timer1.Enabled = false;
            Thread t = new Thread(new ThreadStart(delegate
            {
                try
                {
                    WebClient wc = new WebClient();
                    wc.DownloadProgressChanged += new DownloadProgressChangedEventHandler(wc_DownloadProgressChanged);
                    byte[] version = wc.DownloadData("http://www.odysseyt.com/updator/fireirc/version.txt");
                    string versionInstalled;
                    try { versionInstalled = File.ReadAllText(Path.Combine(UpdatorSystem.InstallPath, "version.txt")); }
                    catch (FileNotFoundException) { versionInstalled = versionInstalled = "0"; }
                    if (Convert.ToInt32(Encoding.ASCII.GetString(version)) > Convert.ToInt32(versionInstalled))
                    {
                        Height = 157;
                        wc.DownloadFileAsync(new Uri("http://www.odysseyt.com/updator/fireirc/fireirc.pak"), Path.Combine(UpdatorSystem.InstallPath, "fireirc.pak"));
                        DownloadingFile();
                        foreach (FireIRCUpdatePackage.FilePackage p in UpdatorSystem.GetPackage().Files)
                        {
                            File.WriteAllBytes(Path.Combine(UpdatorSystem.InstallPath, p.FileName), p.FileData);
                        }
                        File.WriteAllBytes(Path.Combine(UpdatorSystem.InstallPath, "version.txt"), version);
                    }
                    Invoke(new Action(delegate { this.Close(); }));
                }
                catch (Exception ee) { MessageBox.Show("Update Failed for the following reason: " + ee.Message, "FireIRC Updator", MessageBoxButtons.OK, MessageBoxIcon.Error); Invoke(new Action(delegate { this.Close(); })); }
            }));
            t.Start();
        }
        void wc_DownloadProgressChanged(object sender, DownloadProgressChangedEventArgs e)
        {
            Invoke(new Action(delegate
            {
                label2.Visible = true;
                progressBar1.Visible = true;
                progressBar1.Value = e.ProgressPercentage;
                label2.Text = String.Format("{0} of {1} Completed", e.BytesReceived, e.TotalBytesToReceive);
            }));
            if (e.BytesReceived == e.TotalBytesToReceive)
            {
                Invoke(new Action(delegate
            {
                label2.Visible = false;
                progressBar1.Visible = false;
            }));
                downloadingFile = false;
            }
        }
        void DownloadingFile()
        {
            downloadingFile = true;
            while (downloadingFile)
            {
            }
        }
    }
}
