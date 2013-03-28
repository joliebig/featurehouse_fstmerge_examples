using NUnit.Framework;
using System;
using System.Globalization;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;
using System.Threading;
namespace ThoughtWorks.CruiseControl.UnitTests
{
    public static class TestHelpers
    {
        public static void EnsureLanguageIsValid()
        {
            Thread.CurrentThread.CurrentUICulture = new CultureInfo("en");
        }
        public static object RunSerialisationTest(object value)
        {
            object result = null;
            MemoryStream stream = new MemoryStream();
            BinaryFormatter formatter = new BinaryFormatter();
            try
            {
                formatter.Serialize(stream, value);
            }
            catch (Exception error)
            {
                Assert.Fail(string.Format("Unable to serialise: {0}", error.Message));
            }
            stream.Position = 0;
            try
            {
                result = formatter.Deserialize(stream);
            }
            catch (Exception error)
            {
                Assert.Fail(string.Format("Unable to deserialise: {0}", error.Message));
            }
            return result;
        }
    }
}
