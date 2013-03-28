using RssBandit.Resources;
using RssBandit.AppServices;
using RssBandit.WinGui.Controls;
using System.Windows.Forms;
using System.IO;
namespace RssBandit.WinGui.Dialogs
{
 internal class PodcastOptionsDialog : DialogBase
 {
  internal System.Windows.Forms.TextBox txtCopyPodcastToFolder;
  internal System.Windows.Forms.CheckBox chkCopyPodcastToFolder;
  internal System.Windows.Forms.CheckBox chkCopyPodcastToITunesPlaylist;
  private System.Windows.Forms.Label lblAfterDownloadingCopyTo;
  private System.Windows.Forms.Label lblTreatThisFilesAsPodcasts;
  internal System.Windows.Forms.CheckBox chkCopyPodcastToWMPlaylist;
  private RssBandit.WinGui.Controls.OptionSectionPanel sectionPanelGeneral;
  private System.Windows.Forms.Label lblSectionPanelPlaylistName;
  internal System.Windows.Forms.RadioButton optFeedNameAsPlaylistName;
  internal System.Windows.Forms.RadioButton optSinglePlaylistName;
  internal System.Windows.Forms.TextBox textSinglePlaylistName;
  private RssBandit.WinGui.Controls.OptionSectionPanel sectionPanelPlaylist;
  internal System.Windows.Forms.TextBox textPodcastFilesExtensions;
  private System.Windows.Forms.Button btnSelectCopyPodcastToFolder;
  private System.ComponentModel.Container components = null;
  public PodcastOptionsDialog():
   base()
  {
   InitializeComponent();
   ApplyComponentTranslations();
  }
  public PodcastOptionsDialog(RssBanditPreferences prefs, ICoreApplication coreApp):
   this()
  {
   this.textPodcastFilesExtensions.Text = coreApp.PodcastFileExtensions;
   this.txtCopyPodcastToFolder.Text = coreApp.PodcastFolder;
   this.chkCopyPodcastToFolder.Checked = !coreApp.PodcastFolder.Equals(coreApp.EnclosureFolder);
   this.chkCopyPodcastToITunesPlaylist.Checked = prefs.AddPodcasts2ITunes;
   this.chkCopyPodcastToWMPlaylist.Checked = prefs.AddPodcasts2WMP;
   this.optFeedNameAsPlaylistName.Checked = !prefs.SinglePodcastPlaylist;
   this.optSinglePlaylistName.Checked = prefs.SinglePodcastPlaylist;
   this.textSinglePlaylistName.Text = prefs.SinglePlaylistName;
  }
  protected override void InitializeComponentTranslation() {
   base.InitializeComponentTranslation ();
   this.Text = SR.PodcastOptionDialog_Title;
   this.sectionPanelGeneral.Text = SR.PodcastOptionDialog_GeneralSectionCaption;
   this.lblTreatThisFilesAsPodcasts.Text = SR.PodcastOptionDialog_TreatFileTypesCaption;
   this.toolTip.SetToolTip(this.btnSelectCopyPodcastToFolder, SR.PodcastOptionDialog_CopyPodcastFilesLocationBrowseTip);
   this.toolTip.SetToolTip(this.textPodcastFilesExtensions, SR.PodcastOptionDialog_TreatFileTypesTip);
   this.toolTip.SetToolTip(this.txtCopyPodcastToFolder, SR.PodcastOptionDialog_CopyPodcastFilesLocationTip);
   this.lblAfterDownloadingCopyTo.Text = SR.PodcastOptionDialog_CopyPodcastFilesLabelCaption;
   this.chkCopyPodcastToFolder.Text = SR.PodcastOptionDialog_CopyPodcastFiles2FolderOptionCaption;
   this.chkCopyPodcastToITunesPlaylist.Text = SR.PodcastOptionDialog_CopyPodcastFiles2iTunesOptionCaption;
   this.chkCopyPodcastToWMPlaylist.Text = SR.PodcastOptionDialog_CopyPodcastFiles2WMPOptionCaption;
   this.sectionPanelPlaylist.Text = SR.PodcastOptionDialog_PlaylistSectionCaption;
   this.lblSectionPanelPlaylistName.Text = SR.PodcastOptionDialog_PlaylistSectionLabelCaption;
   this.optFeedNameAsPlaylistName.Text = SR.PodcastOptionDialog_PlaylistFromFeedOptionCaption;
   this.optSinglePlaylistName.Text = SR.PodcastOptionDialog_PlaylistFromCustomNameOptionCaption;
   this.toolTip.SetToolTip(this.textSinglePlaylistName, SR.PodcastOptionDialog_PlaylistCustomNameTip);
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
   System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(PodcastOptionsDialog));
   this.sectionPanelGeneral = new RssBandit.WinGui.Controls.OptionSectionPanel();
   this.btnSelectCopyPodcastToFolder = new System.Windows.Forms.Button();
   this.txtCopyPodcastToFolder = new System.Windows.Forms.TextBox();
   this.chkCopyPodcastToFolder = new System.Windows.Forms.CheckBox();
   this.chkCopyPodcastToITunesPlaylist = new System.Windows.Forms.CheckBox();
   this.lblAfterDownloadingCopyTo = new System.Windows.Forms.Label();
   this.textPodcastFilesExtensions = new System.Windows.Forms.TextBox();
   this.lblTreatThisFilesAsPodcasts = new System.Windows.Forms.Label();
   this.chkCopyPodcastToWMPlaylist = new System.Windows.Forms.CheckBox();
   this.sectionPanelPlaylist = new RssBandit.WinGui.Controls.OptionSectionPanel();
   this.textSinglePlaylistName = new System.Windows.Forms.TextBox();
   this.optSinglePlaylistName = new System.Windows.Forms.RadioButton();
   this.optFeedNameAsPlaylistName = new System.Windows.Forms.RadioButton();
   this.lblSectionPanelPlaylistName = new System.Windows.Forms.Label();
   this.sectionPanelGeneral.SuspendLayout();
   this.sectionPanelPlaylist.SuspendLayout();
   this.SuspendLayout();
   this.btnCancel.Location = new System.Drawing.Point(285, 373);
   this.btnCancel.Name = "btnCancel";
   this.toolTip.SetToolTip(this.btnCancel, "Dismiss any changes and close");
   this.horizontalEdge.Location = new System.Drawing.Point(-1, 361);
   this.horizontalEdge.Name = "horizontalEdge";
   this.btnSubmit.Location = new System.Drawing.Point(185, 373);
   this.btnSubmit.Name = "btnSubmit";
   this.toolTip.SetToolTip(this.btnSubmit, "Accept all changes and close");
   this.sectionPanelGeneral.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.sectionPanelGeneral.Controls.Add(this.btnSelectCopyPodcastToFolder);
   this.sectionPanelGeneral.Controls.Add(this.txtCopyPodcastToFolder);
   this.sectionPanelGeneral.Controls.Add(this.chkCopyPodcastToFolder);
   this.sectionPanelGeneral.Controls.Add(this.chkCopyPodcastToITunesPlaylist);
   this.sectionPanelGeneral.Controls.Add(this.lblAfterDownloadingCopyTo);
   this.sectionPanelGeneral.Controls.Add(this.textPodcastFilesExtensions);
   this.sectionPanelGeneral.Controls.Add(this.lblTreatThisFilesAsPodcasts);
   this.sectionPanelGeneral.Controls.Add(this.chkCopyPodcastToWMPlaylist);
   this.sectionPanelGeneral.Image = ((System.Drawing.Image)(resources.GetObject("sectionPanelGeneral.Image")));
   this.sectionPanelGeneral.ImageLocation = new System.Drawing.Point(0, 20);
   this.sectionPanelGeneral.Location = new System.Drawing.Point(10, 10);
   this.sectionPanelGeneral.Name = "sectionPanelGeneral";
   this.sectionPanelGeneral.Size = new System.Drawing.Size(370, 225);
   this.sectionPanelGeneral.TabIndex = 102;
   this.sectionPanelGeneral.Text = "General";
   this.btnSelectCopyPodcastToFolder.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
   this.btnSelectCopyPodcastToFolder.CausesValidation = false;
   this.btnSelectCopyPodcastToFolder.FlatStyle = System.Windows.Forms.FlatStyle.System;
   this.btnSelectCopyPodcastToFolder.ImeMode = System.Windows.Forms.ImeMode.NoControl;
   this.btnSelectCopyPodcastToFolder.Location = new System.Drawing.Point(335, 175);
   this.btnSelectCopyPodcastToFolder.Name = "btnSelectCopyPodcastToFolder";
   this.btnSelectCopyPodcastToFolder.Size = new System.Drawing.Size(22, 22);
   this.btnSelectCopyPodcastToFolder.TabIndex = 7;
   this.btnSelectCopyPodcastToFolder.Text = "...";
   this.btnSelectCopyPodcastToFolder.Click += new System.EventHandler(this.btnSelectCopyPodcastToFolder_Click);
   this.txtCopyPodcastToFolder.AllowDrop = true;
   this.txtCopyPodcastToFolder.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.txtCopyPodcastToFolder.Location = new System.Drawing.Point(40, 175);
   this.txtCopyPodcastToFolder.Name = "txtCopyPodcastToFolder";
   this.txtCopyPodcastToFolder.Size = new System.Drawing.Size(290, 21);
   this.txtCopyPodcastToFolder.TabIndex = 6;
   this.txtCopyPodcastToFolder.Text = "";
   this.txtCopyPodcastToFolder.Validating += new System.ComponentModel.CancelEventHandler(this.OnControlValidating);
   this.chkCopyPodcastToFolder.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.chkCopyPodcastToFolder.FlatStyle = System.Windows.Forms.FlatStyle.System;
   this.chkCopyPodcastToFolder.ImeMode = System.Windows.Forms.ImeMode.NoControl;
   this.chkCopyPodcastToFolder.Location = new System.Drawing.Point(40, 155);
   this.chkCopyPodcastToFolder.Name = "chkCopyPodcastToFolder";
   this.chkCopyPodcastToFolder.Size = new System.Drawing.Size(325, 17);
   this.chkCopyPodcastToFolder.TabIndex = 5;
   this.chkCopyPodcastToFolder.Text = "This folder:";
   this.chkCopyPodcastToITunesPlaylist.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.chkCopyPodcastToITunesPlaylist.FlatStyle = System.Windows.Forms.FlatStyle.System;
   this.chkCopyPodcastToITunesPlaylist.ImeMode = System.Windows.Forms.ImeMode.NoControl;
   this.chkCopyPodcastToITunesPlaylist.Location = new System.Drawing.Point(40, 135);
   this.chkCopyPodcastToITunesPlaylist.Name = "chkCopyPodcastToITunesPlaylist";
   this.chkCopyPodcastToITunesPlaylist.Size = new System.Drawing.Size(325, 17);
   this.chkCopyPodcastToITunesPlaylist.TabIndex = 4;
   this.chkCopyPodcastToITunesPlaylist.Text = "iTunes playlist";
   this.lblAfterDownloadingCopyTo.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.lblAfterDownloadingCopyTo.FlatStyle = System.Windows.Forms.FlatStyle.System;
   this.lblAfterDownloadingCopyTo.ImeMode = System.Windows.Forms.ImeMode.NoControl;
   this.lblAfterDownloadingCopyTo.Location = new System.Drawing.Point(40, 95);
   this.lblAfterDownloadingCopyTo.Name = "lblAfterDownloadingCopyTo";
   this.lblAfterDownloadingCopyTo.Size = new System.Drawing.Size(325, 15);
   this.lblAfterDownloadingCopyTo.TabIndex = 2;
   this.lblAfterDownloadingCopyTo.Text = "After downloading copy the podcast to:";
   this.textPodcastFilesExtensions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.textPodcastFilesExtensions.Location = new System.Drawing.Point(40, 65);
   this.textPodcastFilesExtensions.Name = "textPodcastFilesExtensions";
   this.textPodcastFilesExtensions.Size = new System.Drawing.Size(322, 21);
   this.textPodcastFilesExtensions.TabIndex = 1;
   this.textPodcastFilesExtensions.Text = "mp3;aac;mb4;wma;m4v;mp4;mov;aa;";
   this.lblTreatThisFilesAsPodcasts.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.lblTreatThisFilesAsPodcasts.FlatStyle = System.Windows.Forms.FlatStyle.System;
   this.lblTreatThisFilesAsPodcasts.ImeMode = System.Windows.Forms.ImeMode.NoControl;
   this.lblTreatThisFilesAsPodcasts.Location = new System.Drawing.Point(45, 20);
   this.lblTreatThisFilesAsPodcasts.Name = "lblTreatThisFilesAsPodcasts";
   this.lblTreatThisFilesAsPodcasts.Size = new System.Drawing.Size(320, 45);
   this.lblTreatThisFilesAsPodcasts.TabIndex = 0;
   this.lblTreatThisFilesAsPodcasts.Text = "Treat these file types as podcasts - use semicolons (\';\') to separate entries:";
   this.chkCopyPodcastToWMPlaylist.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.chkCopyPodcastToWMPlaylist.FlatStyle = System.Windows.Forms.FlatStyle.System;
   this.chkCopyPodcastToWMPlaylist.ImeMode = System.Windows.Forms.ImeMode.NoControl;
   this.chkCopyPodcastToWMPlaylist.Location = new System.Drawing.Point(40, 115);
   this.chkCopyPodcastToWMPlaylist.Name = "chkCopyPodcastToWMPlaylist";
   this.chkCopyPodcastToWMPlaylist.Size = new System.Drawing.Size(325, 17);
   this.chkCopyPodcastToWMPlaylist.TabIndex = 3;
   this.chkCopyPodcastToWMPlaylist.Text = "Windows Media Player playlist:";
   this.sectionPanelPlaylist.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
    | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.sectionPanelPlaylist.Controls.Add(this.textSinglePlaylistName);
   this.sectionPanelPlaylist.Controls.Add(this.optSinglePlaylistName);
   this.sectionPanelPlaylist.Controls.Add(this.optFeedNameAsPlaylistName);
   this.sectionPanelPlaylist.Controls.Add(this.lblSectionPanelPlaylistName);
   this.sectionPanelPlaylist.Image = ((System.Drawing.Image)(resources.GetObject("sectionPanelPlaylist.Image")));
   this.sectionPanelPlaylist.ImageLocation = new System.Drawing.Point(0, 20);
   this.sectionPanelPlaylist.Location = new System.Drawing.Point(10, 225);
   this.sectionPanelPlaylist.Name = "sectionPanelPlaylist";
   this.sectionPanelPlaylist.Size = new System.Drawing.Size(370, 125);
   this.sectionPanelPlaylist.TabIndex = 103;
   this.sectionPanelPlaylist.Text = "Playlist";
   this.textSinglePlaylistName.AllowDrop = true;
   this.textSinglePlaylistName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.textSinglePlaylistName.Location = new System.Drawing.Point(40, 85);
   this.textSinglePlaylistName.Name = "textSinglePlaylistName";
   this.textSinglePlaylistName.Size = new System.Drawing.Size(290, 21);
   this.textSinglePlaylistName.TabIndex = 4;
   this.textSinglePlaylistName.Text = "";
   this.optSinglePlaylistName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.optSinglePlaylistName.FlatStyle = System.Windows.Forms.FlatStyle.System;
   this.optSinglePlaylistName.ImeMode = System.Windows.Forms.ImeMode.NoControl;
   this.optSinglePlaylistName.Location = new System.Drawing.Point(40, 55);
   this.optSinglePlaylistName.Name = "optSinglePlaylistName";
   this.optSinglePlaylistName.Size = new System.Drawing.Size(325, 25);
   this.optSinglePlaylistName.TabIndex = 3;
   this.optSinglePlaylistName.Text = "Add all files to a single playlist with this name:";
   this.optFeedNameAsPlaylistName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.optFeedNameAsPlaylistName.Checked = true;
   this.optFeedNameAsPlaylistName.FlatStyle = System.Windows.Forms.FlatStyle.System;
   this.optFeedNameAsPlaylistName.ImeMode = System.Windows.Forms.ImeMode.NoControl;
   this.optFeedNameAsPlaylistName.Location = new System.Drawing.Point(40, 35);
   this.optFeedNameAsPlaylistName.Name = "optFeedNameAsPlaylistName";
   this.optFeedNameAsPlaylistName.Size = new System.Drawing.Size(325, 20);
   this.optFeedNameAsPlaylistName.TabIndex = 2;
   this.optFeedNameAsPlaylistName.TabStop = true;
   this.optFeedNameAsPlaylistName.Text = "Use the Feed name";
   this.lblSectionPanelPlaylistName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.lblSectionPanelPlaylistName.FlatStyle = System.Windows.Forms.FlatStyle.System;
   this.lblSectionPanelPlaylistName.ImeMode = System.Windows.Forms.ImeMode.NoControl;
   this.lblSectionPanelPlaylistName.Location = new System.Drawing.Point(45, 20);
   this.lblSectionPanelPlaylistName.Name = "lblSectionPanelPlaylistName";
   this.lblSectionPanelPlaylistName.Size = new System.Drawing.Size(320, 25);
   this.lblSectionPanelPlaylistName.TabIndex = 0;
   this.lblSectionPanelPlaylistName.Text = "As playlist name:";
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 14);
   this.ClientSize = new System.Drawing.Size(387, 406);
   this.Controls.Add(this.sectionPanelPlaylist);
   this.Controls.Add(this.sectionPanelGeneral);
   this.Name = "PodcastOptionsDialog";
   this.Text = "<All captions taken from SR.Strings!>";
   this.Controls.SetChildIndex(this.btnSubmit, 0);
   this.Controls.SetChildIndex(this.btnCancel, 0);
   this.Controls.SetChildIndex(this.horizontalEdge, 0);
   this.Controls.SetChildIndex(this.sectionPanelGeneral, 0);
   this.Controls.SetChildIndex(this.sectionPanelPlaylist, 0);
   this.sectionPanelGeneral.ResumeLayout(false);
   this.sectionPanelPlaylist.ResumeLayout(false);
   this.ResumeLayout(false);
  }
  private void btnSelectCopyPodcastToFolder_Click(object sender, System.EventArgs e) {
   DirectoryBrowser folderDialog = new DirectoryBrowser();
   folderDialog.Description = SR.BrowseForFolderEnclosureDownloadLocation;
   DialogResult result = folderDialog.ShowDialog();
   if(result == DialogResult.OK) {
    this.txtCopyPodcastToFolder.Text = folderDialog.ReturnPath;
   }
   errorProvider.SetError(this.txtCopyPodcastToFolder, null);
  }
  private void OnControlValidating(object sender, System.ComponentModel.CancelEventArgs e) {
   if(sender == txtCopyPodcastToFolder){
    txtCopyPodcastToFolder.Text = txtCopyPodcastToFolder.Text.Trim();
    if ((txtCopyPodcastToFolder.Text.Length == 0) || !Directory.Exists(txtCopyPodcastToFolder.Text)) {
     errorProvider.SetError(txtCopyPodcastToFolder, SR.ExceptionInvalidEnclosurePath);
     e.Cancel = true;
    }
   }
   if (!e.Cancel)
    errorProvider.SetError((Control)sender, null);
  }
 }
}
