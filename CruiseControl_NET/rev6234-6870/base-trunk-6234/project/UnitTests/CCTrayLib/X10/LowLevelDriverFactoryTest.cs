using System;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.CCTrayLib.X10;
using ThoughtWorks.CruiseControl.CCTrayLib.Configuration;
namespace ThoughtWorks.CruiseControl.UnitTests.CCTrayLib.X10
{
    [TestFixture]
    public class LowLevelDriverFactoryTest
    {
        [Test]
        public void ShouldCreateTheCm11DriverBasedOnType()
        {
            X10Configuration configuration = new X10Configuration();
            configuration.DeviceType = ControllerType.CM11.ToString();
            configuration.ComPort = "COM1";
            LowLevelDriverFactory factory = new LowLevelDriverFactory(configuration);
            try
            {
                IX10LowLevelDriver driver = factory.getDriver();
                if (driver != null)
                {
                    Assert.IsInstanceOfType(typeof(Cm11LowLevelDriver), driver,"driver should be correct type");
                }
            }
            catch (ApplicationException appEx)
            {
                Assert.IsTrue(appEx.InnerException.Message.Contains("The port 'COM1' does not exist."),"threw an exception, but the message was wrong");
            }
        }
        [Test]
        public void ShouldCreateTheCm17aDriverBasedOnType()
        {
            X10Configuration configuration = new X10Configuration();
            configuration.DeviceType = ControllerType.CM17A.ToString();
            configuration.ComPort = "COM1";
            LowLevelDriverFactory factory = new LowLevelDriverFactory(configuration);
            IX10LowLevelDriver driver = factory.getDriver();
            if (driver != null)
            {
                Assert.IsInstanceOfType(typeof(Cm17LowLevelDriver), driver);
            }
        }
    }
}
