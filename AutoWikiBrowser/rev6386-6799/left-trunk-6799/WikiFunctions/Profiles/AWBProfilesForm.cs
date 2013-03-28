using System;
using System.Windows.Forms;
using WikiFunctions.API;
namespace WikiFunctions.Profiles
{
    public partial class AWBProfilesForm : AWBLogUploadProfilesForm
    {
        private readonly Session TheSession;
        public event EventHandler LoggedIn;
        public AWBProfilesForm(Session session)
        {
            InitializeComponent();
            loginAsThisAccountToolStripMenuItem.Visible = true;
            loginAsThisAccountToolStripMenuItem.Click += lvAccounts_DoubleClick;
            btnLogin.Visible = true;
            TheSession = session;
            UsernameOrPasswordChanged(this, null);
        }
        private void PerformLogin(string password)
        {
            PerformLogin(AWBProfiles.GetUsername(int.Parse(lvAccounts.Items[lvAccounts.SelectedIndices[0]].Text)), password);
        }
        private void PerformLogin(string username, string password)
        {
            bool needsUpdate = TheSession.User.IsLoggedIn;
            try
            {
                TheSession.Editor.SynchronousEditor.Login(username, password);
                needsUpdate = true;
            }
            catch (LoginException ex)
            {
                MessageBox.Show(this, ex.Message, "Login failed", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
            }
            if (LoggedIn != null && needsUpdate)
                LoggedIn(null, null);
            if (TheSession.User.IsLoggedIn) Close();
        }
        private void btnLogin_Click(object sender, EventArgs e)
        {
            Login();
        }
        protected override void lvAccounts_DoubleClick(object sender, EventArgs e)
        {
            Login();
        }
        private void Login()
        {
            try
            {
                if (SelectedItem < 0) return;
                Cursor = Cursors.WaitCursor;
                CurrentSettingsProfile =
                    string.IsNullOrEmpty(lvAccounts.Items[lvAccounts.SelectedIndices[0]].SubItems[3].Text)
                        ? ""
                        : lvAccounts.Items[lvAccounts.SelectedIndices[0]].SubItems[3].Text;
                if (lvAccounts.Items[lvAccounts.SelectedIndices[0]].SubItems[2].Text == "Yes")
                {
                    PerformLogin(AWBProfiles.GetPassword(int.Parse(lvAccounts.Items[lvAccounts.SelectedIndices[0]].Text)));
                }
                else
                {
                    UserPassword password = new UserPassword()
                                                {
                                                    Username = lvAccounts.Items[lvAccounts.SelectedIndices[0]].SubItems[1].Text
                                                };
                    if (password.ShowDialog(this) == DialogResult.OK)
                        PerformLogin(password.GetPassword);
                }
                AWBProfiles.LastUsedAccount = lvAccounts.Items[lvAccounts.SelectedIndices[0]].Text;
                Cursor = Cursors.Default;
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
            }
        }
        public void Login(string profileIdOrName)
        {
            if (profileIdOrName.Length == 0)
                return;
            try
            {
                int profileID;
                AWBProfile startupProfile = int.TryParse(profileIdOrName, out profileID) ? AWBProfiles.GetProfile(profileID) : AWBProfiles.GetProfile(profileIdOrName);
                if (startupProfile == null)
                {
                    MessageBox.Show(Parent, "Can't find user '" + profileIdOrName + "'.", "Command line error",
                        MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
                    return;
                }
                if (!string.IsNullOrEmpty(startupProfile.Password))
                {
                    PerformLogin(startupProfile.Username, startupProfile.Password);
                }
                else
                {
                    UserPassword password = new UserPassword()
                                                {
                                                    Username = startupProfile.Username
                                                };
                    if (password.ShowDialog(this) == DialogResult.OK)
                        PerformLogin(startupProfile.Username, password.GetPassword);
                }
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
            }
        }
        private void UsernameOrPasswordChanged(object sender, EventArgs e)
        {
            btnQuickLogin.Enabled = txtPassword.Text.Length > 0 && txtUsername.Text.Length > 0;
        }
        private void btnQuickLogin_Click(object sender, EventArgs e)
        {
            string user = txtUsername.Text;
            string password = txtPassword.Text;
            if (chkSaveProfile.Checked)
            {
                if (AWBProfiles.GetProfile(txtUsername.Text) != null)
                {
                    MessageBox.Show("Username \"" + txtUsername.Text + "\" already exists.", "Username exists");
                    return;
                }
                var profile = new AWBProfile() { Username = user };
                if (chkSavePassword.Checked) profile.Password = password;
                AWBProfiles.AddEditProfile(profile);
            }
            AWBProfiles.LastUsedAccount = user;
            PerformLogin(user, password);
        }
        private void chkSaveProfile_CheckedChanged(object sender, EventArgs e)
        {
            chkSavePassword.Enabled = chkSaveProfile.Checked;
        }
        private void AWBProfilesForm_Load(object sender, EventArgs e)
        {
            string lua = AWBProfiles.LastUsedAccount;
            if (!string.IsNullOrEmpty(lua))
            {
                int id;
                int.TryParse(lua, out id);
                AWBProfile p = AWBProfiles.GetProfile(id);
                if (p == null)
                {
                    txtUsername.Text = lua;
                    return;
                }
                txtUsername.Text = (id > 0) ? p.Username : lua;
            }
        }
    }
}
