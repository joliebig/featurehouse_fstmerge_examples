using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("fileTransferResponse")]
    [Serializable]
    public class FileTransferResponse
        : Response
    {
        private IFileTransfer fileTransfer;
        public FileTransferResponse()
            : base()
        {
        }
        public FileTransferResponse(ServerRequest request)
            : base(request)
        {
        }
        public FileTransferResponse(Response response)
            : base(response)
        {
        }
        [XmlIgnore]
        public IFileTransfer FileTransfer
        {
            get { return fileTransfer; }
            set { fileTransfer = value; }
        }
    }
}
