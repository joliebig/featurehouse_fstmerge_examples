using System.Windows.Forms;
namespace WikiFunctions.Profiles
{
    public partial class UserPassword : Form
    {
        public UserPassword()
        {
            InitializeComponent();
        }
        public string Username
        {
            set { lblText.Text = string.Format(lblText.Text, value); }
        }
        public string GetPassword
        {
            get { return txtPassword.Text; }
        }
    }
}
