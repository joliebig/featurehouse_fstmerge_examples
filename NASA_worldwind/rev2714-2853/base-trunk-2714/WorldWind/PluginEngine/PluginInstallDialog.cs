using System;
using WorldWind.Net;
using WorldWind.PluginEngine;
using System.IO;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
namespace WorldWind.PluginEngine
{
 public class PluginInstallDialog : System.Windows.Forms.Form
 {
  private System.Windows.Forms.Label label1;
  private System.Windows.Forms.Button buttonInstall;
  private System.Windows.Forms.Button buttonCancel;
  private System.Windows.Forms.Label label2;
  private System.Windows.Forms.TextBox url;
  private System.ComponentModel.Container components = null;
  private System.Windows.Forms.Button buttonBrowse;
  private System.Windows.Forms.OpenFileDialog openFileDialog;
  private PluginCompiler m_compiler;
  public PluginInstallDialog(PluginCompiler compiler)
  {
   InitializeComponent();
   m_compiler = compiler;
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
   this.label1 = new System.Windows.Forms.Label();
   this.url = new System.Windows.Forms.TextBox();
   this.buttonInstall = new System.Windows.Forms.Button();
   this.buttonCancel = new System.Windows.Forms.Button();
   this.label2 = new System.Windows.Forms.Label();
   this.buttonBrowse = new System.Windows.Forms.Button();
   this.openFileDialog = new System.Windows.Forms.OpenFileDialog();
   this.SuspendLayout();
   this.label1.Location = new System.Drawing.Point(11, 65);
   this.label1.Name = "label1";
   this.label1.Size = new System.Drawing.Size(163, 14);
   this.label1.TabIndex = 1;
   this.label1.Text = "Plugin File / URL:";
   this.url.Location = new System.Drawing.Point(11, 83);
   this.url.Name = "url";
   this.url.Size = new System.Drawing.Size(333, 20);
   this.url.TabIndex = 2;
   this.url.Text = "";
   this.buttonInstall.Location = new System.Drawing.Point(214, 115);
   this.buttonInstall.Name = "buttonInstall";
   this.buttonInstall.TabIndex = 4;
   this.buttonInstall.Text = "&Install";
   this.buttonInstall.Click += new System.EventHandler(this.buttonInstall_Click);
   this.buttonCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
   this.buttonCancel.Location = new System.Drawing.Point(297, 115);
   this.buttonCancel.Name = "buttonCancel";
   this.buttonCancel.TabIndex = 5;
   this.buttonCancel.Text = "&Cancel";
   this.label2.Location = new System.Drawing.Point(12, 14);
   this.label2.Name = "label2";
   this.label2.Size = new System.Drawing.Size(345, 42);
   this.label2.TabIndex = 0;
   this.label2.Text = "To install a plugin, either copy the URL pointing to the plugin from a web page a" +
    "nd paste it in the field below or paste/browse for a local file.  Then press ins" +
    "tall.";
   this.buttonBrowse.Location = new System.Drawing.Point(345, 81);
   this.buttonBrowse.Name = "buttonBrowse";
   this.buttonBrowse.Size = new System.Drawing.Size(30, 23);
   this.buttonBrowse.TabIndex = 3;
   this.buttonBrowse.Text = "&...";
   this.buttonBrowse.Click += new System.EventHandler(this.buttonBrowse_Click);
   this.openFileDialog.AddExtension = false;
   this.openFileDialog.RestoreDirectory = true;
   this.AcceptButton = this.buttonInstall;
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.CancelButton = this.buttonCancel;
   this.ClientSize = new System.Drawing.Size(383, 148);
   this.Controls.Add(this.buttonBrowse);
   this.Controls.Add(this.label2);
   this.Controls.Add(this.buttonCancel);
   this.Controls.Add(this.buttonInstall);
   this.Controls.Add(this.url);
   this.Controls.Add(this.label1);
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
   this.Name = "PluginInstallDialog";
   this.Text = "Plugin Installation";
   this.ResumeLayout(false);
  }
  private void buttonInstall_Click(object sender, System.EventArgs e)
  {
   string warning = @"WARNING! You must trust the source from where you acquired this Plug-In. It
is NOT from NASA. There is always the possibility that by adding this
plug-in to World Wind, serious harm could come to your computer system. For
this reason, please verify the source for all Plug-Ins BEFORE installing so
that you may be assured of a safe and productive World Wind experience.";
   if(MessageBox.Show(warning, "Security Warning",
    MessageBoxButtons.OKCancel, MessageBoxIcon.Stop, MessageBoxDefaultButton.Button2) != DialogResult.OK)
    return;
   try
   {
    url.Text = url.Text.Trim();
    if(IsWeb)
     InstallFromUrl(new Uri(url.Text));
    else if(IsFile)
     InstallFromFile(url.Text);
    else
    {
     MessageBox.Show("Please specify an existing filename or a web url starting with 'http://'.", "Not found", MessageBoxButtons.OK, MessageBoxIcon.Error );
     url.Focus();
     return;
    }
    Close();
   }
   catch(ApplicationException)
   {
   }
  }
  bool IsFile
  {
   get
   {
    return File.Exists(url.Text);
   }
  }
  bool IsWeb
  {
   get
   {
    return url.Text.ToLower().StartsWith("http://");
   }
  }
  void InstallFromFile( string pluginPath )
  {
   string fileName = Path.GetFileName( pluginPath );
   string destPath = GetDestinationPath( fileName );
   if(destPath == null)
    return;
   File.Copy(pluginPath, destPath);
   ShowSuccessMessage( fileName );
  }
  void InstallFromUrl( Uri uri )
  {
   string fileName = Path.GetFileName( uri.LocalPath );
   string destPath = GetDestinationPath( fileName );
   if(destPath == null)
    return;
   using(WebDownload dl = new WebDownload(uri.ToString()))
    dl.DownloadFile(destPath);
   ShowSuccessMessage( fileName );
  }
  string GetDestinationPath( string fileName )
  {
   string directory = Path.Combine(m_compiler.PluginRootDirectory, Path.GetFileNameWithoutExtension(fileName));
   Directory.CreateDirectory(directory);
   string fullPath = Path.Combine(directory, fileName);
   if(File.Exists(fullPath))
   {
    string msg = string.Format("You already have {0} installed.  Do you wish to overwrite it?",
     Path.GetFileNameWithoutExtension(fileName) );
    if( MessageBox.Show(msg, "Overwrite?", MessageBoxButtons.YesNo, MessageBoxIcon.Question,
     MessageBoxDefaultButton.Button2) != DialogResult.Yes)
     throw new ApplicationException("Install aborted.");
   }
   return fullPath;
  }
  static void ShowSuccessMessage( string fileName )
  {
   string msg = string.Format("{0} was successfully installed.",
    Path.GetFileNameWithoutExtension(fileName) );
   MessageBox.Show(msg, "Success", MessageBoxButtons.OK, MessageBoxIcon.Information);
  }
  private void buttonBrowse_Click(object sender, System.EventArgs e)
  {
   OpenFileDialog of = new OpenFileDialog();
   if(IsFile)
    of.FileName = url.Text;
   if(of.ShowDialog()!=DialogResult.OK)
    return;
   url.Text = of.FileName;
  }
 }
}
