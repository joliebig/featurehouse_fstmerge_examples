namespace ThoughtWorks.CruiseControl.UnitTests.Remote.Messages
{
    using System;
    using System.Collections.Generic;
    using NUnit.Framework;
    using ThoughtWorks.CruiseControl.Remote.Messages;
    [TestFixture]
    public class ErrorMessageTests
    {
        [Test]
        public void MessageConstructorInitialisesTheValues()
        {
            var message = "MyNewSession";
            var request = new ErrorMessage(message);
            Assert.AreEqual(message, request.Message);
        }
        [Test]
        public void FullConstructorInitialisesTheValues()
        {
            var message = "MyNewSession";
            var type = "TheErrorType";
            var request = new ErrorMessage(message, type);
            Assert.AreEqual(message, request.Message);
            Assert.AreEqual(type, request.Type);
        }
        [Test]
        public void TypeCanBeSetAndRetrieved()
        {
            var request = new ErrorMessage();
            var type = "TheErrorType";
            request.Type = type;
            Assert.AreEqual(type, request.Type);
        }
    }
}
