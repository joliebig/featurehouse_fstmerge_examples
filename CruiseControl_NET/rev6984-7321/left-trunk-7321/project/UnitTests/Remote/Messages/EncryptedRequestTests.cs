namespace ThoughtWorks.CruiseControl.UnitTests.Remote.Messages
{
    using NUnit.Framework;
    using ThoughtWorks.CruiseControl.Remote.Messages;
    [TestFixture]
    public class EncryptedRequestTests
    {
        [Test]
        public void SessionConstructorInitialisesTheValues()
        {
            var sessionId = "MyNewSession";
            var request = new EncryptedRequest(sessionId);
            Assert.AreEqual(sessionId, request.SessionToken);
        }
        [Test]
        public void FullConstructorInitialisesTheValues()
        {
            var sessionId = "MyNewSession";
            var data = "SomeEncryptedData";
            var request = new EncryptedRequest(sessionId, data);
            Assert.AreEqual(sessionId, request.SessionToken);
            Assert.AreEqual(data, request.EncryptedData);
        }
        [Test]
        public void EncryptedDataCanBeSetAndRetrieved()
        {
            var request = new EncryptedRequest();
            var data = "SomeEncryptedData";
            request.EncryptedData = data;
            Assert.AreEqual(data, request.EncryptedData);
        }
    }
}
