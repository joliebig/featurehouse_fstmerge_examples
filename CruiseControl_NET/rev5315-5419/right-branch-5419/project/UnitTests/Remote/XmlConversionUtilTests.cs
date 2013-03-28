using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Remote;
using Rhino.Mocks;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.UnitTests.Remote
{
    [TestFixture]
    public class XmlConversionUtilTests
    {
        private MockRepository mocks = new MockRepository();
        [Test]
        public void FindMessageTypeMatchesKnownXmlMessage()
        {
            Type messageType = XmlConversionUtil.FindMessageType("response");
            Assert.AreEqual(typeof(Response), messageType);
        }
        [Test]
        public void FindMessageTypeReturnsNullForUnknownXmlMessage()
        {
            Type messageType = XmlConversionUtil.FindMessageType("garbage");
            Assert.IsNull(messageType);
        }
        [Test]
        public void ConvertXmlToObjectConvertsCorrectly()
        {
            string xml = string.Format("<response xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "result=\"{0}\" timestamp=\"{1:yyyy-MM-ddTHH:mm:ss.FFFFFFFzzz}\" />",
                ResponseResult.Success,
                DateTime.Today);
            object result = XmlConversionUtil.ConvertXmlToObject(typeof(Response), xml);
            Assert.IsInstanceOfType(typeof(Response), result);
            Assert.AreEqual(xml, result.ToString());
        }
        [Test]
        public void ProcessResponseHandlesKnownMessage()
        {
            string xml = string.Format("<response xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "result=\"{0}\" timestamp=\"{1:yyyy-MM-ddTHH:mm:ss.FFFFFFFzzz}\" />",
                ResponseResult.Success,
                DateTime.Today);
            object result = XmlConversionUtil.ProcessResponse(xml);
            Assert.IsInstanceOfType(typeof(Response), result);
            Assert.AreEqual(xml, result.ToString());
        }
        [Test]
        [ExpectedException(typeof(CommunicationsException))]
        public void ProcessResponseThrowsAnExceptionForUnknownMessage()
        {
            string xml = "<garbage/>";
            object result = XmlConversionUtil.ProcessResponse(xml);
        }
    }
}
