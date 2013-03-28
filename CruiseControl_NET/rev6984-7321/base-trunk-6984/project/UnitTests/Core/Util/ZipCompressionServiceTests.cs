namespace ThoughtWorks.CruiseControl.UnitTests.Core.Util
{
    using NUnit.Framework;
    using ThoughtWorks.CruiseControl.Core.Util;
    using System;
    using System.Diagnostics;
    public class ZipCompressionServiceTests
    {
        [Test]
        public void CompressStringValidatesInput()
        {
            var service = new ZipCompressionService();
            var error = Assert.Throws<ArgumentNullException>(() =>
            {
                service.CompressString(null);
            });
            Assert.AreEqual("value", error.ParamName);
        }
        [Test]
        public void CompressStringCompressesAString()
        {
            var service = new ZipCompressionService();
            var inputString = "This is a string to compress - with multiple data, data, data!";
            var expected = "eJxNxEEKwCAMBMCvrPf6m34gtGICRsWs9PvtsTDMqRb4CILLegUHruFzlQhkPEaF70abreAWyvE7vbhUFbg=";
            var actual = service.CompressString(inputString);
            Assert.AreEqual(expected, actual);
        }
        [Test]
        public void ExpandStringValidatesInput()
        {
            var service = new ZipCompressionService();
            var error = Assert.Throws<ArgumentNullException>(() =>
            {
                service.ExpandString(null);
            });
            Assert.AreEqual("value", error.ParamName);
        }
        [Test]
        public void ExpandStringExpandsAString()
        {
            var service = new ZipCompressionService();
            var inputString = "eJxNxEEKwCAMBMCvrPf6m34gtGICRsWs9PvtsTDMqRb4CILLegUHruFzlQhkPEaF70abreAWyvE7vbhUFbg=";
            var expected = "This is a string to compress - with multiple data, data, data!";
            var actual = service.ExpandString(inputString);
            Assert.AreEqual(expected, actual);
        }
    }
}
