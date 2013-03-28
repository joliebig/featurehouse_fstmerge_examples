namespace ThoughtWorks.CruiseControl.UnitTests.Remote.Messages
{
    using System.Collections.Generic;
    using NUnit.Framework;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Remote.Messages;
    [TestFixture]
    public class ListPackagesResponseTests
    {
        [Test]
        public void RequestConstructorInitialisesTheValues()
        {
            var request = new EncryptedRequest();
            var response = new ListPackagesResponse(request);
            Assert.AreEqual(request.Identifier, response.RequestIdentifier);
        }
        [Test]
        public void FullConstructorInitialisesTheValues()
        {
            var response1 = new ListPackagesResponse();
            response1.RequestIdentifier = "12345";
            var response2 = new ListPackagesResponse(response1);
            Assert.AreEqual(response1.RequestIdentifier, response2.RequestIdentifier);
        }
        [Test]
        public void EncryptedDataCanBeSetAndRetrieved()
        {
            var request = new ListPackagesResponse();
            var packages = new List<PackageDetails>();
            request.Packages = packages;
            Assert.AreEqual(packages, request.Packages);
        }
    }
}
