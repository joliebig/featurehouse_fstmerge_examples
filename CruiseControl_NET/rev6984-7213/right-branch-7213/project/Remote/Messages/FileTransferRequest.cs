using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("fileTransferMessage")]
    [Serializable]
    public class FileTransferRequest
        : ProjectRequest
    {
        private string fileName;
        public FileTransferRequest()
        {
        }
        public FileTransferRequest(string sessionToken)
            : base(sessionToken)
        {
        }
        public FileTransferRequest(string sessionToken, string projectName, string fileName)
            : base(sessionToken, projectName)
        {
            this.fileName = fileName;
        }
        [XmlElement("fileName")]
        public string FileName
        {
            get { return fileName; }
            set { fileName = value; }
        }
    }
}
