using System;
using System.Drawing;
using System.Windows.Forms;
using ProcessHacker.Common;
using ProcessHacker.Common.Ui;
using ProcessHacker.Native;
using ProcessHacker.Native.Api;
using ProcessHacker.Native.Objects;
using ProcessHacker.Native.Security;
using ProcessHacker.Native.Security.AccessControl;
using ProcessHacker.UI;
namespace ProcessHacker.Components
{
    public partial class TokenProperties : UserControl
    {
        private IWithToken _object;
        private TokenGroupsList _groups;
        public TokenProperties(IWithToken obj)
        {
            InitializeComponent();
            _object = obj;
            listPrivileges.SetDoubleBuffered(true);
            listPrivileges.ListViewItemSorter = new SortedListViewComparer(listPrivileges);
            GenericViewMenu.AddMenuItems(copyMenuItem.MenuItems, listPrivileges, null);
            listPrivileges.ContextMenu = menuPrivileges;
            _object = obj;
            try
            {
                using (TokenHandle thandle = _object.GetToken(TokenAccess.Query))
                {
                    try
                    {
                        textUser.Text = thandle.GetUser().GetFullName(true);
                        textUserSID.Text = thandle.GetUser().StringSid;
                        textOwner.Text = thandle.GetOwner().GetFullName(true);
                        textPrimaryGroup.Text = thandle.GetPrimaryGroup().GetFullName(true);
                    }
                    catch (Exception ex)
                    {
                        textUser.Text = "(" + ex.Message + ")";
                    }
                    try
                    {
                        textSessionID.Text = thandle.GetSessionId().ToString();
                    }
                    catch (Exception ex)
                    {
                        textSessionID.Text = "(" + ex.Message + ")";
                    }
                    try
                    {
                        var type = thandle.GetElevationType();
                        if (type == TokenElevationType.Default)
                            textElevated.Text = "N/A";
                        else if (type == TokenElevationType.Full)
                            textElevated.Text = "True";
                        else if (type == TokenElevationType.Limited)
                            textElevated.Text = "False";
                    }
                    catch (Exception ex)
                    {
                        textElevated.Text = "(" + ex.Message + ")";
                    }
                    try
                    {
                        TokenWithLinkedToken tokWLT = new TokenWithLinkedToken(thandle);
                        tokWLT.GetToken().Dispose();
                    }
                    catch
                    {
                       buttonLinkedToken.Visible = false;
                    }
                    try
                    {
                        bool virtAllowed = thandle.IsVirtualizationAllowed();
                        bool virtEnabled = thandle.IsVirtualizationEnabled();
                        if (virtEnabled)
                            textVirtualized.Text = "Enabled";
                        else if (virtAllowed)
                            textVirtualized.Text = "Disabled";
                        else
                            textVirtualized.Text = "Not Allowed";
                    }
                    catch (Exception ex)
                    {
                        textVirtualized.Text = "(" + ex.Message + ")";
                    }
                    try
                    {
                        using (TokenHandle tokenSource = _object.GetToken(TokenAccess.QuerySource))
                        {
                            var source = tokenSource.GetSource();
                            textSourceName.Text = source.SourceName.TrimEnd('\0', '\r', '\n', ' ');
                            long luid = source.SourceIdentifier.QuadPart;
                            textSourceLUID.Text = "0x" + luid.ToString("x");
                        }
                    }
                    catch (Exception ex)
                    {
                        textSourceName.Text = "(" + ex.Message + ")";
                    }
                    try
                    {
                        var statistics = thandle.GetStatistics();
                        textTokenType.Text = statistics.TokenType.ToString();
                        textImpersonationLevel.Text = statistics.ImpersonationLevel.ToString();
                        textTokenId.Text = "0x" + statistics.TokenId.ToString();
                        textAuthenticationId.Text = "0x" + statistics.AuthenticationId.ToString();
                        textMemoryUsed.Text = Utils.FormatSize(statistics.DynamicCharged);
                        textMemoryAvailable.Text = Utils.FormatSize(statistics.DynamicAvailable);
                    }
                    catch (Exception ex)
                    {
                        textTokenType.Text = "(" + ex.Message + ")";
                    }
                    try
                    {
                        var groups = thandle.GetGroups();
                        _groups = new TokenGroupsList(groups);
                        foreach (var group in groups)
                            group.Dispose();
                        _groups.Dock = DockStyle.Fill;
                        tabGroups.Controls.Add(_groups);
                    }
                    catch (Exception ex)
                    {
                        tabGroups.Text = "(" + ex.Message + ")";
                    }
                    try
                    {
                        var privileges = thandle.GetPrivileges();
                        for (int i = 0; i < privileges.Length; i++)
                        {
                            var privilege = privileges[i];
                            ListViewItem item = listPrivileges.Items.Add(privilege.Name.ToLower(), privilege.Name, 0);
                            item.BackColor = GetAttributeColor(privilege.Attributes);
                            item.SubItems.Add(new ListViewItem.ListViewSubItem(item, GetAttributeString(privilege.Attributes)));
                            item.SubItems.Add(new ListViewItem.ListViewSubItem(item, privilege.DisplayName));
                        }
                    }
                    catch (Exception ex)
                    {
                        tabPrivileges.Text = "(" + ex.Message + ")";
                    }
                }
            }
            catch (Exception ex)
            {
                tabControl.Visible = false;
                Label errorMessage = new Label();
                errorMessage.Text = ex.Message;
                this.Padding = new Padding(15, 10, 0, 0);
                this.Controls.Add(errorMessage);
            }
            if (!OSVersion.HasUac)
            {
                labelElevated.Enabled = false;
                textElevated.Enabled = false;
                textElevated.Text = "";
                labelVirtualization.Enabled = false;
                textVirtualized.Enabled = false;
                textVirtualized.Text = "";
            }
            if (tabControl.TabPages[Properties.Settings.Default.TokenWindowTab] != null)
                tabControl.SelectedTab = tabControl.TabPages[Properties.Settings.Default.TokenWindowTab];
            ColumnSettings.LoadSettings(Properties.Settings.Default.PrivilegeListColumns, listPrivileges);
            listPrivileges.AddShortcuts();
        }
        public IWithToken Object
        {
            get { return _object; }
        }
        public void SaveSettings()
        {
            if (_groups != null)
                _groups.SaveSettings();
            Properties.Settings.Default.TokenWindowTab = tabControl.SelectedTab.Name;
            Properties.Settings.Default.PrivilegeListColumns = ColumnSettings.SaveSettings(listPrivileges);
        }
        private string GetAttributeString(SePrivilegeAttributes Attributes)
        {
            if ((Attributes & SePrivilegeAttributes.EnabledByDefault) != 0)
                return "Default Enabled";
            else if ((Attributes & SePrivilegeAttributes.Enabled) != 0)
                return "Enabled";
            else if (Attributes == SePrivilegeAttributes.Disabled)
                return "Disabled";
            else
                return "Unknown";
        }
        private Color GetAttributeColor(SePrivilegeAttributes Attributes)
        {
            if ((Attributes & SePrivilegeAttributes.EnabledByDefault) != 0)
                return Color.FromArgb(0xc0f0c0);
            else if ((Attributes & SePrivilegeAttributes.Enabled) != 0)
                return Color.FromArgb(0xe0f0e0);
            else if (Attributes == SePrivilegeAttributes.Disabled)
                return Color.FromArgb(0xf0e0e0);
            else
                return Color.White;
        }
        private void menuPrivileges_Popup(object sender, EventArgs e)
        {
            if (listPrivileges.SelectedItems.Count == 0)
            {
                menuPrivileges.DisableAll();
            }
            else
            {
                menuPrivileges.EnableAll();
            }
            if (listPrivileges.Items.Count > 0)
            {
                selectAllMenuItem.Enabled = true;
            }
            else
            {
                selectAllMenuItem.Enabled = false;
            }
        }
        private void enableMenuItem_Click(object sender, EventArgs e)
        {
            foreach (ListViewItem item in listPrivileges.SelectedItems)
            {
                try
                {
                    using (var thandle = _object.GetToken(TokenAccess.AdjustPrivileges))
                        thandle.SetPrivilege(item.Text, SePrivilegeAttributes.Enabled);
                    if (item.SubItems[1].Text != "Default Enabled")
                    {
                        item.BackColor = GetAttributeColor(SePrivilegeAttributes.Enabled);
                        item.SubItems[1].Text = GetAttributeString(SePrivilegeAttributes.Enabled);
                    }
                }
                catch (Exception ex)
                {
                    if (!PhUtils.ShowContinueMessage(
                        "Unable to enable " + item.Text,
                        ex
                        ))
                        return;
                }
            }
        }
        private void disableMenuItem_Click(object sender, EventArgs e)
        {
            foreach (ListViewItem item in listPrivileges.SelectedItems)
            {
                if (item.SubItems[1].Text == "Default Enabled")
                {
                    if (!PhUtils.ShowContinueMessage(
                        "Unable to disable " + item.Text,
                        new Exception("Invalid operation.")
                        ))
                        return;
                    continue;
                }
                try
                {
                    using (var thandle = _object.GetToken(TokenAccess.AdjustPrivileges))
                        thandle.SetPrivilege(item.Text, SePrivilegeAttributes.Disabled);
                    item.BackColor = GetAttributeColor(SePrivilegeAttributes.Disabled);
                    item.SubItems[1].Text = GetAttributeString(SePrivilegeAttributes.Disabled);
                }
                catch (Exception ex)
                {
                    if (!PhUtils.ShowContinueMessage(
                        "Unable to disable " + item.Text,
                        ex
                        ))
                        return;
                }
            }
        }
        private void removeMenuItem_Click(object sender, EventArgs e)
        {
            if (PhUtils.ShowConfirmMessage(
                "remove",
                "the selected privilege(s)",
                "Removing privileges may reduce the functionality of the process, " +
                "and is permanent for the lifetime of the process.",
                false
                ))
            {
                foreach (ListViewItem item in listPrivileges.SelectedItems)
                {
                    try
                    {
                        using (var thandle = _object.GetToken(TokenAccess.AdjustPrivileges))
                            thandle.SetPrivilege(item.Text, SePrivilegeAttributes.Removed);
                        item.Remove();
                    }
                    catch (Exception ex)
                    {
                        if (!PhUtils.ShowContinueMessage(
                            "Unable to remove " + item.Text,
                            ex
                            ))
                            return;
                    }
                }
            }
        }
        private void selectAllMenuItem_Click(object sender, EventArgs e)
        {
            Utils.SelectAll(listPrivileges.Items);
        }
        private void buttonLinkedToken_Click(object sender, EventArgs e)
        {
            using (var thandle = _object.GetToken(TokenAccess.Query))
            {
                var token = new TokenWithLinkedToken(thandle);
                TokenWindow window = new TokenWindow(token);
                window.ShowDialog();
            }
        }
        private void buttonPermissions_Click(object sender, EventArgs e)
        {
            try
            {
                SecurityEditor.EditSecurity(
                    this,
                    SecurityEditor.GetSecurable(
                        NativeTypeFactory.ObjectType.Token,
                        (access) => _object.GetToken((TokenAccess)access)),
                    "Token",
                    NativeTypeFactory.GetAccessEntries(NativeTypeFactory.ObjectType.Token)
                    );
            }
            catch (Exception ex)
            {
                PhUtils.ShowException("Unable to edit security", ex);
            }
        }
    }
}
