using System;
using System.Windows.Forms;
namespace WikiFunctions.Profiles
{
    public partial class AWBProfileAdd : Form
    {
        private readonly int Editid;
        public AWBProfileAdd()
        {
            InitializeComponent();
            Editid = -1;
            Text = "Add new Profile";
        }
        public AWBProfileAdd(AWBProfile profile)
        {
            InitializeComponent();
            Editid = profile.ID;
            txtUsername.Text = profile.Username;
            txtPassword.Text = profile.Password;
            txtPath.Text = profile.DefaultSettings;
            chkUseForUpload.Checked = profile.UseForUpload;
            txtNotes.Text = profile.Notes;
            if (!string.IsNullOrEmpty(txtPath.Text))
                chkDefaultSettings.Checked = true;
            if (!string.IsNullOrEmpty(txtPassword.Text))
                chkSavePassword.Checked = true;
            Text = "Edit Profile";
        }
        private void chkSavePassword_CheckedChanged(object sender, EventArgs e)
        {
            txtPassword.Enabled = chkSavePassword.Checked;
        }
        private void AWBProfileAdd_Load(object sender, EventArgs e)
        {
            openDefaultFile.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
        }
        private void chkDefaultSettings_CheckedChanged(object sender, EventArgs e)
        {
            txtPath.Enabled = chkDefaultSettings.Checked;
            btnBrowse.Enabled = chkDefaultSettings.Checked;
        }
        private void btnBrowse_Click(object sender, EventArgs e)
        {
            if (chkDefaultSettings.Checked && (openDefaultFile.ShowDialog() == DialogResult.OK))
                txtPath.Text = openDefaultFile.FileName;
        }
        private void btnSave_Click(object sender, EventArgs e)
        {
            if (string.IsNullOrEmpty(txtUsername.Text))
                MessageBox.Show("The Username cannot be blank");
            else
            {
                if (AWBProfiles.GetProfile(txtUsername.Text) != null)
                {
                    MessageBox.Show("Username \"" +txtUsername.Text + "\" already exists.", "Username exists");
                    return;
                }
                AWBProfile profile = new AWBProfile() {Username = txtUsername.Text};
                if (chkSavePassword.Checked && !string.IsNullOrEmpty(txtPassword.Text))
                    profile.Password = txtPassword.Text;
                profile.DefaultSettings = txtPath.Text;
                int idUpload = AWBProfiles.GetIDOfUploadAccount();
                if (chkUseForUpload.Checked && (idUpload != -1) && (idUpload != Editid))
                    AWBProfiles.SetOtherAccountsAsNotForUpload();
                profile.UseForUpload = chkUseForUpload.Checked;
                profile.Notes = txtNotes.Text;
                profile.ID = Editid;
                AWBProfiles.AddEditProfile(profile);
                DialogResult = DialogResult.Yes;
            }
        }
    }
}
