namespace ThoughtWorks.CruiseControl.UnitTests.Remote.Messages
{
    using NUnit.Framework;
    using ThoughtWorks.CruiseControl.Remote.Messages;
    [TestFixture]
    public class EncryptedResponseTests
    {
        [Test]
        public void RequestConstructorInitialisesTheValues()
        {
            var request = new EncryptedRequest();
            var response = new EncryptedResponse(request);
            Assert.AreEqual(request.Identifier, response.RequestIdentifier);
        }
        [Test]
        public void FullConstructorInitialisesTheValues()
        {
            var response1 = new EncryptedResponse();
            response1.RequestIdentifier = "12345";
            var response2 = new EncryptedResponse(response1);
            Assert.AreEqual(response1.RequestIdentifier, response2.RequestIdentifier);
        }
        [Test]
        public void EncryptedDataCanBeSetAndRetrieved()
        {
            var request = new EncryptedResponse();
            var data = "SomeEncryptedData";
            request.EncryptedData = data;
            Assert.AreEqual(data, request.EncryptedData);
        }
    }
}
