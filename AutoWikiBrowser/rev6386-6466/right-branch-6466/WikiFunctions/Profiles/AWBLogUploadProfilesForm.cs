using System;
using System.Windows.Forms;
namespace WikiFunctions.Profiles
{
    public partial class AWBLogUploadProfilesForm : Form
    {
        protected string CurrentSettingsProfile;
        public AWBLogUploadProfilesForm()
        {
            InitializeComponent();
        }
        private void AWBProfiles_Load(object sender, EventArgs e)
        {
            if (!DesignMode)
                LoadProfiles();
        }
        protected int SelectedItem
        {
            get
            {
                try
                {
                    if (lvAccounts.SelectedIndices.Count == 0)
                        return -1;
                    return int.Parse(lvAccounts.Items[lvAccounts.SelectedIndices[0]].Text);
                }
                catch { return -1; }
            }
        }
        private void UpdateUI()
        {
            btnLogin.Enabled = btnDelete.Enabled = BtnEdit.Enabled = loginAsThisAccountToolStripMenuItem.Enabled =
                editThisAccountToolStripMenuItem.Enabled = changePasswordToolStripMenuItem.Enabled =
                deleteThisAccountToolStripMenuItem.Enabled = (lvAccounts.SelectedItems.Count > 0);
        }
        private void LoadProfiles()
        {
            lvAccounts.BeginUpdate();
            lvAccounts.Items.Clear();
            foreach (AWBProfile profile in AWBProfiles.GetProfiles())
            {
                ListViewItem item = new ListViewItem(profile.ID.ToString());
                item.SubItems.Add(profile.Username);
                item.SubItems.Add(!string.IsNullOrEmpty(profile.Password) ? "Yes" : "No");
                item.SubItems.Add(profile.DefaultSettings);
                item.SubItems.Add(profile.UseForUpload ? "Yes" : "No");
                item.SubItems.Add(profile.Notes);
                lvAccounts.Items.Add(item);
            }
            UpdateUI();
            lvAccounts.ResizeColumns();
            lvAccounts.EndUpdate();
        }
        private void btnAdd_Click(object sender, EventArgs e)
        {
            Add();
        }
        private void addNewAccountToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Add();
        }
        private void Add()
        {
            AWBProfileAdd add = new AWBProfileAdd();
            if (add.ShowDialog() == DialogResult.Yes)
                LoadProfiles();
        }
        private void btnDelete_Click(object sender, EventArgs e)
        {
            Delete();
        }
        private void deleteThisSavedAccountToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Delete();
        }
        private void Delete()
        {
            try
            {
                if (SelectedItem < 0) return;
                AWBProfiles.DeleteProfile(SelectedItem);
                LoadProfiles();
            }
            finally
            {
                UpdateUI();
            }
        }
        private void changePasswordToolStripMenuItem_Click(object sender, EventArgs e)
        {
   try
   {
             UserPassword password = new UserPassword()
                                         {
                                             Username = lvAccounts.Items[lvAccounts.SelectedIndices[0]].SubItems[1].Text
                                         };
       if (password.ShowDialog() == DialogResult.OK)
                 AWBProfiles.SetPassword(int.Parse(lvAccounts.Items[lvAccounts.SelectedIndices[0]].Text), password.GetPassword);
   }
   finally
   {
    LoadProfiles();
   }
        }
        private void editThisAccountToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Edit();
        }
        private void Edit()
        {
            try
            {
                AWBProfileAdd add = new AWBProfileAdd(AWBProfiles.GetProfile(int.Parse(lvAccounts.Items[lvAccounts.SelectedIndices[0]].Text)));
                if (add.ShowDialog() == DialogResult.Yes)
                    LoadProfiles();
            }
            catch { }
        }
        private void btnExit_Click(object sender, EventArgs e)
        {
            Close();
        }
        public string SettingsToLoad
        {
            get { return CurrentSettingsProfile; }
        }
        private void lvAccounts_SelectedIndexChanged(object sender, EventArgs e)
        {
            UpdateUI();
        }
        protected virtual void lvAccounts_DoubleClick(object sender, EventArgs e)
        {
            Edit();
        }
        private void BtnEdit_Click(object sender, EventArgs e)
        {
            Edit();
        }
    }
}
