using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("changePasswordMessage")]
    [Serializable]
    public class ChangePasswordRequest
        : ServerRequest
    {
        private string oldPassword;
        private string newPassword;
        private string userName;
        [XmlAttribute("oldPassword")]
        public string OldPassword
        {
            get { return oldPassword; }
            set { oldPassword = value; }
        }
        [XmlAttribute("newPassword")]
        public string NewPassword
        {
            get { return newPassword; }
            set { newPassword = value; }
        }
        [XmlAttribute("userName")]
        public string UserName
        {
            get { return userName; }
            set { userName = value; }
        }
    }
}
