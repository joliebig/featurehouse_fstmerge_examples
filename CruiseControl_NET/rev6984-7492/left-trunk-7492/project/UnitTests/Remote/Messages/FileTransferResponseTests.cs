namespace ThoughtWorks.CruiseControl.UnitTests.Remote.Messages
{
    using NUnit.Framework;
    using Rhino.Mocks;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Remote.Messages;
    [TestFixture]
    public class FileTransferResponseTests
    {
        [Test]
        public void RequestConstructorInitialisesTheValues()
        {
            var request = new EncryptedRequest();
            var response = new FileTransferResponse(request);
            Assert.AreEqual(request.Identifier, response.RequestIdentifier);
        }
        [Test]
        public void FullConstructorInitialisesTheValues()
        {
            var response1 = new FileTransferResponse();
            response1.RequestIdentifier = "12345";
            var response2 = new FileTransferResponse(response1);
            Assert.AreEqual(response1.RequestIdentifier, response2.RequestIdentifier);
        }
        [Test]
        public void FileTransferCanBeSetAndRetrieved()
        {
            var request = new FileTransferResponse();
            var mocks = new MockRepository();
            var transfer = mocks.StrictMock<IFileTransfer>();
            request.FileTransfer = transfer;
            Assert.AreEqual(transfer, request.FileTransfer);
        }
    }
}
